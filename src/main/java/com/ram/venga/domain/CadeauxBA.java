package com.ram.venga.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ram.venga.model.CadeauxBADTO;
import javax.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CadeauxBA {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "cadeaux_ba_sequence",
            sequenceName = "cadeaux_ba_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cadeaux_ba_sequence"
    )
    private Long id;


    @Column(nullable = false)
    private Integer nbrPoint;

    @Column
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_emission_id")
    private OrigineEmission origineEmission;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_cadeau_id", nullable = false)
    private CategorieCadeau categorieCadeau;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;
    @JsonIgnore
    @OneToMany(mappedBy = "cadeauxBA", fetch = FetchType.EAGER)
    private Set<LigneCommande> ligneCommandes;

    public Long getIdOrigineEmission(){
        return origineEmission.getId();
    }
    public Long getIdFournisseur(){
        return fournisseur.getId();
    }
    public Long getIdCategorieCadeau(){
        return categorieCadeau.getId();
    }

}

