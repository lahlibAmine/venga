package com.ram.venga.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.ram.venga.domain.*;
import com.ram.venga.repos.ClasseProduitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.ConcoursMapper;
import com.ram.venga.model.ConcoursDTO;
import com.ram.venga.repos.ClasseReservationRepository;
import com.ram.venga.repos.ConcoursRepository;
import com.ram.venga.repos.OrigineEmissionRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class ConcoursService {

	private final ConcoursMapper concoursMapper;
    private final ConcoursRepository concoursRepository;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final ClasseReservationRepository classeReservationRepository;
    private final ClasseProduitRepository classeProduitRepository;


    public ConcoursService(final ConcoursMapper concoursMapper,
                           final ConcoursRepository concoursRepository,
                           final OrigineEmissionRepository origineEmissionRepository,
                           final ClasseReservationRepository classeReservationRepository, ClasseProduitRepository classeProduitRepository) {
    	this.concoursMapper = concoursMapper;
        this.concoursRepository = concoursRepository;
        this.origineEmissionRepository = origineEmissionRepository;
        this.classeReservationRepository = classeReservationRepository;
        this.classeProduitRepository = classeProduitRepository;
    }

    public Page<ConcoursDTO> findAll(Pageable pageable) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateCreated").ascending()
        );
        final Page<Concours> concours = concoursRepository.findAll(pageable);
        return concours.map(concoursMapper::toDto);
    }

    public ConcoursDTO get(final Long id) {
        return concoursRepository.findById(id)
                .map(concours -> concoursMapper.toDto(concours))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create(final ConcoursDTO concoursDTO) {
        return saveConcours(concoursDTO);
    }

    public ResponseEntity<?> saveConcours(ConcoursDTO concoursDTO){
        Set<ClasseProduit> classeProduit = null;
        Map<String,String> map = new HashMap<>();
        // Vérification de la période spécifiée dans ConcoursDTO
        LocalDate dateDebutDTO = concoursDTO.getDateDebut();
        List<Concours> concoursOverlap = null;
        LocalDate dateFinDTO = concoursDTO.getDateFin();
        Concours savedConcours = null;
        Optional<Set<ClasseProduit>> classeProduitOptional = classeProduitRepository.findByIdIn(concoursDTO.getClasseProduit().stream().toList());
        OrigineEmission origineEmission = origineEmissionRepository.findById(concoursDTO.getOrigineEmission()).orElseThrow(NotFoundException::new);
        try{
            classeProduit = classeProduitOptional.get();

        }catch (Exception e){
            map.put("message","classeProduit not found .");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
        if(concoursDTO.getId()!= null && dateDebutDTO != null && dateFinDTO != null){
    concoursOverlap = concoursRepository.findOverlappingConcours(dateDebutDTO, dateFinDTO,concoursDTO.getClasseProduit(),concoursDTO.getId(),concoursDTO.getOrigineEmission());
            if (!concoursOverlap.isEmpty()) {
                map.put("message", "La période spécifiée chevauche une période existante.");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
            Concours co = concoursMapper.toEntity(concoursDTO);
            co.setOrigineEmission(origineEmission);
            co.setClasseProduits(classeProduit);
            savedConcours = concoursRepository.save(co);

    }else {
            // Requête pour vérifier si la période chevauche une période existante
             concoursOverlap = concoursRepository.findOverlappingConcours(dateDebutDTO, dateFinDTO,concoursDTO.getClasseProduit(),concoursDTO.getId(),concoursDTO.getOrigineEmission());
            if (!concoursOverlap.isEmpty()) {
                map.put("message", "La période spécifiée chevauche une période existante.");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }

    Concours co = concoursMapper.toEntity(concoursDTO);
    co.setOrigineEmission(origineEmission);
    co.setClasseProduits(classeProduit);
    savedConcours = concoursRepository.save(co);
        }



        return new ResponseEntity<>(savedConcours.getId(), HttpStatus.CREATED);
    }
    public ResponseEntity<?> update( final ConcoursDTO concoursDTO) {
        return saveConcours(concoursDTO);
    }

    public void delete(final Long id) {
        concoursRepository.deleteById(id);
    }

    public boolean libelleExists(final String libelle) {
        return concoursRepository.existsByLibelleIgnoreCase(libelle);
    }

    public Page<ConcoursDTO> getAllConcoursByKeyword(String origin, List<String> classProduit, Pageable pageable){
        if(classProduit.isEmpty()){
            classProduit = null;
        }
       List<String> classProduits = classProduit != null ? classProduit.stream().map(String::toLowerCase).collect(Collectors.toList()) : null;
        Page<Concours> concours = concoursRepository.findByKeyword(origin,classProduits, pageable);
        return concours.map(concoursMapper::toDto);
    }

}

