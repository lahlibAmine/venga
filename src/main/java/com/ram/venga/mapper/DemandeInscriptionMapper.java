package com.ram.venga.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ram.venga.model.enumeration.ProfilEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.DemandeInscription;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.DemandeInscriptionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Mapper(componentModel = "spring")
public interface DemandeInscriptionMapper extends EntityMapper<DemandeInscriptionDTO, DemandeInscription> {

    // /   /  / / /  /  /
    // nom collaborateur / prenom / validateur 2 / date Validation

    @Mapping(target = "dateDemande", source = "dateDemande")
    @Mapping(target = "nomCollaborateur", source = "collaborateur.nom")
    @Mapping(target = "emailCollaborateur", source = "collaborateur.email")
    @Mapping(target = "prenomCollaborateur", source = "collaborateur.prenom")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "agence", source = "collaborateur.entite.nom")
    @Mapping(target = "dateValidation", source = "dateValidationAdminF")
    @Mapping(target = "commentaire", source = "commentaire")
    @Mapping(target = "statut", source = "statut")
    @Mapping(target = "fonction", source = "collaborateur.fonction")
    @Mapping(target = "validateur", source = "validateurs" , qualifiedByName = "utilisateurToIdSet")
    DemandeInscriptionDTO toDto(DemandeInscription demandeInscription);

    List<DemandeInscriptionDTO> toDtos(List<DemandeInscription> demandesInscription);

 /*   @Mapping(target = "validateurs", source = "validateurs")
    DemandeInscription toEntity(DemandeInscriptionDTO demandeInscriptionDto);*/

    @Named("utilisateurToIdSet")
    static Map<ProfilEnum, String> utilisateurToIdSet(Set<Utilisateur> validateurs) {
        return validateurs.stream()
                .collect(Collectors.groupingBy(Utilisateur::getProfil,
                        Collectors.mapping(utilisateur -> utilisateur.getCollaborateur().getNom(),
                                Collectors.joining(", ")))); // Merge collaborator names into a single String with comma separation
    }


    default Set<Utilisateur> mapValidateurs(List<Long> validateurIds) {
        return validateurIds.stream()
                .map(id -> {
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.setId(id);
                    return utilisateur;
                })
                .collect(Collectors.toSet());
    }

    default Collaborateur mapCollaborateur(Long collaborateurId) {
		if (collaborateurId == null) {
			return null;
		}

		Collaborateur collaborateur = new Collaborateur();
		collaborateur.setId(collaborateurId);

		return collaborateur;
	}
}
