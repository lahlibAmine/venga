package com.ram.venga.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SegmentDTO {

    private Long id;

    @Size(max = 16)
    private String code;

    @NotNull
    @Size(max = 16)
    private String escaleDepart;

    @NotNull
    @Size(max = 16)
    private String escaleDestination;

}
