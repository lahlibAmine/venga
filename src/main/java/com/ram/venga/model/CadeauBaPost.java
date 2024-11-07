package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class CadeauBaPost {
    private Long id;
    private Long categorieCadeau;
    private Long devise;
    private Long fournisseur;
    private Long nbrPoint;
    private Long origineEmission;

}
