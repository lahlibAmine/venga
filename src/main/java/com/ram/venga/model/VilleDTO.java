package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VilleDTO {

    private Long id;

    @NotNull
    @Size(max = 16)
    private String nom;

    @NotNull
    private Long pays;

}
