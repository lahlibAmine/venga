package com.ram.venga.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.Ville;
import com.ram.venga.mapper.VilleMapper;
import com.ram.venga.model.VilleDTO;
import com.ram.venga.repos.PaysRepository;
import com.ram.venga.repos.VilleRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class VilleService {

	private final VilleMapper villeMapper;
    private final VilleRepository villeRepository;
    private final PaysRepository paysRepository;

    public VilleService(final VilleMapper villeMapper,
    		final VilleRepository villeRepository,
            final PaysRepository paysRepository) {
    	this.villeMapper = villeMapper;
        this.villeRepository = villeRepository;
        this.paysRepository = paysRepository;
    }

    public List<VilleDTO> findAll() {
        final List<Ville> villes = villeRepository.findAll(Sort.by("dateCreated").descending());
        return villes.stream()
                .map(ville -> villeMapper.toDto(ville))
                .toList();
    }

    public VilleDTO get(final Long id) {
        return villeRepository.findById(id)
        		.map(ville -> villeMapper.toDto(ville))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VilleDTO villeDTO) {
        final Ville ville = new Ville();

        return villeRepository.save(villeMapper.toEntity(villeDTO)).getId();
    }

    public void update(final Long id, final VilleDTO villeDTO) {
        final Ville ville = villeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        villeMapper.toEntity(villeDTO);
        villeRepository.save(ville);
    }

    public void delete(final Long id) {
        villeRepository.deleteById(id);
    }

    public boolean nomExists(final String nom) {
        return villeRepository.existsByNomIgnoreCase(nom);
    }

}
