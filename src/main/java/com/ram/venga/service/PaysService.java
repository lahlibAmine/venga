package com.ram.venga.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.Pays;
import com.ram.venga.mapper.PaysMapper;
import com.ram.venga.model.PaysDTO;
import com.ram.venga.repos.PaysRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class PaysService {

	private final PaysMapper paysMapper;
    private final PaysRepository paysRepository;

    public PaysService(final PaysMapper paysMapper,
    		final PaysRepository paysRepository) {
    	this.paysMapper = paysMapper;
        this.paysRepository = paysRepository;
    }

    public List<PaysDTO> findAll() {
        final List<Pays> payss = paysRepository.findAll(Sort.by("id"));
        return payss.stream()
                .map(pays -> paysMapper.toDto(pays))
                .toList();
    }

    public PaysDTO get(final Long id) {
        return paysRepository.findById(id)
        		.map(pays -> paysMapper.toDto(pays))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PaysDTO paysDTO) {
        return paysRepository.save(paysMapper.toEntity(paysDTO)).getId();
    }

    public void update(final Long id, final PaysDTO paysDTO) {
        final Pays pays = paysRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        paysMapper.toEntity(paysDTO);
        paysRepository.save(pays);
    }

    public void delete(final Long id) {
        paysRepository.deleteById(id);
    }

    public boolean codeIsoExists(final String codeIso) {
        return paysRepository.existsByCodeIsoIgnoreCase(codeIso);
    }

    public boolean nomExists(final String nom) {
        return paysRepository.existsByNomIgnoreCase(nom);
    }

    public List<PaysDTO> getPaysNotInOrigin(){
        List<PaysDTO> paysDisponibles = paysRepository.getPaysNotInOrigin().stream()
                .map(paysMapper::toDto).toList();
        return paysDisponibles;
    }

    public List<PaysDTO> getPaysNotInOriginIncludeCurrent(Long id){
        List<PaysDTO> paysDisponibles = paysRepository.getPaysNotInOrigin().stream()
                .map(paysMapper::toDto).toList();
        PaysDTO paysDTO = paysRepository.findById(id).map(paysMapper::toDto)
                .orElseThrow(NotFoundException::new);
        List<PaysDTO> res = new ArrayList<>(paysDisponibles);
        res.add(paysDTO);
        return res;
    }


}
