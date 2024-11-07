package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ram.venga.domain.Devise;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrigineEmissionDTO {

    private Long id;

    @NotNull
    @Size(max = 16)
    private String nom;

    @NotNull
    private Integer nbrPointBienvenue;

    private Long pays;
    private Devise devise;

}
