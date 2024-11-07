package com.ram.venga.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.Pays;
import com.ram.venga.domain.Ville;
import com.ram.venga.model.VilleDTO;


@Mapper(componentModel = "spring")
public interface VilleMapper extends EntityMapper<VilleDTO, Ville> {


    @Mapping(target = "pays", source = "pays.id")
    VilleDTO toDto(Ville ville);

    default Pays mapPays(Long paysId) {
		if (paysId == null) {
			return null;
		}

		Pays pays = new Pays();
		pays.setId(paysId);

		return pays;
	}
}
