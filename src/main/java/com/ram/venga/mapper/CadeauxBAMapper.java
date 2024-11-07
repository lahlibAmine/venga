package com.ram.venga.mapper;

import com.ram.venga.domain.*;
import com.ram.venga.model.BonCommandeDTO;
import com.ram.venga.model.CadeauBaPost;
import com.ram.venga.model.CadeauxBaByFournisseurDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ram.venga.model.CadeauxBADTO;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CadeauxBAMapper extends EntityMapper<CadeauxBADTO, CadeauxBA> {

	LigneCommandeMapper ligneCommandeMapper = Mappers.getMapper(LigneCommandeMapper.class);

	@Mapping(target = "origineEmission", source = "origineEmission.id")
	@Mapping(target = "nomOrigine", source = "origineEmission.nom")
	@Mapping(target = "categorieCadeau", source = "categorieCadeau.id")
	@Mapping(target = "nomCategorieCadeau", source = "categorieCadeau.libelle")
	@Mapping(target = "fournisseur", source = "fournisseur.id")
	@Mapping(target = "nomFournisseur", source = "fournisseur.nom")
	@Mapping(target = "devise", source = "origineEmission.devise")
	CadeauxBADTO toDto(CadeauxBA cadeauxBA);
	@Mapping(target = "categorieCadeau.id", source = "categorieCadeau")
	@Mapping(target = "origineEmission.devise.id", source = "devise")
	@Mapping(target = "fournisseur.id", source = "fournisseur")
	@Mapping(target = "origineEmission.id", source = "origineEmission")
	CadeauxBA toEntity(CadeauBaPost cadeauxBADTO);

	@Mapping(target = "id", source = "cadeauxBA.id")
	@Mapping(target = "nbrPoint", source = "cadeauxBA.nbrPoint")
	@Mapping(target = "libelle", source = "cadeauxBA.categorieCadeau.libelle")
	@Mapping(target = "devise", source = "cadeauxBA.origineEmission.devise")
	CadeauxBaByFournisseurDto toDtoByFournisseur(CadeauxBA cadeauxBA);

	List<CadeauxBaByFournisseurDto> toDtosByFournisseur(List<CadeauxBA> cadeauxBA);

	/*@Named("toDtoWithoutIds")
	@Mapping(target = "origineEmission", expression = "java( null )")
	@Mapping(target = "fournisseur", expression = "java( null )")
	@Mapping(target = "categorieCadeau", expression = "java( null )")
	@Mapping(target = "bonCommande", expression = "java( null )")
	CadeauxBADTO toDtoWithoutIds(CadeauxBA cadeauxBA);*/

	default OrigineEmission mapOrigineEmission(Long origineEmissionId) {
		if (origineEmissionId == null) {
			return null;
		}

		OrigineEmission origineEmission = new OrigineEmission();
		origineEmission.setId(origineEmissionId);
		// Additional mapping logic for other fields in OrigineEmission, if needed
		return origineEmission;
	}

	default CategorieCadeau mapCategorieCadeau(Long categorieCadeauId) {
		if (categorieCadeauId == null) {
			return null;
		}

		CategorieCadeau categorieCadeau = new CategorieCadeau();
		categorieCadeau.setId(categorieCadeauId);

		return categorieCadeau;
	}

	default Fournisseur mapFournisseur(Long fournisseurId) {
		if (fournisseurId == null) {
			return null;
		}

		Fournisseur fournisseur = new Fournisseur();
		fournisseur.setId(fournisseurId);

		return fournisseur;
	}


}
