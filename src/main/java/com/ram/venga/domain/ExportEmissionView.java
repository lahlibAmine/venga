package com.ram.venga.domain;

import com.ram.venga.model.enumeration.StatutVenteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ExportEmissionView {

    @Id
    private Long id;
    private String numBillet;
    private Integer nbrCoupon;
    private String officeId;
    private String pnr;
    private String nomCollaborateur;
    private String signature;
    private String nomEntite;
    private Integer debit;
    @Enumerated(EnumType.STRING)
    private StatutVenteEnum statutVente;
    private String cieVol;
    private String numCoupon;
    private LocalDateTime dateTransport;
    private LocalDateTime dateEmission;
    private String escaleDepart;
    private String escaleArrivee;
    private String classeReservation;
    private Double montantBrut;
    @ManyToOne(fetch = FetchType.EAGER)
    private Entite entite;
    private String categorie;
    @ManyToOne(fetch = FetchType.EAGER)
    private Collaborateur collaborateur;
    private String codeIATA;
    private String motif_vente;
    private String motif_recette;


}