package com.ram.venga.model;

import com.ram.venga.model.enumeration.CategorieEntiteEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EntiteDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String code;

    @NotNull
    @Size(max = 64)
    private String nom;

    @NotNull
    private CategorieEntiteEnum categorie;

    @Size(max = 255)
    private String adresse;

    @Size(max = 16)
    private String telephone;

    @Size(max = 16)
    private String codePostal;

    @Size(max = 16)
    private String fax;

    @Size(max = 64)
    private String email;

    private Long parent;

    private Long origineEmission;
    private String nomOrigine;
    private Long ville;
    private String nomVille;

    private Long representation;
    private String nomRepresentation;
    private String nomParent;
    private String officeId;
    private List<UsersAgenceDto> usersAgenceDto;

}
