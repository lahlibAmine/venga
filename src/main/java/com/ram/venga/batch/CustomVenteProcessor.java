package com.ram.venga.batch;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.ram.venga.rest.BatchResource;
import com.ram.venga.service.BatchService;
import com.ram.venga.util.ObjectFindValue;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class CustomVenteProcessor implements ItemProcessor<String, String> {

	@Autowired
	ObjectFindValue objectFindValue;
	@Autowired
	@Lazy
	BatchResource batchResource;

	@Override
	public String process(String item) throws Exception {



		Map<String, String[]> map = batchResource.sendElementToExtract().getBody();

		//System.out.println(map);

		if(map==null)
			throw new Exception();

		String [] pnr =  map.get("pnr");
		String [] dateEmission ;
		String [] dateEmission1 = map.get("dateEmission1") ;
		String [] timeEmission =  map.get("timeEmission");
		String [] signatureAgent =  map.get("signatureAgent");
		String [] numBillet =  map.get("numBillet");
		String [] cieVol =  map.get("cieVol");
		String [] officeId =  map.get("officeId");
		String [] nbrCoupon =  map.get("nbrCoupon");
		String [] companyDetail =  map.get("companyDetail");
		String [] productDetails =  map.get("productDetails");
		String [] firstNumBillet = map.get("firstNumBillet");
		String [] anneeEmission = map.get("anneeEmission");
		String [] moisEmission = map.get("moisEmission");
		String [] jourEmission = map.get("jourEmission");
		String [] classeReservation = map.get("classeReservation");


		item = item.replaceAll("&", "&amp;");


		// Utilisez une expression régulière pour supprimer les balises <freetext>
		item = item.replaceAll("<freetext>.*?</freetext>", "");


		// Utilisez une expression régulière pour supprimer le contenu de toutes les balises qui contiennent < ou >
		item = item.replaceAll("<[^>]*>[^<>]*[<>]+[^<>]*</[^>]*>", "");


		List<String> extractedPnr = objectFindValue.extractValues(item,pnr);
		List<String> extractedDateEmission1 = objectFindValue.extractValues(item,dateEmission1);
		List<String> extractedAllSignatureAgent = objectFindValue.extractValues(item,signatureAgent);
		List<String> extractedNumBillet = objectFindValue.extractValues(item,numBillet);
		List<String> extractedAnnee = objectFindValue.extractValues(item,anneeEmission );
		List<String> extractedmois = objectFindValue.extractValues(item,moisEmission );
		List<String> extractedjours = objectFindValue.extractValues(item,jourEmission );
		List<String> extractedCieVol = objectFindValue.extractValues(item,cieVol );
		List<String> extractedTimeEmission = objectFindValue.extractValues(item,timeEmission );
		List<String> extractedOfficeId = objectFindValue.extractValues(item,officeId );
		List<String> extractedNbrCoupon = objectFindValue.extractValues(item,nbrCoupon );
		List<String> extractedCompanyDetail = objectFindValue.extractValues(item,companyDetail );
		List<String> extractedProductDetails = objectFindValue.extractValues(item,productDetails );
		List<String> extractedFirstNumBillet = objectFindValue.extractValues(item,firstNumBillet);
		List<String> extractedClasseReservation = objectFindValue.extractValues(item,classeReservation);

		Map<String, List<String>> extractedMap = new HashMap<String, List<String>>();
		String annee=null;
		String mois=null;
		String jour= null;

		if(extractedAnnee!= null && extractedAnnee.size() > 0 && extractedmois!= null && extractedmois.size() > 0 && extractedjours!= null && extractedjours.size() > 0){
			annee=   extractedAnnee.stream().findFirst().get().substring(2,4);
			mois=  	 extractedmois.stream().findFirst().get();
			jour=   extractedjours.stream().findFirst().get();
		}

		String dateEmissionConcatenated = jour+mois+annee;
		dateEmission = new String[]{dateEmissionConcatenated};

		extractedMap.put("pnr", extractedPnr);
		extractedMap.put("dateEmission1", extractedDateEmission1);
		extractedMap.put("dateEmission", Arrays.asList(dateEmission));
		extractedMap.put("timeEmission", extractedTimeEmission);
		extractedMap.put("signatureAgent", extractedAllSignatureAgent);
		extractedMap.put("numBillet", extractedNumBillet);
		extractedMap.put("cieVol", extractedCieVol);
		extractedMap.put("officeId", extractedOfficeId);
		extractedMap.put("nbrCoupon", extractedNbrCoupon);
		extractedMap.put("companyDetail", extractedCompanyDetail);
		extractedMap.put("productDetails", extractedProductDetails);
		extractedMap.put("firstNumBillet", extractedFirstNumBillet);
		extractedMap.put("classeReservation",extractedClasseReservation);


		ObjectMapper objectMapper = new ObjectMapper();

		ResponseEntity<?> response = batchResource.sendProcessedVenteObject(objectMapper.writeValueAsString(extractedMap));
		String string;  // Déclarez la variable ici pour qu'elle soit accessible après l'if

		if (response.getBody() != null) {
			string = response.getBody().toString();
		} else {
			// Gestion du cas où le body est null
			System.out.println("Le corps de la réponse est null.");
			string = "";  // Valeur par défaut ou " " selon votre logique
		}

		return string;

	}
}
