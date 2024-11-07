package com.ram.venga.model;

import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.CiviliteEnum;
import com.ram.venga.model.enumeration.ProfilEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@Setter
public class ProfileDto {


    private Long id;
    @NotBlank
    @NotNull
    private String nom;
    @NotBlank
    @NotNull
    private String prenom;
    @NotBlank
    @NotNull
    @Email
    private String email;

    @NotNull
    private CategorieCollaborateurEnum profil;

    @NotNull
    private CiviliteEnum civilite;

    private Long belongTo;
    private List<Long> belongToRepresentation;

}
