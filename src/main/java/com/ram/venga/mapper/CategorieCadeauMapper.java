package com.ram.venga.mapper;

import org.mapstruct.Mapper;

import com.ram.venga.domain.CategorieCadeau;
import com.ram.venga.model.CategorieCadeauDTO;


@Mapper(componentModel = "spring")
public interface CategorieCadeauMapper extends EntityMapper<CategorieCadeauDTO, CategorieCadeau> {
	
}
