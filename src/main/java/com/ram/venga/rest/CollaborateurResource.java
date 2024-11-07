package com.ram.venga.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Vente;
import com.ram.venga.model.*;
import com.ram.venga.service.AuthService;
import com.ram.venga.service.CollaborateurService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/collaborateurs", produces = "application/json")
public class CollaborateurResource {

    private final CollaborateurService collaborateurService;
    private final AuthService authService;


    public CollaborateurResource(final CollaborateurService collaborateurService, AuthService authService) {
        this.collaborateurService = collaborateurService;
        this.authService = authService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CollaborateurDTO>>  getAllCollaborateurs() {
        return ResponseEntity.ok(collaborateurService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CollaborateurSearchDTO>> getAllAgentsWithSearch(@RequestParam String keyword,Pageable pageable) {
        return ResponseEntity.ok(collaborateurService.findAllWithSearch(keyword,pageable));
    }

    @PostMapping("/update-status/update")
    public ResponseEntity<Void> updateStatus(@RequestBody CollaborateurChangeStatusDTO  collaborateurChangeStatusDTO) {
        collaborateurService.updateStatus(collaborateurChangeStatusDTO);
        return ResponseEntity.created(null).build();
    }

    @PostMapping("/update-infos/update")
    public ResponseEntity<Void> updateAgentInfo(@RequestBody AgentUpdateInfos agentUpdateInfos) {
        collaborateurService.updateAgentInfo(agentUpdateInfos);
        return ResponseEntity.created(null).build();
    }


    @GetMapping
    public ResponseEntity<CollaborateurDTO> getCollaborateur(
            ) {
        return ResponseEntity.ok(collaborateurService.get());
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateCollaborateur(
            @RequestBody @Valid final CollaborateurDTO collaborateurDTO,@RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        collaborateurService.update( collaborateurDTO, authorizationHeader);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCollaborateur(@PathVariable(name = "id") final Long id) {
        collaborateurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/solde")
    public Map<String,Integer> getNbrPointBYCollaborateur(
            @PathVariable(name = "id") final Long id) {
        return collaborateurService.getNbrPointBYCollaborateur(id);
    }

    @GetMapping("/solde")
    public Page<CollaborateurDTO>  getTotalVenteByAllCollaborateur( Pageable pageable , @RequestParam String keyWord,@RequestParam String portfeuille,@RequestParam String representation
    ) {
        return collaborateurService.TotalVenteByAllCollaborateur( pageable,keyWord,portfeuille,representation);
    }

    @GetMapping(path = "/{id}/forgetedPassword")
    public ResponseEntity forgetedPasssword(@PathVariable(name = "id") final Long id, @RequestHeader("Authorization") String authorizationHeader) throws  UnsupportedEncodingException, JsonProcessingException, javax.mail.MessagingException {
        return authService.generatePassword(id,authorizationHeader);
    }

    @PostMapping("/updatePassword/update")
    public ResponseEntity<Long> updatePassword(
            @RequestBody ChangePassDto changePassDto) throws JsonProcessingException {
        final Long updateSignature = collaborateurService.updatePassword(changePassDto.getOldPass(),changePassDto.getNewPass());
        return new ResponseEntity<>(updateSignature, HttpStatus.CREATED);
    }

    @GetMapping("/graph/chiffre-affaire")
    public List<ChiffreAffaireDto> chiffreAffaireTotalgraph() {
        return collaborateurService.getChiffreAffaireByAgent();
    }
    @GetMapping("/total-chiffre-affaire-point")
    public Map<String , Double> chiffreAffaireTotal() {
        return collaborateurService.getTotalChiffreAffaire();
    }

    @GetMapping("/sendEmail")
    public void sendEmail(@RequestParam String sujet) throws MessagingException, UnsupportedEncodingException {
         collaborateurService.sendEmail(sujet);
    }

    @GetMapping("/Administration")
    public  ResponseEntity<?> getAllAgent(@RequestParam String keyword,Pageable pageable){
        return collaborateurService.getAllAgentWithPage(keyword,pageable);
    }

    @PostMapping("/Administration/update")
    public  ResponseEntity<?> updateAgent(@RequestBody AdministartionAgentDto administartionAgentDto){
        return collaborateurService.updateAgent(administartionAgentDto);
    }

    @PostMapping("/accounts/create")
    public ResponseEntity<?> createAccountAdminTech(@RequestBody @Valid final ProfileDto postedData) throws JsonProcessingException, UnsupportedEncodingException, javax.mail.MessagingException {
        return collaborateurService.createAccount(postedData);
    }

    @GetMapping("/accounts")
    public  ResponseEntity<Page<CollaborateurUserDTO>> getAllProfiles(@RequestParam String keyword, Pageable pageable){
        return collaborateurService.getAllProfilesWithPage(keyword,pageable);
    }

    @PostMapping("/delete/accounts/{id}")
    public void deleteProfile(@PathVariable(name = "id") final Long id){
        collaborateurService.deleteProfile(id);
    }

    @PostMapping("/accounts/update")
    public ResponseEntity<CollaborateurDTO> updateProfile(@RequestBody @Valid final ProfileDto postedData) throws JsonProcessingException, UnsupportedEncodingException, javax.mail.MessagingException {
        return collaborateurService.updateProfile(postedData);
    }
    @PostMapping("/email")
    public void email(@RequestParam String Email) throws MessagingException, UnsupportedEncodingException {
         collaborateurService.envoyerEmail(Email);
    }

    @PostMapping("/agent")
    public ResponseEntity<String> updateAgentPersonalInfo(@RequestBody @Valid UpdateAgentPersonalInfosDTO agentDTO) {
        return collaborateurService.updateAgentPersonalInfo(agentDTO);
    }

    @GetMapping("/rattache/agents")
    public ResponseEntity<Page<CollaborateurSearchDTO>> getAllAgentsByAuthenticatedRattache(@RequestParam String keyword,Pageable pageable) {
        return ResponseEntity.ok(collaborateurService.agentsByAuthenticatedRattache(keyword,pageable));
    }

    @GetMapping("/consultant/agents")
    public ResponseEntity<Page<CollaborateurSearchDTO>> getAllAgentsByAuthenticatedConsultant(@RequestParam String keyword,Pageable pageable) {
        return ResponseEntity.ok(collaborateurService.agentsByAuthenticatedConsultant(keyword,pageable));
    }

    @GetMapping("/admin/nombre-de-points/export")
    public void exportNombreDePointsToExcelAdmins(HttpServletResponse response, String keyWord, String representation, String portefeuille) {
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=collaborators.xlsx");
        collaborateurService.exportNombreDePointsToExcelAdmins(response, keyWord, representation, portefeuille);
    }

    @GetMapping("/Agents/export")
    public void getAllAgentsWithSearchExport(HttpServletResponse response,@RequestParam String keyword) {
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Agents.xlsx");
         collaborateurService.findAllWithSearchExport(response,keyword);
    }

    @GetMapping("/debit-credit")
    public Map<String,Double> getDebitCredit(){
        return collaborateurService.getDebitCredit();
    }

}
