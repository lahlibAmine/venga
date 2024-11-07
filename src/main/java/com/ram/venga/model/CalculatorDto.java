package com.ram.venga.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Month;
import java.time.YearMonth;

@Getter
@Setter
public class CalculatorDto {
    private Month month;
    private Integer sumDebit;
}
