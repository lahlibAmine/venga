package com.ram.venga.model;

import com.ram.venga.model.enumeration.CategorieEntiteEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


@Getter
@Setter
public class EntiteCreateListDTO {

    private String nom;
    private String code ;
    private List<String> nomUser;
    private List<String> prenomUser;

}
