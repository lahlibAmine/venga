package com.ram.venga.mapper;

import com.ram.venga.domain.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.model.ConcoursDTO;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface ConcoursMapper extends EntityMapper<ConcoursDTO, Concours> {

	@Mapping(target = "origineEmission", source = "origineEmission.id")
	@Mapping(target = "classeProduit", source = "classeProduits", qualifiedByName = "concoursListId")
	@Mapping(target = "classeProduitCode", source = "classeProduits", qualifiedByName = "concoursListCode")
	@Mapping(target = "nomOrigineEmission", source = "origineEmission.nom")
	ConcoursDTO toDto(Concours concours);

	@Mapping(target = "origineEmission.id", source = "origineEmission")
	Concours toEntity(ConcoursDTO concoursDTO);

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

	@Named("concoursListCode")
	default List<String> concoursListCode(Set<ClasseProduit> classeProduits) {
		if (classeProduits == null || classeProduits.isEmpty()) {
			return Collections.emptyList();
		}
		return classeProduits.stream().map(ClasseProduit::getCode).collect(Collectors.toList());
	}

	@Named("concoursListId")
	default Set<Long> concoursListId(Set<ClasseProduit> classeProduits) {
		if (classeProduits == null || classeProduits.isEmpty()) {
			return Collections.emptySet();
		}
		return classeProduits.stream().map(ClasseProduit::getId).collect(Collectors.toSet());
	}
}
