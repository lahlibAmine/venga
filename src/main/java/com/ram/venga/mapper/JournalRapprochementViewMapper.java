package com.ram.venga.mapper;

import com.ram.venga.domain.JournalRapprochement;
import com.ram.venga.domain.JournalRapprochementView;
import com.ram.venga.model.JournalRapprochementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface JournalRapprochementViewMapper extends EntityMapper<JournalRapprochementDTO, JournalRapprochementView> {
   // RecetteBruteMapper RECETTE_BRUTE_MAPPER = Mappers.getMapper(RecetteBruteMapper.class);

    @Mapping(target = "dateRapprochement", source = "lastUpdated")
    @Mapping(target = "statutVente", source = "statutVente")
    @Mapping(target = "numBillet", source = "numBillet")
    @Mapping(target = "venteRapprocheDto.statut", source = "venteRapproche")
    @Mapping(target = "venteRapprocheDto.nbrCouponVenteNonRapproche", source = "nbrCouponNonRapprocher")
    @Mapping(target = "venteRapprocheDto.nbrCouponVente", source = "nbrCoupon")
    JournalRapprochementDTO toDto(JournalRapprochementView journalRapprochementView);

  /*  @Named("toDate")
    static LocalDate toDate(LocalDate date) {
        return date.toLocalDate();
    }
*/

}
