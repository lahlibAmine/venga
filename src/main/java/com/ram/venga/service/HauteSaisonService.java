package com.ram.venga.service;

import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.repos.OrigineEmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.HauteSaison;
import com.ram.venga.mapper.HauteSaisonMapper;
import com.ram.venga.model.HauteSaisonDTO;
import com.ram.venga.repos.HauteSaisonRepository;
import com.ram.venga.util.NotFoundException;

import java.util.Optional;


@Service
public class HauteSaisonService {

	private final HauteSaisonMapper hauteSaisonMapper;
    private final HauteSaisonRepository hauteSaisonRepository;
    private final OrigineEmissionRepository origineEmissionRepository;

    public HauteSaisonService(final HauteSaisonMapper HauteSaisonMapper,
                              final HauteSaisonRepository hauteSaisonRepository, OrigineEmissionRepository origineEmissionRepository) {
    	this.hauteSaisonMapper = HauteSaisonMapper;
        this.hauteSaisonRepository = hauteSaisonRepository;
        this.origineEmissionRepository = origineEmissionRepository;
    }

    public Page<HauteSaisonDTO> findAll(String keyword, Pageable pageable) {
        Page<HauteSaison> hauteSaison = hauteSaisonRepository.findAllByKeyword(keyword, pageable);
        return hauteSaison.map(hauteSaisonMapper::toDto);  // Apply map function to each HauteSaison entity
    }


    public HauteSaisonDTO get(final Long id) {
        return hauteSaisonRepository.findById(id)
        		.map(hauteSaison -> hauteSaisonMapper.toDto(hauteSaison))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final HauteSaisonDTO hauteSaisonDTO) {

       Optional<OrigineEmission> origineEmission =  origineEmissionRepository.findById(hauteSaisonDTO.getOrigineEmission());
        if(!origineEmission.isPresent()){
            throw new NotFoundException("cette origine emission n'exist pas");
        }
        return hauteSaisonRepository.save(hauteSaisonMapper.toEntity(hauteSaisonDTO)).getId();
    }

    public void update(final HauteSaisonDTO hauteSaisonDTO) {
        final HauteSaison hauteSaison = hauteSaisonRepository.findById(hauteSaisonDTO.getId())
                .orElseThrow(NotFoundException::new);
        hauteSaisonRepository.save( hauteSaisonMapper.toEntity(hauteSaisonDTO));
    }

    public void delete(final Long id) {
        hauteSaisonRepository.deleteById(id);
    }

    public boolean libelleExists(final String libelle) {
        return hauteSaisonRepository.existsByLibelleIgnoreCase(libelle);
    }

}
