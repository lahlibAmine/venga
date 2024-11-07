package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OpperationTraiterGraphDto {

   // private Map<Year,Map<Month,CalculatorDto>> yearMonthCalcul;

    private Year year;
    private List<CalculatorDto> data;

//    private List<OpperationDTO> opperationDTOS;

}
