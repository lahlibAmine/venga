package com.ram.venga.model;

import com.ram.venga.model.enumeration.StatutRapprochementEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class RecetteBruteRapprochementDTO {

    private Long id;
    private LocalDate dateTransport;
    private Boolean statut;
    private String numCoupon;
}
