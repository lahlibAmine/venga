package com.ram.venga.model;

import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.StatutDemandeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ValidationDto {
    private Long id;
    private StatutDemandeEnum status;
    private String commentaire;
    private Set<Utilisateur> validateurs;

}
