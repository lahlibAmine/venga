package com.ram.venga.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysema.commons.lang.Pair;
import com.ram.config.Roles;
import com.ram.exception.UserAlreadyExistException;
import com.ram.exception.VengaException;
import com.ram.venga.domain.*;
import com.ram.venga.mapper.VenteMapper;
import com.ram.venga.model.*;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.OTPTypeEnum;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.model.enumeration.StatutDemandeEnum;
import com.ram.venga.model.ProfileDto;
import com.ram.venga.projection.CollaborateurSearchProjection;
import com.ram.venga.projection.CollaborateursPointsReportProjection;
import com.ram.venga.repos.*;
import com.ram.venga.util.ExcelGeneratorUtility;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.CollaborateurMapper;
import com.ram.venga.util.NotFoundException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
public class CollaborateurService {

    private final CollaborateurMapper collaborateurMapper;
    private final CollaborateurRepository collaborateurRepository;
    private final  UtilisateurRepository utilisateurRepository;
    private final  UtilisateurService utilisateurService;
    private final KeycloackService keycloackService;
    private final DemandeInscriptionService demandeInscriptionService;
    private final MailService mailService;
    private final EntiteRepository entiteRepository;
    private final OTPService otpService;


    @Value("${user.admin.usernameAdmin}")
    String  adminUsername ;
    @Value("${user.admin.passwordAdmin}")
    String  adminPassword;
    @Value("${keycloak.credentials.secret}")
    private String secret;
    @Value("${keycloak.resource}")
    private String client_id;
    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${userMessage.checked}")
    String  checkedMessage ;

    public List<CollaborateurDTO> findAll() {
        final List<Collaborateur> collaborateurs = collaborateurRepository.findAll(Sort.by("dateCreated").descending());
        return collaborateurs.stream()
                .map(collaborateur -> collaborateurMapper.toDto(collaborateur))
                .toList();
    }

    public Page<CollaborateurSearchDTO> findAllWithSearch(String keyword, Pageable pageable) {
        Page<Collaborateur> collaborateurPage = collaborateurRepository.findAllByKeyword(keyword,pageable);
        return collaborateurPage.map(collaborateurMapper::toDtoSearch);
    }

    public CollaborateurDTO get() {
       /* KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal=(KeycloakPrincipal)token.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();*/
       String userIdByToken = keycloackService.getIdUserToken();

        return utilisateurRepository.findByRefKUser(userIdByToken)
                .map(Utilisateur::getCollaborateur).map(colla ->collaborateurMapper.toDto(colla))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity create(final CollaborateurDTO collaborateurDTO) throws JsonProcessingException, UnsupportedEncodingException, javax.mail.MessagingException {
        String signature = "AINCAS";
        Map<String,String> map = new HashMap<>();
        if(collaborateurDTO.getCivilite() == null){
            return new ResponseEntity<>(map.put("Message","veuiller entrez votre Civilite"), HttpStatus.BAD_REQUEST);

        }
        if(collaborateurDTO.getNomAgent() == null){
            return new ResponseEntity<>(map.put("Message","veuiller entrez votre Nom"), HttpStatus.BAD_REQUEST);

        }
        if(collaborateurDTO.getPrenomAgent() == null){
            return new ResponseEntity<>(map.put("Message","veuiller entrez votre Prenom"), HttpStatus.BAD_REQUEST);

        }

        if(collaborateurDTO.getEmailAgent() == null){
            return new ResponseEntity<>(map.put("Message","veuiller entrez votre Email"), HttpStatus.BAD_REQUEST);

        }

        Collaborateur collaborateurResponse = collaborateurRepository.findByEmail(collaborateurDTO.getEmailAgent());



        if(collaborateurResponse != null){
            Utilisateur user = utilisateurRepository.findByCollaborateurId(collaborateurResponse.getId());
            if(collaborateurResponse.getUtilisateur().getLastOtpIsValid()){
                return new ResponseEntity<>(checkedMessage, HttpStatus.BAD_REQUEST);
            }
            otpService.generateOTPCodeAndSendItByEmail("Your One-Time Password (OTP) for Demand Registration Request","complete your Demand registration request",user);

            return ResponseEntity.ok("OTP envoyé avec succès, veuillez vérifier votre boîte de réception.");


        }
        List<String> lastSignature = collaborateurRepository.findMaxSignatureNumber();
        List<Integer> list = new ArrayList<>();
        for (String Number : lastSignature) {
            // Replace the indices with the desired substring range
            String subString = Number.substring(6); // Example: extracting the first 5 characters
            list.add(Integer.valueOf(subString));
        }
        int maxNumber = Integer.MIN_VALUE;
        for (Integer num : list) {
            if (num > maxNumber) {
                maxNumber = num;
            }
        }
        Collaborateur col = collaborateurMapper.toEntity(collaborateurDTO);

        if(!lastSignature.isEmpty()){
            int num = 1+maxNumber;
            col.setSignature(signature+num);
        }else{
            col.setSignature(signature+1);
        }
        col.setSoldePoint(0);
        col.setChiffreAffaire(0);
        Collaborateur newCollaborateur = collaborateurRepository.save(col);

        UtilisateurDemandeInscriptionDTO utilisateurDTO = UtilisateurDemandeInscriptionDTO.builder()
                .collaborateur(newCollaborateur.getId())
                .dateCreated(OffsetDateTime.now())
                .lastUpdated(OffsetDateTime.now())
                .login(newCollaborateur.getEmail())
                .active(false)
                .condGeneralAccepted(false)
                .newsLetterAccepted(false)
                .email(newCollaborateur.getEmail())
                .profil(ProfilEnum.REPRESENTANT)
                .signature(newCollaborateur.getSignature())
                .lastOtpIsValid(false)
                .otpCreationDateTime(OffsetDateTime.now())
                .build();
        Utilisateur user = utilisateurService.createWithDemandeInscription(utilisateurDTO);

        otpService.generateOTPCodeAndSendItByEmail("Your One-Time Password (OTP) for Demand Registration Request","complete your Demand registration request",user);

        return ResponseEntity.ok(collaborateurRepository.findByEmail(collaborateurDTO.getEmailAgent()).getId());
    }

    public void createUserInkeyCloak(Collaborateur collaborateur,Utilisateur user) throws JsonProcessingException, MessagingException, UnsupportedEncodingException {
        String token =keycloackService.getToken(adminUsername, adminPassword).get("tokenAccess");
        traiterRattache(keycloackService.addUserKeyCloak(collaborateurMapper.toDto(collaborateur),collaborateur.getSignature(),token,user).getId(),"/rattache/Referentiel-des-agents/validationRefus");
    }

    public void update(final CollaborateurDTO collaborateurDTO,String authorizationHeader) throws JsonProcessingException {
       String idUser = keycloackService.getIdUserToken();
       Collaborateur collaborateur= utilisateurRepository.findByRefKUser(idUser).map(Utilisateur::getCollaborateur).orElseThrow(NotFoundException::new);
        collaborateurDTO.setIdCollaborateur(collaborateur.getId());
        collaborateurRepository.save(collaborateurMapper.toEntity(collaborateurDTO));
        RestTemplate restTemplate = new RestTemplate();
        String krck = utilisateurRepository.findByCollaborateurId(collaborateurDTO.getIdCollaborateur()).getRefKUser();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(krck).get();
        utilisateur.setEmail(collaborateurDTO.getEmailAgence());
        utilisateurRepository.save(utilisateur);
        keycloackService.updateUserInKeycloak(collaborateurDTO, restTemplate,krck, authorizationHeader);
    }

    public StatutDemandeEnum traiterRattache(Long id,String link) throws  UnsupportedEncodingException, javax.mail.MessagingException {
        // Chercher la demande d'inscription par id :
        String Email = null;
        DemandeInscription inscriptionTrouvee = demandeInscriptionService.getInscriptionById(id);
        Optional<String> emailRattache = utilisateurRepository.findConcernedRattacheEmail(inscriptionTrouvee.getCollaborateur().getEntite().getParent().getId());
        if(emailRattache.isPresent() && inscriptionTrouvee.getStatut().equals(StatutDemandeEnum.EN_COURS)){
            mailService.envoyerEmailActivationCompteRattache(emailRattache.get(),link);
        }
        return inscriptionTrouvee.getStatut();
    }


    public void delete(final Long id) {
        collaborateurRepository.deleteById(id);
    }

    public boolean codeExists(final String code) {
        return collaborateurRepository.existsByCodeIgnoreCase(code);
    }

    public Map<String , Integer> getNbrPointBYCollaborateur(Long id) {
        Map<String , Integer> map = new HashMap<>();
        map.put("point",collaborateurRepository.findById(id).get().getSoldePoint());
       return map;
    }

    public Page<CollaborateurDTO> TotalVenteByAllCollaborateur(Pageable pageable,String keyWord,String portfeuille,String representation) {
        String idUser =keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
        List<Long> idEntites = new ArrayList<>();
        Sort sort = Sort.by(Sort.Order.desc("soldePoint").nullsLast());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Collaborateur> page = null;

        if (utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) ||utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN) ){
           page = collaborateurRepository.findAllCollaborateurPointsAdmin(sortedPageable,keyWord,portfeuille,representation);

        }else if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL) ){
            Long entite = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            idEntites = entiteRepository.findByParentId(entite);
            page = collaborateurRepository.findAllCollaborateurPointsRattacher(sortedPageable,keyWord,idEntites,portfeuille,representation);

        }else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            idEntites = entiteRepository.findByRepresentationAgence(idEntiteRa);
            page = collaborateurRepository.findAllCollaborateurPointsRattacher(sortedPageable,keyWord,idEntites,portfeuille,representation);

        }

       // Page<Collaborateur> page = collaborateurRepository.findAllCollaborateurPoints(sortedPageable, keyWord, idEntites, isAdminFonctionnel, portfeuille, representation);

        //Page<Collaborateur> page = collaborateurRepository.findAllCollaborateurPoints(sortedPageable,keyWord,idEntites,isAdminFonctionnel,portfeuille,representation);
        Page<CollaborateurDTO> LigneCommandePageDTOS = page.map(collaborateurMapper::toDto);
        return LigneCommandePageDTOS;
    }

    public Long updatePassword( String oldPass, String newPass) throws JsonProcessingException {
        String idUser =keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        PasswordEncoder springPasswordEncoder = passwordEncoder;

        boolean matches = springPasswordEncoder.matches(oldPass, utilisateur.getPassword());

        if (matches) {
            String newPassEncoder = passwordEncoder.encode(newPass);
            String token = keycloackService.getToken(utilisateur.getEmail(), oldPass).get("tokenAccess");

            utilisateur.setPassword(newPassEncoder);
            utilisateurRepository.save(utilisateur);
            keycloackService.createPasswordKeycloack(utilisateur.getRefKUser(), utilisateur.getEmail(), newPass, token);
        } else {
            throw new VengaException("Le mot de passe est incorrect.");
        }

        return utilisateur.getCollaborateur().getId();
    }

    public List<ChiffreAffaireDto> getChiffreAffaireByAgent() {
        String idUser = keycloackService.getIdUserToken();
        List<Collaborateur> collaborateurs = new ArrayList<>();
        List<ChiffreAffaireDto> chiffreAffaireDtos = null;
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElseThrow(() -> new RuntimeException("User not found."));
        Pageable pageable = PageRequest.of(0, 10);
        if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
            Long idEntiteRa = utilisateur != null && utilisateur.getCollaborateur() != null  && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            List<Long> idEntite = entiteRepository.findByParentId(idEntiteRa);
            collaborateurs = collaborateurRepository.findByProfilRattacherWithEntiteLimit(idEntite, pageable);
        } else if (utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL)) {
            collaborateurs = collaborateurRepository.findAllByAgentLimit(pageable);
        }else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            List<Long> idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
            collaborateurs = collaborateurRepository.findByProfilRattacherWithEntiteLimit(idEntite, pageable);
        }
        else {
            throw new RuntimeException("Invalid user profile.");
        }
        chiffreAffaireDtos = collaborateurs.stream()
                .map(collaborateurMapper::toDtoChiffre)
                .collect(Collectors.toList());

        return chiffreAffaireDtos;
    }

    public Map<String , Double> getTotalChiffreAffaire() {
        String idUser = keycloackService.getIdUserToken();
        double sumChiffreAffaire = 0;
        double sumPointGagne = 0;
        Map<String,Double> map = new HashMap<>();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElseThrow(() -> new RuntimeException("User not found."));
        if (utilisateur.getProfil() == ProfilEnum.RATTACHE_COMMERCIAL) {
            Long idEntiteRa = utilisateur != null && utilisateur.getCollaborateur() != null  && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            List<Long> idEntite = entiteRepository.findByParentId(idEntiteRa);
            if(!idEntite.isEmpty()){
                sumChiffreAffaire = collaborateurRepository.sumChiffreAffaireRattacher(idEntite);

                sumPointGagne = collaborateurRepository.sumPointRattacher(idEntite);
            }
        }else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            List<Long> idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
            if(!idEntite.isEmpty()){
                sumChiffreAffaire = collaborateurRepository.sumChiffreAffaireRattacher(idEntite);
                sumPointGagne = collaborateurRepository.sumPointRattacher(idEntite);

            }
         }
        else if (utilisateur.getProfil() == ProfilEnum.ADMIN_FONCTIONNEL) {
            sumPointGagne = collaborateurRepository.sumPointAdmin();
            sumChiffreAffaire = collaborateurRepository.sumChiffreAffaireAdmin();
        } else {
            throw new RuntimeException("Invalid user profile.");
        }
        map.put("totalPointGagner",sumPointGagne);
        map.put("totalChiffreAffaire",sumChiffreAffaire);
        return map;
    }

    public void sendEmail( String sujet) throws MessagingException, UnsupportedEncodingException {
       String email = "amine.lahlib98@gmail.com";
       String message = "hello";
        mailService.email(email,message,sujet);
    }

    public ResponseEntity<?> getAllAgentWithPage( String keyword, Pageable pageable) {
        Page<Collaborateur> collaborateurs = collaborateurRepository.findAllByAgentPage(keyword,pageable);
       Page<AdministartionAgentDto> page = collaborateurs.map(collaborateurMapper::toDtoAdmin);
        return ResponseEntity.ok( page);
    }

    public ResponseEntity<?> updateAgent(AdministartionAgentDto administartionAgentDto) {

        Collaborateur collaborateur =  collaborateurRepository.findById(administartionAgentDto.getId()).get();
        Entite entite = entiteRepository.findById(administartionAgentDto.getEntite().getId()).get();
    collaborateur.setEntite(entite);
       return ResponseEntity.ok(collaborateurRepository.save(collaborateur).getId());
    }

    public void updateStatus(CollaborateurChangeStatusDTO collaborateurChangeStatusDTO) {
        Optional<Utilisateur> utilisateur = Optional.ofNullable(utilisateurRepository.findByCollaborateurId(collaborateurChangeStatusDTO.getIdCollaborateur()));
        try {
            if (utilisateur.isPresent()) {
                utilisateur.get().setActive(!utilisateur.get().getActive());
                utilisateurRepository.save(utilisateur.get());
            }else {
                throw new RuntimeException("User not found.");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while updating user status.");
        }

    }

    @Transactional
    public void updateAgentInfo(AgentUpdateInfos agentUpdateInfos) {
        Collaborateur collaborateur = collaborateurRepository.findById(agentUpdateInfos.getIdCollaborateur())
                .orElseThrow(() -> new RuntimeException("Collaborateur or Entite not found."));
        Utilisateur utilisateur = utilisateurRepository.findByCollaborateurId(collaborateur.getId());
        Optional<Entite> optionalEntite = entiteRepository.findById(agentUpdateInfos.getAgenceId());
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .clientId(client_id)
                .clientSecret(secret)
                .build();
        if(utilisateur != null){
            Entite entite = optionalEntite.get();
            collaborateur.setEmail(agentUpdateInfos.getEmail());
            collaborateur.setTelephone(agentUpdateInfos.getTelephone());
            collaborateur.setEntite(entite);
            utilisateur.setEmail(agentUpdateInfos.getEmail());
            collaborateurRepository.save(collaborateur);
            utilisateurRepository.save(utilisateur);
            String userId = utilisateur.getRefKUser();
            UserRepresentation userRepresentation = keycloak.realm(realm).users().get(userId).toRepresentation();
            userRepresentation.setEmail(agentUpdateInfos.getEmail());
            keycloak.realm(realm).users().get(userId).update(userRepresentation);
        }else{
            throw new IllegalArgumentException("l'utilisateur n'as pas encore de compte ! veuillez réessayer plus tard .");
        }


    }
    @Transactional
    public ResponseEntity<?> createAccount(ProfileDto postedData)
            throws JsonProcessingException, MessagingException, UnsupportedEncodingException {
        try{
        String token = keycloackService.getToken();

   //     String idKeycloak = keycloackService.getUserId(postedData.getEmail());
        Collaborateur col = createCollaborateurInDb(postedData);
        createProfileInKeycloak(token, postedData);
        String idKeycloack = keycloackService.getUserId(postedData.getEmail());

        String password = generateRandomPassword();
        keycloackService.createPasswordProfilKeycloack(idKeycloack, postedData.getEmail(), password, token, postedData.getProfil());

        List<String> list = List.of(Roles.manageUsers,Roles.viewUsers,Roles.viewClients);

        UtilisateurDTO utilisateur = createUserDto(col, postedData, idKeycloack);
        Long utilisateurId = utilisateurService.create(utilisateur);
        if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){
            keycloackService.affectManagementClientRoles(idKeycloack, list,token);
        }
        if (utilisateurId == null) {
            throw new UserAlreadyExistException("User Exist Deja.");
        }

        mailService.envoyerEmailActivationCompte(postedData.getEmail(),postedData.getEmail(),Character.toUpperCase(postedData.getPrenom().charAt(0)) + postedData.getPrenom().substring(1)+" "+Character.toUpperCase(postedData.getNom().charAt(0)) + postedData.getNom().substring(1),password);
        return ResponseEntity.ok(collaborateurMapper.toDto(col));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors du creation d'utilisateur. Veuillez réessayer plus tard.");
        }
    }

    private void createProfileInKeycloak(String token, ProfileDto postedData) throws JsonProcessingException {
        String url = keycloakAuthUrl + "/admin/realms/" + realm + "/users";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization","Bearer "+ token);
        keycloackService.createProfilInKeycloak(postedData, restTemplate, url, headers);
    }

    private Collaborateur createCollaborateurInDb(ProfileDto postedData) {
        Collaborateur collaborateurResponse = collaborateurRepository.findByEmail(postedData.getEmail());
        if(collaborateurResponse != null){
            throw new UserAlreadyExistException("Email already exists in our database!");
        }
        Collaborateur col = collaborateurMapper.fromProfileDtoToCollaborateur(postedData);
        col.setSoldePoint(0);
        col.setChiffreAffaire(0);
        col.setSignature(null);
        if (postedData.getProfil().equals(CategorieCollaborateurEnum.RATTACHE) && postedData.getBelongTo() != null) {
            boolean isAlreadyAffected = entiteRepository.isPortefeuilleAlreadyAffected(postedData.getBelongTo());
            if (!isAlreadyAffected){
                Optional<Entite> entite = entiteRepository.findById(postedData.getBelongTo());
                col.setEntite(entite.orElseThrow(() -> new UserAlreadyExistException("Entite not found.")));
            }else{
                throw new UserAlreadyExistException("Ce portefeuille est déjà assigné à un autre rattaché.");
            }
        }
        if (postedData.getProfil().equals(CategorieCollaborateurEnum.CONSULTANT) && postedData.getBelongToRepresentation() != null && postedData.getBelongToRepresentation().size() >= 1) {
            Set<Entite> entites = new HashSet<>();
            for (Long entiteRepresentation : postedData.getBelongToRepresentation()) {
                Optional<Entite> entiteOptional = entiteRepository.findById(entiteRepresentation);
                Entite entite = entiteOptional.orElseThrow(() -> new UserAlreadyExistException("Entite not found."));
                entites.add(entite);
            }
            if (entites.isEmpty()) {
                throw new UserAlreadyExistException("Entite not found.");
            } else {
                col.setEntites(entites);
            }
        }

        col.setCode(String.valueOf(Integer.parseInt(collaborateurRepository.getMaxCodeCollaborateur()) + 1));
        collaborateurRepository.save(col);
        return col;
    }

    private String generateRandomPassword() {
        return demandeInscriptionService.generateRandomPassword();
    }

    private UtilisateurDTO createUserDto(Collaborateur col, ProfileDto postedData, String idKeycloak) {
        UtilisateurDTO utilisateur = new UtilisateurDTO();
        utilisateur.setCollaborateur(col.getId());
        utilisateur.setEmail(postedData.getEmail());
        utilisateur.setLogin(postedData.getEmail());
        utilisateur.setActive(true);
        utilisateur.setNom(col.getNom());
        utilisateur.setPrenom(col.getPrenom());
        utilisateur.setCivilite(postedData.getCivilite());
        utilisateur.setRefKUser(idKeycloak);
        utilisateur.setCondGeneralAccepted(true);
        utilisateur.setNewsLetterAccepted(false);

        setProfilAndEntite(utilisateur, col, postedData);

        return utilisateur;
    }

    private void setProfilAndEntite(UtilisateurDTO utilisateur, Collaborateur col, ProfileDto postedData) {
        if (postedData.getProfil().equals(CategorieCollaborateurEnum.RATTACHE)) {
            utilisateur.setProfil(ProfilEnum.RATTACHE_COMMERCIAL);
            if (col.getEntite() != null)
                utilisateur.setIdEntite(col.getEntite().getId());
            else
                utilisateur.setIdEntite(null);
        } else if (postedData.getProfil().equals(CategorieCollaborateurEnum.FONCTIONNEL)) {
            utilisateur.setProfil(ProfilEnum.ADMIN_FONCTIONNEL);
            utilisateur.setIdEntite(null);
        } else if (postedData.getProfil().equals(CategorieCollaborateurEnum.TECHNIQUE)) {
            utilisateur.setProfil(ProfilEnum.SUPER_ADMIN);
            utilisateur.setIdEntite(null);
        }
        else if (postedData.getProfil().equals(CategorieCollaborateurEnum.CONSULTANT)) {
            utilisateur.setProfil(ProfilEnum.CONSULTANT);
            utilisateur.setIdEntite(null);
        }else {
            throw new RuntimeException("Invalid user profile.");
        }
    }

    public ResponseEntity<Page<CollaborateurUserDTO>> getAllProfilesWithPage(String keyword, Pageable pageable) {
        Page<Collaborateur> collaborateurs = collaborateurRepository.findAllProfilesPageable(keyword,pageable);
        Page<CollaborateurUserDTO> collaborateurDTOS = collaborateurs.map(collaborateurMapper::toDtoUser);
        return ResponseEntity.ok(collaborateurDTOS);
    }

    public void deleteProfile(Long id) {
        // delete utilisateur associated with this collaborateur
        Optional<Utilisateur> utilisateur = utilisateurService.findByCollaborateurId(id);
        if (utilisateur.isPresent()){
            keycloackService.deleteUser(utilisateur.get().getRefKUser(), keycloackService.getToken());
            utilisateurService.delete(utilisateur.get().getId());
          }else {
            throw new RuntimeException("Utilisateur not found.");
        }
        // delete collaborateur
        collaborateurRepository.deleteById(id);
    }

    public ResponseEntity<CollaborateurDTO> updateProfile(ProfileDto postedData) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurService.findByCollaborateurId(postedData.getId());
        if (optionalUtilisateur.isEmpty()) {
            throw new RuntimeException("Utilisateur not found.");
        }
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .clientId(client_id)
                .clientSecret(secret)
                .build();
        String roleASupprimer = null;
        if(optionalUtilisateur.isPresent() ){
            roleASupprimer = keycloackService.getRoleKeycloack(optionalUtilisateur.get().getCollaborateur().getCategorie());
        }

        Utilisateur utilisateur = optionalUtilisateur.get();
        String userId = utilisateur.getRefKUser();
        UserRepresentation userRepresentation = keycloak.realm(realm).users().get(userId).toRepresentation();
        // Update Keycloak user
        userRepresentation.setEmail(postedData.getEmail());
        userRepresentation.setUsername(postedData.getEmail());
        userRepresentation.setFirstName(postedData.getPrenom());
        userRepresentation.setLastName(postedData.getNom());
        userRepresentation.setEnabled(true);
        userRepresentation.setId(utilisateur.getRefKUser());
   //     keycloackService.updateUser(userRepresentation);
        RoleRepresentation roleToRemove = keycloak.realm(realm).roles().get(roleASupprimer).toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(Collections.singletonList(roleToRemove));
        RoleRepresentation roleToAdd = keycloak.realm(realm).roles().get(keycloackService.getRoleKeycloack(postedData.getProfil())).toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(roleToAdd));
        // Update roles if necessary
        List<String> roles = new ArrayList<>();

        //userRepresentation.setRealmRoles(roles);
        keycloak.realm(realm).users().get(userId).update(userRepresentation);

        // Update utilisateur and collaborateur
        Collaborateur collaborateur = collaborateurRepository.findById(postedData.getId())
                .orElseThrow(() -> new RuntimeException("Collaborateur not found."));
        utilisateur.setLogin(postedData.getEmail());
        utilisateur.setEmail(postedData.getEmail());
        collaborateur.setNom(postedData.getNom());
        collaborateur.setPrenom(postedData.getPrenom());
        collaborateur.setCivilite(postedData.getCivilite());
        collaborateur.setEmail(postedData.getEmail());
        collaborateur.setCategorie(postedData.getProfil());

        Optional<Entite> entiteOptional = Optional.empty();
        Set<Entite> entites = new HashSet<>();

        if (postedData.getBelongTo() != null && postedData.getProfil().equals(CategorieCollaborateurEnum.RATTACHE)) {
            entiteOptional = entiteRepository.findById(postedData.getBelongTo());
        } else if (postedData.getBelongToRepresentation() != null && !postedData.getBelongToRepresentation().isEmpty()&& postedData.getProfil().equals(CategorieCollaborateurEnum.CONSULTANT)) {
            for (Long entiteRepresentation : postedData.getBelongToRepresentation()) {
                Entite entite = entiteRepository.findById(entiteRepresentation)
                        .orElseThrow(() -> new UserAlreadyExistException("Entite not found."));
                entites.add(entite);
            }
        }

        switch (postedData.getProfil()) {
            case TECHNIQUE, FONCTIONNEL -> {
                if (postedData.getProfil().equals(CategorieCollaborateurEnum.FONCTIONNEL))
                    utilisateur.setProfil(ProfilEnum.ADMIN_FONCTIONNEL);
                else
                    utilisateur.setProfil(ProfilEnum.SUPER_ADMIN);
                collaborateur.setEntite(null);
            }
            case RATTACHE -> {
                utilisateur.setProfil(ProfilEnum.RATTACHE_COMMERCIAL);
                collaborateur.setEntite(entiteOptional.orElse(null));
                collaborateur.setEntites(null);/// Allow setting entite to null
            }
            case CONSULTANT -> {
                utilisateur.setProfil(ProfilEnum.CONSULTANT);
                collaborateur.setEntites(entites.isEmpty() ? null : entites);
                collaborateur.setEntite(null);// Allow setting entites to null
            }
            default -> throw new RuntimeException("Invalid user profile.");
        }

        collaborateurRepository.save(collaborateur);
        utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(collaborateurMapper.toDto(collaborateur));
    }





/*
    public String increment(String signature){
        String prefix = signature.replaceAll("[0-9]", "");

        // Extraire la partie numérique du string (ex. "6656")
        String numericPart = signature.replaceAll("[^0-9]", "");

        // Convertir la partie numérique en un entier et l'incrémenter de 1
        int numericValue = Integer.parseInt(numericPart);
        numericValue++;
        return prefix + numericValue;
    }*/

    public boolean isRecetteInHauteSaison(Vente vente, List<HauteSaison> hauteSaisons) {
            LocalDateTime EmisDate = vente.getDateEmission();
            for (HauteSaison saison : hauteSaisons) {
                LocalDateTime saisonDebut = saison.getDateDebut().atStartOfDay();
                LocalDateTime saisonFin = saison.getDateFin().atTime(LocalTime.MAX);

                if (EmisDate.isAfter(saisonDebut) && EmisDate.isBefore(saisonFin)) {
                    return true; // Sale falls within a high season, no need to check further
                }
            }
        return false; // No high season found for any sale
    }


 //   @Scheduled(cron = "0 0 0 1 1 ?")
    /*  @Scheduled(cron = "0 2 * * * ?")
    public void resetSoldeCollaborator() {
        List<Collaborateur> collaborateurs = collaborateurRepository.findAllByAgent();
        for (Collaborateur collaborateur : collaborateurs){
            collaborateur.setSoldeExpired(collaborateur.getSoldePoint());
            collaborateur.setSoldePoint(0);
            collaborateurRepository.save(collaborateur);
        }
    }*/


    public void envoyerEmail(String Email) throws MessagingException, UnsupportedEncodingException {
        String link ="/rattache//Referentiel-des-agents//validationRefus";
        mailService.envoyerEmailActivationCompteRattache(Email,link);

    }

    public ResponseEntity<String> updateAgentPersonalInfo(UpdateAgentPersonalInfosDTO agentDTO) {
        Utilisateur user = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborateur non trouvé."));
        Collaborateur collaborateur = user.getCollaborateur();
        Boolean isValid = otpService.validateOTPCode(agentDTO.getOtpCode(), user);

        if (isValid == null) {
            return ResponseEntity.badRequest().body("Échec de la mise à jour des informations personnelles de l'agent. Le code OTP fourni a expiré.");
        } else if (!isValid) {
            return ResponseEntity.badRequest().body("Échec de la mise à jour des informations personnelles de l'agent. Le code OTP fourni est incorrect.");
        } else {
            collaborateur.setMobile(agentDTO.getPhoneNumber());
            if (!agentDTO.getFonction().isEmpty()) {
                collaborateur.setFonction(agentDTO.getFonction());
            }
            collaborateurRepository.save(collaborateur);
            return ResponseEntity.ok("Informations personnelles de l'agent mises à jour avec succès.");
        }
    }

    public Page<CollaborateurSearchDTO> agentsByAuthenticatedRattache(String keyword, Pageable pageable) {
        Utilisateur utilisateur = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborateur not found."));
        Long idPortefeuille = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
        Page<Collaborateur> collaborateurPage = collaborateurRepository.findAgentsByPorteuille(idPortefeuille, keyword, pageable);
        return collaborateurPage.map(collaborateurMapper::toDtoSearch);
    }

    public Page<CollaborateurSearchDTO> agentsByAuthenticatedConsultant(String keyword, Pageable pageable) {
        Utilisateur user = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collaborateur not found."));
        List<Long> ids = user.getCollaborateur().getEntites().stream().map(Entite::getId).toList();
        Page<Collaborateur> collaborateurPage = collaborateurRepository.findAgentsByListOfRepresentationIds(ids, keyword, pageable);
        return collaborateurPage.map(collaborateurMapper::toDtoSearch);
    }


    public Collaborateur findCollaborateurByEmail(String email) {
        return collaborateurRepository.findByEmail(email);
    }

    public void deleteCollaborateur() {
            List<String> signatureAgents = new ArrayList<>();

            ClassPathResource resource = new ClassPathResource("files/signature_collaborateur.xlsx");
            try (InputStream inputStream = resource.getInputStream()) {
                Workbook workbook = WorkbookFactory.create(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    Cell cell = row.getCell(0); // Assuming data is in the first column
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue().trim();
                        if (!cellValue.isEmpty()) {
                            signatureAgents.add(cellValue);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public void exportNombreDePointsToExcelAdmins(HttpServletResponse response, String keyWord, String representation, String portefeuille) {
        String idUser =keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
        List<CollaborateursPointsReportProjection> collaborateurs = new ArrayList<>();
        if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
            Long portfeuilleId = utilisateur.getCollaborateur().getEntite().getId();
            collaborateurs =  collaborateurRepository.findAllCollaborateurPointsListForRAttByPortfeuilleId(keyWord, representation, portefeuille,portfeuilleId);
        }
        if (utilisateur.getProfil().equals(ProfilEnum.CONSULTANT)){
            List<Long> representationIds = utilisateur.getCollaborateur().getEntites().stream().map(Entite::getId).toList();
            collaborateurs =  collaborateurRepository.findAllCollaborateurPointsListForRAttORCon(keyWord, representation, portefeuille,representationIds);
        }
        if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){
            collaborateurs =  collaborateurRepository.findAllCollaborateurPointsListForAdmins(keyWord, representation, portefeuille);
        }
        List<Pair<String, Function<CollaborateursPointsReportProjection, String>>> pairs = new ArrayList<>();
        pairs.add(Pair.of("Signature", CollaborateursPointsReportProjection::getSignature));
        pairs.add(Pair.of("Nom", CollaborateursPointsReportProjection::getNom));
        pairs.add(Pair.of("Prenom", CollaborateursPointsReportProjection::getPrenom));
        pairs.add(Pair.of("Email", CollaborateursPointsReportProjection::getEmail));
        pairs.add(Pair.of("Agence", CollaborateursPointsReportProjection::getAgence));
        pairs.add(Pair.of("Code IATA", CollaborateursPointsReportProjection::getCodeIATA));
        pairs.add(Pair.of("Portefeuille", CollaborateursPointsReportProjection::getPortefeuille));
        pairs.add(Pair.of("Representation", CollaborateursPointsReportProjection::getRepresentation));
        pairs.add(Pair.of("Nombre De Points", proj -> proj.getNumberDePoints() != null ? proj.getNumberDePoints().toString() : ""));
        ExcelGeneratorUtility.generateExcelReport(response, collaborateurs, pairs);
    }

    public void findAllWithSearchExport(HttpServletResponse response,String keyword) {
        String idUser =keycloackService.getIdUserToken();
        List<Long> ids = new ArrayList<>();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
        List<CollaborateurSearchProjection> collaborateurs = new ArrayList<>();
        if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
            ids.add(utilisateur.getCollaborateur().getEntite().getId());
            collaborateurs = collaborateurRepository.findAllByKeywordListFilterPotfeuille(keyword,ids);
        }
        if (utilisateur.getProfil().equals(ProfilEnum.CONSULTANT)){
            ids.addAll(utilisateur.getCollaborateur().getEntites().stream().map(Entite::getId).toList());
            List<Long> entite = entiteRepository.findByPortfeuilleAgence(ids);
            collaborateurs = collaborateurRepository.findAllByKeywordListFilterPotfeuille(keyword,entite);
        }if(utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){
            collaborateurs = collaborateurRepository.findAllByKeywordList(keyword);
        }
        List<Pair<String, Function<CollaborateurSearchProjection, String>>> pairs = new ArrayList<>();
        pairs.add(Pair.of("Code Iata", CollaborateurSearchProjection::getAgenceCode));
        pairs.add(Pair.of("Signature", CollaborateurSearchProjection::getSignature));
        pairs.add(Pair.of("Prenom", CollaborateurSearchProjection::getPrenom));
        pairs.add(Pair.of("Nom", CollaborateurSearchProjection::getNom));
        pairs.add(Pair.of("Email", CollaborateurSearchProjection::getEmail));
        pairs.add(Pair.of("Telephone", CollaborateurSearchProjection::getTelephone));
        pairs.add(Pair.of("Portfeuille", CollaborateurSearchProjection::getPortfeuilleNom));
        pairs.add(Pair.of("Representation", CollaborateurSearchProjection::getRepresentationNom));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        pairs.add(Pair.of("Date creation", proj -> proj.getDateCreation().format(formatter)));
        pairs.add(Pair.of("Nom Agence", CollaborateurSearchProjection::getAgenceNom));
        pairs.add(Pair.of("Status", CollaborateurSearchProjection::getStatut));


        ExcelGeneratorUtility.generateExcelReport(response, collaborateurs, pairs);

    }

    public Map<String, Double> getDebitCredit() {
        String idUser =keycloackService.getIdUserToken();
        String signature = utilisateurRepository.findByRefKUser(idUser).get().getCollaborateur().getSignature();
        Double sumCredit = collaborateurRepository.getSumCredit(signature);
        Double sumDebit = collaborateurRepository.getSumDebit(signature);
        Map<String , Double> map = new HashMap<>();
        map.put("credit",sumCredit);
        map.put("debit",sumDebit);
        return map;

    }
}
