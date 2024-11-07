package com.ram.venga.service;

import java.util.*;

import com.ram.venga.mapper.LigneCommandeMapper;
import com.ram.venga.repos.CollaborateurRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.BonCommande;
import com.ram.venga.mapper.BonCommandeMapper;
import com.ram.venga.model.BonCommandeDTO;
import com.ram.venga.repos.BonCommandeRepository;
import com.ram.venga.repos.UtilisateurRepository;
import com.ram.venga.util.NotFoundException;


@Service
public class BonCommandeService {

	private final BonCommandeMapper bonCommandeMapper;
	private final BonCommandeRepository bonCommandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final LigneCommandeMapper ligneCommandeMapper;

    public BonCommandeService(final BonCommandeMapper bonCommandeMapper,
                              final BonCommandeRepository bonCommandeRepository,
                              final UtilisateurRepository utilisateurRepository, CollaborateurRepository collaborateurRepository, LigneCommandeMapper ligneCommandeMapper) {
    	this.bonCommandeMapper = bonCommandeMapper;
        this.bonCommandeRepository = bonCommandeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.ligneCommandeMapper = ligneCommandeMapper;
    }

    public List<BonCommandeDTO> findAll() {
        final List<BonCommande> bonCommandes = bonCommandeRepository.findAll(Sort.by("dateCreated").descending());
        return bonCommandes.stream()
                .map(bonCommande -> bonCommandeMapper.toDto(bonCommande))
                .toList();
    }

    public BonCommandeDTO get(final Long id) {
        return bonCommandeRepository.findById(id)
                .map(bonCommande -> bonCommandeMapper.toDto(bonCommande))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BonCommandeDTO bonCommandeDTO) {

        return bonCommandeRepository.save(bonCommandeMapper.toEntity(bonCommandeDTO)).getId();
    }

    public void update(final Long id, final BonCommandeDTO bonCommandeDTO) {
        final BonCommande bonCommande = bonCommandeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        bonCommandeMapper.toEntity(bonCommandeDTO);
        bonCommandeRepository.save(bonCommande);
    }

    public void delete(final Long id) {
        bonCommandeRepository.deleteById(id);
    }

    public boolean referenceExists(final String reference) {
        return bonCommandeRepository.existsByReferenceIgnoreCase(reference);
    }


  /*  public List<BonCommande> getCommandeByUser(Long id) {
       return bonCommandeRepository.findByAgentCommercial_Id(id);
    }*/

    public BonCommande getByCollaborateur(Long id) {
        return bonCommandeRepository.findById(id).get();
    }
}
