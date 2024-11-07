package com.ram.venga.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Segment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.ClasseReservation;
import com.ram.venga.mapper.ClasseReservationMapper;
import com.ram.venga.model.ClasseReservationDTO;
import com.ram.venga.repos.ClasseProduitRepository;
import com.ram.venga.repos.ClasseReservationRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class ClasseReservationService {

	private final ClasseReservationMapper classeReservationMapper;
    private final ClasseReservationRepository classeReservationRepository;
    private final ClasseProduitRepository classeProduitRepository;

    public ClasseReservationService(final ClasseReservationMapper classeReservationMapper,
    		final ClasseReservationRepository classeReservationRepository,
            final ClasseProduitRepository classeProduitRepository) {
    	this.classeReservationMapper = classeReservationMapper;
        this.classeReservationRepository = classeReservationRepository;
        this.classeProduitRepository = classeProduitRepository;
    }

    public Object findAll(Boolean pageCheck,String keyword, Pageable pageable) {

        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        }
        if(pageCheck == true){
            Page<ClasseReservation> reservations ;


            String finalKeyword = keyword;
            if (keyword.trim().isEmpty()) {
                reservations =  classeReservationRepository.findAll(pageable);
            }else{
                reservations = classeReservationRepository.findAllByCodeOrLibelleLike(keyword,pageable);
            }
            return reservations;
        }else{
            final List<ClasseReservation> segments =  classeReservationRepository.findAll(Sort.by(Sort.Order.asc("code")));
            return segments.stream().map(classeReservationMapper::toDto);
        }


    }

    public ClasseReservationDTO get(final Long id) {
        return classeReservationRepository.findById(id)
                .map(classeReservation -> classeReservationMapper.toDto(classeReservation))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create(final ClasseReservationDTO classeReservationDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            classeReservationRepository.save(classeReservationMapper.toEntity(classeReservationDTO));
        }catch(Exception e){
            map.put("message","class reservation déjà exist");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(classeReservationDTO.getId(),HttpStatus.OK);

    }

    public ResponseEntity<?> update(final ClasseReservationDTO classeReservationDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            classeReservationRepository.save(classeReservationMapper.toEntity(classeReservationDTO));
        }catch(Exception e){
            map.put("message","class reservation déjà exist");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(classeReservationDTO.getId(),HttpStatus.OK);
    }

    public ResponseEntity<?> delete(final Long id ) {
        Map<String,String> map = new HashMap<>();
        try {
            classeReservationRepository.deleteById(id);

        }catch (Exception e){
            map.put("message","Ce classe produit a au moins une concours");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(id, HttpStatus.OK);

    }

    public boolean codeExists(final String code) {
        return classeReservationRepository.existsByCodeIgnoreCase(code);
    }

}

