package com.ram.venga.mapper;

import org.mapstruct.Mapper;

import com.ram.venga.domain.ClasseProduit;
import com.ram.venga.model.ClasseProduitDTO;


@Mapper(componentModel = "spring")
public interface ClasseProduitMapper extends EntityMapper<ClasseProduitDTO, ClasseProduit> {

}