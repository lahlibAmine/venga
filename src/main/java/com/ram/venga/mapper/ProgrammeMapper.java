package com.ram.venga.mapper;

import org.mapstruct.Mapper;

import com.ram.venga.domain.Programme;
import com.ram.venga.model.ProgrammeDTO;


@Mapper(componentModel = "spring")
public interface ProgrammeMapper extends EntityMapper<ProgrammeDTO, Programme> {
	
}