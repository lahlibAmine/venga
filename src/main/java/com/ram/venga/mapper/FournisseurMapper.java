package com.ram.venga.mapper;

import com.ram.venga.domain.*;
import com.ram.venga.model.*;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface FournisseurMapper extends EntityMapper<FournisseurDTO, Fournisseur> {
    OrigineEmissionMapper ligneCommandeMapper = Mappers.getMapper(OrigineEmissionMapper.class);
    CadeauxBAMapper CadeauxBAMapper = Mappers.getMapper(CadeauxBAMapper.class);

    @Mapping(target = "cadeauxBAs", source = "fournisseur.cadeauxBAs", qualifiedByName = "mapCadeauxBAList")
    @Mapping(target = "nomOrigine" , source = "fournisseur.cadeauxBAs",qualifiedByName = "origineEmissionDto")
    @Mapping(target = "origineEmission" , source = "fournisseur.cadeauxBAs",qualifiedByName = "origineEmissionEntiteDto")
    FournisseurDTO toDto(Fournisseur fournisseur);

    @Named("mapCadeauxBAList")
    default List<CadeauxBaByFournisseurDto> mapCadeauxBAList(Set<CadeauxBA> cadeauxBAs) {
        return CadeauxBAMapper.toDtosByFournisseur(new ArrayList<>(cadeauxBAs));
    }

    @Named("origineEmissionDto")
    default String origineEmissionDto(Set<CadeauxBA> cadeauxBAS) {
        return cadeauxBAS.stream()
                .map(CadeauxBA::getOrigineEmission)
                .map(oe -> oe != null ? oe.getNom() : null) // Optional chaining here
                .filter(Objects::nonNull) // Remove null values
                .findFirst()
                .orElse(null); // Return null if no non-null value is found
    }
    @Named("origineEmissionEntiteDto")
    default Long origineEmissionEntiteDto(Set<CadeauxBA> cadeauxBAS) {
        return cadeauxBAS.stream()
                .map(CadeauxBA::getOrigineEmission)
                .map(oe -> oe != null ? oe.getId() : null) // Optional chaining here
                .filter(Objects::nonNull) // Remove null values
                .findFirst()
                .orElse(null); // Return null if no non-null value is found
    }
}

