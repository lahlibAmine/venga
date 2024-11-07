package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.ram.venga.model.enumeration.StatutBAEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class LigneCommandeDto {
    @NotNull
    @Size(max = 16)
    private String reference;

    @NotNull
    private StatutBAEnum etat;

    @NotNull @Positive
    private Integer nbrPointCredit;

    private String fournisseur;
    private Long id;

    @Positive @NotNull
    private Integer quantite;
    private Long cadeauxBADTO;
    private String nomCadeau;
    private Long bonCommandeDTO;
    private OffsetDateTime dateCreated;
    private OffsetDateTime lastUpdated;
    private Integer resultat;
    private Long idCollaborateur;


}
