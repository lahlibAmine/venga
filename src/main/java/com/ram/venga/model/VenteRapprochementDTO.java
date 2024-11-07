package com.ram.venga.model;

import com.ram.venga.model.enumeration.StatutRapprochementEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class VenteRapprochementDTO {
    private Long id;
 //   private String numBillet;
    private Boolean statut;
    private Integer nbrCouponVenteNonRapproche;
    private Integer nbrCouponVente;
   // private StatutVenteEnum statutVente;

}
