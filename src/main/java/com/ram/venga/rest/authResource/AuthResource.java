package com.ram.venga.rest.authResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.model.CollaborateurDTO;
import com.ram.venga.model.EntiteDTO;
import com.ram.venga.model.ResetPasswordDTO;
import com.ram.venga.model.securite.AuthRequestDto;
import com.ram.venga.service.AuthService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.ram.venga.service.CollaborateurService;
import com.ram.venga.service.EntiteService;
import com.ram.venga.service.KeycloackService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/auth", produces = "application/json")
public class AuthResource {
/*
    @Value("${keycloak.auth-server-url}")
    private String keycloakAuthUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloack.client.id}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String secret;
*/
@Value("${keycloak.auth-server-url}")
private String keycloakAuthUrl;
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.credentials.secret}")
    private String secret;
    @Value("${keycloak.resource}")
    private String clientId;
    /*
@Value("${keycloak.auth-server-url}")
private String keycloakAuthUrl;*/
private final HttpServletRequest request;

    private final AuthService authService;
    private final EntiteService entiteService;
    private final CollaborateurService collaborateurService;

    private final KeycloackService keycloackService;


    public AuthResource(HttpServletRequest request, AuthService authService, EntiteService entiteService, CollaborateurService collaborateurService, KeycloackService keycloackService) {
        this.request = request;
        this.authService = authService;

        this.entiteService = entiteService;
        this.collaborateurService = collaborateurService;
        this.keycloackService = keycloackService;
    }

    @PostMapping("/login")
    public ResponseEntity authenticateUser(@RequestBody AuthRequestDto authRequest){

        return authService.login(authRequest,keycloakAuthUrl,realm,clientId,secret);

    }
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestParam String refreshToken) throws Exception {
        // Clear the authentication session
      return  authService.logout(refreshToken);

        // Redirect to a post-logout page or any other page you prefer
      //  return "redirect:/logout-success"; // Replace with your desired URL
    }

    @GetMapping("/code")
    public EntiteDTO findByCodeIata(@RequestParam String code,@RequestParam(required = false) String officeId){
        return entiteService.findByCodeIata(code,officeId);
    }

    @PostMapping("/inscription")
    public ResponseEntity<Long> createCollaborateur(
            @RequestBody @Valid final CollaborateurDTO collaborateurDTO) throws JsonProcessingException, UnsupportedEncodingException, javax.mail.MessagingException {
        return collaborateurService.create(collaborateurDTO);
    }

    @PostMapping("/request-forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam String email) {
        return keycloackService.forgetPassword(email);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> passwordReset(@RequestBody @Valid ResetPasswordDTO resetPasswordDto) {
        return keycloackService.resetPassword(resetPasswordDto);
    }
    @PostMapping("/refresh-token")
    public String refreshToken(@RequestParam String refreshToken) {
        return keycloackService.refreshToken(refreshToken);
    }

}
