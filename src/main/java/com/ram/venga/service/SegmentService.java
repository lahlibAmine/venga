package com.ram.venga.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ram.venga.repos.PrimeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.Segment;
import com.ram.venga.mapper.SegmentMapper;
import com.ram.venga.model.SegmentDTO;
import com.ram.venga.repos.SegmentRepository;
import com.ram.venga.util.NotFoundException;

import javax.persistence.OrderBy;


@Service
public class SegmentService {

	private final SegmentMapper segmentMapper;
    private final SegmentRepository segmentRepository;
    private final PrimeRepository primeRepository;

    public SegmentService(final SegmentMapper segmentMapper,
                          final SegmentRepository segmentRepository, PrimeRepository primeRepository) {
    	this.segmentMapper = segmentMapper;
        this.segmentRepository = segmentRepository;
        this.primeRepository = primeRepository;
    }

    public Object findAll(Boolean pageCheck, Pageable pageable,String keyword) {
        if(pageCheck == true){
            return segmentRepository.findAllByCodeOrEscaleDepartOrEscaleDestinationLike(keyword,pageable);


        }else{
            List<Segment> segments = segmentRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
            return segments.stream().map(segmentMapper::toDto);
        }

    }

    public SegmentDTO get(final Long id) {
        return segmentRepository.findById(id)
        		.map(segment -> segmentMapper.toDto(segment))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create(final SegmentDTO segmentDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            segmentDTO.setCode(segmentDTO.getEscaleDepart()+segmentDTO.getEscaleDestination());
            segmentRepository.save(segmentMapper.toEntity(segmentDTO));
        }catch (Exception e){
            map.put("message","segment existe deja .");
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new  ResponseEntity<>(segmentDTO.getId(), HttpStatus.OK);
    }

    public ResponseEntity<?> update(final SegmentDTO segmentDTO) {
        Map<String, String> map = new HashMap<>();
        try{
            segmentRepository.findById(segmentDTO.getId())
                    .orElseThrow(NotFoundException::new);
            segmentDTO.setCode(segmentDTO.getEscaleDepart()+"-"+segmentDTO.getEscaleDestination());
            segmentRepository.save(segmentMapper.toEntity(segmentDTO));
        }catch (Exception e){
            map.put("message","segment existe deja .");
             return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(segmentDTO.getId(),HttpStatus.OK);
    }

    public ResponseEntity<?> delete(final Long id) {
        Map<String,String> map = new HashMap<>();
        try{
            segmentRepository.deleteById(id);
        }catch (Exception e){
            map.put("message","Ce segment a au moins un prime . ");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
        return  new ResponseEntity<>("" , HttpStatus.OK);
    }

    public boolean codeExists(final String code) {
        return segmentRepository.existsByCodeIgnoreCase(code);
    }

    public Segment findByCode(String code) {
        return segmentRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException(String.format("Le segment avec le code %s n'a pas été trouvé.", code)));
    }

}
