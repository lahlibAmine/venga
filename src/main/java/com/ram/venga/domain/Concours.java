package com.ram.venga.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
public class Concours {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "concours_sequence",
            sequenceName = "concours_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "concours_sequence"
    )
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String libelle;

    private LocalDate dateDebut;

    private LocalDate dateFin;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_emission_id")
    private OrigineEmission origineEmission;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

    private Float facteurPromotion;

    private Float psMajoration;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "concours_classe_produit",
            joinColumns = @JoinColumn(name = "concours_id"),
            inverseJoinColumns = @JoinColumn(name = "classe_produit_id")
    )
    private Set<ClasseProduit> classeProduits;
    public Long getIdOrigineEmission(){
        return origineEmission.getId();
    }
    private LocalDate dateDebutVente;

    private LocalDate dateFinVente;
}
