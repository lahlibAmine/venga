package com.ram.venga.mapper;

import com.ram.venga.domain.Devise;
import com.ram.venga.model.DeviseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DevisesMapper extends EntityMapper<DeviseDto, Devise> {

    DeviseDto toDto(Devise devise);


}
