package com.ram.venga.mapper;

import com.ram.venga.domain.*;
import com.ram.venga.model.BonCommandeDTO;
import com.ram.venga.model.LigneCommandeDto;
import com.ram.venga.model.VenteRapprochementDTO;
import com.ram.venga.model.enumeration.ProfilEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.model.JournalRapprochementDTO;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JournalRapprochementMapper extends EntityMapper<JournalRapprochementDTO, JournalRapprochement> {
    RecetteBruteMapper RECETTE_BRUTE_MAPPER = Mappers.getMapper(RecetteBruteMapper.class);

  /*  @Mapping(target = "dateRapprochement", source = "date", qualifiedByName = "toDate")
    @Mapping(target = "statutRapprochement", source = "statut")
    @Mapping(target = "venteRapprocheDto.statut", source = "vente.venteRapproche")
    @Mapping(target = "venteRapprocheDto.nbrCouponVenteNonRapproche", source = "vente.nbrCouponNonRapprocher")
    @Mapping(target = "venteRapprocheDto.nbrCouponVente", source = "vente.nbrCoupon")
    @Mapping(target = "venteRapprocheDto.dateEmission", source = "vente.dateEmission" , qualifiedByName = "toDate")
    @Mapping(target = "venteRapprocheDto.statutVente", source = "vente.statutVente")
    JournalRapprochementDTO toDto(JournalRapprochement journalRapprochement);

    @Named("toDate")
    static LocalDate toDate(LocalDateTime date) {
        return date.toLocalDate();
    }
*/

}
