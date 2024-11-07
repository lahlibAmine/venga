package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


@Getter
@Setter
public class OrigineEmissionDeviseAddDto {
    private Long id;
    @NotNull
    private String nom;

    private Long pays;
    private Long devise;
    private Integer nbrPointBienvenue;

}
