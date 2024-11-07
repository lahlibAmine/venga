package com.ram.venga.model;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Entite;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class ExportEmissionDTO {

    private Long id;
    private String numBillet;
    private Integer nbrCoupon;
    private String officeId;
    private String pnr;
    private String nomCollaborateur;
    private String signature;
    private String nomEntite;
    private Integer debit;
    private StatutVenteEnum statutVente;
    private String cieVol;
    private String numCoupon;
    private LocalDateTime dateTransport;
    private String escaleDepart;
    private String escaleArrivee;
    private String classeReservation;
    private Double montantBrut;
    private Collaborateur collaborateur;
    private Entite entite;
    private LocalDateTime dateEmission;
    private  String codeIATA;
    private String motif_vente;
    private String motif_recette;

}
