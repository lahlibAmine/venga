package com.ram.venga.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Notification;
import com.ram.venga.model.enumeration.ProfilEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UtilisateurDemandeInscriptionDTO {

    private String email;

    private ProfilEnum profil;

    private Long collaborateur;

    private OffsetDateTime dateCreated;

    private OffsetDateTime lastUpdated;

    private String lastOTP;

    private Boolean lastOtpIsValid;
    private Boolean active;
    private OffsetDateTime otpCreationDateTime;
    private Boolean condGeneralAccepted;
    private Boolean newsLetterAccepted;
    private String login;
    private String signature;

}
