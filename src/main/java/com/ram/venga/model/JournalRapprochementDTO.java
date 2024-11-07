package com.ram.venga.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ram.venga.domain.RecetteBrute;
import com.ram.venga.model.enumeration.StatutRapprochementEnum;

import javax.validation.constraints.NotNull;

import com.ram.venga.model.enumeration.StatutVenteEnum;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class JournalRapprochementDTO {


    @NotNull
    private LocalDate dateRapprochement;

   // @NotNull
    private StatutVenteEnum statutVente;
    private String numBillet;

    private VenteRapprochementDTO venteRapprocheDto;

    private List<RecetteBruteRapprochementDTO> recetteRapprochertDto;


}
