package com.ram.venga.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ram.venga.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Entite;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Ville;


@Mapper(componentModel = "spring")
public interface EntiteMapper extends EntityMapper<EntiteDTO, Entite> {
	
	@Mapping(target = "parent", source = "parent.id")
	@Mapping(target = "nomParent", source = "parent.nom")
	@Mapping(target = "ville", source = "ville.id")
	@Mapping(target = "nomVille", source = "ville.nom")
	@Mapping(target = "origineEmission", source = "origineEmission.id")
	@Mapping(target = "nomOrigine", source = "origineEmission.nom")
	@Mapping(target = "representation", source = "parent.parent.id")
	@Mapping(target = "nomRepresentation", source = "parent.parent.nom")
	//@Mapping(target = "collaborateurs", source = "collaborateurs", qualifiedByName = "collaborateurToIdSet")
	EntiteDTO toDto(Entite entite);

	EntiteCreateDTO toDtoEntiteNbrBienvenuDto(Entite entite);
	@Mapping(target = " nomUser",expression = "java(nomUser(entite.getCollaborateurs()))")
	@Mapping(target = " prenomUser", expression = "java(prenomUser(entite.getCollaborateurs()))")
	EntiteCreateListDTO ENTITE_CREATE_LIST_DTO(Entite entite);
	Entite toEntiteEntiteNbrBienvenuDto(EntiteCreateDTO entiteCreateDTO);
	EntiteNomUdpadeDto toDtoAll(Entite entite);

	EntiteCollaborateurUserDto toDtoCollaborateurUser(Entite entite);
	List<EntiteCollaborateurUserDto> toDtoCollaborateurRepresentationUser(Set<Entite> entite);


	@Named("collaborateurToIdSet")
    static List<Long> utilisateurToIdSet(List<Collaborateur> collaborateurs) {
        return collaborateurs.stream()
                .map(Collaborateur::getId)
                .collect(Collectors.toList());
    }

	 @Named("idSetToCollaborateur")
	    static Set<Collaborateur> idSetToCollaborateur(Set<Long> collaborateurs) {
	        return collaborateurs.stream()
	                .map(id -> {
	                	Collaborateur collaborateur = new Collaborateur();
	                	collaborateur.setId(id);
	                    return collaborateur;
	                })
	                .collect(Collectors.toSet());
	    }

	default Entite mapParent(Long parentId) {
		if (parentId == null) {
			return null;
		}

		Entite entite = new Entite();
		entite.setId(parentId);

		return entite;
	}

	default OrigineEmission mapOrigineEmission(Long origineEmissionId) {
		if (origineEmissionId == null) {
			return null;
		}

		OrigineEmission origineEmission = new OrigineEmission();
		origineEmission.setId(origineEmissionId);
		// Additional mapping logic for other fields in OrigineEmission, if needed
		return origineEmission;
	}

	default Ville mapVille(Long villeId) {
		if (villeId == null) {
			return null;
		}

		Ville ville = new Ville();
		ville.setId(villeId);

		return ville;
	}

	@Mapping(target = "nomUser", qualifiedByName = "nomUser")
	default List<String> nomUser(Set<Collaborateur> collaborateurs) {
		return collaborateurs.stream().map(Collaborateur::getNom).collect(Collectors.toList());
	}

	@Mapping(target = "prenomUser", qualifiedByName = "prenomUser")
	default List<String> prenomUser(Set<Collaborateur> collaborateurs) {
		return collaborateurs.stream().map(Collaborateur::getPrenom).collect(Collectors.toList());
	}
}