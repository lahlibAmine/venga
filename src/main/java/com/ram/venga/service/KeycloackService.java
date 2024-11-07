package com.ram.venga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.config.Roles;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.DemandeInscription;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.mapper.CollaborateurMapper;
import com.ram.venga.model.CollaborateurDTO;
import com.ram.venga.model.ProfileDto;
import com.ram.venga.model.ResetPasswordDTO;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.model.enumeration.StatutDemandeEnum;
import com.ram.venga.model.enumeration.TypeDemandeEnum;
import com.ram.venga.repos.*;
import com.ram.venga.util.JwtTokenUtil;
import com.ram.venga.util.NotFoundException;
import com.ram.venga.util.Util;
import io.jsonwebtoken.Claims;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;

import javax.ws.rs.core.Response;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class KeycloackService {
  /*  @Autowired
    private UserTokenRepository UserTokenRepository;*/
  @Value("${user.admin.usernameAdmin}")
  String  adminUsername ;
    @Value("${frontend.server.url}")
    String  url ;
   @Value("${user.admin.passwordAdmin}")
    String  adminPassword;

    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthUrl;
    @Value("${keycloak.realm}")
    private String realm;
    private final AuditUserActionRepository auditUserActionRepository;
    @Value("${keycloak.credentials.secret}")
    private String secret;
    @Value("${keycloak.resource}")
    private String client_id;
    private final UtilisateurRepository utilisateurRepository;
    private final CollaborateurMapper collaborateurMapper;
    private final CollaborateurRepository collaborateurRepository;
    private final DemandeInscriptionRepository demandeInscriptionRepository;
    private final BonCommandeRepository bonCommandeRepository;



    private final MailService mailService;

    private final JwtTokenUtil jwtTokenUtil;
  //  private final RealmResource realmResource;



    public KeycloackService(AuditUserActionRepository auditUserActionRepository,  UtilisateurRepository utilisateurRepository, CollaborateurMapper collaborateurMapper, CollaborateurRepository collaborateurRepository, DemandeInscriptionRepository demandeInscriptionRepository,/*, RealmResource realmResource*/ BonCommandeRepository bonCommandeRepository, MailService mailService, JwtTokenUtil jwtTokenUtil) {
        this.auditUserActionRepository = auditUserActionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.collaborateurMapper = collaborateurMapper;
        this.collaborateurRepository = collaborateurRepository;
        this.demandeInscriptionRepository = demandeInscriptionRepository;
        this.bonCommandeRepository = bonCommandeRepository;
        this.mailService = mailService;
        /*  this.realmResource = realmResource;*/
        this.jwtTokenUtil = jwtTokenUtil;
    }


    // This moethod not used for now but it will be very usfull in the future especially if u don't want to use secret
    public Map<String,String> getToken(String username,String password) throws JsonProcessingException {
        MultiValueMap<String, String> req = new LinkedMultiValueMap();

        Map<String,String> map = new HashMap<>();
        req.add("username",username);
        req.add("password",password);
        req.add("grant_type","password");
        req.add("client_id",client_id);
        req.add("client_secret",secret);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(req, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(keycloakAuthUrl+"/realms/"+realm+"/protocol/openid-connect/token",entity, String.class);
        Map<String,Object> mp = new ObjectMapper().readValue(response.getBody(), HashMap.class);
        map.put("tokenAccess",mp.get("access_token").toString());
        return map;
    }
    public DemandeInscription addUserKeyCloak(final CollaborateurDTO collaborateurDTO,String signature, String token,Utilisateur user) throws JsonProcessingException{
        String url = keycloakAuthUrl + "/admin/realms/" + realm + "/users";
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization","Bearer "+ token);
        createUserInKeycloak(collaborateurDTO,signature, restTemplate, url,headers);
        Collaborateur collaborateur = null;
        DemandeInscription demandeInscription = null;
        collaborateur = collaborateurRepository.findByEmail(collaborateurDTO.getEmailAgent());
        demandeInscription = new DemandeInscription();
        demandeInscription.setCollaborateur(collaborateur);
        demandeInscription.setDateDemande(LocalDateTime.now());
        demandeInscription.setTypeDemande(TypeDemandeEnum.DEMANDE_INSCRIPTION);
        demandeInscription.setStatut(StatutDemandeEnum.EN_COURS);
        demandeInscriptionRepository.save(demandeInscription);
        String idKeyCloak = getUserId(collaborateur.getEmail());
        if(idKeyCloak == null)
            throw new NotFoundException("Utilisateur avec l'adresse email " + collaborateur.getEmail() + " introuvable dans Keycloak");
        user.setRefKUser(idKeyCloak);
        utilisateurRepository.save(user);
        return demandeInscription;

    }

    private void createUserInKeycloak(CollaborateurDTO collaborateurDTO,String signature, RestTemplate restTemplate, String url, MultiValueMap<String, String> headers) throws JsonProcessingException {
        UserRepresentation newUserRepresentation = new UserRepresentation();
        newUserRepresentation.setUsername(signature);
        newUserRepresentation.setEmail(collaborateurDTO.getEmailAgent());
        newUserRepresentation.setFirstName(collaborateurDTO.getPrenomAgence());
        newUserRepresentation.setLastName(collaborateurDTO.getNomAgent());
        newUserRepresentation.setEnabled(true);
        headers.set("Content-Type", "application/json");
        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(newUserRepresentation, headers);
        restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, UserRepresentation[].class);
    }

    public void createProfilInKeycloak(ProfileDto collaborateurDTO, RestTemplate restTemplate, String url, MultiValueMap<String, String> headers) throws JsonProcessingException {
        UserRepresentation newUserRepresentation = new UserRepresentation();
        newUserRepresentation.setUsername(collaborateurDTO.getEmail());
        newUserRepresentation.setEmail(collaborateurDTO.getEmail());
        newUserRepresentation.setFirstName(collaborateurDTO.getPrenom());
        newUserRepresentation.setLastName(collaborateurDTO.getNom());
        newUserRepresentation.setEnabled(true);
        headers.set("Content-Type", "application/json");
        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(newUserRepresentation, headers);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, UserRepresentation[].class);
    }
    public String getUserId(String email) throws JsonProcessingException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String url2 = keycloakAuthUrl + "/admin/realms/" + realm + "/users?email=" + email;
        String token = getToken(adminUsername,adminPassword).get("tokenAccess");
        headers.add("Authorization","Bearer "+ token);
        ResponseEntity<UserRepresentation[]> keycloakUsersResponse = new RestTemplate().exchange(
                url2, HttpMethod.GET, new HttpEntity<Object>(headers),
                UserRepresentation[].class);
        Optional<String> optional =  Arrays.stream(Objects.requireNonNull(keycloakUsersResponse.getBody())).map(UserRepresentation::getId).toList().stream().findFirst();
        return optional.orElse(null);
    }

    public String extractBearerToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            // Gérer le cas où l'en-tête Authorization est manquant ou invalide
            throw new RuntimeException("Bearer token not found in the request header");
        }
    }

    public void updateUserInKeycloak(CollaborateurDTO collaborateurDTO, RestTemplate restTemplate, String krck, String authorizationHeader) {
        String url = keycloakAuthUrl + "/admin/realms/" + realm + "/users/";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + extractBearerToken(authorizationHeader));
        ResponseEntity<UserRepresentation> keycloakUserResponse = restTemplate.exchange(
                url+ krck, HttpMethod.GET, new HttpEntity<>(headers), UserRepresentation.class);
        UserRepresentation userFromKC = keycloakUserResponse.getBody();
        userFromKC.setFirstName(collaborateurDTO.getPrenomAgence());
        userFromKC.setLastName(collaborateurDTO.getNomAgence());
        userFromKC.setEnabled(true);
        userFromKC.setEmail(collaborateurDTO.getEmailAgent());
        userFromKC.setUsername(collaborateurDTO.getEmailAgence());

        url = url + "/" + userFromKC.getId();
        headers.set("Content-Type", "application/json");
        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(userFromKC, headers);

        ResponseEntity<UserRepresentation[]> keycloakUsersResponse = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, UserRepresentation[].class);
    }


    public ResponseEntity affectManagementClientRoles(String userId, List<String> role, String token) throws RestClientException, JsonProcessingException {
        List<RoleRepresentation> managementClientRoles = getManagementClientRolesById(userId);
        List<RoleRepresentation> rolesList = new ArrayList<>();
    for(String roleLise : role){
        if (!managementClientRoles.stream().anyMatch(o -> o.getName().equals(roleLise))) {
            if (getManagementClientRoleByName(Roles.manageUsers) != null) {
                rolesList.add(getManagementClientRoleByName(roleLise));
                //keycloakUserResponse(userId, null);
                RestTemplate restTemplate = new RestTemplate();
                String url = keycloakAuthUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/clients/" + "d9f1cf22-cc5a-4369-9855-61bc778f30cc";
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

                headers.add("Authorization", "Bearer " + token);
                headers.add("Content-Type", "application/json"); // Set the content type
                HttpEntity<List<RoleRepresentation>> requestEntity = new HttpEntity<>(rolesList, headers);
                restTemplate.exchange(
                        url, HttpMethod.POST, requestEntity, UserRepresentation.class);

                // ...
            } else {
                return ResponseEntity.badRequest().body("You need the role " + Roles.manageUsers + " for Realm-management");
            }

        } else {
            return ResponseEntity.badRequest().body("The role " + role + " not found");
        }
    }
        return ResponseEntity.ok("management role affected");
    }

    public void createPasswordKeycloack(String refKUser, String user, String randomPassword, String token) throws JsonProcessingException {
        CredentialRepresentation crAdmin = new CredentialRepresentation();
        crAdmin.setType("password");
        crAdmin.setValue(randomPassword);
        crAdmin.setTemporary(false);
        UserRepresentation adminRepresentation = new UserRepresentation();
        adminRepresentation.setUsername(user);
        adminRepresentation.setEnabled(true);
        adminRepresentation.setCredentials(Arrays.asList(crAdmin));
        affectRealmRoles(refKUser,"agent",token);
        keycloakUserResponse(adminRepresentation,refKUser,token);
    }

    public void createPasswordProfilKeycloack(String refKUser, String user, String randomPassword, String token, CategorieCollaborateurEnum profil) throws JsonProcessingException {
        CredentialRepresentation crAdmin = new CredentialRepresentation();
        crAdmin.setType("password");
        crAdmin.setValue(randomPassword);
        crAdmin.setTemporary(false);
        UserRepresentation adminRepresentation = new UserRepresentation();
        adminRepresentation.setUsername(user);
        adminRepresentation.setEnabled(true);
        adminRepresentation.setCredentials(List.of(crAdmin));
        if (profil.equals(CategorieCollaborateurEnum.RATTACHE)) {
            affectRealmRoles(refKUser, "Rattache", token);
        } else if (profil.equals(CategorieCollaborateurEnum.FONCTIONNEL)) {
            affectRealmRoles(refKUser, "Admin Fonctionnel", token);
        } else if (profil.equals(CategorieCollaborateurEnum.TECHNIQUE)) {
            affectRealmRoles(refKUser, "Admin technique", token);
        }
        else if (profil.equals(CategorieCollaborateurEnum.CONSULTANT)) {
            affectRealmRoles(refKUser, "Consultant", token);
        }
        keycloakUserResponse(adminRepresentation,refKUser,token);
    }

    public void keycloakUserResponse( UserRepresentation adminRepresentation, String refKUser, String token) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String url = keycloakAuthUrl+"/admin/realms/"+realm+"/users/"+refKUser; // Update the URL
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-Type", "application/json");
        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(adminRepresentation, headers);
        restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, UserRepresentation.class);
    }


    public RoleRepresentation getManagementClientRoleByName(String name) throws JsonProcessingException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        RestTemplate restTemplate = new RestTemplate();
        String token = getToken(adminUsername, adminPassword).get("tokenAccess");
        String url = keycloakAuthUrl + "/admin/realms/" + realm + "/clients/" + "d9f1cf22-cc5a-4369-9855-61bc778f30cc" + "/roles";
        headers.add("Authorization", "Bearer " + token);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        for (JsonNode node : jsonNode) {
            RoleRepresentation roleRepresentation = objectMapper.convertValue(node, RoleRepresentation.class);
            if (roleRepresentation.getName().equals(name)) {
                return roleRepresentation;
            }
        }
        return null;
    }

    public List<RoleRepresentation> getManagementClientRolesById(String userId) throws RestClientException, JsonProcessingException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        RestTemplate restTemplate = new RestTemplate();
        String token = null;

            token=  getToken(adminUsername, adminPassword).get("tokenAccess");

        String url = keycloakAuthUrl+"/admin/realms/"+realm+"/users/"+userId+"/role-mappings/clients/"+"d9f1cf22-cc5a-4369-9855-61bc778f30cc";
        headers.add("Authorization", "Bearer " + token);
        ResponseEntity<RoleRepresentation[]> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), RoleRepresentation[].class);
        return Arrays.asList(response.getBody());
    }

    public void setPassword(String randomPassword) {
    }

    public String getIdUserToken(){
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Principal principal = (Principal) authentication.getPrincipal();

        String userIdByToken = "";

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
            IDToken token = kPrincipal.getKeycloakSecurityContext().getToken();
            userIdByToken = token.getSubject();
        }
        return userIdByToken;
    }

    public String getToken(){
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Principal principal = (Principal) authentication.getPrincipal();

        String token = null;

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
             token = kPrincipal.getKeycloakSecurityContext().getTokenString();
        }
        return token;
    }

    public List<RoleRepresentation> getRealmRolesById(String userId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String url2 = keycloakAuthUrl+"/admin/realms/"+realm+"/users/"+userId+"/role-mappings/realm";
        String token = getToken(adminUsername,adminPassword).get("tokenAccess");
        headers.add("Authorization","Bearer "+ token);
        ResponseEntity<RoleRepresentation[]> response = new RestTemplate().exchange(
                url2, HttpMethod.GET, new HttpEntity<Object>(headers),
                RoleRepresentation[].class);
        return Arrays.asList(response.getBody());
    }


    public ResponseEntity affectRealmRoles(String userId,String role,String token) throws JsonProcessingException {
        List<RoleRepresentation> realmRoles = getRealmRolesById(userId);
        RestTemplate restTemplate = new RestTemplate();
        if (!realmRoles.stream().anyMatch(o -> o.getName().equals(role))) {
            if (getRealmRoleByName(role,token) != null) {
                List<RoleRepresentation> rolesList = new ArrayList<>();
                rolesList.add(getRealmRoleByName(role,token));
                MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

                headers.add("Authorization", "Bearer " + token);
                headers.add("Content-Type", "application/json"); // Set the content type
                HttpEntity<List<RoleRepresentation>> requestEntity = new HttpEntity<>(rolesList, headers);
                restTemplate.exchange(
                        keycloakAuthUrl+"/admin/realms/"+realm+"/users/"+userId+"/role-mappings/realm", HttpMethod.POST, requestEntity, RoleRepresentation[].class);

            //    keycloakRestTemplate.postForEntity(keycloakAuthUrl+"/admin/realms/"+realm+"/users/"+userId+"/role-mappings/realm",rolesList, RoleRepresentation[].class);
            } else {
                return ResponseEntity.ok("You need the realm role "+Roles.manageUsers);
            }
        }
        return ResponseEntity.ok("Realm role affected");
    }

    public RoleRepresentation getRealmRoleByName(String name,String token){
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        RestTemplate restTemplate = new RestTemplate();
        headers.add("Authorization", "Bearer " + token);
        ResponseEntity<RoleRepresentation[]> response = restTemplate.exchange(
                keycloakAuthUrl+"/admin/realms/"+realm+"/roles", HttpMethod.GET, new HttpEntity<>(headers), RoleRepresentation[].class);
        for (RoleRepresentation roleRepresentation : response.getBody()){
            if (roleRepresentation.getName().equals(name)){
                return roleRepresentation;
            }
        }
        return null;
    }

    public String deleteUser(String userId, String token) {
        try {
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            RestTemplate restTemplate = new RestTemplate();
            headers.add("Authorization", "Bearer " + token);

            // First, make a DELETE request to delete the user
            ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                    keycloakAuthUrl + "/admin/realms/" + realm + "/users/" + userId,
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class
            );

            if (deleteResponse.getStatusCode().is2xxSuccessful()) {
                return "success";
            } else {
                // Handle the case where the delete request was not successful
                return "delete_failed";
            }
        } catch (RestClientException e) {
            // Handle exceptions that may occur during the HTTP request
            e.printStackTrace();
            return "error";
        }
    }


    public String updateUser(UserRepresentation userRepresentation) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        RestTemplate restTemplate = new RestTemplate();
        headers.add("Authorization", "Bearer " + getToken());
        // Assuming you have your authentication headers properly set

        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(userRepresentation, headers);

        // The URL should include the user's ID that you want to update
        String userUpdateUrl = keycloakAuthUrl + "/admin/realms/" + realm + "/users/" + userRepresentation.getId();

        ResponseEntity<Void> responseEntity = restTemplate.exchange(userUpdateUrl, HttpMethod.PUT, requestEntity, Void.class);

        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            return "success";
        } else {
            return "update_failed";
        }
    }
//  first request to get the token
    public ResponseEntity<?> forgetPassword(String email) {
        try {
            Utilisateur utilisateurFinded = utilisateurRepository.findByEmailLower(email.toLowerCase());

            if (utilisateurFinded == null) {
                return new ResponseEntity<>("User with email: " + email + " not found", HttpStatus.NOT_FOUND);
            }
            if(utilisateurFinded.getCollaborateur().getCategorie().equals(CategorieCollaborateurEnum.COMMERCIAL)){
            Context context = new Context();
            context.setVariable("userName", utilisateurFinded.getCollaborateur().getNom());
            context.setVariable("login", utilisateurFinded.getCollaborateur().getSignature());
            context.setVariable("resetToken", jwtTokenUtil.generateToken(utilisateurFinded.getRefKUser()));
            context.setVariable("url",url);
            mailService.envoyerEmailHTML(List.of(email),"Réinitialisation de votre mot de passe VENGA RAM","emailForgetPasswordTemplate", context);
            }else{
                Context context = new Context();
                context.setVariable("userName", utilisateurFinded.getCollaborateur().getNom());
                context.setVariable("login", utilisateurFinded.getCollaborateur().getEmail());
                context.setVariable("resetToken", jwtTokenUtil.generateToken(utilisateurFinded.getRefKUser()));
                context.setVariable("url",url);
                mailService.envoyerEmailHTML(List.of(email),"Réinitialisation de votre mot de passe VENGA RAM","emailForgetPasswordTemplate", context);

            }
            return new ResponseEntity<>("Password reset email sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error sending reset password email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
// second request to update the password
    public ResponseEntity<?> resetPassword(ResetPasswordDTO resetPasswordDto) {
        String token = jwtTokenUtil.getApiKeyFromHeader();
        if (token == null) {
            return new ResponseEntity<>("Le jeton n'est pas valide", HttpStatus.BAD_REQUEST);
        }
        if (!jwtTokenUtil.validateToken(token)) {
            return new ResponseEntity<>("cette session est invalide ou expirée", HttpStatus.BAD_REQUEST);
        }

        Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByRefKUser(claims.get("id").toString());

        if (utilisateurOptional.isEmpty()) {
            return new ResponseEntity<>("Jeton invalide ou utilisateur introuvable", HttpStatus.BAD_REQUEST);
        }

        String password = resetPasswordDto.getPassword();
        if (!isPasswordValid(password)) {
            return new ResponseEntity<>("Le mot de passe doit contenir :\n" +
                    " - Au moins une lettre minuscule.\n" +
                    " - Au moins une lettre majuscule.\n" +
                    " - Au moins un chiffre.\n" +
                    " - Au moins un caractère non alphanumérique.\n" +
                    " - Longueur minimale de 8 caractères et maximale de 30 caractères.", HttpStatus.BAD_REQUEST);
        }

        try {
            String adToken = getToken(adminUsername, adminPassword).get("tokenAccess");
            createPasswordKeycloack(utilisateurOptional.get().getRefKUser(), utilisateurOptional.get().getEmail(), password, adToken);
            return new ResponseEntity<>("Mot de passe a réinitialisé", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Quelque chose s'est mal passé lors de la réinitialisation du mot de passe.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,30}$";
        return Pattern.matches(passwordPattern, password);
    }



  /*  public void createRole(String roleName) {
        RolesResource rolesResource = realmResource.roles();
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(roleName);
        rolesResource.create(roleRepresentation);
    }

    public void assignRoleToUser(String userId, String roleName) {
        UsersResource usersResource = realmResource.users();
        UserRepresentation user = usersResource.get(userId).toRepresentation();
        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Arrays.asList(role));
    }*/


    public void createReferencialUsersInKeycloak() {
        List<Collaborateur> collaborateurCommerciaux = collaborateurRepository.getCollaborateursByCategorie(CategorieCollaborateurEnum.COMMERCIAL);
        System.out.println("Nombre des agents dans la Bdd : " + collaborateurCommerciaux.size());

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .clientId(client_id)
                .clientSecret(secret)
                .build();

        for (Collaborateur agent : collaborateurCommerciaux) {
            if (agent.getEmail() != null && !agent.getEmail().isEmpty()) {
                //String username = agent.getEmail().split("@", 2)[0];
                String email = agent.getEmail();
                String signature = agent.getSignature();

                try {
                    if(signature == null || signature.isEmpty()){
                        System.out.println("Aucune signature n'a été trouvée pour cet agent commercial");
                    }
                    else {
                        List<UserRepresentation> keycloakUsersByUsername = keycloak.realm(realm).users().search(signature,true);
                        if (!keycloakUsersByUsername.isEmpty()) {
                            System.out.println("Cet utilisateur existe déjà sur Keycloak : " + keycloakUsersByUsername.get(0).getUsername());
                        } else {
                                UserRepresentation keycloakUser = new UserRepresentation();
                                keycloakUser.setFirstName(agent.getPrenom());
                                keycloakUser.setLastName(agent.getNom());
                                keycloakUser.setUsername(signature);
                                keycloakUser.setEmail(agent.getEmail());

                                // Créer l'utilisateur dans Keycloak
                                try  {
                                    Response response = keycloak.realm(realm).users().create(keycloakUser);
                                    if (response.getStatus() == 201) {
                                        String keycloakUserId = CreatedResponseUtil.getCreatedId(response);

                                        // Affecter un rôle à l'utilisateur
                                        String role = "agent";
                                        RoleRepresentation roleRepresentation = keycloak.realm(realm).roles().get(role).toRepresentation();
                                        keycloak.realm(realm).users().get(keycloakUserId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));

                                        // Activer l'utilisateur
                                        UserResource userResource = keycloak.realm(realm).users().get(keycloakUserId);
                                        UserRepresentation updatedUser = userResource.toRepresentation();
                                        updatedUser.setEnabled(true);
                                        userResource.update(updatedUser);

                                        // Affecter un mot de passe aléatoire
                                        String randomPassword = Util.generateRandomPassword(12);
                                        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                                        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                                        credentialRepresentation.setValue(randomPassword);

                                        Utilisateur utilisateur = new Utilisateur();
                                        utilisateur.setEmail(agent.getEmail());
                                        utilisateur.setActive(true);
                                        utilisateur.setLogin(agent.getEmail());
                                        utilisateur.setProfil(ProfilEnum.REPRESENTANT);
                                        utilisateur.setCollaborateur(agent);
                                        utilisateur.setRefKUser(keycloakUserId);
                                        utilisateur.setNewsLetterAccepted(false);
                                        utilisateur.setCondGeneralAccepted(false);
                                        utilisateur.setDateCreated(OffsetDateTime.now());
                                        utilisateurRepository.save(utilisateur);
                                        System.out.println("Username : " + signature + " Mot de passe : " + randomPassword);

                                        userResource.resetPassword(credentialRepresentation);
                                        System.out.println("L'utilisateur avec signature "+ signature + " a bien été ajouté sur Keycloak");
                                    } else {
                                        System.out.println("Échec de la création de l'utilisateur sur Keycloak. Code de statut : " + response.getStatus());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.out.println("Erreur lors de la création de l'utilisateur sur Keycloak : " + e.getMessage());
                                }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Erreur lors de la recherche de l'utilisateur sur Keycloak : " + e.getMessage());
                }
            } else {
                System.out.println("Impossible de créer un compte pour un utilisateur n'ayant pas d'e-mail");
            }
        }
    }

    public void deleteReferencialUsersInKeycloak() {
        List<Collaborateur> collaborateurCommerciaux = collaborateurRepository.getCollaborateursByCategorie(CategorieCollaborateurEnum.COMMERCIAL);
        System.out.println("Nombre des agents dans la Bdd : " + collaborateurCommerciaux.size());

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .clientId(client_id)
                .clientSecret(secret)
                .build();
        for (Collaborateur agent : collaborateurCommerciaux) {
            if (agent.getEmail() != null && !agent.getEmail().isEmpty()) {
                //String username = agent.getEmail().split("@", 2)[0];
                String email = agent.getEmail();
                String signature = agent.getSignature();

                try {
                    if(signature == null || signature.isEmpty()){
                        System.out.println("Aucune signature n'a été trouvée pour cet agent commercial");
                    }
                    else {
                        List<UserRepresentation> keycloakUsersByUsername = keycloak.realm(realm).users().search(signature.trim());
                        if (!keycloakUsersByUsername.isEmpty()) {

                            // Créer l'utilisateur dans Keycloak
                            try  {
                                if(agent.getUtilisateur()!=null){
                                    if(bonCommandeRepository.existsByAgentCommercial(agent.getUtilisateur())){
                                        bonCommandeRepository.delete(bonCommandeRepository.findByAgentCommercial(agent.getUtilisateur()));
                                    }

                                    if(auditUserActionRepository.existsByUser(agent.getUtilisateur())){
                                        auditUserActionRepository.delete(auditUserActionRepository.findByUser(agent.getUtilisateur()));
                                    }
                                    utilisateurRepository.deleteById(agent.getUtilisateur().getId());
                                    List<UserRepresentation> keycloakUser = keycloak.realm(realm).users().search(signature);
                                    if(keycloakUser != null && keycloakUser.size()>0){
                                        Response response = keycloak.realm(realm).users().delete(agent.getUtilisateur().getRefKUser());
                                        if (response.getStatus() == 201) {
                                            System.out.println("Cet est supprimer");

                                        }
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                             }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Erreur lors de la recherche de l'utilisateur sur Keycloak : " + e.getMessage());
                }
            } else {
                System.out.println("Impossible de créer un compte pour un utilisateur n'ayant pas d'e-mail");
            }
        }

    }
    public void createReferencialUsersNotCommercialInKeycloak() {
        List<Collaborateur> collaborateurCommerciaux = collaborateurRepository.getCollaborateursNotCommercial();
        System.out.println("Nombre des agents dans la Bdd : " + collaborateurCommerciaux.size());

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakAuthUrl)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .clientId(client_id)
                .clientSecret(secret)
                .build();

        for (Collaborateur agent : collaborateurCommerciaux) {
            if (agent.getEmail() != null && !agent.getEmail().isEmpty()) {

                try {

                        List<UserRepresentation> keycloakUsersByUsername = keycloak.realm(realm).users().search(agent.getEmail(),true);
                        if (!keycloakUsersByUsername.isEmpty()) {
                            System.out.println("Cet utilisateur existe déjà sur Keycloak : " + keycloakUsersByUsername.get(0).getUsername());
                        } else {
                            UserRepresentation keycloakUser = new UserRepresentation();
                            keycloakUser.setFirstName(agent.getPrenom());
                            keycloakUser.setLastName(agent.getNom());
                            keycloakUser.setUsername(agent.getEmail());
                            keycloakUser.setEmail(agent.getEmail());

                            // Créer l'utilisateur dans Keycloak
                            try  {
                                Response response = keycloak.realm(realm).users().create(keycloakUser);
                                if (response.getStatus() == 201) {
                                    String keycloakUserId = CreatedResponseUtil.getCreatedId(response);

                                    // Affecter un rôle à l'utilisateur
                                    RoleRepresentation roleRepresentation = keycloak.realm(realm).roles().get(getRoleKeycloack(agent.getCategorie())).toRepresentation();
                                    keycloak.realm(realm).users().get(keycloakUserId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));

                                    // Activer l'utilisateur
                                    UserResource userResource = keycloak.realm(realm).users().get(keycloakUserId);
                                    UserRepresentation updatedUser = userResource.toRepresentation();
                                    updatedUser.setEnabled(true);
                                    userResource.update(updatedUser);

                                    // Affecter un mot de passe aléatoire
                                    String randomPassword = Util.generateRandomPassword(12);
                                    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                                    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                                    credentialRepresentation.setValue(randomPassword);

                                    Utilisateur utilisateur = new Utilisateur();
                                    utilisateur.setEmail(agent.getEmail());
                                    utilisateur.setActive(true);
                                    utilisateur.setLogin(agent.getEmail());
                                    utilisateur.setProfil(roleByUserNotCommercial(agent.getCategorie()));
                                    utilisateur.setCollaborateur(agent);
                                    utilisateur.setRefKUser(keycloakUserId);
                                    utilisateur.setNewsLetterAccepted(false);
                                    utilisateur.setCondGeneralAccepted(false);
                                    utilisateur.setDateCreated(OffsetDateTime.now());
                                    utilisateurRepository.save(utilisateur);
                                    System.out.println("Username : " + agent.getEmail() + " Mot de passe : " + randomPassword);

                                    userResource.resetPassword(credentialRepresentation);
                                    System.out.println("L'utilisateur avec signature "+ agent.getEmail()+ " a bien été ajouté sur Keycloak");
                                } else {
                                    System.out.println("Échec de la création de l'utilisateur sur Keycloak. Code de statut : " + response.getStatus());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("Erreur lors de la création de l'utilisateur sur Keycloak : " + e.getMessage());
                            }
                        }
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Erreur lors de la recherche de l'utilisateur sur Keycloak : " + e.getMessage());
                }
            } else {
                System.out.println("Impossible de créer un compte pour un utilisateur n'ayant pas d'e-mail");
            }
        }
    }

    public ProfilEnum roleByUserNotCommercial(CategorieCollaborateurEnum categorieCollaborateur){
        ProfilEnum role = null;
        if( categorieCollaborateur.equals(CategorieCollaborateurEnum.RATTACHE)){
            role = ProfilEnum.RATTACHE_COMMERCIAL;
        }
        else if(categorieCollaborateur.equals(CategorieCollaborateurEnum.TECHNIQUE)){
            role= ProfilEnum.SUPER_ADMIN;
        }
        else if(categorieCollaborateur.equals(CategorieCollaborateurEnum.FONCTIONNEL)){
            role = ProfilEnum.ADMIN_FONCTIONNEL;
        }
        else if(categorieCollaborateur.equals(CategorieCollaborateurEnum.CONSULTANT)){
            role = ProfilEnum.CONSULTANT;
        }
        return role;

    }

    public String getRoleKeycloack(CategorieCollaborateurEnum categorieCollaborateur){
        String roleASupprimer = null;
        if( categorieCollaborateur.equals(CategorieCollaborateurEnum.RATTACHE)){
            roleASupprimer = "Rattache";
        }
        else if(categorieCollaborateur.equals(CategorieCollaborateurEnum.TECHNIQUE)){
            roleASupprimer= "Admin technique";
        }
        else if(categorieCollaborateur.equals(CategorieCollaborateurEnum.FONCTIONNEL)){
            roleASupprimer = "Admin Fonctionnel";
        }
        else if(categorieCollaborateur.equals(CategorieCollaborateurEnum.CONSULTANT)){
            roleASupprimer = "Consultant";
        }
        return roleASupprimer;
    }

    public String refreshToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", client_id);
        body.add("client_secret", secret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        String url = UriComponentsBuilder.fromHttpUrl(keycloakAuthUrl)
                .pathSegment("realms", realm, "protocol", "openid-connect", "token")
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // Handle the response as needed
        return response.getBody();
    }
}
