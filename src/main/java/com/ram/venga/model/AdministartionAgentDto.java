package com.ram.venga.model;

import com.ram.venga.domain.Entite;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministartionAgentDto {
    private Long id;
    private String telephone;
    private String nom;
    private String email;
    private EntiteNomUdpadeDto entite;
    private String signature;
}
