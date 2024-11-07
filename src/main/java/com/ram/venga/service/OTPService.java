package com.ram.venga.service;

import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.AuditUserActionDTO;
import com.ram.venga.model.UtilisateurDemandeInscriptionDTO;
import com.ram.venga.model.enumeration.AuditActionEnum;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.repos.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class OTPService {

    @Value("${otp.validity-duration-minutes}")
    private long otp_duration;
    private final MailService mailService;
    private final AuditUserActionService auditUserActionService;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final KeycloackService keycloackService;

    public OTPService(MailService mailService, AuditUserActionService auditUserActionService, UtilisateurService utilisateurService, UtilisateurRepository utilisateurRepository, KeycloackService keycloackService) {
        this.mailService = mailService;
        this.auditUserActionService = auditUserActionService;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.keycloackService = keycloackService;
    }

    public void generateOTPCodeAndSendIt(String subject, String actionText,Utilisateur user) throws MessagingException, UnsupportedEncodingException {
        String otpCode = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        processUserAction(subject,actionText,otpCode,user);
    }


    public void generateOTPCodeAndSendItByEmail(String subject, String actionText,Utilisateur user) throws MessagingException, UnsupportedEncodingException {
        String otpCode = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        processUserAction(subject,actionText,otpCode,user);
    }

    /**
     * Processes a user action involving OTP generation and sending.
     *
     * This method handles the necessary steps for generating and sending an OTP to a user,
     * including updating the user's OTP details, saving the updated user information to
     * the repository, creating an audit record of the action, and sending an OTP email
     * to the user.
     *
     * @param subject   the subject of the OTP email to be sent
     * @param actionText the text describing the action for which the OTP is generated
     * @param otpCode   the generated OTP code to be sent to the user
     * @param user      the user for whom the OTP is generated
     * @throws UnsupportedEncodingException if an encoding exception occurs while sending the email
     * @throws MessagingException if an error occurs while sending the email
     */
    private void processUserAction(String subject,
                                   String actionText,
                                   String otpCode,
                                   Utilisateur user)
            throws UnsupportedEncodingException, MessagingException {

        // Create an AuditUserActionDTO to record the OTP action for auditing purposes
        AuditUserActionDTO userActionDTO = AuditUserActionDTO.getInstance(
                AuditActionEnum.OTP,
                user.getId(),
                user.getId(),
                user.getCollaborateur() != null ? user.getEmail() : null,
                user.getLastOTP() != null ? user.getLastOTP() : "",
                otpCode
        );

        // Update the user object with the new OTP information
        user.setLastOTP(otpCode);
        user.setLastOtpIsValid(false);
        user.setOtpCreationDateTime(OffsetDateTime.now());

        // Save the updated user object to the repository
        utilisateurRepository.save(user);

        // Create an audit record of the OTP action
        auditUserActionService.create(userActionDTO);

        // Send the OTP email to the user
        mailService.SendOTPEmail(subject, actionText, otpCode, user.getEmail(),
                user.getCollaborateur() != null ? user.getCollaborateur().getNom() : null);
    }

    public Boolean validateOTPCode(String otpCode,Utilisateur user){
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime otpCreationDateTime = user.getOtpCreationDateTime();
        Duration duration = Duration.between(otpCreationDateTime, now);

        long minutesElapsed = duration.toMinutes();
        if (minutesElapsed > otp_duration) {
            return null;
        }
        if (otpCode.equals(user.getLastOTP())) {
            user.setLastOtpIsValid(true);
            return true;
        }else{
            return false;
        }
    }

}
