package com.ram.venga.mapper;

import com.ram.venga.model.PrimeIdSegmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Prime;
import com.ram.venga.domain.Segment;
import com.ram.venga.model.PrimeDTO;

import javax.validation.constraints.NotNull;


@Mapper(componentModel = "spring")
public interface PrimeMapper extends EntityMapper<PrimeDTO, Prime> {

	@Mapping(target = "origineEmission", source = "origineEmission.id")
	@Mapping(target = "segment", source = "segment")
	@Mapping(target = "classeProduit", source = "classeProduit")
	@Mapping(target = "nomClasseProduit", source = "classeProduit.libelle")
	@Mapping(target = "nomOrigine", source = "origineEmission.nom")
	PrimeDTO toDto(Prime prime);


	@Mapping(target = "origineEmission.id", source = "origineEmission")
	@Mapping(target = "segment.id", source = "segment")
	@Mapping(target = "classeProduit.id", source = "classeProduit")
	Prime toEntitWithId(PrimeIdSegmentDTO primeIdSegmentDTO);


	default OrigineEmission mapOrigineEmission(Long origineEmissionId) {
		if (origineEmissionId == null) {
			return null;
		}

		OrigineEmission origineEmission = new OrigineEmission();
		origineEmission.setId(origineEmissionId);

		return origineEmission;
	}
	
	default ClasseProduit mapClasseProduit(Long classeProduitId) {
		if (classeProduitId == null) {
			return null;
		}

		ClasseProduit classeProduit = new ClasseProduit();
		classeProduit.setId(classeProduitId);

		return classeProduit;
	}

}
