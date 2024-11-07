package com.ram.venga.service;

import com.ram.venga.domain.Offre;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.mapper.OffreMapper;
import com.ram.venga.model.OffreDTO;
import com.ram.venga.repos.OffreRepository;
import com.ram.venga.repos.OrigineEmissionRepository;
import com.ram.venga.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class OffreService {

    private final OffreRepository offreRepository;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final OffreMapper offreMapper;

    public OffreService(final OffreRepository offreRepository,
                        final OrigineEmissionRepository origineEmissionRepository, OffreMapper offreMapper) {
        this.offreRepository = offreRepository;
        this.origineEmissionRepository = origineEmissionRepository;
        this.offreMapper = offreMapper;
    }

    public List<OffreDTO> findAll() {
        final List<Offre> offres = offreRepository.findAll(Sort.by("id"));
        return offres.stream()
                .map(offre -> offreMapper.toDto(offre))
                .toList();
    }

    public OffreDTO get(final Long id) {
        return offreRepository.findById(id)
                .map(offre -> offreMapper.toDto(offre))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final OffreDTO offreDTO) {
        final Offre offre = new Offre();

        return offreRepository.save( offreMapper.toEntity(offreDTO)).getId();
    }

    public void update(final Long id, final OffreDTO offreDTO) {
        final Offre offre = offreRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        offreMapper.toEntity(offreDTO);
        offreRepository.save(offre);
    }

    public void delete(final Long id) {
        offreRepository.deleteById(id);
    }

    private OffreDTO mapToDTO(final Offre offre, final OffreDTO offreDTO) {
        offreDTO.setIdOffre(offre.getId());
        offreDTO.setOrigineEmission(offre.getOrigineEmission() == null ? null : offre.getOrigineEmission().getId());
        return offreDTO;
    }

    private Offre mapToEntity(final OffreDTO offreDTO, final Offre offre) {
        final OrigineEmission origineEmission = offreDTO.getOrigineEmission() == null ? null : origineEmissionRepository.findById(offreDTO.getOrigineEmission())
                .orElseThrow(() -> new NotFoundException("origineEmission not found"));
        offre.setOrigineEmission(origineEmission);
        return offre;
    }

}
