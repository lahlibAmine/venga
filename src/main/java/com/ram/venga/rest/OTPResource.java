package com.ram.venga.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.mapper.CollaborateurMapper;
import com.ram.venga.model.OtpRequestDTO;
import com.ram.venga.model.ValidateOtpByEmailDTO;
import com.ram.venga.model.enumeration.OTPTypeEnum;
import com.ram.venga.service.CollaborateurService;
import com.ram.venga.service.KeycloackService;
import com.ram.venga.service.OTPService;
import com.ram.venga.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/otp", produces = "application/json")
public class OTPResource {

    private final OTPService otpService;
    private final UtilisateurService utilisateurService;
    private final CollaborateurService collaborateurService;
    private final KeycloackService keycloackService;

    public OTPResource(OTPService otpService, UtilisateurService utilisateurService, CollaborateurService collaborateurService, KeycloackService keycloackService) {
        this.otpService = otpService;
        this.utilisateurService = utilisateurService;
        this.collaborateurService = collaborateurService;
        this.keycloackService = keycloackService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> sendOtp(@RequestBody @Valid OtpRequestDTO otpRequestDTO) throws MessagingException, UnsupportedEncodingException {
        Utilisateur user = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElseThrow();
        String subject = "Your One-Time Password (OTP) for ";
        String actionPurpose;

        OTPTypeEnum action = Objects.requireNonNull(otpRequestDTO.getAction());
        switch (action) {
            case UPDATE_PERSONAL_INFO -> {
                subject += "Updating Personal Information";
                actionPurpose = "update your personal information";
            }
            case DESACTIVATION_DEMAND -> {
                subject += "Deactivation Request";
                actionPurpose = "complete your deactivation request";
            }
            default -> throw new IllegalArgumentException("Type de OTP invalide");
        }
        otpService.generateOTPCodeAndSendIt(subject, actionPurpose, user);
        return ResponseEntity.ok("OTP envoyé avec succès");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateOtpByEmail(@RequestBody ValidateOtpByEmailDTO validateOtpByEmailDTO) throws JsonProcessingException, MessagingException, UnsupportedEncodingException {
        Utilisateur user = utilisateurService.findByEmail(validateOtpByEmailDTO.getEmail()).orElseThrow();
        Boolean isValid = otpService.validateOTPCode(validateOtpByEmailDTO.getOtpCode(),user);
        if (isValid == null){
            return ResponseEntity.badRequest().body("Échec de la demande d'inscription. Le code OTP fourni a expiré.");
        } else if (!isValid) {
            return ResponseEntity.badRequest().body("Échec de la demande d'inscription. Le code OTP fourni est incorrect.");
        } else {
            try {
                collaborateurService.createUserInkeyCloak(user.getCollaborateur(),user);
                return ResponseEntity.ok("Demande d'inscription complétée avec succès.");
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors du traitement de votre demande. Veuillez réessayer plus tard.");
            }
        }
    }

}
