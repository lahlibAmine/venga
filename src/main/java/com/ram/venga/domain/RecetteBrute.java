package com.ram.venga.domain;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class RecetteBrute {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "recette_brute_sequence",
            sequenceName = "recette_brute_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "recette_brute_sequence"
    )
    private Long id;

    @Column(nullable = false, length = 16)
    private String numBillet;

    @Column( length = 32)
    private String origineEmission;

    @Column(nullable = false, length = 32)
    private String codeIATA;

    @Column( length = 32)
    private String signatureAgent;

    @Column
    private LocalDateTime dateEmission;

    @Column
    private LocalDateTime dateTransport;

    @Column(nullable = false, length = 8)
    private String escaleDepart;

    @Column(nullable = false)
    private String escaleArrivee;

    @Column
    private Boolean recetteRapproche;

    @Column
    private Boolean recetteIntegre;

    @Column(length = 32)
    private String cieTitre;

    @Column(nullable = false, length = 32)
    private String numCoupon;

    @Column(length = 32)
    private String cieVol;

    @Column( length = 16)
    private String classeReservation;

    @Column( length = 16)
    private String classeProduit;

    private double montantBrut;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;
    private String motif;
    private String codeIATAVente;

    private boolean isArchived;
}
