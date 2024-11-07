package com.ram.venga.mapper;

import com.ram.venga.domain.BonCommande;
import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.domain.LigneCommande;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.BonCommandeDTO;
import com.ram.venga.model.CadeauxBADTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BonCommandeMapper extends EntityMapper<BonCommandeDTO, BonCommande> {

	LigneCommandeMapper ligneCommandeMapper = Mappers.getMapper(LigneCommandeMapper.class);

	@Mapping(target = "agentCommercial", source = "bonCommande.agentCommercial.id")
	@Mapping(target = "ligneCommande", expression = "java(ligneCommandeMapper.toDtoWithoutCadeauxIdsList(bonCommande.getLigneCommandes()))")
	BonCommandeDTO toDto(BonCommande bonCommande);
	@Named("cadeauxBASToIdSet")
	static List<Long> cadeauxBASToIdSet(Set<CadeauxBA> cadeauxBAS) {
		return cadeauxBAS.stream()
				.map(CadeauxBA::getId)
				.collect(Collectors.toList());
	}

	default Set<CadeauxBA> mapCadeauxBas(List<Long> cadeauxBASIds) {
		return cadeauxBASIds.stream()
				.map(id -> {
					CadeauxBA cadeauxBA = new CadeauxBA();
					cadeauxBA.setId(id);
					return cadeauxBA;
				})
				.collect(Collectors.toSet());
	}

	default Utilisateur mapUtilisateur(Long utilisateurId) {
		if (utilisateurId == null) {
			return null;
		}

		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setId(utilisateurId);

		return utilisateur;
	}

/*	@Mapping(target = "ligneCommande", qualifiedByName = "toDtoWithoutIdsList")
	BonCommandeDTO toDtoWithoutIds(BonCommande bonCommande);

	@Named("toDtoWithoutIdsList")
	default List<BonCommandeDTO> toDtoWithoutIdsList(List<BonCommande> bonCommandes) {
		return bonCommandes.stream()
				.map(this::toDtoWithoutIds)
				.collect(Collectors.toList());
	}*/
}
