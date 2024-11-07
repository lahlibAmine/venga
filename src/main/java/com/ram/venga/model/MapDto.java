package com.ram.venga.model;

import java.util.Map;

import com.ram.venga.domain.Entite;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapDto {
	
	 private Map<String, String> data;

}
