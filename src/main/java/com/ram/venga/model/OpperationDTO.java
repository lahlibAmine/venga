package com.ram.venga.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ram.venga.domain.BonCommande;
import com.ram.venga.domain.Vente;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OpperationDTO {

    private Long id;
    private LocalDate date;
    private Integer debit;
    private Integer credit;
    private Integer pointGagne;
    private Long recetteBrute;
    private Long bonCommande;
    private String origine;
    private String destination;
    private String classReservation;
    private String classProduit;
    private String numBillet;
    private LocalDate dateEmission;
    private String signature;
}
