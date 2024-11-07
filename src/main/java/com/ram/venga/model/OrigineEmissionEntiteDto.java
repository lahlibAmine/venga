package com.ram.venga.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ram.venga.domain.Entite;
import com.ram.venga.domain.OrigineEmission;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Getter
@Setter
public class OrigineEmissionEntiteDto {

    //private OrigineEmissionCreateEntiteDTO origineEmissionDto;


    private EntiteCreateDTO entiteDto;

    private Integer nbrPointBienvenue;
}
