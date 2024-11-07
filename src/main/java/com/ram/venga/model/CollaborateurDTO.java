package com.ram.venga.model;

import java.time.LocalDate;

import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.CiviliteEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CollaborateurDTO {

    private Long idCollaborateur;

    @Size(max = 64)
    private String code;


    private String nomAgence;
    private String prenomAgence;
    private String prenomAgent;



    @Size(max = 64)
    private String nomAgent;

    private Integer nbrPoint;

    private String teleAgence;
    private String adresseAgence;
    private String emailAgence;
    private String mobileAgence;
    private String emailAgent;



    @Size(max = 64)
    private String signature;



    private Long entite;
    private LocalDate dateNaissance;

    private String fonction;

    private String adresseAgent;
    private String codePostal;
    private String telephone;
    private CategorieCollaborateurEnum categorie;
    private CiviliteEnum civilite;
    private String mobile;
    private Integer chiffreAffaire;
    private String officeId;
    private String ville;

}
