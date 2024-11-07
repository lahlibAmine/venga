package com.ram.venga.mapper;

import com.ram.venga.domain.LigneCommande;
import com.ram.venga.model.LigneCommandeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.domain.ClasseReservation;
import com.ram.venga.model.ClasseReservationDTO;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClasseReservationMapper extends EntityMapper<ClasseReservationDTO, ClasseReservation> {
	
	@Mapping(target = "classeProduit", source = "classeProduit.id")
	ClasseReservationDTO toDto(ClasseReservation classeReservation);

	default ClasseProduit mapClasseProduit(Long classeProduitId) {
		if (classeProduitId == null) {
			return null;
		}

		ClasseProduit classeProduit = new ClasseProduit();
		classeProduit.setId(classeProduitId);

		return classeProduit;
	}
}