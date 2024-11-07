package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HauteSaisonDTO {

    private Long id;

    @NotNull
    @Size(max = 64)
    private String libelle;

    @NotNull
    private LocalDate dateDebut;

    @NotNull
    private LocalDate dateFin;

    private Long origineEmission;
    private String nomOrigine;
}
