package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
public class OpperationTraiterDto {

    private Long id;
    private YearMonth yearMonth;
    private Integer sumDebit;
    private Integer sumCredit;
    private List<OpperationDTO> opperationDTOS;

}
