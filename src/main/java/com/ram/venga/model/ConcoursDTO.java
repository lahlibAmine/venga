package com.ram.venga.model;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.ram.venga.util.DateRangeValid;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@DateRangeValid
public class ConcoursDTO {

    private Long id;

    @NotNull
    @Size(max = 32)
    private String libelle;

    @NotNull
    private LocalDate dateDebut;

    @NotNull
    private LocalDate dateFin;

    private Long origineEmission;

    private Set<Long> classeProduit;

    private Float facteurPromotion;

    private Float psMajoration;

    private List<String> classeProduitCode;
    private String classeReservationCode;
    private String nomOrigineEmission;

        private LocalDate dateDebutVente;

        private LocalDate dateFinVente;
}
