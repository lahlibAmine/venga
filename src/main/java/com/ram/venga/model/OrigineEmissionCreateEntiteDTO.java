package com.ram.venga.model;

import com.ram.venga.domain.Devise;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
public class OrigineEmissionCreateEntiteDTO {

    private Long id;

    @NotNull
    @Size(max = 16)
    private String nom;


}
