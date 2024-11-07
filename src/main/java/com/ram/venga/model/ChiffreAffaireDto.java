package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
@Getter
@Setter
public class ChiffreAffaireDto {
    private Long id;
    private String nom;
    private String prenom;
    @Size(max = 64)
    private String codeIATA;
    private String nomAgence;
    private Integer chiffreAffaire;
    private String nomPortefeuille;
    private String nomRepresentation;
}
