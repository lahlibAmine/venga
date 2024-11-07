package com.ram.venga.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.domain.AuditUserAction;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.AuditUserActionDTO;


@Mapper(componentModel = "spring")
public interface AuditUserActionMapper extends EntityMapper<AuditUserActionDTO, AuditUserAction> {

    @Mapping(target = "user", source = "user.id")
    AuditUserActionDTO toDto(AuditUserAction auditUserAction);

    default Utilisateur mapUser(Long userId) {
		if (userId == null) {
			return null;
		}

		Utilisateur user = new Utilisateur();
		user.setId(userId);

		return user;
	}
}
