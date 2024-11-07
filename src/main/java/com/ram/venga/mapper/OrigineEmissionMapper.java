package com.ram.venga.mapper;

import com.ram.venga.model.OrigineEmissionDeviseAddDto;
import com.ram.venga.model.OrigineEmissionDeviseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Pays;
import com.ram.venga.model.OrigineEmissionDTO;

@Mapper(componentModel = "spring")
public interface OrigineEmissionMapper extends EntityMapper<OrigineEmissionDTO, OrigineEmission> {

    @Mapping(target = "pays", source = "pays.id")
    OrigineEmissionDTO toDto(OrigineEmission origineEmission);
	@Mapping(target = "pays", source = "pays")
	@Mapping(target = "devise", source = "devise")
	@Mapping(target = "nom", source = "nom")
	OrigineEmissionDeviseDTO toDtoOrigine(OrigineEmission origineEmission);

	@Mapping(target = "nom", source = "nom")
	@Mapping(target = "pays.id", source = "pays")
	@Mapping(target = "devise.id", source = "devise")
	OrigineEmission toEntity(OrigineEmissionDeviseAddDto origineEmissionDeviseAddDto);

    default Pays mapPays(Long paysId) {
		if (paysId == null) {
			return null;
		}

		Pays pays = new Pays();
		pays.setId(paysId);

		return pays;
	}
}
	
