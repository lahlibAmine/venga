package com.ram.venga.mapper;

import com.ram.venga.domain.ExportEmissionView;
import com.ram.venga.domain.Fournisseur;
import com.ram.venga.domain.JournalRapprochementView;
import com.ram.venga.model.ExportEmissionDTO;
import com.ram.venga.model.FournisseurDTO;
import com.ram.venga.model.JournalRapprochementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExportEmissionViewMapper extends EntityMapper<ExportEmissionDTO, ExportEmissionView> {
    ExportEmissionDTO toDto(ExportEmissionView exportEmissionView);


}
