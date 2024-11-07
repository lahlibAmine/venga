package com.ram.venga.model;

import com.ram.venga.domain.Segment;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class PrimeIdSegmentDTO {

    private Long id;

    @NotNull
    private Integer nbrPoint;

    private Long origineEmission;

    private Long segment;

    private Long classeProduit;
    private String nomOrigine;
    private String nomClasseProduit;
    private String escalDepart;
    private String escalArriver;

}
