package com.ram.venga.model;

import com.ram.venga.model.enumeration.CiviliteEnum;
import com.ram.venga.model.enumeration.ProfilEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UtilisateurDTO {

    private Long idUser;


    @NotNull
    @Size(max = 32)
    private String login;

    @NotNull
    @Size(max = 64)
    private String email;

    @NotNull
    private Boolean active;

    @NotNull
    private ProfilEnum profil;

    @NotNull
    private Boolean condGeneralAccepted;

    @NotNull
    private Boolean newsLetterAccepted;

    @NotNull
    private Long collaborateur;
    private Long idEntite;
    private String tokent;
    private String nom;
    private String prenom;
    private CiviliteEnum civilite;

    private String refKUser;
    private String signature;

    private String officeId;
    private String codeIata;

    private Boolean desactivation;
    private String refreshToken;
    private List<Long> representations;
}
