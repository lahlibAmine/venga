package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import com.ram.venga.model.enumeration.StatutBAEnum;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BonAchatDTO {

    private Long id;

    @NotNull
    @Size(max = 16)
    private String reference;

    private LocalDate dateReception;

    private LocalDate dateLivraison;

    @NotNull
    private StatutBAEnum statut;

    @NotNull
    private Long bonCommande;

}
