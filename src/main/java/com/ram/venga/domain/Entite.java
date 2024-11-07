package com.ram.venga.domain;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ram.venga.model.enumeration.CategorieEntiteEnum;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Entite {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "entite_sequence",
            sequenceName = "entite_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "entite_sequence"
    )
    private Long id;

    @Column(nullable = false, length = 255)
    private String code;

    @Column(nullable = false, length = 64)
    private String nom;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategorieEntiteEnum categorie;

    @Column
    private String adresse;

    @Column(length = 16)
    private String telephone;

    @Column(length = 16)
    private String codePostal;

    @Column(length = 16)
    private String fax;

    @Column(length = 64)
    private String email;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Entite parent;

    @JsonIgnore
    @OneToMany(mappedBy = "entite", fetch =FetchType.EAGER )
    private Set<Collaborateur> collaborateurs;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_emission_id")
    private OrigineEmission origineEmission;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ville_id")
    private Ville ville;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    private String officeId;

    @ManyToMany(mappedBy = "entites")
    private Set<Collaborateur> collaborateurSet = new HashSet<>();


    @LastModifiedDate
    private OffsetDateTime lastUpdated;
    public Long getIdOrigineEmission(){
        return origineEmission.getId();
    }
    public Long getIdVille(){
        return ville.getId();
    }

}

