package com.ram.venga.service;

import com.mysema.commons.lang.Pair;
import com.ram.venga.domain.*;
import com.ram.venga.mapper.BonCommandeMapper;
import com.ram.venga.mapper.LigneCommandeMapper;
import com.ram.venga.model.LigneCommandeDto;
import com.ram.venga.model.LigneCommandeValidateurDto;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.model.enumeration.StatutBAEnum;
import com.ram.venga.projection.CollaborateursPointsReportProjection;
import com.ram.venga.projection.CommandeProjection;
import com.ram.venga.repos.*;
import com.ram.venga.util.ExcelGeneratorUtility;
import com.ram.venga.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LigneCommandeService {

    private final BonCommandeMapper bonCommandeMapper;
    private final BonCommandeRepository bonCommandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final LigneCommandeMapper ligneCommandeMapper;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final KeycloackService keycloackService;
    private final MailService mailService ;
    private final CadeauxBARepository cadeauxBARepository;
    private final EntiteRepository entiteRepository;
    private final OpperationRepository opperationRepository;
    private final VenteRepository venteRepository;

    public LigneCommandeService(BonCommandeMapper bonCommandeMapper, BonCommandeRepository bonCommandeRepository, UtilisateurRepository utilisateurRepository, CollaborateurRepository collaborateurRepository, LigneCommandeMapper ligneCommandeMapper, LigneCommandeRepository ligneCommandeRepository, KeycloackService keycloackService, MailService mailService, CadeauxBARepository cadeauxBARepository, EntiteRepository entiteRepository, OpperationRepository opperationRepository, VenteRepository venteRepository) {
        this.bonCommandeMapper = bonCommandeMapper;
        this.bonCommandeRepository = bonCommandeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.ligneCommandeMapper = ligneCommandeMapper;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.keycloackService = keycloackService;
        this.mailService = mailService;
        this.cadeauxBARepository = cadeauxBARepository;
        this.entiteRepository = entiteRepository;
        this.opperationRepository = opperationRepository;
        this.venteRepository = venteRepository;
    }

    public Page<LigneCommandeDto>   getLigneCommande( Pageable pageable, String cadeau, String dateDebut, String dateFin, Integer pointDebut, Integer pointFin,String fournisseur) {
        if(pointDebut == null){
            pointDebut = 0;
        }
        if(pointFin == null){
            pointFin =0;
        }
        if(cadeau == ""){
            cadeau = null;
        }
        if(fournisseur == ""){
            fournisseur = null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTimeDebut = dateDebut.isEmpty() ? null : LocalDate.parse(dateDebut, formatter).atStartOfDay();
        LocalDateTime dateTimeFin = dateFin.isEmpty() ? null : LocalDate.parse(dateFin, formatter).atStartOfDay();

        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur =  utilisateurRepository.findByRefKUser(idUser).get();
        Page<LigneCommande> bonCommandePage = ligneCommandeRepository
                .findByBonCommandeFilter(utilisateur.getId(), cadeau, dateTimeDebut, dateTimeFin, pointDebut, pointFin,fournisseur,pageable);

        Page<LigneCommandeDto> LigneCommandePageDTOS = bonCommandePage.map(ligneCommandeMapper::toDto);

        //Page<BonCommandeDTO> paginatedResponse = convertToPage(bonCommandePageDTOS, pageable);

        return  LigneCommandePageDTOS;
    }

    // Create Multi-select ligneCommande
    public ResponseEntity create(List<LigneCommandeDto> ligneCommandeDtos) throws MessagingException, UnsupportedEncodingException {

        String idUser = keycloackService.getIdUserToken();

        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser)
                .orElseThrow(() -> new NotFoundException("Utilisateur not found"));

        Collaborateur collaborateur = utilisateur.getCollaborateur();

        int totalPointThatShouldBePayed = ligneCommandeDtos.stream()
                .mapToInt(ligneCommandeDto -> {

                    int quantity = ligneCommandeDto.getQuantite();

                    CadeauxBA cadeauxBA = cadeauxBARepository.findById(ligneCommandeDto.getCadeauxBADTO())
                            .orElseThrow(() -> new NotFoundException("CadeauxBA not found"));

                    return quantity * cadeauxBA.getNbrPoint();
                }).sum();

        if(totalPointThatShouldBePayed > utilisateur.getCollaborateur().getSoldePoint())
            return new ResponseEntity<>("Vous n'avez pas assez de point" ,HttpStatus.BAD_REQUEST);

        if(totalPointThatShouldBePayed < 0)
            return new ResponseEntity<>("Vous ne pouvez pas commander des cadeaux avec des points négatifs" ,HttpStatus.BAD_REQUEST);

        ligneCommandeDtos.forEach(ligneCommandeDto -> {

            CadeauxBA cadeauxBA = cadeauxBARepository.findById(ligneCommandeDto.getCadeauxBADTO())
                    .orElseThrow(() -> new NotFoundException("CadeauxBA not found"));

            int totalPointOfThisCommand = ligneCommandeDto.getQuantite() * cadeauxBA.getNbrPoint();
            int soldAfterCommand = collaborateur.getSoldePoint() - totalPointOfThisCommand;

            collaborateur.setSoldePoint(soldAfterCommand);

            BonCommande bonCommande = new BonCommande();
            bonCommande.setEtat(StatutBAEnum.COMMANDE);
            bonCommande.setAgentCommercial(utilisateur);
            bonCommande.setNbrPointCredit(totalPointOfThisCommand);
            bonCommande.setReference(UUID.randomUUID().toString());
            bonCommande.setDate(LocalDateTime.now());
            BonCommande savedBonCommande = bonCommandeRepository.save(bonCommande);

            Opperation opperation = new Opperation();
            opperation.setSolde(soldAfterCommand);
            opperation.setBonCommande(bonCommande);
            opperation.setDate(LocalDateTime.now());
            opperation.setDebit(0);
            opperation.setCredit(0);
            opperationRepository.save(opperation);

            collaborateurRepository.save(collaborateur);

            ligneCommandeDto.setBonCommandeDTO(savedBonCommande.getId());
            ligneCommandeRepository.save(ligneCommandeMapper.toEntity(ligneCommandeDto));
        });

        sendEmailToAdmin(utilisateur.getEmail(),utilisateur.getCollaborateur().getEntite().getNom());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void sendEmailToAdmin(@RequestParam String nomAgent,@RequestParam String agence) {
        String user =keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(user).get();
            List<String> functAdminsEmails = utilisateurRepository.findConcernedFunctAdminsEmails();
            if (!functAdminsEmails.isEmpty())
                functAdminsEmails.forEach((email)->{
                    try {
                        mailService.sendEmailCommandeToAdmin(email,nomAgent,agence);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
        }

    public Page<LigneCommandeValidateurDto> getLigneCommandeValidateur(Pageable pageable, String cadeau, String dateDebut, String dateFin, Integer pointDebut, Integer pointFin, String fournisseur, String agent/*,String signature*/, String agence) {

        System.out.println("DateDebut"+dateDebut);
        System.out.println("DateFin"+dateFin);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTimeDebut = dateDebut.isEmpty() ? null : LocalDate.parse(dateDebut, formatter).atStartOfDay();
        LocalDateTime dateTimeFin = dateFin.isEmpty() ? null : LocalDate.parse(dateFin, formatter).atStartOfDay().plusDays(1);
        if(pointDebut == null){
            pointDebut = 0;
        }
        if(pointFin == null){
            pointFin =0;
        }
        System.out.println("dateTimeDebut"+dateTimeDebut);
        System.out.println("dateTimeFin"+dateTimeFin);
        Page<LigneCommande> bonCommandePage = null;
        String idUser =keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
        if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){
            bonCommandePage =  ligneCommandeRepository
                    .findByBonCommande( cadeau, dateTimeDebut, dateTimeFin, pointDebut, pointFin,fournisseur,agent/*,signature*/,agence,pageable);
        }else if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
            Long portefeuilleId = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            bonCommandePage =  ligneCommandeRepository
                    .getLigneCommandeRelatedToAuthenticatedRatache( cadeau, dateTimeDebut, dateTimeFin, pointDebut, pointFin,fournisseur,agent,agence,portefeuilleId,pageable);
        }
        else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            List<Long> representationsIds = utilisateur.getCollaborateur().getEntites().stream().map(Entite::getId).collect(Collectors.toList());
            bonCommandePage = ligneCommandeRepository.getLigneCommandeRelatedToAuthenticatedConsultant(cadeau, dateTimeDebut, dateTimeFin, pointDebut, pointFin,fournisseur,agent,agence,representationsIds,pageable);
        }

        Page<LigneCommandeValidateurDto> LigneCommandePageDTOS = bonCommandePage.map(ligneCommandeMapper::toDtoValidateur);


        return  LigneCommandePageDTOS;
    }

    public void updateStatus(Long id, StatutBAEnum etat) {
        LigneCommande commande = ligneCommandeRepository.findById(id).get();
      BonCommande bonCommande =  commande.getBonCommande();
        bonCommande.setEtat(etat);
        bonCommandeRepository.save(bonCommande);

    }

    public void getLigneCommandeValidateurExport(HttpServletResponse response, String cadeau, String dateDebut, String dateFin, Integer pointDebut, Integer pointFin, String fournisseur, String agent/*,String signature*/, String agence) {

        System.out.println("DateDebut"+dateDebut);
        System.out.println("DateFin"+dateFin);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTimeDebut = dateDebut.isEmpty() ? null : LocalDate.parse(dateDebut, formatter).atStartOfDay();
        LocalDateTime dateTimeFin = dateFin.isEmpty() ? null : LocalDate.parse(dateFin, formatter).atStartOfDay().plusDays(1);
        if(pointDebut == null){
            pointDebut = 0;
        }
        if(pointFin == null){
            pointFin =0;
        }
        System.out.println("dateTimeDebut"+dateTimeDebut);
        System.out.println("dateTimeFin"+dateTimeFin);
        List<CommandeProjection> bonCommandePage = null;
        String idUser =keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
        if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){
            bonCommandePage =  ligneCommandeRepository
                    .findByBonCommandeList( cadeau, dateTimeDebut, dateTimeFin, pointDebut, pointFin,fournisseur,agent/*,signature*/,agence);
        }else if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
            Long portefeuilleId = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            bonCommandePage =  ligneCommandeRepository
                    .getLigneCommandeRelatedToAuthenticatedRatacheList( cadeau, dateTimeDebut, dateTimeFin, pointDebut, pointFin,fournisseur,agent,agence,portefeuilleId);
        }
        else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            List<Long> representationsIds = utilisateur.getCollaborateur().getEntites().stream().map(Entite::getId).collect(Collectors.toList());
            bonCommandePage = ligneCommandeRepository.getLigneCommandeRelatedToAuthenticatedConsultantList(cadeau, dateTimeDebut, dateTimeFin, pointDebut, pointFin,fournisseur,agent,agence,representationsIds);
        }
        List<Pair<String, Function<CommandeProjection, String>>> pairs = new ArrayList<>();
        pairs.add(Pair.of("Signature", CommandeProjection::getSignature));
        pairs.add(Pair.of("Nombre Point", CommandeProjection::getNbrPoint));
        pairs.add(Pair.of("Quantite", CommandeProjection::getQuantite));
        pairs.add(Pair.of("num Commande", CommandeProjection::getNumCommande));
        pairs.add(Pair.of("Agence", CommandeProjection::getAgence));
        pairs.add(Pair.of("Agent", CommandeProjection::getAgent));
        pairs.add(Pair.of("Cadeau", CommandeProjection::getCadeau));
        pairs.add(Pair.of("Fournisseur", CommandeProjection::getFournisseur));
        DateTimeFormatter formatterExcel = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        pairs.add(Pair.of("Date creation", proj -> proj.getDate().format(formatterExcel)));
        ExcelGeneratorUtility.generateExcelReport(response, bonCommandePage, pairs);
    }
}
