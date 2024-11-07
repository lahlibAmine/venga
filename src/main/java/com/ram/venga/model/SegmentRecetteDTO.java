package com.ram.venga.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SegmentRecetteDTO {
    @NotNull
    @Size(max = 16)
    private String escaleDepart;

    @NotNull
    @Size(max = 16)
    private String escaleDestination;

    @NotNull
    @Size(max = 32)
    private String numCoupon;

    private LocalDateTime dateTransport;

    private Boolean recetteRapproche;

    private Boolean recetteIntegre;

    private double montantBrut;

    private String numVol;

    private String classReservation;

    private Integer pointGagne;
    private String motif;
    private String classProduit;
    private String codeIATA;
    private String numBillet;


}
