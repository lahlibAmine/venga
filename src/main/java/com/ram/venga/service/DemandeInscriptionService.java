package com.ram.venga.service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.exception.UserAlreadyExistException;
import com.ram.venga.domain.*;
import com.ram.venga.model.*;
import com.ram.venga.model.enumeration.*;
import com.ram.venga.projection.inscriptionsEvolutionView;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;

import com.ram.venga.repos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.DemandeInscriptionMapper;
import com.ram.venga.util.NotFoundException;

import javax.transaction.Transactional;


//import javax.mail.MessagingException;


@Service
@Transactional
@ComponentScan("com.ram.keycloack")
public class DemandeInscriptionService {

	private final DemandeInscriptionMapper demandeInscriptionMapper;
    private final DemandeInscriptionRepository demandeInscriptionRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    private final KeycloackService keycloackService;

    private final  MailService mailService;
    private final EntiteRepository entiteRepository;
    private final OpperationRepository opperationRepository;
    private final BonCommandeRepository bonCommandeRepository;
    private final OTPService otpService;

    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#&$%^*()-_=+";
    private static final int PASSWORD_LENGTH = 10;

   @Value("${user.admin.usernameAdmin}")
    String  adminUsername ;
    @Value("${user.admin.passwordAdmin}")
    String  adminPassword;

   // @Value("${keycloak.auth-server-url}")
    private String keycloakAuthUrl;
   // @Value("${keycloak.realm}")
    private String realm;



    public DemandeInscriptionService(
            final DemandeInscriptionMapper demandeInscriptionMapper,
            final DemandeInscriptionRepository demandeInscriptionRepository,
            final CollaborateurRepository collaborateurRepository,
            final UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, PasswordEncoder passwordEncoder, KeycloackService keycloackService, MailService mailService, EntiteRepository entiteRepository, OpperationRepository opperationRepository, BonCommandeRepository bonCommandeRepository, OTPService otpService) {
    	this.demandeInscriptionMapper = demandeInscriptionMapper;
        this.demandeInscriptionRepository = demandeInscriptionRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.keycloackService = keycloackService;
        this.mailService = mailService;
        this.entiteRepository = entiteRepository;
        this.opperationRepository = opperationRepository;
        this.bonCommandeRepository = bonCommandeRepository;
        this.otpService = otpService;
    }

    public List<DemandeInscriptionDTO> findAll() {
        final List<DemandeInscription> demandeInscriptions = demandeInscriptionRepository.findAll(Sort.by("id"));
        return demandeInscriptions.stream()
                .map(demandeInscription -> demandeInscriptionMapper.toDto(demandeInscription))
                .toList();
    }

    public DemandeInscriptionDTO get(final Long id) {
        return demandeInscriptionRepository.findById(id)
                .map(demandeInscription -> demandeInscriptionMapper.toDto(demandeInscription))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DemandeInscriptionDTO demandeInscriptionDTO) {
        final DemandeInscription demandeInscription = new DemandeInscription();
        demandeInscriptionMapper.toEntity(demandeInscriptionDTO);
        return demandeInscriptionRepository.save(demandeInscription).getId();
    }

    public void update(final Long id, final DemandeInscriptionDTO demandeInscriptionDTO) {
        final DemandeInscription demandeInscription = demandeInscriptionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        demandeInscriptionMapper.toEntity(demandeInscriptionDTO);
        demandeInscriptionRepository.save(demandeInscription);
    }

    public void delete(final Long id) {
        demandeInscriptionRepository.deleteById(id);
    }
    public DemandeInscription getInscriptionById(Long id){
        return demandeInscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Aucun inscription ne correspond à l'id : %d !", id)));
    }

    public Map<String,StatutDemandeEnum> updateStatus(ValidationDto validationDto) throws  UnsupportedEncodingException, JsonProcessingException, javax.mail.MessagingException {
       String idUser = keycloackService.getIdUserToken();
        Set<DemandeInscription> inscriptions = new HashSet<>();

        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
       DemandeInscription inscription =  demandeInscriptionRepository.findById(validationDto.getId()).get();
        inscription.setStatut(validationDto.getStatus());
        Set<Utilisateur> utilisateurs = new HashSet<>();
        inscription.setCommentaire(validationDto.getCommentaire());
        Map<String,StatutDemandeEnum> map = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        String token = keycloackService.getToken();
        utilisateurs.add(utilisateur);
        if(!validationDto.getStatus().equals(StatutDemandeEnum.REJETE_R)||!validationDto.getStatus().equals(StatutDemandeEnum.REJETE_AF)){
                if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL) && validationDto.getStatus().equals(StatutDemandeEnum.VALIDE_R)){
                    traiterAdmin(validationDto.getId());
                    inscription.setValidateurs(utilisateurs);
                    inscription.setDateValidationRattache(LocalDateTime.now());
                    inscription.setDateModification(LocalDateTime.now());
                }
                if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) && validationDto.getStatus().equals(StatutDemandeEnum.VALIDE_AF)){
                    inscription.setDateValidationAdminF(LocalDateTime.now());
                    inscription.getValidateurs().add(utilisateur);
                    inscription.setDateModification(LocalDateTime.now());
                    // 3. Set the validateurs for the inscription
                 //   inscription.setValidateurs(utilisateurs);
                    Optional.ofNullable(inscription.getCollaborateur())
                            .map(Collaborateur::getEntite)
                            .map(Entite::getOrigineEmission)
                            .map(OrigineEmission::getNbrPointBienvenue)
                            .ifPresentOrElse(
                                    nbrPointBienvenue -> inscription.getCollaborateur().setSoldePoint(nbrPointBienvenue),
                                    () -> inscription.getCollaborateur().setSoldePoint(0)
                            );

                    String newPassword = generateRandomPassword();
                    Collaborateur emailCola = collaborateurRepository.findById(inscription.getCollaborateur().getId()).get();
                    generateNewPassword(emailCola.getEmail(),newPassword);
                    Utilisateur utilisateurCollaborateur = utilisateurRepository.findByEmail(emailCola.getEmail());
                    Opperation opperation = new Opperation();
                    String uuidAsString = uuid.toString();
                    BonCommande bonCommande = new BonCommande();
                    bonCommande.setEtat(StatutBAEnum.COMMANDE);
                    bonCommande.setDate(LocalDateTime.now());
                    bonCommande.setAgentCommercial(utilisateurCollaborateur);
                    bonCommande.setNbrPointCredit( inscription.getCollaborateur().getSoldePoint());
                    bonCommande.setReference(uuidAsString);
                    bonCommandeRepository.save(bonCommande);
                    opperation.setSolde(inscription.getCollaborateur().getSoldePoint());
                    opperation.setBonCommande(bonCommande);
                    opperation.setDate(LocalDateTime.now());
                    opperation.setDebit(inscription.getCollaborateur().getSoldePoint());
                    opperation.setCredit(0);
                    opperationRepository.save(opperation);
                 //   if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN) ){
                   //     List<String> list = List.of(Roles.manageUsers,Roles.viewUsers,Roles.viewClients);
                     //   keycloackService.affectManagementClientRoles(utilisateurCollaborateur.getRefKUser(), list,token);
                   // }
                 //   keycloackService.assignRoleToUser(utilisateurCollaborateur.getRefKUser(),"agent");
                    keycloackService.createPasswordKeycloack(utilisateurCollaborateur.getRefKUser(),utilisateur.getEmail(),newPassword,token);
                    mailService.envoyerEmailActivationCompte(emailCola.getEmail(),emailCola.getSignature(), Character.toUpperCase(emailCola.getPrenom().charAt(0)) + emailCola.getPrenom().substring(1)+" "+Character.toUpperCase(emailCola.getNom().charAt(0)) + emailCola.getNom().substring(1),newPassword);
                }
           }
        if(validationDto.getStatus().equals(StatutDemandeEnum.REJETE_R)){
                inscription.setDateRefusR(LocalDateTime.now());
                inscription.setValidateurs(utilisateurs);
                inscription.setDateModification(LocalDateTime.now());
            }
            if(validationDto.getStatus().equals(StatutDemandeEnum.REJETE_AF)){
                inscription.setDateRefusAF(LocalDateTime.now());
                inscription.setValidateurs(utilisateurs);
                inscription.setDateModification(LocalDateTime.now());
            }

            demandeInscriptionRepository.save(inscription);
        map.put("status",validationDto.getStatus());
        return map;

    }

    public StatutDemandeEnum traiterAdmin(Long id) {
        // Chercher la demande d'inscription par id :
        String Email = null;
        String link = "/af/validation";
        DemandeInscription inscriptionTrouvee = demandeInscriptionRepository.findById(id).get();
        List<String> functAdminsEmails = utilisateurRepository.findConcernedFunctAdminsEmails();

        if(!functAdminsEmails.isEmpty() && inscriptionTrouvee.getStatut().equals(StatutDemandeEnum.VALIDE_R)){
            functAdminsEmails.forEach((e)->{
                try {
                    mailService.envoyerEmailActivationCompteRattache(e,link);
                } catch (UnsupportedEncodingException | MessagingException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        return inscriptionTrouvee.getStatut();
    }

    public ResponseEntity<Utilisateur> generateNewPassword(String email,String newPassword)  {
        Utilisateur user = utilisateurRepository.findByEmail(email);
        // Générer un nouveau mot de passe :
        // Encoder et enregistrer le mot de passe la DB :

       // user.setPassword(passwordEncoder.encode(newPassword));
        user.setActive(true);
        utilisateurRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);

    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHAR_SET.length());
            password.append(CHAR_SET.charAt(index));
        }

        return password.toString();
    }

    public ResponseEntity getDemandeByStatus(StatutDemandeEnum statutDemandeEnum,String keyword,TypeDemandeEnum typeDemande, Pageable pageable) {
        if(keyword.isEmpty()){
            keyword=null;
        }
        Page<DemandeInscription>demandeInscriptions = null;
        String idUser =  keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
         if(statutDemandeEnum.equals(StatutDemandeEnum.EN_COURS) && utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
             Long entite = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;

             List<Long> idEntite = entiteRepository.findByParentId(entite);
             demandeInscriptions=  demandeInscriptionRepository.findByStatutAndEntite(statutDemandeEnum,keyword,idEntite,typeDemande,pageable);
        }
         else if (statutDemandeEnum.equals(StatutDemandeEnum.EN_COURS) && utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
             Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
             List<Long> idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
             demandeInscriptions=  demandeInscriptionRepository.findByStatutAndEntite(statutDemandeEnum,keyword,idEntite,typeDemande,pageable);
         }
        else if((statutDemandeEnum.equals(StatutDemandeEnum.VALIDE_R) && utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL))|| (statutDemandeEnum.equals(StatutDemandeEnum.VALIDE_R) && utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN))){

            demandeInscriptions = demandeInscriptionRepository.findByStatut(statutDemandeEnum,keyword,typeDemande,pageable);

        }
        else{
            return new ResponseEntity<>("vous n'avez pas le droit .", HttpStatus.BAD_REQUEST);


        }
        Page<DemandeInscriptionDTO> demandeInscriptionDTOS = demandeInscriptions.map(demandeInscriptionMapper::toDto);
        return ResponseEntity.ok(demandeInscriptionDTOS);
    }

    public Page<DemandeInscriptionDTO> gethistoriqueRattache(Pageable pageable,String keyword,TypeDemandeEnum typeDemandeEnum) {
        String idUser = keycloackService.getIdUserToken();
        Page<DemandeInscription> demandeInscriptionPage = null;
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();


        if ((utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL)) || (utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)) ) {
            demandeInscriptionPage = demandeInscriptionRepository.findAllByAdminStatutNot(pageable,keyword,typeDemandeEnum);
        } else if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
            Long parentId = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            List<Long> idEntite = entiteRepository.findByParentId(parentId);
            demandeInscriptionPage = demandeInscriptionRepository.findAllByRattacherStatutNot(pageable, idEntite,keyword,typeDemandeEnum);
        }else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            List<Long> idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
            demandeInscriptionPage = demandeInscriptionRepository.findAllByRattacherStatutNot(pageable, idEntite,keyword,typeDemandeEnum);
        }

        Page<DemandeInscriptionDTO> LigneCommandePageDTOS = demandeInscriptionPage.map(demandeInscriptionMapper::toDto);

        // Use forEach to update the elements directly
        return LigneCommandePageDTOS;
    }



    public List<InscriptionEvolutionDto> getEvolutionInscription(String dateDebut, String dateFin) {
      int debutYear = 0;
      int finYear = 0;
        if(!dateDebut.isEmpty()){
            debutYear = Integer.parseInt(dateDebut);
        }
        if(!dateFin.isEmpty()){
            finYear = Integer.parseInt(dateFin);
        }
        List<Map<String,Object>> mapList = new ArrayList<>();
        List<inscriptionsEvolutionView> inscriptions = new ArrayList<>();
        List<InscriptionEvolutionDto> list = new ArrayList<>();
       String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser( idUser).get();
        List<Long> idEntite = new ArrayList<>();
        if (debutYear == 0 || finYear == 0 ){
            if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)  ){
                inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionYear(debutYear,finYear);
            }else if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
                Long entite = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
                  idEntite =entiteRepository.findByParentId(entite);
                inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionYearEntite(debutYear,finYear,idEntite);

            } else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
                Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
                idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
                inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionYearEntite(debutYear,finYear,idEntite);
            }
            inscriptions.forEach(inscri -> {
                list.add(new InscriptionEvolutionDto(inscri.getTime(),inscri.getInscriptions()));
            });

        }else {
            if (debutYear ==finYear) {
                if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){
                    inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionMounth(debutYear,finYear);

                }else if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
                    Long entite = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
                    idEntite =entiteRepository.findByParentId(entite);
                    inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionMounthEntite(debutYear,finYear,idEntite);
                }else{
                    Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
                    idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
                    inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionMounthEntite(debutYear,finYear,idEntite);

                }
                for (int i = 1; i <= 12; i++) {
                    int finalI = i;
                    Optional<inscriptionsEvolutionView> inscriptionsEvolutionView = inscriptions.stream().filter(inscri -> Integer.parseInt(inscri.getTime().substring(inscri.getTime().length()-2)) == finalI).findFirst();
                    if (inscriptionsEvolutionView.isPresent()){
                        inscriptionsEvolutionView inscri = inscriptionsEvolutionView.get();
                        list.add(new InscriptionEvolutionDto(inscri.getTime(),inscri.getInscriptions()));
                    }else {
                        String mounth = (i>9) ? String.valueOf(finalI) : '0'+String.valueOf(finalI);
                        list.add(new InscriptionEvolutionDto(dateDebut +'-'+mounth, 0L));
                    }
                }
            } else {
                int debut =debutYear-1;
                int fin =finYear+1;
                if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){

                    inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionYear(debut,fin);

                }else if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
                    Long entite = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
                    idEntite =entiteRepository.findByParentId(entite);
                    inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionYearEntite(debut,fin,idEntite);

                }else{
                    Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
                    idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
                    inscriptions=   demandeInscriptionRepository.getEvolutionInscriptionMounthEntite(debutYear,finYear,idEntite);

                }
                for (int i = Integer.parseInt(dateDebut); i <= Integer.parseInt(dateFin); i++) {
                    int finalI = i;

                    Optional<inscriptionsEvolutionView> inscriptionsEvolutionView = inscriptions.stream().filter(inscri -> Integer.parseInt(inscri.getTime().substring(0,4)) == finalI).findFirst();
                    if (inscriptionsEvolutionView.isPresent()){
                        inscriptionsEvolutionView inscri = inscriptionsEvolutionView.get();
                        list.add(new InscriptionEvolutionDto(inscri.getTime(),inscri.getInscriptions()));
                    }else {
                        list.add(new InscriptionEvolutionDto(String.valueOf(finalI), 0L));
                    }
                }
            }
        }



        return list;
    }

    public ResponseEntity<String> updateStatusDesactivationCompte(CollaborateurChangeStatusDTO collaborateurChangeStatusDTO) {
        Utilisateur user = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow();
        Collaborateur collaborateur = user.getCollaborateur();
        Boolean isValid = otpService.validateOTPCode(collaborateurChangeStatusDTO.getOtpCode(),user);
        if (isValid == null){
            return ResponseEntity.badRequest().body("Échec de la demande désactivation du compte. Le code OTP fourni a expiré.");
        } else if (!isValid) {
            return ResponseEntity.badRequest().body("Échec de la demande désactivation du compte. Le code OTP fourni est incorrect.");
        } else {
            try {
                DemandeInscription demandeInscription = new DemandeInscription();
                demandeInscription.setTypeDemande(TypeDemandeEnum.DEMANDE_DESACTIVATION);
                user.setDesactivation(true);
                collaborateurRepository.save(collaborateur);
                demandeInscription.setStatut(StatutDemandeEnum.EN_COURS);
                demandeInscription.setDateDemande(LocalDateTime.now());
                demandeInscription.setCollaborateur(collaborateur);
                demandeInscriptionRepository.save(demandeInscription);
                return ResponseEntity.ok("Demande désactivation du compte complétée avec succès.");
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors du traitement de votre demande. Veuillez réessayer plus tard.");
            }
        }
    }

    public Map<String,StatutDemandeEnum> updateValidationDesactivationCompte(ValidationDto validationDto) {
        String idUser = keycloackService.getIdUserToken();

        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
        DemandeInscription inscription =  demandeInscriptionRepository.findById(validationDto.getId()).get();
        Set<Utilisateur> utilisateurs = new HashSet<>();
        Map<String,StatutDemandeEnum> map = new HashMap<>();
        inscription.setStatut(validationDto.getStatus());
        utilisateurs.add(utilisateur);
        if(!validationDto.getStatus().equals(StatutDemandeEnum.REJETE_R)||!validationDto.getStatus().equals(StatutDemandeEnum.REJETE_AF)){
            if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL) && validationDto.getStatus().equals(StatutDemandeEnum.VALIDE_R)){
                inscription.setValidateurs(utilisateurs);
                inscription.setDateValidationRattache(LocalDateTime.now());
                inscription.setDateModification(LocalDateTime.now());
            }else if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) && validationDto.getStatus().equals(StatutDemandeEnum.VALIDE_AF)){
                inscription.setDateValidationAdminF(LocalDateTime.now());
                inscription.getValidateurs().add(utilisateur);
                inscription.getCollaborateur().getUtilisateur().setActive(false);
                inscription.setDateModification(LocalDateTime.now());

            }
        }
        if(validationDto.getStatus().equals(StatutDemandeEnum.REJETE_R)){
            inscription.setDateRefusR(LocalDateTime.now());
            inscription.setValidateurs(utilisateurs);
            inscription.setDateModification(LocalDateTime.now());
            inscription.setCommentaire(validationDto.getCommentaire());
            inscription.getCollaborateur().getUtilisateur().setDesactivation(false);
         //   inscription.setDesactivation(false);

        }
        if(validationDto.getStatus().equals(StatutDemandeEnum.REJETE_AF)){
            inscription.setDateRefusAF(LocalDateTime.now());
            inscription.setValidateurs(utilisateurs);
            inscription.setDateModification(LocalDateTime.now());
            inscription.setCommentaire(validationDto.getCommentaire());
        //    inscription.setDesactivation(false);
            inscription.getCollaborateur().getUtilisateur().setDesactivation(false);
        }
        demandeInscriptionRepository.save(inscription);
        map.put("status",validationDto.getStatus());
        return map;

    }

}
