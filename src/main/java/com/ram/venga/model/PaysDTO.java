package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaysDTO {

    private Long id;

    @NotNull
    @Size(max = 8)
    private String codeIso;

    @NotNull
    @Size(max = 64)
    private String nom;

}
