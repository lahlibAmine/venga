package com.ram.venga.service;


import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.domain.DemandeInscription;
import com.ram.venga.domain.Devise;
import com.ram.venga.mapper.DevisesMapper;
import com.ram.venga.model.DeviseDto;
import com.ram.venga.repos.DevisesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DevisesService {

    private final DevisesRepository devisesRepository;

    private final DevisesMapper devisesMapper;

    public DevisesService(DevisesRepository devisesRepository, DevisesMapper devisesMapper) {
        this.devisesRepository = devisesRepository;
        this.devisesMapper = devisesMapper;
    }

    public List<DeviseDto> devises(){
        List<Devise> devises = devisesRepository.findAll();
        List<DeviseDto> dto=  devisesMapper.toDto(devises);
        return dto;

    }

    public Page<DeviseDto> devisesPage(String keyword,Pageable pageable) {
        Page<Devise> devises = devisesRepository.findAllByKeyword(keyword,pageable);
        Page<DeviseDto> dto=  devises.map(devisesMapper::toDto);
        return dto;
    }

    public ResponseEntity<?> saveDevises(DeviseDto deviseDto) {
        return deviceOpperation(deviseDto);
    }

    public ResponseEntity<?> updateDevises(DeviseDto deviseDto) {
       return deviceOpperation(deviseDto);
    }

    public ResponseEntity<?> deviceOpperation(DeviseDto deviseDto) {
        Map<String, String> map = new HashMap<>();
        try{
            return ResponseEntity.ok(devisesRepository.save(devisesMapper.toEntity(deviseDto)).getId());

        }catch(Exception e){
            map.put("message","ce dévise exist déjà");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
    }
    public ResponseEntity<?> delete(final Long id) {
        Map<String,String> map = new HashMap<>();
        try{
            devisesRepository.deleteById(id);
        }catch(Exception e){
            map.put("message","Ce dévise a une relation avec origine emission .");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);

    }


}
