package com.ram.venga.mapper;

import com.ram.venga.domain.Opperation;
import com.ram.venga.model.OpperationDTO;
import com.ram.venga.model.OpperationTraiterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OpperationTraiterMapper extends EntityMapper<OpperationTraiterDto, Opperation>{

    @Mapping(target = "sumDebit", source = "opperation.debit")
    @Mapping(target = "sumCredit", source = "opperation.credit")
  //  @Mapping(target = "opperationDTOS", source = "opperation")
    OpperationTraiterDto toDto(Opperation opperation);

}
