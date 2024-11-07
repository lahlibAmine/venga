package com.ram.venga.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.model.enumeration.StatutBilletEnum;

import com.ram.venga.model.enumeration.StatutDemandeEnum;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ram.venga.model.enumeration.StatutVenteEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VenteDTO {

    private Long id;

    @NotNull
    @Size(max = 64)
    private String numBillet;

    @NotNull
    private StatutBilletEnum statutBillet;

    @NotNull
    @Size(max = 16)
    private String codeIATA;

    @NotNull
    @Size(max = 32)
    private String signatureAgent;

    @Size(max = 32)
    private String cieTitre;

    @Size(max = 32)
    private String cieVol;

    @NotNull
    @Size(max = 16)
    private String classeReservation;

    private LocalDateTime dateEmission;


    @NotNull
    private Long nbrPoint;

    @NotNull
    private Boolean venteIntgre;

    @NotNull
    private Boolean venteRapproche;

    private OrigineEmissionSignatureDto origine;

    private Long collaborateur;

    private double montantBrut;
    private String officeId;
    private String pnr;

    private String nomAgent;
    private String nomAgence;

    private String mobileAgent;
    private String emailAgent;

    private String teleAgence;
    private String adresseAgence;
    private Integer chifferAffaire;
    private String emailAgence;
    private String nomRepresentation;
    private String nomPortfeuille;

    List<SegmentRecetteDTO> segmentDTOList;

    @Column(nullable = false, length = 32)
    private Integer nbrCoupon;
    private StatutVenteEnum statutVente;
    private String motif;
    private String nomAgenceAgent;

    private OffsetDateTime dateCreated;

    public VenteDTO( String numBillet, String pnr,Integer nbrCoupon,LocalDateTime dateEmission,String signatureAgent, String codeIATA,String nomAgence, Long nbrPoint,Boolean venteIntgre, Boolean venteRapproche, String nomAgent,  String emailAgent,String mobileAgent,StatutVenteEnum statutVente, String nomAgenceAgent,String nomRepresentation, String nomPortfeuille,OffsetDateTime dateCreated) {
        this.numBillet = numBillet;
        this.codeIATA = codeIATA;
        this.signatureAgent = signatureAgent;
        this.dateEmission = dateEmission;
        this.nbrPoint = nbrPoint;
        this.venteIntgre = venteIntgre;
        this.venteRapproche = venteRapproche;
        this.pnr = pnr;
        this.nomAgent = nomAgent;
        this.nomAgence = nomAgence;
        this.mobileAgent = mobileAgent;
        this.emailAgent = emailAgent;
         this.nbrCoupon = nbrCoupon;
        this.statutVente = statutVente;
        this.nomAgenceAgent = nomAgenceAgent;
        this.nomRepresentation = nomRepresentation;
        this.nomPortfeuille = nomPortfeuille;
        this.dateCreated = dateCreated;

    }


}
