package com.ram.venga.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ram.venga.domain.ClasseReservation;
import com.ram.venga.repos.ClasseReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.mapper.ClasseProduitMapper;
import com.ram.venga.model.ClasseProduitDTO;
import com.ram.venga.repos.ClasseProduitRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class ClasseProduitService {

	private final ClasseProduitMapper classeProduitMapper;
    private final ClasseProduitRepository classeProduitRepository;
    private final ClasseReservationRepository classeReservationRepository;

    public ClasseProduitService(final ClasseProduitMapper classeProduitMapper,
                                final ClasseProduitRepository classeProduitRepository, ClasseReservationRepository classeReservationRepository) {
    	this.classeProduitMapper = classeProduitMapper;
        this.classeProduitRepository = classeProduitRepository;
        this.classeReservationRepository = classeReservationRepository;
    }

    public List<ClasseProduitDTO> findAll() {
        final List<ClasseProduit> classeProduits = classeProduitRepository.findAll(Sort.by("dateCreated").descending());
        return classeProduits.stream()
                .map(classeProduit -> classeProduitMapper.toDto(classeProduit))
                .toList();
    }

    public ClasseProduitDTO get(final Long id) {
        return classeProduitRepository.findById(id)
                .map(classeProduit -> classeProduitMapper.toDto(classeProduit))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create(final ClasseProduitDTO classeProduitDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            ClasseProduit classeProduit = classeProduitMapper.toEntity(classeProduitDTO);
            classeProduitRepository.save(classeProduit);
        }catch (Exception e){
            map.put("message","Classe produit déjà exist");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(classeProduitDTO.getId(),HttpStatus.OK);
    }

    public  ResponseEntity<?> update(final Long id, final ClasseProduitDTO classeProduitDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            ClasseProduit classeProduit = classeProduitMapper.toEntity(classeProduitDTO);
            classeProduit.setId(id);
            classeProduitRepository.save(classeProduit);
        }catch (Exception e){
            map.put("message","Classe produit déjà exist");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<>(id,HttpStatus.OK);
    }

    public ResponseEntity<?> delete(final Long id) {
        Map<String,String> map = new HashMap<>();
        try {
            classeProduitRepository.deleteById(id);

        }catch (Exception e){
            map.put("message","Ce classe produit a au moins une classe reservation");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    public boolean codeExists(final String code) {
        return classeProduitRepository.existsByCodeIgnoreCase(code);
    }

    public Page<ClasseProduitDTO> findByKeyWord(String keyword, Pageable pageable){
        Page<ClasseProduit> classeProduitPage = classeProduitRepository.findByKeyWord(keyword,pageable);
        return classeProduitPage.map(classeProduitMapper::toDto);
    }

    public ClasseProduit findByCode(String code) {
        return classeProduitRepository.findByCode(code)
                .orElseThrow(()-> new NotFoundException(String.format("une classe produit avec le code %s n'a pas été trouvée.",code)));
    }
}

