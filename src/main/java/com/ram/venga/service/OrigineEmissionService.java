package com.ram.venga.service;

import java.time.LocalDate;
import java.util.*;

import com.ram.venga.domain.*;
import com.ram.venga.mapper.EntiteMapper;
import com.ram.venga.model.*;
import com.ram.venga.repos.CadeauxBARepository;
import com.ram.venga.repos.DevisesRepository;
import com.ram.venga.repos.PaysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.OrigineEmissionMapper;
import com.ram.venga.repos.OrigineEmissionRepository;
import com.ram.venga.util.NotFoundException;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class OrigineEmissionService {

	private final OrigineEmissionMapper origineEmissionMapper;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final PrimeService primeService;
    private final CadeauxBARepository cadeauxBARepository;
    private final DevisesRepository devisesRepository;
    private final EntiteMapper entiteMapper;
    private final PaysRepository paysRepository;

    public Page<OrigineEmissionDeviseDTO> findAll(String keyword,Pageable pageable) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateCreated").ascending()
        );
        final Page<OrigineEmission> origineEmissions = origineEmissionRepository.findAllWithKeyword(keyword,pageable);
        return origineEmissions.map(origineEmissionMapper::toDtoOrigine);
    }

    public Page<OrigineEmissionDTO> findAll(){
        final Page<OrigineEmission> origineEmissions = origineEmissionRepository.findAll(Pageable.unpaged());
        return origineEmissions.map(origineEmissionMapper::toDto);
    }

    public OrigineEmissionDTO get(final Long id) {
        return origineEmissionRepository.findById(id)
        		.map(origineEmission -> origineEmissionMapper.toDto(origineEmission))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create(final OrigineEmissionDeviseAddDto origineEmissionDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            OrigineEmission origineEmission = origineEmissionMapper.toEntity(origineEmissionDTO);
            origineEmission.setDateCreated(LocalDate.now());
            origineEmission.setDevise(null);
            origineEmissionRepository.save(origineEmission);

        }catch (Exception e){
            map.put("message","Nom existant");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<>(origineEmissionDTO.getId(), HttpStatus.OK);
    }

    public ResponseEntity<?> update(final OrigineEmissionDeviseAddDto origineEmissionDTO) {
        Map<String,String> map = new HashMap<>();
        OrigineEmission origineEmissionRequest = origineEmissionRepository.findById(origineEmissionDTO.getId()).get();

        try{
            OrigineEmission origineEmission = origineEmissionMapper.toEntity(origineEmissionDTO);
            origineEmission.setLastUpdated(LocalDate.now());
            origineEmission.setDateCreated(LocalDate.now());
            origineEmission.setDevise(origineEmissionRequest.getDevise());
            origineEmission.setPays(origineEmissionRequest.getPays());
            return  new ResponseEntity<>(origineEmissionRepository.save(origineEmission).getId(), HttpStatus.OK);
        }catch (Exception e){
            map.put("message","Nom existant");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
    }

    public ResponseEntity<?> delete(final Long id) {
        Map<String,String> map = new HashMap<>();
        try{
            origineEmissionRepository.deleteById(id);
        }catch (Exception e){
            map.put("message","Ce origine d'emission lié à un prime");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    public boolean nomExists(final String nom) {
        return origineEmissionRepository.existsByNomIgnoreCase(nom);
    }

    public ResponseEntity<?> createDevise(final OrigineEmissionDeviseAddDto origineEmissionDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            OrigineEmission origineEmission = origineEmissionMapper.toEntity(origineEmissionDTO);
            origineEmission.setDateCreated(LocalDate.now());
            origineEmissionRepository.save(origineEmission);

        }catch (Exception e){
            map.put("message","origine exist déjà");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<>(origineEmissionDTO.getId(), HttpStatus.OK);
    }

    public Page<OrigineEmissionDeviseDTO> findAllByDevise(String keyword, Pageable pageable) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateCreated").ascending()
        );
        final Page<OrigineEmission> origineEmissions = origineEmissionRepository.findAllWithKeywordDevise(keyword,pageable);
        return origineEmissions.map(origineEmissionMapper::toDtoOrigine);

    }

    public ResponseEntity<?> updateDevise(OrigineEmissionDeviseAddDto origineEmissionDTO) {
        //   origineEmissionDTO.setId(origineEmissionDTO.getId());
         //  Map<String,String> map = new HashMap<>();

          //  try{
                OrigineEmission emission = origineEmissionRepository.findById(origineEmissionDTO.getId()).orElseThrow(() -> new NotFoundException("cette origine emission n'existe pas"));
                Devise devise = devisesRepository.findById(origineEmissionDTO.getDevise()).orElseThrow(() -> new NotFoundException("cette devise  n'existe pas"));
                Pays pays = paysRepository.findById(origineEmissionDTO.getPays()).orElseThrow(() -> new NotFoundException("ce pays  n'existe pas"));
                OrigineEmission checkNom = origineEmissionRepository.findByNomNotId(origineEmissionDTO.getNom(),origineEmissionDTO.getId());
                if(checkNom != null){
                    throw new IllegalArgumentException("ce nom existe deja !!");
                }
                emission.setNom(origineEmissionDTO.getNom());
                emission.setDevise(devise);
                emission.setPays(pays);
                emission.setNom(origineEmissionDTO.getNom());
                emission.setLastUpdated(LocalDate.now());
                emission.setDateCreated(LocalDate.now());
                emission.setDevise(devise);
                return  new ResponseEntity<>(origineEmissionRepository.save(emission).getId(), HttpStatus.OK);
            /*}catch (Exception e){
                map.put("message","origine exist déja");
                return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }*/
    }

    public ResponseEntity<?> deleteNbrPoint(final Long id) {
       OrigineEmission emission =  origineEmissionRepository.findById(id).get();
       //emission.setNbrPointBienvenue(null);
       origineEmissionRepository.save(emission);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    public Page<OrigineEmissionDeviseDTO> findAllNbrPoint(String keyword,Pageable pageable) {
        final Page<OrigineEmission> origineEmissions = origineEmissionRepository.findAllWithKeywordNbr(keyword,pageable);
        return origineEmissions.map(origineEmissionMapper::toDtoOrigine);
    }

    public OrigineEmission findById(Long id) {
        return origineEmissionRepository.findById(id)
                .orElseThrow(()-> new NotFoundException(String.format("une classe produit avec l'identifiant %s n'a pas été trouvée.",id)));
    }
}
