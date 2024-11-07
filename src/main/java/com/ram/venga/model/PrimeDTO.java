package com.ram.venga.model;

import javax.validation.constraints.NotNull;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.domain.Segment;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PrimeDTO {

    private Long id;

    @NotNull
    private Integer nbrPoint;

    private Long origineEmission;

    private Segment segment;

    private ClasseProduitDTO classeProduit;
    private String nomOrigine;
    private String nomClasseProduit;

}
