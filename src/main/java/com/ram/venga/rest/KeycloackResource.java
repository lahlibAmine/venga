package com.ram.venga.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.service.KeycloackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/keycloack", produces = "application/json")
public class KeycloackResource {
    KeycloackService keycloackService;
    public KeycloackResource(KeycloackService keycloackService){
        this.keycloackService = keycloackService;
    }

    @PostMapping("/tokenAdmin")
    public Map<String,String> getTokenAdmin(@Value("admin") String  adminUsername , @Value("rwLnYIQYFnzwnQtNEwc1") String  adminPassword) throws JsonProcessingException {
        return keycloackService.getToken(adminUsername,adminPassword);
    }

    @PostMapping("/create-referencial-users")
    public String createReferencialUsersInKeycloak() {
        // Récupérer les collaborateurs avec la catégorie "COMMERCIAL" et créer leurs comptes au niveau de keycloak
        keycloackService.createReferencialUsersInKeycloak();
        System.out.println("Test");
        return "Utilisateurs créés avec succès dans Keycloak.";
    }

    @PostMapping("/delete-referencial-users")
    public String deleteReferencialUsersInKeycloak() {
        // Récupérer les collaborateurs avec la catégorie "COMMERCIAL" et créer leurs comptes au niveau de keycloak
        keycloackService.deleteReferencialUsersInKeycloak();
        System.out.println("Test");
        return "Utilisateurs créés avec succès dans Keycloak.";
    }

    @PostMapping("/create-referencial-users-not-commercial")
    public String createReferencialUsersInKeycloakNotCoomercial() {
        // Récupérer les collaborateurs avec la catégorie Not "COMMERCIAL" et créer leurs comptes au niveau de keycloak
        keycloackService.createReferencialUsersNotCommercialInKeycloak();
        System.out.println("Test");
        return "Utilisateurs créés avec succès dans Keycloak.";
    }
    // Reset the password Impl
    //@PostMapping("/forget-password")
    //public ResponseEntity<?> forgetPasswordImpl(@RequestBody NewPasswordDTO newPasswordDTO) {
    //    return keycloackService.forgetPasswordImpl(newPasswordDTO);
    //}
   /* @PostMapping("/roles")
    public void createRole(@RequestBody String roleName) {
        keycloackService.createRole(roleName);
    }*/

}
