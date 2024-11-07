package com.ram.venga.service;

import java.util.*;
import java.util.stream.Collectors;

import com.ram.venga.domain.*;
import com.ram.venga.model.CadeauBaPost;
import com.ram.venga.repos.*;
import com.ram.venga.util.UUIDGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.CadeauxBAMapper;
import com.ram.venga.model.CadeauxBADTO;
import com.ram.venga.util.NotFoundException;

import javax.persistence.EntityNotFoundException;


@Service
public class CadeauxBAService {

	private final CadeauxBAMapper cadeauxBAMapper;
    private final CadeauxBARepository cadeauxBARepository;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final FournisseurRepository fournisseurRepository;
    private final CategorieCadeauRepository categorieCadeauRepository;
    private final PaysRepository paysRepository;
    private final DevisesRepository devisesRepository;
    private final LigneCommandeRepository ligneCommandeRepository;

    public CadeauxBAService(final CadeauxBAMapper cadeauxBAMapper,
                            final CadeauxBARepository cadeauxBARepository,
                            final OrigineEmissionRepository origineEmissionRepository,
                            final FournisseurRepository fournisseurRepository,
                            final CategorieCadeauRepository categorieCadeauRepository, PaysRepository paysRepository, DevisesRepository devisesRepository, LigneCommandeRepository ligneCommandeRepository) {
    	this.cadeauxBAMapper = cadeauxBAMapper;
        this.cadeauxBARepository = cadeauxBARepository;
        this.origineEmissionRepository = origineEmissionRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.categorieCadeauRepository = categorieCadeauRepository;
        this.paysRepository = paysRepository;
        this.devisesRepository = devisesRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
    }

    public List<CadeauxBADTO> findAll() {
        final List<CadeauxBA> cadeauxBAs = cadeauxBARepository.findAll(Sort.by("dateCreated").descending());
        return cadeauxBAs.stream()
                .map(cadeauxBA -> cadeauxBAMapper.toDto(cadeauxBA))
                .toList();
    }

    public Page<CadeauxBADTO> findByKeyWord(String keyword, Pageable pageable){
        Page<CadeauxBA> cadeauxBAPage = cadeauxBARepository.findByKeyWord(keyword,pageable);
        return cadeauxBAPage.map(cadeauxBAMapper::toDto);
    }

    public CadeauxBADTO get(final Long id) {
        return cadeauxBARepository.findById(id)
                .map(cadeauxBA -> cadeauxBAMapper.toDto(cadeauxBA))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create(final CadeauBaPost cadeauxBADTO) {
        // Check if the nbrPoint is positive
        throwExceptionIfCadeauxBAINombrePointIsNegative(cadeauxBADTO);

        // Retrieve the required entities
        Devise devise = null;
        OrigineEmission origineEmission = null;
        Map<String,String> map = new HashMap<>();
        Optional<Devise> deviseOptional = devisesRepository.findById(cadeauxBADTO.getDevise());
        try{
            devise = deviseOptional.get();

        }catch (Exception e){
            map.put("message","devise not found .");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
        try {
            origineEmission = origineEmissionRepository.findById(cadeauxBADTO.getOrigineEmission())
                    .get();

        }catch (Exception e){
            map.put("message","origine not found .");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }


        // Create a new CadeauxBA entity from DTO
        CadeauxBA ba = cadeauxBAMapper.toEntity(cadeauxBADTO);

        // Associate the retrieved Devise with the OrigineEmission
        origineEmission.setDevise(devise);

        // Update and save the OrigineEmission
        origineEmissionRepository.save(origineEmission);

        // Update and save the CadeauxBA entity with the updated OrigineEmission
        ba.setOrigineEmission(origineEmission);
        CadeauxBA savedCadeauxBA = cadeauxBARepository.save(ba);

        return new ResponseEntity<>(savedCadeauxBA.getId(), HttpStatus.CREATED);
    }

    //@Transactional
    public ResponseEntity<?> update(final CadeauBaPost cadeauxBADTO) {

        // Check if the nbrPoint is positive
        throwExceptionIfCadeauxBAINombrePointIsNegative(cadeauxBADTO);

        // Retrieve the required entities
        Devise devise = null;
        OrigineEmission origineEmission = null;
        Map<String,String> map = new HashMap<>();
        Optional<Devise> deviseOptional = devisesRepository.findById(cadeauxBADTO.getDevise());
        try{
             devise = deviseOptional.get();

        }catch (Exception e){
            map.put("message","devise not found .");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
    try {
         origineEmission = origineEmissionRepository.findById(cadeauxBADTO.getOrigineEmission())
                .get();

    }catch (Exception e){
        map.put("message","origine not found .");
        return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
        CadeauxBA savedCadeauxBA = null;
        try{

        // Create a new CadeauxBA entity from DTO
        CadeauxBA ba = cadeauxBAMapper.toEntity(cadeauxBADTO);
            ba.setOrigineEmission(origineEmission);
            savedCadeauxBA =   cadeauxBARepository.save(ba);

        // Associate the retrieved Devise with the OrigineEmission
        origineEmission.setDevise(devise);

        // Update and save the OrigineEmission
        origineEmissionRepository.save(origineEmission);

        // Update and save the CadeauxBA entity with the updated OrigineEmission

    }catch(Exception e){
        map.put("message","l'origine existe deja .");
        return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }

        return new ResponseEntity<>(savedCadeauxBA.getId(), HttpStatus.CREATED);
    }



    public ResponseEntity<?> delete(final Long id) {
        List<LigneCommande> ligneCommandes = ligneCommandeRepository.findBycadeauxBAId(id);
        Map<String,String> map = new HashMap<>();
        if (ligneCommandes.size() > 0){
            map.put("message","Ce catalogue bon d'achat ne peut pas être supprimé car il contient au moins une commande.");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        cadeauxBARepository.deleteById(id);
        return new ResponseEntity<>(id,HttpStatus.OK);
    }


    public Map<String,List<CadeauxBA>> getLigneCommandeByFournisseur() {
        Map<String,List<CadeauxBA>> map= cadeauxBARepository.findAll().stream().collect(Collectors.groupingBy(cadeauxBA -> cadeauxBA.getFournisseur().getNom()));

      return map;
    }

    private void throwExceptionIfCadeauxBAINombrePointIsNegative(CadeauBaPost cadeauBaPost) {
        if (cadeauBaPost.getNbrPoint() < 0) {
            throw new IllegalArgumentException("le nombre doit être positive");
        }
    }
}
