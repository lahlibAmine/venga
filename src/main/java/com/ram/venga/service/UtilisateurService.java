package com.ram.venga.service;

import java.util.List;
import java.util.Optional;

import com.ram.venga.model.UtilisateurDemandeInscriptionDTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.Utilisateur;
import com.ram.venga.mapper.UtilisateurMapper;
import com.ram.venga.model.UtilisateurDTO;
import com.ram.venga.repos.CollaborateurRepository;
import com.ram.venga.repos.DemandeInscriptionRepository;
import com.ram.venga.repos.UtilisateurRepository;
import com.ram.venga.util.NotFoundException;

import javax.transaction.Transactional;


@Service
@Transactional
public class UtilisateurService {

	private final UtilisateurMapper utilisateurMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final DemandeInscriptionRepository demandeInscriptionRepository;

    public UtilisateurService(final UtilisateurMapper utilisateurMapper,
    		final UtilisateurRepository utilisateurRepository,
            final CollaborateurRepository collaborateurRepository,
            final DemandeInscriptionRepository demandeInscriptionRepository) {
    	this.utilisateurMapper = utilisateurMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.demandeInscriptionRepository = demandeInscriptionRepository;
    }

    public List<UtilisateurDTO> findAll() {
        final List<Utilisateur> utilisateurs = utilisateurRepository.findAll(Sort.by("dateCreated").descending());
        return utilisateurs.stream()
                .map(utilisateur -> utilisateurMapper.toDto(utilisateur))
                .toList();
    }

    public UtilisateurDTO get(final Long id) {
        return utilisateurRepository.findById(id)
        		.map(utilisateur -> utilisateurMapper.toDto(utilisateur))
                .orElseThrow(NotFoundException::new);
    }
    public Long create(final UtilisateurDTO utilisateur) {
        return utilisateurRepository.save(utilisateurMapper.toEntity(utilisateur)).getId();
    }
    public Utilisateur createWithDemandeInscription(final UtilisateurDemandeInscriptionDTO utilisateur) {
        return utilisateurRepository.save(utilisateurMapper.toEntity(utilisateur));
    }


    public void update(final Long id, final UtilisateurDTO utilisateurDTO) {
        final Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        utilisateurMapper.toEntity(utilisateurDTO);
        utilisateurRepository.save(utilisateur);
    }

    public void delete(final Long id) {
        final Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        demandeInscriptionRepository.findAllByValidateurs(utilisateur)
                .forEach(demandeInscription -> demandeInscription.getValidateurs().remove(utilisateur));
        utilisateurRepository.delete(utilisateur);
    }

    public boolean refKUserExists(final String refKUser) {
        return utilisateurRepository.existsByRefKUserIgnoreCase(refKUser);
    }

    public boolean loginExists(final String login) {
        return utilisateurRepository.existsByLoginIgnoreCase(login);
    }

    public boolean emailExists(final String email) {
        return utilisateurRepository.existsByEmailIgnoreCase(email);
    }

    public Optional<Utilisateur> findByEmail(final String email) {
        return utilisateurRepository.findUtilisateurByEmail(email);
    }

    public Optional<Utilisateur> findByCollaborateurId(final Long collaborateurId) {
        return Optional.ofNullable(utilisateurRepository.findByCollaborateurId(collaborateurId));
    }

    public Optional<Utilisateur> findByKckRefUser(final String kckRefUser) {
        return Optional.ofNullable(utilisateurRepository.findByRefKUser(kckRefUser).get());
    }
}
