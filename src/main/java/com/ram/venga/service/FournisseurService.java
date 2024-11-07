package com.ram.venga.service;

import java.util.*;
import java.util.stream.Collectors;

import com.ram.venga.domain.*;
import com.ram.venga.repos.CadeauxBARepository;
import com.ram.venga.repos.OrigineEmissionRepository;
import com.ram.venga.repos.UtilisateurRepository;
import net.snowflake.client.jdbc.internal.google.api.gax.rpc.AlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.FournisseurMapper;
import com.ram.venga.model.FournisseurDTO;
import com.ram.venga.repos.FournisseurRepository;
import com.ram.venga.util.NotFoundException;

import javax.persistence.EntityNotFoundException;

@Service
public class FournisseurService {

	private final FournisseurMapper fournisseurMapper;
    private final FournisseurRepository fournisseurRepository;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final CadeauxBARepository cadeauxBARepository;
    private final KeycloackService keycloackService;
    private final UtilisateurRepository utilisateurRepository;

    public FournisseurService(final FournisseurMapper fournisseurMapper,
                              final FournisseurRepository fournisseurRepository, OrigineEmissionRepository origineEmissionRepository, CadeauxBARepository cadeauxBARepository, KeycloackService keycloackService, UtilisateurRepository utilisateurRepository, KeycloackService keycloackService1, UtilisateurRepository utilisateurRepository1) {
    	this.fournisseurMapper = fournisseurMapper;
        this.fournisseurRepository = fournisseurRepository;
        this.origineEmissionRepository = origineEmissionRepository;
        this.cadeauxBARepository = cadeauxBARepository;
        this.keycloackService = keycloackService1;
        this.utilisateurRepository = utilisateurRepository1;
    }

    public List<FournisseurDTO> findAll() {
        String keyUser = keycloackService.getIdUserToken();
        Long idOrigineByUser = utilisateurRepository.findByRefKUser(keyUser)
                .map(utilisateur -> utilisateur.getCollaborateur().getEntite().getOrigineEmission().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found or has no origineEmission"));
        final List<Fournisseur> fournisseurs = fournisseurRepository.findAllByOrigineEmission(idOrigineByUser);
        fournisseurs.forEach(fournisseur -> {
            Set<CadeauxBA> filteredCadeauxBAs = fournisseur.getCadeauxBAs().stream()
                    .filter(cadeauxBA -> cadeauxBA.getOrigineEmission().getId().equals(idOrigineByUser))
                    .collect(Collectors.toSet());
            fournisseur.setCadeauxBAs(filteredCadeauxBAs);
        });
        return fournisseurs.stream()
                .map(fournisseurMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<FournisseurDTO> findAllList() {
        final List<Fournisseur> fournisseurs = fournisseurRepository.findAll();
        return fournisseurs.stream()
                .map(fournisseurMapper::toDto)
                .toList();
    }


    public FournisseurDTO get(final Long id) {
        return fournisseurRepository.findById(id)
        		.map(fournisseur -> fournisseurMapper.toDto(fournisseur))
                .orElseThrow(NotFoundException::new);
    }

    public  ResponseEntity<?> create(final FournisseurDTO fournisseurDTO) {
        return saveFournisseur(fournisseurDTO);
    }

    public ResponseEntity<?> update( final FournisseurDTO fournisseurDTO) {
        return saveFournisseur(fournisseurDTO);
    }


    public ResponseEntity<?> saveFournisseur(FournisseurDTO fournisseurDTO){
        Map<String,String> map = new HashMap<>();
        Fournisseur fournisseur  = fournisseurMapper.toEntity(fournisseurDTO);
        Fournisseur fournisseurCheck = fournisseurRepository.findByNomOrEmail(fournisseurDTO.getNom(),fournisseur.getEmail());
        if(fournisseurCheck!= null){
            throw new IllegalArgumentException("email or nom already existe");
        }
            fournisseurRepository.save(fournisseur);
        return new ResponseEntity<>(fournisseurDTO.getId(), HttpStatus.OK);
    }


    public ResponseEntity<?> delete(final Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id).get();
        Map<String,String> map = new HashMap<>();
        if (fournisseur.getCadeauxBAs().size() != 0){
            map.put("message","Ce fournisseur a au moins un cadeaux bon achat");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        fournisseurRepository.deleteById(id);
        return new ResponseEntity<>(id,HttpStatus.OK);
    }

    public boolean nomExists(final String nom) {
        return fournisseurRepository.existsByNomIgnoreCase(nom);
    }

    public Page<FournisseurDTO> findAllWithPage(String keyword,Pageable pageable) {
        final Page<Fournisseur> fournisseurs = fournisseurRepository.findAllWithKeyword(keyword,pageable);
        return fournisseurs.map(fournisseurMapper::toDto);

    }
}
