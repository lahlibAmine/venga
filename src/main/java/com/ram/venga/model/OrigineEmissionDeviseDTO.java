package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
public class OrigineEmissionDeviseDTO {
    private Long id;
    @NotNull
    @Size(max = 16)
    private String nom;

    private PaysDTO pays;
    private DeviseDto devise;
    private Integer nbrPointBienvenue;

}
