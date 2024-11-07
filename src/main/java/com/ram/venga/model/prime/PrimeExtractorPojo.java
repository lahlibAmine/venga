package com.ram.venga.model.prime;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PrimeExtractorPojo {

    @ExcelRow
    private long rowIndex;

    @ExcelCell(0)
    @NotEmpty(message = "Origine Emission cannot be empty")
    private String origineEmission;

    @ExcelCell(1)
    @NotEmpty(message = "Segment cannot be empty")
    private String segment;

    @ExcelCell(2)
    @NotEmpty(message = "Classe Produit cannot be empty")
    private String classeProduit;

    @ExcelCell(3)
    @NotEmpty(message = "Number of points cannot be empty")
    @Pattern(regexp = "\\d+", message = "Number of points must be a valid integer")
    private String nbrPoint;
}
