package com.ram.venga.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.RecetteBrute;
import com.ram.venga.mapper.RecetteBruteMapper;
import com.ram.venga.model.RecetteBruteDTO;
import com.ram.venga.repos.RecetteBruteRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class RecetteBruteService {

	private final RecetteBruteMapper recetteBruteMapper;
	private final RecetteBruteRepository recetteBruteRepository;

    public RecetteBruteService(final RecetteBruteMapper recetteBruteMapper,
    		final RecetteBruteRepository recetteBruteRepository) {
    	this.recetteBruteMapper = recetteBruteMapper;
        this.recetteBruteRepository = recetteBruteRepository;
    }

    public List<RecetteBruteDTO> findAll() {
        final List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAll(Sort.by("dateCreated").descending());
        return recetteBrutes.stream()
                .map(recetteBrute -> recetteBruteMapper.toDto(recetteBrute))
                .toList();
    }

    public RecetteBruteDTO get(final Long id) {
        return recetteBruteRepository.findById(id)
        		.map(recetteBrute -> recetteBruteMapper.toDto(recetteBrute))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final RecetteBruteDTO recetteBruteDTO) {
        final RecetteBrute recetteBrute = new RecetteBrute();
        recetteBruteMapper.toEntity(recetteBruteDTO);
        return recetteBruteRepository.save(recetteBrute).getId();
    }

    public void update(final Long id, final RecetteBruteDTO recetteBruteDTO) {
        final RecetteBrute recetteBrute = recetteBruteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        recetteBruteMapper.toEntity(recetteBruteDTO);
        recetteBruteRepository.save(recetteBrute);
    }

    public void delete(final Long id) {
        recetteBruteRepository.deleteById(id);
    }

}
