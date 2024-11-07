package com.ram.venga.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.CiviliteEnum;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Collaborateur {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "collaborateur_sequence",
            sequenceName = "collaborateur_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "collaborateur_sequence"
    )
    private Long id;

    @Column(nullable = false,  length = 64)
    private String code;

    @Column(nullable = false, length = 64)
    private String nom;

    @Column(nullable = false, length = 64)
    private String prenom;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CiviliteEnum civilite;

    @Column( length = 64)
    private String signature;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategorieCollaborateurEnum categorie;

    @Column
    private String adresse;

    @Column(length = 16)
    private String codePostal;

    @Column
    private LocalDate dateNaissance;

    @Column(length = 32)
    private String mobile;

    @Column(length = 32)
    private String telephone;

    @Column(length = 64)
    private String fonction;
    @Column
    private Integer soldeExpired;
    @Column
    private Integer soldePoint;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entite_id", nullable = true)
    private Entite entite;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

    private String email;
    private Integer chiffreAffaire;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER,mappedBy = "collaborateur")
    private Utilisateur utilisateur;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "collaborateur_entite",
            joinColumns = {@JoinColumn(name = "collaborateur_id")},
            inverseJoinColumns = {@JoinColumn(name = "representation")})
    private Set<Entite> entites = new HashSet<>();
}

