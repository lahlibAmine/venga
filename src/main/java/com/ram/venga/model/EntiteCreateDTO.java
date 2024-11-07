package com.ram.venga.model;

import com.ram.venga.model.enumeration.CategorieEntiteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
public class EntiteCreateDTO {

    private Long id;



    @NotNull
    @Size(max = 64)
    private String nom;

    @NotNull
    private CategorieEntiteEnum categorie;

    private Integer nbrPointBienvenue;

}
