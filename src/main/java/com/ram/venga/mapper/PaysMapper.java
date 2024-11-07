package com.ram.venga.mapper;

import org.mapstruct.Mapper;

import com.ram.venga.domain.Pays;
import com.ram.venga.model.PaysDTO;


@Mapper(componentModel = "spring")
public interface PaysMapper extends EntityMapper<PaysDTO, Pays> {
	
}
