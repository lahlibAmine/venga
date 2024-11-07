package com.ram.venga.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Vente;
import com.ram.venga.model.VenteDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Mapper(componentModel = "spring")
public interface VenteMapper extends EntityMapper<VenteDTO, Vente> {

	@Mapping(target = "origine", source = "origineEmission")
	@Mapping(target = "collaborateur", source = "collaborateur.id")
	@Mapping(target = "nomAgent", source = "collaborateur.nom")
	@Mapping(target = "nomAgence", source = "collaborateur.entite.nom")
	@Mapping(target = "mobileAgent", source = "collaborateur.mobile")
	@Mapping(target = "emailAgent", source = "collaborateur.email")
	@Mapping(target = "teleAgence", source = "collaborateur.entite.telephone")
	@Mapping(target = "adresseAgence", source = "collaborateur.entite.adresse")
	@Mapping(target = "chifferAffaire", source = "collaborateur.chiffreAffaire")
	@Mapping(target = "emailAgence", source = "collaborateur.entite.email")
	@Mapping(target = "nomPortfeuille", source = "collaborateur.entite.parent.nom")
	@Mapping(target = "nomRepresentation", source = "collaborateur.entite.parent.parent.nom")
	@Mapping(target = "nbrPoint", source = "collaborateur.soldePoint")
	@Mapping(target = "dateEmission" , source = "vente.dateEmission")
	VenteDTO toDto(Vente vente);



	default OrigineEmission mapOrigineEmission(Long origineEmissionId) {
		if (origineEmissionId == null) {
			return null;
		}

		OrigineEmission origineEmission = new OrigineEmission();
		origineEmission.setId(origineEmissionId);

		return origineEmission;
	}
	
    default Collaborateur mapCollaborateur(Long collaborateurId) {
		if (collaborateurId == null) {
			return null;
		}

		Collaborateur collaborateur = new Collaborateur();
		collaborateur.setId(collaborateurId);
	
		return collaborateur;
	}

	@Mapping(target = "dateEmission", qualifiedByName = "toDtoDate")
	default LocalDate toDtoDate(LocalDateTime date) {
		if(date!= null){
			return date.toLocalDate();

		}
		return null;
	}
}