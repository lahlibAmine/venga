package com.ram.venga.domain;

import java.time.OffsetDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ram.venga.model.enumeration.ProfilEnum;

import lombok.Getter;
import lombok.Setter;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Utilisateur {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "utilisateur_sequence",
            sequenceName = "utilisateur_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "utilisateur_sequence"
    )
    private Long id ;

    @Column(unique = true)
    private String refKUser;

    @Column(nullable = false, unique = true, length = 32)
    private String login;

    @Column(nullable = false, unique = true, length = 64)
    private String email;

    @Transient
    private String tokent;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfilEnum profil;

    @Column(nullable = false)
    private Boolean condGeneralAccepted;

    @Column(nullable = false)
    private Boolean newsLetterAccepted;

    @JsonIgnore
    @OneToMany(mappedBy = "utilisateur")
    private Set<Notification> notifications;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collaborateur_id", nullable = false, unique = true)
    private Collaborateur collaborateur;

    //@OneToMany(mappedBy = "agentCommercial")
    //private Set<BonCommande> bonCommandes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

    private String password;

    @Column(name = "last_otp")
    private String lastOTP;

    @Column(name = "last_otp_is_valid")
    private Boolean lastOtpIsValid;

    @Column(name = "otp_creation_date_time")
    private OffsetDateTime otpCreationDateTime;


    public Long getIdCollaborateur(){
        return collaborateur.getId();
    }

    private boolean desactivation;

}

