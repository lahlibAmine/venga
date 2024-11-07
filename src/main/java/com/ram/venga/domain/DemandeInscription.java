package com.ram.venga.domain;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ram.venga.model.enumeration.TypeDemandeEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ram.venga.model.enumeration.StatutDemandeEnum;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class DemandeInscription {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "demande_inscription_sequence",
            sequenceName = "demande_inscription_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "demande_inscription_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateDemande;

    @Column
    private LocalDateTime dateValidationRattache;

    @Column
    private LocalDateTime dateValidationAdminF;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutDemandeEnum statut;

    @Column
    private String commentaire;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collaborateur_id", nullable = false, unique = true)
    private Collaborateur collaborateur;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "validation_inscription",
            joinColumns = @JoinColumn(name = "demande_inscription_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> validateurs;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;


    private LocalDateTime dateRefusR;

    private LocalDateTime dateRefusAF;

    @Column
    private LocalDateTime dateModification;
    @Enumerated(EnumType.STRING)
    private TypeDemandeEnum typeDemande;

}
