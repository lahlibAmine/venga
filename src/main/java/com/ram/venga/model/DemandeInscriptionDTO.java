package com.ram.venga.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.model.enumeration.StatutDemandeEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DemandeInscriptionDTO {

    private Long id;

    @NotNull
    private LocalDateTime dateDemande;

    private LocalDateTime dateValidationRattache;

    private LocalDateTime dateValidationAdminF;

    @NotNull
    private StatutDemandeEnum statut;
    private String nom;
    private String prenom;
    private String email;

    @Size(max = 255)
    private String commentaire;

    private String emailCollaborateur;

    private String nomCollaborateur;
    private String prenomCollaborateur;

    private Map<ProfilEnum,String> validateur;
    private LocalDateTime dateValidation;

    private String agence;
    private String fonction;

}
