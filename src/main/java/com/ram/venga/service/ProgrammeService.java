package com.ram.venga.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.Programme;
import com.ram.venga.mapper.ProgrammeMapper;
import com.ram.venga.model.ProgrammeDTO;
import com.ram.venga.repos.ProgrammeRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class ProgrammeService {

	private final ProgrammeMapper programmeMapper;
    private final ProgrammeRepository programmeRepository;

    public ProgrammeService(final ProgrammeMapper programmeMapper,
    		final ProgrammeRepository programmeRepository) {
    	this.programmeMapper = programmeMapper;
        this.programmeRepository = programmeRepository;
    }

    public Page<ProgrammeDTO> findAll(Pageable pageable) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateCreated").descending()
        );
        final Page<Programme> programmes = programmeRepository.findAll(pageable);
        return programmes.map(programmeMapper::toDto);
    }

    public ProgrammeDTO get(final Long id) {
        return programmeRepository.findById(id)
        		.map(programme -> programmeMapper.toDto(programme))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProgrammeDTO programmeDTO) {
        Programme programme = programmeMapper.toEntity(programmeDTO);
        return programmeRepository.save(programme).getId();
    }

    public void update(final Long id, final ProgrammeDTO programmeDTO) {
        programmeDTO.setId(id);
        Programme programme = programmeMapper.toEntity(programmeDTO);
        programmeRepository.save(programme);
    }

    public void delete(final Long id) {
        programmeRepository.deleteById(id);
    }
    
}
