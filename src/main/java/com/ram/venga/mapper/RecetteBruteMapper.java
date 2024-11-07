package com.ram.venga.mapper;

import com.ram.venga.domain.Opperation;
import com.ram.venga.model.OpperationTraiterDto;
import org.mapstruct.Mapper;

import com.ram.venga.domain.RecetteBrute;
import com.ram.venga.model.RecetteBruteDTO;


@Mapper(componentModel = "spring")
public interface RecetteBruteMapper extends EntityMapper<RecetteBruteDTO, RecetteBrute> {
    RecetteBruteDTO toDto(RecetteBrute recetteBrute);


}
