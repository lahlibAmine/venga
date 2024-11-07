package com.ram.venga.domain;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

import com.ram.venga.model.enumeration.StatutVenteEnum;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ram.venga.model.enumeration.StatutBilletEnum;

import lombok.Getter;
import lombok.Setter;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Vente {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "vente_sequence",
            sequenceName = "vente_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "vente_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String numBillet;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutBilletEnum statutBillet;

    @Column(nullable = true)
    private String codeIATA;

    @Column(nullable = false)
    private String signatureAgent;

    private String cieTitre;

    private String cieVol;

    private String numCoupon;

    private String classeReservation;

    private String classeProduit;

    @Column
    private LocalDateTime dateEmission;

    @Column
    private LocalDateTime dateTransport;

    private String escaleDepart;

    private String escaleArrivee;

    private Integer nbrPoint;

    @Column(nullable = false)
    private Boolean venteIntgre;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean venteRapproche;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "origine_emission_id")
    private OrigineEmission origineEmission;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collaborateur_id")
    private Collaborateur collaborateur;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    private OffsetDateTime lastUpdated;

    @Column(nullable = false, length = 32)
    private Integer nbrCoupon;

    @Column(nullable = false, length = 32)
    private Integer nbrCouponNonRapprocher;


    private Integer montantBrut;

    private String officeId;

    private String pnr;
    @Transient
    private Long idOrigineEmission;
    @Transient
    private Long idCollaborateur;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatutVenteEnum statutVente;

    private String motif;
    private String qualification;
    private String codeIataUpd;
    private String codeIataCre;
    private String codeIataOwn;
    private String officeIdUpd;
    private String officeIdCre;
    private String officeIdOwn;
    private String codeIataInHouse;
    private boolean isArchived;


}
