package com.ram.venga.model;

import com.ram.venga.model.enumeration.EtatBonCommandeEnum;
import com.ram.venga.model.enumeration.StatutBAEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
public class LigneCommandeValidateurDto {
    private Long id;
    private String nomAgent;
    private String nomAgence;
    private OffsetDateTime date;
    private String fournisseur;
    private String cadeau;
    private Integer nbrPoint;
    private StatutBAEnum etat;
    private Integer quantite;
    private String nomPortfeuille;
    private String nomRepresentation;
    private Integer resultat;
    private String signature;

}
