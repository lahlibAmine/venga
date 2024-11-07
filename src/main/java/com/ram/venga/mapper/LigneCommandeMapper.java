package com.ram.venga.mapper;

import com.ram.venga.domain.*;
import com.ram.venga.model.BonCommandeDTO;
import com.ram.venga.model.CadeauxBADTO;
import com.ram.venga.model.LigneCommandeDto;
import com.ram.venga.model.LigneCommandeValidateurDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LigneCommandeMapper extends EntityMapper<LigneCommandeDto, LigneCommande>{

    @Mapping(target = "cadeauxBADTO", source = "ligneCommandes.cadeauxBA.id")
    @Mapping(target = "bonCommandeDTO", source = "ligneCommandes.bonCommande.id")
	@Mapping(target = "reference", source = "ligneCommandes.bonCommande.reference")
	@Mapping(target = "nbrPointCredit", source = "ligneCommandes.bonCommande.nbrPointCredit")
	@Mapping(target = "fournisseur", source = "ligneCommandes.cadeauxBA.fournisseur.nom")
	@Mapping(target = "etat", source = "ligneCommandes.bonCommande.etat")
	@Mapping(target = "resultat", source = "ligneCommandes.resultat")
	@Mapping(target = "nomCadeau", source = "ligneCommandes.cadeauxBA.categorieCadeau.libelle")
	LigneCommandeDto toDto(LigneCommande  ligneCommandes);

	@Mapping(target = "nomAgent", source = "bonCommande.agentCommercial.collaborateur.nom")
	@Mapping(target = "nomAgence", source = "bonCommande.agentCommercial.collaborateur.entite.nom")
	@Mapping(target = "date", source = "dateCreated")
	@Mapping(target = "fournisseur", source = "cadeauxBA.fournisseur.nom")
	@Mapping(target = "cadeau", source = "cadeauxBA.categorieCadeau.libelle")
	@Mapping(target = "nbrPoint", source = "bonCommande.nbrPointCredit")
	@Mapping(target = "etat", source = "bonCommande.etat")
	@Mapping(target = "nomPortfeuille", source = "bonCommande.agentCommercial.collaborateur.entite.parent.nom")
	@Mapping(target = "nomRepresentation", source = "bonCommande.agentCommercial.collaborateur.entite.parent.parent.nom")
	@Mapping(target = "resultat",source = "resultat")
	@Mapping(target = "signature",source = "bonCommande.agentCommercial.collaborateur.signature")
	LigneCommandeValidateurDto toDtoValidateur(LigneCommande ligneCommande);

	@Mapping(target = "cadeauxBA.id", source = "cadeauxBADTO")
	@Mapping(target = "bonCommande.id", source = "bonCommandeDTO")
	LigneCommande toEntity(LigneCommandeDto  ligneCommandesDto);



	@Mapping(target = "ligneCommande", qualifiedByName = "toDtoWithoutCadeauxIdsList")
	default Set<LigneCommandeDto> toDtoWithoutCadeauxIdsList(Set<LigneCommande> ligneCommandes) {
		return ligneCommandes.stream()
				.map(ligneCommande -> toDto(ligneCommande)) // Pass individual ligneCommande object
				.collect(Collectors.toSet());
	}

}
