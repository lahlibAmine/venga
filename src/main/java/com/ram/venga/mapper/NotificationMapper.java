package com.ram.venga.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.Notification;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.NotificationDTO;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {

    @Mapping(target = "utilisateur", source = "utilisateur.id")
    NotificationDTO toDto(Notification notification);

    default Utilisateur mapUtilisateur(Long utilisateurId) {
		if (utilisateurId == null) {
			return null;
		}

		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setId(utilisateurId);

		return utilisateur;
	}
}

