package com.ram.venga.model;

import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.CiviliteEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollaborateurUserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private CiviliteEnum civilite;
    private CategorieCollaborateurEnum categorie;
    private EntiteCollaborateurUserDto entite;
    private List<EntiteCollaborateurUserDto> entiteRepresentations;
    private UtilisateurCollaborateurUserDTO utilisateur;
}
