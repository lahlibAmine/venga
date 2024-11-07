package com.ram.venga.mapper;

import com.ram.venga.domain.Entite;
import com.ram.venga.model.EntiteCollaborateurUserDto;
import com.ram.venga.model.UtilisateurCollaborateurUserDTO;
import com.ram.venga.model.UtilisateurDemandeInscriptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.UtilisateurDTO;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface UtilisateurMapper extends EntityMapper<UtilisateurDTO, Utilisateur> {
	@Mapping(target = "idUser", source = "id")
	@Mapping(target = "collaborateur", source = "collaborateur.id")
	@Mapping(target = "idEntite", source = "collaborateur.entite.id")
	@Mapping(target = "nom", source = "collaborateur.nom")
	@Mapping(target = "prenom", source = "collaborateur.prenom")
	@Mapping(target = "civilite", source = "collaborateur.civilite")
	@Mapping(target = "signature", source = "collaborateur.signature")
	@Mapping(target = "officeId", source = "collaborateur.entite.officeId")
	@Mapping(target = "codeIata", source = "collaborateur.entite.code")
	@Mapping(target = "representations", source = "collaborateur.entites", qualifiedByName ="getIdRepresentation" )

	UtilisateurDTO toDto(Utilisateur utilisateur);

	UtilisateurCollaborateurUserDTO toDtoCollaborateur(Utilisateur utilisateur);

	Utilisateur toEntity(UtilisateurDemandeInscriptionDTO utilisateurDTO);

	default Collaborateur mapCollaborateur(Long collaborateurId) {
		if (collaborateurId == null) {
			return null;
		}

		Collaborateur collaborateur = new Collaborateur();
		collaborateur.setId(collaborateurId);
	
		return collaborateur;
	}
	@Named("getIdRepresentation")
	default List<Long> getIdRepresentation (Set<Entite> entites) {
		return entites.stream().map(Entite::getId).toList();
	}
}