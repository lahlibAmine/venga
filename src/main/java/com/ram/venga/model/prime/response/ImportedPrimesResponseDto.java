package com.ram.venga.model.prime.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImportedPrimesResponseDto {
    @JsonProperty("Origine d'émission")
    private String origineEmission;
    @JsonProperty("Segment")
    private String segment;
    @JsonProperty("Classe de produit")
    private String classeProduit;
    @JsonProperty("Nombre de points")
    private Integer nbrPoint;
}
