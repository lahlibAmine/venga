package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class FournisseurDTO {

    private Long id;

    @NotNull
    @Size(max = 32)
    private String nom;

    @Size(max = 16)
   private String telephone;

    @Size(max = 64)
    private String email;

    @Size(max = 255)
    private String adresse;

    List<CadeauxBaByFournisseurDto>  cadeauxBAs;

    private String nomOrigine;

    private Long origineEmission;

}
