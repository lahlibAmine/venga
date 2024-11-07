package com.ram.venga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.domain.DemandeInscription;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.mapper.UtilisateurMapper;
import com.ram.venga.model.UtilisateurDTO;
import com.ram.venga.model.securite.AuthRequestDto;
import com.ram.venga.model.securite.AuthResponseDto;
import com.ram.venga.repos.AuditUserActionRepository;
import com.ram.venga.repos.CollaborateurRepository;
import com.ram.venga.repos.DemandeInscriptionRepository;
import com.ram.venga.repos.UtilisateurRepository;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.credentials.secret}")
    private String secret;
    @Value("${keycloak.resource}")
    private String client_id;
    private static final String LOGOUT_ENDPOINT = "/realms/%s/protocol/openid-connect/logout";

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;

    private final MailService mailService;
    private final DemandeInscriptionService demandeInscriptionService;
    private final CollaborateurRepository collaborateurRepository;

    private final KeycloackService keycloackService;
    private final DemandeInscriptionRepository demandeInscriptionRepository;
    public AuthService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper, MailService mailService, DemandeInscriptionService demandeInscriptionService, CollaborateurRepository collaborateurRepository, KeycloackService keycloackService, DemandeInscriptionRepository demandeInscriptionRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.mailService = mailService;
        this.demandeInscriptionService = demandeInscriptionService;
        this.collaborateurRepository = collaborateurRepository;
        this.keycloackService = keycloackService;
        this.demandeInscriptionRepository = demandeInscriptionRepository;
    }

    public ResponseEntity login(AuthRequestDto authRequest, String keycloakAuthUrl, String realm, String clientId, String secret) {
        AuthResponseDto authRequestDto = new AuthResponseDto();
        Utilisateur user = utilisateurRepository.findBySignatureLower(authRequest.getEmail().trim().toLowerCase());
        AuthzClient authzClient = AuthzClient.create(new Configuration(keycloakAuthUrl,realm,clientId,new HashMap<>(){{put("secret",secret);}},null));
        AccessTokenResponse response = new AccessTokenResponse();
        // check if user Activated
        if(!user.getActive()){
            return new ResponseEntity<>("Votre compte n'est pas activé", HttpStatus.BAD_REQUEST);
        }
        try{
            response = authzClient.obtainAccessToken(authRequest.getEmail().trim().toLowerCase(), authRequest.getPassword());
        }catch (Exception e){
            return new ResponseEntity<>("votre email ou mot de passe est incorrect", HttpStatus.BAD_REQUEST);

        }
      //  DemandeInscription demandeInscription = demandeInscriptionRepository.findByCollaborateurId(user.getCollaborateur().getId());
       UtilisateurDTO userDto= utilisateurMapper.toDto(user);
        userDto.setDesactivation(user.isDesactivation());
        userDto.setTokent(response.getToken());
        userDto.setRefreshToken(response.getRefreshToken());
        authRequestDto.setUserDto(userDto);
        return ResponseEntity.ok(authRequestDto);
    }

    public ResponseEntity generatePassword(Long id,String authorizationHeader) throws  UnsupportedEncodingException, JsonProcessingException, javax.mail.MessagingException {
        String newPassword = demandeInscriptionService.generateRandomPassword();
        String nom = collaborateurRepository.findById(id).get().getNom();
        String prenom = collaborateurRepository.findById(id).get().getPrenom();
        String email = demandeInscriptionService.generateNewPassword(collaborateurRepository.findById(id).get().getEmail(),newPassword).getBody().getEmail();
      String refKUser =  utilisateurRepository.findByEmail(email).getRefKUser();
        String randomPassword = demandeInscriptionService.generateRandomPassword();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        RestTemplate restTemplate = new RestTemplate();
        mailService.envoyerEmailActivationCompte(email,email,Character.toUpperCase(prenom.charAt(0)) + prenom.substring(1)+" "+Character.toUpperCase(nom.charAt(0)) + nom.substring(1),randomPassword);
        CredentialRepresentation crAdmin = new CredentialRepresentation();
        crAdmin.setType("password");
        crAdmin.setValue(randomPassword);
        crAdmin.setTemporary(false);
        UserRepresentation adminRepresentation = new UserRepresentation();
        adminRepresentation.setCredentials(Arrays.asList(crAdmin));
        keycloackService.keycloakUserResponse(adminRepresentation,refKUser,keycloackService.extractBearerToken(authorizationHeader));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public ResponseEntity<String> logout(String refreshToken) {
        ResponseEntity<String> response = null;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", client_id);
        params.add("client_secret", secret);
        params.add("refresh_token", refreshToken);

        String logoutUrl = String.format(keycloakAuthUrl + LOGOUT_ENDPOINT, realm);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
         response = restTemplate.exchange(logoutUrl, HttpMethod.POST, entity, String.class);
         if(response.getStatusCode().is2xxSuccessful()){
             return ResponseEntity.ok("Success");
         }else{
             return ResponseEntity.status(response.getStatusCode()).build();
         }

      /*  if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("test");
        }*/
    }
}

