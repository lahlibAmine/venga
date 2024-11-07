package com.ram.venga.mapper;

import com.ram.venga.domain.*;
import com.ram.venga.model.LigneCommandeDto;
import com.ram.venga.model.OffreDTO;
import com.ram.venga.model.OpperationDTO;
import com.ram.venga.model.OpperationTraiterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OpperationMapper extends EntityMapper<OpperationDTO, Opperation>{
    @Mapping(target = "recetteBrute", source = "opperation.recetteBrute.id")
    @Mapping(target = "bonCommande", source = "opperation.bonCommande.id")
    @Mapping(target = "origine", source = "opperation.recetteBrute.escaleDepart")
    @Mapping(target = "destination", source = "opperation.recetteBrute.escaleArrivee")
    @Mapping(target = "classReservation", source = "opperation.recetteBrute.classeReservation")
    @Mapping(target = "classProduit", source = "opperation.recetteBrute.classeProduit")
    @Mapping(target = "numBillet", source = "opperation.recetteBrute.numBillet")
    @Mapping(target = "pointGagne", source = "opperation.debit")
    @Mapping(target = "date" ,expression = "java(toDtoDate(opperation.getDate()))")
    OpperationDTO toDto(Opperation opperation);

        default RecetteBrute mapRecette(Long recetteId ) {
        if (recetteId == null) {
            return null;
        }

        RecetteBrute recetteBrute = new RecetteBrute();
            recetteBrute.setId(recetteId);

        return recetteBrute;
    }
    default BonCommande mapBonCommande(Long bonCommandeId ) {
        if (bonCommandeId == null) {
            return null;
        }

        BonCommande bonCommande = new BonCommande();
        bonCommande.setId(bonCommandeId);

        return bonCommande;
    }
    @Mapping(target = "date", qualifiedByName = "toDtoDate")
    default LocalDate toDtoDate(LocalDateTime date) {
        return date.toLocalDate();
    }
}
