package com.ram.venga.mapper;

import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Offre;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.OffreDTO;
import com.ram.venga.model.UtilisateurDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OffreMapper extends EntityMapper<OffreDTO, Offre>{
    @Mapping(target = "origineEmission", source = "origineEmission.id")
    OffreDTO toDto(Offre offre);

    default OrigineEmission mapOrigineEmission(Long origineEmissionId ) {
        if (origineEmissionId == null) {
            return null;
        }

        OrigineEmission origineEmission = new OrigineEmission();
        origineEmission.setId(origineEmissionId);

        return origineEmission;
    }
}

