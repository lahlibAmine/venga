package com.ram.venga.mapper;

import com.ram.venga.domain.Image;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.model.ImageDTO;
import org.mapstruct.Mapper;

import com.ram.venga.domain.HauteSaison;
import com.ram.venga.model.HauteSaisonDTO;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface HauteSaisonMapper extends EntityMapper<HauteSaisonDTO, HauteSaison> {
     @Mapping(target = "origineEmission.id", source = "origineEmission")
    HauteSaison toEntity(HauteSaisonDTO hauteSaisonDTO);

    @Mapping(target = "origineEmission", source = "origineEmission.id")
    @Mapping(target = "nomOrigine", source = "origineEmission.nom")
    HauteSaisonDTO toDto(HauteSaison hauteSaison);
    default OrigineEmission mapOrigineEmission(Long origineEmissionId) {
        if (origineEmissionId == null) {
            return null;
        }

        OrigineEmission origineEmission = new OrigineEmission();
        origineEmission.setId(origineEmissionId);
        // Additional mapping logic for other fields in OrigineEmission, if needed
        return origineEmission;
    }


}