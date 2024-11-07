package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CollaborateurSearchDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String agenceNom;
    private String agenceCode;
    private String portfeuilleNom;
    private String representationNom;
    private LocalDate dateCreation;
    private String statut;
    private String signature;


}
