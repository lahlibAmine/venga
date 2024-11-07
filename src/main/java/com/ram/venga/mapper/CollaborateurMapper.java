package com.ram.venga.mapper;

import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Entite;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface CollaborateurMapper extends EntityMapper<CollaborateurDTO, Collaborateur> {

	EntiteMapper ENTITE_MAPPER = Mappers.getMapper(EntiteMapper.class);
	UtilisateurMapper UTILISATEUR_MAPPER = Mappers.getMapper(UtilisateurMapper.class);

	@Mapping(target = "nom", source = "collaborateur.nom")
	@Mapping(target = "prenom", source = "collaborateur.prenom")
	@Mapping(target = "codeIATA", source = "collaborateur.code")
	@Mapping(target = "nomAgence", source = "entite.nom")
	@Mapping(target = "nomPortefeuille", source = "entite.parent.nom")
	@Mapping(target = "nomRepresentation", source = "entite.parent" , qualifiedByName = "getParent")
	ChiffreAffaireDto toDtoChiffre(Collaborateur collaborateur);

	@Mapping(target = "entite", source = "entite.id")
	@Mapping(target = "code", source = "entite.code")
	@Mapping(target = "nomAgence", source = "entite.nom")
	@Mapping(target = "nomAgent", source = "collaborateur.nom")
	@Mapping(target = "nbrPoint", source = "collaborateur.soldePoint")
	@Mapping(target = "teleAgence", source = "entite.telephone")
	@Mapping(target = "adresseAgence", source = "entite.adresse")
	@Mapping(target = "emailAgence", source = "entite.email")
	@Mapping(target = "mobileAgence", source = "entite.fax")
	@Mapping(target = "emailAgent", source = "collaborateur.email")
	@Mapping(target = "prenomAgent", source = "collaborateur.prenom")
	@Mapping(target = "adresseAgent", source = "collaborateur.adresse")
	@Mapping(target = "idCollaborateur", source = "collaborateur.id")
	@Mapping(target = "chiffreAffaire", source = "collaborateur.chiffreAffaire")
	@Mapping(target = "officeId", source = "entite.officeId")
	@Mapping(target = "ville",source = "entite.ville.nom")

	CollaborateurDTO toDto(Collaborateur collaborateur);
	@Mapping(target = "entite.id", source = "entite")
	@Mapping(target = "code", source = "code")
	@Mapping(target = "nom", source = "nomAgent")
	@Mapping(target = "soldePoint", source = "nbrPoint")
	@Mapping(target = "entite.telephone", source = "teleAgence")
	@Mapping(target = "entite.adresse", source = "adresseAgence")
	@Mapping(target = "entite.email", source = "emailAgence")
	@Mapping(target = "entite.fax", source = "mobileAgence")
	@Mapping(target = "email", source = "emailAgent")
	@Mapping(target = "prenom", source = "prenomAgent")
	@Mapping(target = "adresse", source = "adresseAgent")
	Collaborateur toEntity(CollaborateurDTO collaborateurDTO);

	@Mapping(target = "id", source = "collaborateur.id")
	@Mapping(target = "nom", source = "collaborateur.nom")
	@Mapping(target = "prenom", source = "collaborateur.prenom")
	@Mapping(target = "email", source = "collaborateur.email")
	@Mapping(target = "telephone", source = "collaborateur.telephone")
	@Mapping(target = "agenceNom", source = "collaborateur.entite.nom")
	@Mapping(target = "agenceCode", source = "collaborateur.entite.code")
	@Mapping(target = "portfeuilleNom", source = "collaborateur.entite.parent.nom")
	@Mapping(target = "representationNom", source = "collaborateur.entite.parent.parent.nom")
	@Mapping(target = "dateCreation", source = "collaborateur.dateCreated", qualifiedByName = "offsetDateTimeToLocalDate")
	@Mapping(target = "statut",source = "collaborateur.utilisateur.active")
	CollaborateurSearchDTO toDtoSearch(Collaborateur collaborateur);

	@Mapping(target = "nom", source = "profileDto.nom")
	@Mapping(target = "prenom", source = "profileDto.prenom")
	@Mapping(target = "email", source = "profileDto.email")
	@Mapping(target = "categorie", source = "profileDto.profil")
	@Mapping(target = "entite", source = "profileDto.belongTo")
	Collaborateur fromProfileDtoToCollaborateur(ProfileDto profileDto);
	@Mapping(target = "codeIATA", source = "entite.code")
	@Mapping(target = "nomAgence", source = "entite.nom")
	@Mapping(target = "nomAgent", source = "nom")
	@Mapping(target = "signatureAgent", source = "signature")
	@Mapping(target = "chifferAffaire", source = "chiffreAffaire")
	@Mapping(target = "adresseAgence", source = "entite.adresse")
	@Mapping(target = "emailAgence", source = "entite.email")
	@Mapping(target = "emailAgent", source = "email")
	@Mapping(target = "teleAgence", source = "entite.telephone")
	@Mapping(target = "mobileAgent", source = "mobile")
	CollaborateurEntiteDTO toDtoCollaborateurEntite(Collaborateur collaborateur);


	@Mapping(target = "entite",source = "collaborateur.entite",qualifiedByName = "mapEntiteToEntiteCollaborateurUserDto")
	@Mapping(target = "entiteRepresentations",source = "collaborateur.entites",qualifiedByName = "mapEntiteToEntiteRepresentationsCollaborateurUserDto")
	@Mapping(target = "utilisateur",source = "collaborateur.utilisateur",qualifiedByName = "mapUtilisateurToUtilisateurCollaborateurUserDto")
	CollaborateurUserDTO toDtoUser(Collaborateur collaborateur);

	UsersAgenceDto AGENCE_DTO(Collaborateur collaborateur);
	List<CollaborateurSearchDTO> toDtosSearch(List<Collaborateur> collaborateurs);

	AdministartionAgentDto toDtoAdmin(Collaborateur collaborateur);

	Collaborateur toEntityAdmin(AdministartionAgentDto dto);

	@Named("mapEntiteToEntiteCollaborateurUserDto")
	default EntiteCollaborateurUserDto fromEntiteToEntiteCollaborateurUserDto (Entite entite) {
		return ENTITE_MAPPER.toDtoCollaborateurUser(entite);
	}
	@Named("mapEntiteToEntiteRepresentationsCollaborateurUserDto")
	default List<EntiteCollaborateurUserDto> fromEntiteToEntiteCollaborateurRepresentationUserDto (Set<Entite> entite) {
		return ENTITE_MAPPER.toDtoCollaborateurRepresentationUser(entite);
	}
	@Named("mapUtilisateurToUtilisateurCollaborateurUserDto")
	default UtilisateurCollaborateurUserDTO fromUtilisateurToUtilisateurCollaborateurUserDto (Utilisateur utilisateur) {
		return UTILISATEUR_MAPPER.toDtoCollaborateur(utilisateur);
	}


	default Entite mapEntite(Long entiteId) {
		if (entiteId == null) {
			return null;
		}

		Entite entite = new Entite();
		entite.setId(entiteId);

		return entite;
	}

	@Named("getParent")
	static String utilisateurToIdSet(Entite entite) {
		if(entite!=null){
			if(entite.getParent() != null)
				return entite.getParent().getNom();
		}
		return null;
	}

	@Named("offsetDateTimeToLocalDate")
	default LocalDate mapOffsetDateTimeToLocalDate(OffsetDateTime offsetDateTime) {
		return offsetDateTime.toLocalDate();
	}
}
