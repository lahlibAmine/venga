package com.ram.venga.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcelImportResponse {
    private int totalRowsProcessed;
    private int totalRowsPersisted;
    private String description;
}
