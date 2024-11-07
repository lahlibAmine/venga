package com.ram.venga.mapper;

import com.ram.venga.domain.Image;
import com.ram.venga.domain.Offre;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.model.ImageDTO;
import com.ram.venga.model.OffreDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMapper extends EntityMapper<ImageDTO, Image>{
    @Mapping(target = "offre", source = "offre.id")
    ImageDTO toDto(Image image);

    default Offre mapOffre(Long offreId ) {
        if (offreId == null) {
            return null;
        }

        Offre offre = new Offre();
        offre.setId(offreId);

        return offre;
    }
}
