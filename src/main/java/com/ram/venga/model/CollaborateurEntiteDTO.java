package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
public class CollaborateurEntiteDTO {

    @NotNull
    @Size(max = 16)
    private String codeIATA;

    private String nomAgence;
    private String nomAgent;

    @NotNull
    @Size(max = 32)
    private String signatureAgent;
    private Integer chifferAffaire;
    private String adresseAgence;
    private String emailAgence;
    private String emailAgent;
    private String teleAgence;
    private String mobileAgent;

}
