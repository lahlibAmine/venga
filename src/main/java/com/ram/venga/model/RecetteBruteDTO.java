package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RecetteBruteDTO {

    private Long id;

    @NotNull
    @Size(max = 16)
    private String numBillet;

    @NotNull
    @Size(max = 16)
    private String origineEmission;

    @NotNull
    @Size(max = 32)
    private String codeIATA;

    @NotNull
    @Size(max = 32)
    private String signatureAgent;

    private LocalDateTime dateEmission;

    private LocalDateTime dateTransport;

    @NotNull
    @Size(max = 8)
    private String escaleDepart;

    @NotNull
    @Size(max = 255)
    private String escaleArrivee;

    private Boolean recetteRapproche;

    private Boolean recetteIntegre;

}
