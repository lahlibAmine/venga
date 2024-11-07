	package com.ram.venga.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.domain.LigneCommande;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.EtatBonCommandeEnum;


import com.ram.venga.model.enumeration.StatutBAEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


    @Getter
@Setter
public class BonCommandeDTO {

    private Long id;

    @NotNull
    @Size(max = 16)
    private String reference;

    private LocalDateTime date;

    @NotNull
    private StatutBAEnum etat;

    @NotNull
    private Integer nbrPointCredit;

    private Long agentCommercial;

    private Set<LigneCommandeDto> ligneCommande;

    private Set<OpperationDTO> opperation;


}
