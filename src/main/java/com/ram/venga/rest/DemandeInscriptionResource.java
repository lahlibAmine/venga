package com.ram.venga.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.model.*;
import com.ram.venga.model.enumeration.StatutDemandeEnum;
import com.ram.venga.model.enumeration.TypeDemandeEnum;
import com.ram.venga.service.DemandeInscriptionService;
import javax.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import javax.mail.MessagingException;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/demandeInscriptions",produces = "application/json")
public class DemandeInscriptionResource {

    private final DemandeInscriptionService demandeInscriptionService;

    public DemandeInscriptionResource(final DemandeInscriptionService demandeInscriptionService) {
        this.demandeInscriptionService = demandeInscriptionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeInscriptionDTO> getDemandeInscription(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(demandeInscriptionService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createDemandeInscription(
            @RequestBody @Valid final DemandeInscriptionDTO demandeInscriptionDTO) {
        final Long createdId = demandeInscriptionService.create(demandeInscriptionDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateDemandeInscription(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DemandeInscriptionDTO demandeInscriptionDTO) {
        demandeInscriptionService.update(id, demandeInscriptionDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDemandeInscription(@PathVariable(name = "id") final Long id) {
        demandeInscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validation/update")
    public Map<String, StatutDemandeEnum> updateStatusRattache(@RequestBody ValidationDto validation) throws  UnsupportedEncodingException,  JsonProcessingException, javax.mail.MessagingException {
        return demandeInscriptionService.updateStatus(validation);
    }

    @GetMapping("")
    public ResponseEntity getDemandesByStatus(@RequestParam("status") StatutDemandeEnum statutDemandeEnum, @RequestParam String keyword, @RequestParam("typeDemande") TypeDemandeEnum typeDemande, Pageable pageable) {
        return demandeInscriptionService.getDemandeByStatus(statutDemandeEnum,keyword,typeDemande, pageable);
    }

    @GetMapping("/historique")
    public Page<DemandeInscriptionDTO> gethistoriqueRattache(Pageable pageable,@RequestParam String keyword, @RequestParam("typeDemande") TypeDemandeEnum typeDemande) {
        return demandeInscriptionService.gethistoriqueRattache(pageable,keyword,typeDemande);
    }

    @GetMapping("/evolutionInscription")
    public List<InscriptionEvolutionDto>getEvolutionInscription(@RequestParam String dateDebut,
                                                                @RequestParam String dateFin) {
        return demandeInscriptionService.getEvolutionInscription(dateDebut,dateFin);
    }

    @PostMapping("/update/update-status")
    public ResponseEntity<String> updateStatusDesactivationCompte(@RequestBody CollaborateurChangeStatusDTO collaborateurChangeStatusDTO){
        return demandeInscriptionService.updateStatusDesactivationCompte(collaborateurChangeStatusDTO);
    }

    @PostMapping("/update/validation-desactivation")
    public void updateValidationDesactivationCompte(@RequestBody ValidationDto validationDto){
        demandeInscriptionService.updateValidationDesactivationCompte(validationDto);
    }
}

