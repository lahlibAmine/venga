package com.ram.venga.service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mysema.commons.lang.Pair;
import com.ram.exception.UserAlreadyExistException;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.mapper.CollaborateurMapper;
import com.ram.venga.model.*;
import com.ram.venga.model.enumeration.CategorieEntiteEnum;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.projection.AgenceSearchProjection;
import com.ram.venga.projection.CollaborateurSearchProjection;
import com.ram.venga.repos.*;
import com.ram.venga.util.ExcelGeneratorUtility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.domain.Entite;
import com.ram.venga.mapper.EntiteMapper;
import com.ram.venga.util.NotFoundException;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;


@Service
public class EntiteService {
	
	private final EntiteMapper entiteMapper;
    private final EntiteRepository entiteRepository;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final VilleRepository villeRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final CollaborateurMapper collaborateurMapper;
    private final UtilisateurService utilisateurService;
    private final KeycloackService keycloackService;
    private final VenteRepository venteRepository;
    private final UtilisateurRepository utilisateurRepository;


    public EntiteService(final EntiteMapper entiteMapper,
                         final EntiteRepository entiteRepository,
                         final OrigineEmissionRepository origineEmissionRepository,
                         final VilleRepository villeRepository, CollaborateurRepository collaborateurRepository, CollaborateurMapper collaborateurMapper, UtilisateurService utilisateurService, KeycloackService keycloackService, VenteRepository venteRepository, UtilisateurRepository utilisateurRepository) {
    	this.entiteMapper = entiteMapper;
        this.entiteRepository = entiteRepository;
        this.origineEmissionRepository = origineEmissionRepository;
        this.villeRepository = villeRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.collaborateurMapper = collaborateurMapper;
        this.utilisateurService = utilisateurService;
        this.keycloackService = keycloackService;
        this.venteRepository = venteRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<EntiteDTO> findAll() {
        final List<Entite> entites = entiteRepository.findAll(Sort.by("id"));
        return entites.stream()
                .map(entiteMapper::toDto)
                .toList();
    }

    public EntiteDTO get(final Long id) {
        return entiteRepository.findById(id)
        		.map(entite -> entiteMapper.toDto(entite))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create(final EntiteDTO entiteDTO) {
        Map<String,String> map = new HashMap<>();
        try{
            entiteRepository.save( entiteMapper.toEntity(entiteDTO));
        }catch(Exception e){
            map.put("message","Code déjà exist");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(entiteDTO.getId(), HttpStatus.CREATED);
    }

    public ResponseEntity<?> update( final EntiteDTO entiteDTO) {
        Entite entite = entiteRepository.findById(entiteDTO.getId())
                .orElseThrow(NotFoundException::new);
        Map<String,String> map = new HashMap<>();
       try{
            Entite updatedEntity = entiteMapper.toEntity(entiteDTO);
            entiteRepository.save(updatedEntity);
        }catch (Exception e){
            map.put("message","Code déjà exist");
          return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

        }
          return new ResponseEntity<>(entiteDTO.getId(),HttpStatus.OK);
    }

    public ResponseEntity<?> delete(final Long id) {
        Optional<Entite> entiteOptional = entiteRepository.findById(id);
        String message = "";
        Map<String,String> map = new HashMap<>();
        Entite entite = null;
        if (entiteOptional.isPresent()) {
            entite = entiteOptional.get();

            if (entite.getCategorie().equals(CategorieEntiteEnum.AGENCE) && entite.getCollaborateurs().size() > 0){
                message = "Cette agence a au moins un collaborateur";
                throw new UserAlreadyExistException(message);
            }
        /*    if(entite.getParent().getCategorie().equals(CategorieEntiteEnum.REPRESENTATION)){
                message = "Cette représentation a au moins un portefeuille";
                throw new UserAlreadyExistException(message);

            }
            if(entite.getParent().getParent().getCategorie().equals(CategorieEntiteEnum.PORTEFEUILLE)){
                message = "Ce portefeuille a au moins une agence";
                throw new UserAlreadyExistException(message);

            }*/

        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
        List<Long> entites = entiteRepository.findByParentId(id);
        if (entites.size() > 0){
            message = "Cette "+ entiteRepository.findById(id).get().getCategorie() +" a au moins des " +entiteRepository.findByParentIdcategorie(id);
            map.put("message",message);
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        entiteRepository.deleteById(id);
        return new ResponseEntity<>(entite.getId(),HttpStatus.OK);
    }

    public boolean codeExists(final String code) {
        return entiteRepository.existsByCodeIgnoreCase(code);
    }

    public EntiteDTO findByCodeIata(String code,String officeId) {
        if(code != null){
            code = code.toUpperCase();
        }
        if(officeId != null){
            officeId = officeId.toUpperCase();
        }
        Entite entite = entiteRepository.findByCodeOrOfficeId(code , officeId);
        return entiteMapper.toDto(entite);
    }

    public Page<EntiteDTO> findByCategorie(CategorieEntiteEnum categorie,String keyWord, Pageable pageable) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateCreated").descending()
        );
        Page<Entite> entites = entiteRepository.findByCategoryOrKeyword(categorie,keyWord,pageable);
        return entites.map(entiteMapper::toDto);
    }

    public List<EntiteDTO> finAllByCategorie(CategorieEntiteEnum categorieEntiteEnum,Boolean isCreate){
        List<Entite> entites = null;
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElseThrow(() -> new RuntimeException("User not found."));

        if (categorieEntiteEnum.equals(CategorieEntiteEnum.PORTEFEUILLE) && isCreate)
            entites = entiteRepository.findUnassignedPortefeuille();
        else
        {
            if(utilisateur.getProfil().equals(ProfilEnum.CONSULTANT)){
                    List<Long> idEntiteRepresentation = utilisateur.getCollaborateur().getEntites().stream().map(Entite::getId).toList();
                    List<Long> idEntitePortfeuille = entiteRepository.findByRepresentationPortfeuille(idEntiteRepresentation);

                entites = entiteRepository.findAllByCategorieByRepresentation(categorieEntiteEnum,idEntiteRepresentation,idEntitePortfeuille);
            }else{
                entites = entiteRepository.findAllByCategorie(categorieEntiteEnum);
            }
        }
        return entites.stream().map(entiteMapper::toDto).toList();
    }

    public Page<EntiteDTO> agenceFilter(String codeIata,
                                        String nomAgence,
                                        String originEmission,
                                        String porteFeuille,
                                        String representation,
                                        Pageable pageable) {
        Page<Entite> entites = entiteRepository.agenceFilter(codeIata, nomAgence, originEmission, porteFeuille, representation, pageable);

        return agenciesWithTheirAgents(entites);
    }

    public List<EntiteNomUdpadeDto> findAllEntite() {
        List<Entite> entites = entiteRepository.findAll();
       return entites.stream().map(entiteMapper::toDtoAll).toList();
    }

    public List<EntiteCreateListDTO> portfeuilleByRepresentation(Long idRepresentation) {
        List<Entite> portfeuille = entiteRepository.findByRepresentationPortefeuille(idRepresentation);
        List<EntiteCreateListDTO> entiteCreateListDTO = portfeuille.stream().map(entiteMapper::ENTITE_CREATE_LIST_DTO).toList();
        return entiteCreateListDTO;
    }

    public Map<String,List<EntiteDTO>> finAllByCategorieSearch(String portfeuille,String representation){
        Map<String,List<EntiteDTO>> map = new HashMap<>();
        if(!portfeuille.isEmpty()){
            List<Entite> entitesAgence = entiteRepository.findAgenceByPortfeuille(portfeuille);
            List<EntiteDTO> entiteAgenceDTOS =  entitesAgence.stream().map(entiteMapper::toDto).toList();
            map.put("agence",entiteAgenceDTOS);
            return map;
        }
        if(!representation.isEmpty()){
            List<Entite> entitesPortfeuille = entiteRepository.findPortfeuilleByRepresentation(representation);
            List<Entite> entitesAgenceByRepresentation = entiteRepository.findAgenceByRepresentation(representation);

            List<EntiteDTO> entitePortfeuilleDTOS =  entitesPortfeuille.stream().map(entiteMapper::toDto).toList();
            List<EntiteDTO> entitesAgenceByRepresentationeDTOS =  entitesAgenceByRepresentation.stream().map(entiteMapper::toDto).toList();

            map.put("portfeuille",entitePortfeuilleDTOS);
            map.put("agence",entitesAgenceByRepresentationeDTOS);
            return map;
        }
      return null;
    }

    public Page<EntiteDTO> agencesByAuthenticatedRattache(String keyword, Pageable pageable) {
        Utilisateur utilisateur = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborateur not found."));
        Long idPortefeuille = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
        Page<Entite> entites = entiteRepository.findAgencesByPorteuille(idPortefeuille,keyword, pageable);
        return agenciesWithTheirAgents(entites);
    }

    private Page<EntiteDTO> agenciesWithTheirAgents(Page<Entite> entites) {
        return entites.map(entite -> {
            List<Collaborateur> collaborateurs = collaborateurRepository.findAllByEntite(entite);
            List<UsersAgenceDto> usersAgenceDtos = collaborateurs.stream()
                    .map(collaborateurMapper::AGENCE_DTO)
                    .collect(Collectors.toList());

            EntiteDTO entiteDTO = entiteMapper.toDto(entite);
            entiteDTO.setUsersAgenceDto(usersAgenceDtos);

            return entiteDTO;
        });
    }

    public Page<EntiteDTO> agencesByAuthenticatedConsultant(String keyword, Pageable pageable) {
        Utilisateur user = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborateur not found."));
        List<Long> ids = user.getCollaborateur().getEntites().stream().map(Entite::getId).toList();
        Page<Entite> entites = entiteRepository.findAgencesByListOfRepresentationIds(ids,keyword, pageable);
        return agenciesWithTheirAgents(entites);
    }

    public void agencesByAuthenticatedConsultantExport(HttpServletResponse response, String keyword,String codeIata,String nomAgence,String originEmission, String porteFeuille,String representation, Pageable pageable) {
        Utilisateur user = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborateur not found."));
        List<AgenceSearchProjection> entites = new ArrayList<>();
        if(user.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
            Long idPortfeuille = user.getCollaborateur().getEntite().getId();
            entites = entiteRepository.findAgencesByListOfPortfeuilleList(idPortfeuille,keyword);

        }
        else if(user.getProfil().equals(ProfilEnum.CONSULTANT)){
            List<Long> idRepresentation = user.getCollaborateur().getEntites().stream().map(Entite::getId).toList();
            entites = entiteRepository.findAgencesByListOfRepresentationIdsList(idRepresentation,keyword);

        }else {
            entites = entiteRepository.findAgencesByList(keyword,codeIata,nomAgence,originEmission,porteFeuille,representation);

        }
        List<Pair<String, Function<AgenceSearchProjection, String>>> pairs = new ArrayList<>();
        pairs.add(Pair.of("Code Iata", AgenceSearchProjection::getCodeIata));
        pairs.add(Pair.of("Adresse", AgenceSearchProjection::getAdresse));
        pairs.add(Pair.of("Code Postal", AgenceSearchProjection::getCodePostal));
        pairs.add(Pair.of("Nom", AgenceSearchProjection::getNom));
        pairs.add(Pair.of("Email", AgenceSearchProjection::getEmail));
        pairs.add(Pair.of("Fax", AgenceSearchProjection::getFax));
        pairs.add(Pair.of("Portfeuille", AgenceSearchProjection::getPortfeuille));
        pairs.add(Pair.of("Representation", AgenceSearchProjection::getRepresentation));
         pairs.add(Pair.of("Origine Emission", AgenceSearchProjection::getOrigine));
        pairs.add(Pair.of("Ville", AgenceSearchProjection::getVille));
        pairs.add(Pair.of("TelePhone", AgenceSearchProjection::getTele));



        ExcelGeneratorUtility.generateExcelReport(response, entites, pairs);

    }
}
