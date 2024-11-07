package com.ram.venga.model;

import com.ram.venga.domain.CadeauxBA;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
public class CadeauxBADTO {

    private Long id;

    private String code;

    @NotNull
    private Integer nbrPoint;

    @Size(max = 255)
    private String description;

    private Long origineEmission;

    private String nomOrigine;

    private Long fournisseur;

    private String nomFournisseur;

    @NotNull
    private Long categorieCadeau;

    private String nomCategorieCadeau;

    private Set<LigneCommandeDto> ligneCommande;
    private DeviseDto devise;


}
