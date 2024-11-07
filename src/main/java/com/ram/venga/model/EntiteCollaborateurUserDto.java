package com.ram.venga.model;

import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Ville;
import com.ram.venga.model.enumeration.CategorieEntiteEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntiteCollaborateurUserDto {
    private Long id;
    private String nom;
    private String code;
    private CategorieEntiteEnum categorie;
}
