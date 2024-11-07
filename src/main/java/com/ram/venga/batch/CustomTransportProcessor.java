package com.ram.venga.batch;


import com.ram.venga.rest.BatchResource;
import com.ram.venga.util.ObjectFindValue;
import liquibase.pro.packaged.B;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomTransportProcessor implements ItemProcessor<String, String> {

	@Autowired
	ObjectFindValue objectFindValue;

	@Autowired
	BatchResource batchResource;

	@Override
	public String process(String item) throws Exception {

		Map<String, Long[]> map = batchResource.sendTransportElementToExtract().getBody();

		String cieTitre = null;
		String numBillet = null;
		String numCoupon = null;
		String cieVol = null;
		String dateTransport = null;
		String escaleDepart = null;
		String escaleArrivee = null;
		String classeProduit = null;
		String classeReservation = null;
		String montantBrut = null;
		String codeIATA = null;

		// Définissez les informations sur l'emplacement et la taille

		if (map.containsKey("cieTitre"))
			cieTitre = extractSubstring(item, map.get("cieTitre")[0].intValue(), map.get("cieTitre")[1].intValue());

		if (map.containsKey("numBillet"))
			numBillet = extractSubstring(item, map.get("numBillet")[0].intValue(), map.get("numBillet")[1].intValue());

		if (map.containsKey("numCoupon"))
			numCoupon = extractSubstring(item, map.get("numCoupon")[0].intValue(), map.get("numCoupon")[1].intValue());

		if (map.containsKey("cieVol"))
			cieVol = extractSubstring(item, map.get("cieVol")[0].intValue(), map.get("cieVol")[1].intValue());

		if (map.containsKey("dateTransport"))
			dateTransport = extractSubstring(item, map.get("dateTransport")[0].intValue(),
					map.get("dateTransport")[1].intValue());

		if (map.containsKey("escaleDepart"))
			escaleDepart = extractSubstring(item, map.get("escaleDepart")[0].intValue(),
					map.get("escaleDepart")[1].intValue());

		if (map.containsKey("escaleArrivee"))
			escaleArrivee = extractSubstring(item, map.get("escaleArrivee")[0].intValue(),
					map.get("escaleArrivee")[1].intValue());

		if (map.containsKey("classeProduit"))
			classeProduit = extractSubstring(item, map.get("classeProduit")[0].intValue(),
					map.get("classeProduit")[1].intValue());

		if (map.containsKey("classeReservation"))
			classeReservation = extractSubstring(item, map.get("classeReservation")[0].intValue(),
					map.get("classeReservation")[1].intValue());

		if (map.containsKey("montantBrut"))
			montantBrut = extractSubstring(item, map.get("montantBrut")[0].intValue(),
					map.get("montantBrut")[1].intValue());

		if (map.containsKey("codeIATA"))
			codeIATA = extractSubstring(item, map.get("codeIATA")[0].intValue(), map.get("codeIATA")[1].intValue());

		Map<String, String> extractedMap = new HashedMap<String, String>();

		extractedMap.put("cieTitre", cieTitre);
		extractedMap.put("numBillet", numBillet);
		extractedMap.put("numCoupon", numCoupon);
		extractedMap.put("cieVol", cieVol);
		extractedMap.put("dateTransport", dateTransport);
		extractedMap.put("escaleDepart", escaleDepart);
		extractedMap.put("escaleArrivee", escaleArrivee);
		extractedMap.put("classeProduit", classeProduit);
		extractedMap.put("classeReservation", classeReservation);
		extractedMap.put("montantBrut", montantBrut);
		extractedMap.put("codeIATA", codeIATA);
		String responseEntity = batchResource.sendProcessedTransportObject(extractedMap);


		return responseEntity;
	}

	private static String extractSubstring(String input, int start, int length) {
		if (start >= 0 && start + length <= input.length()) {
			return input.substring(start, start + length);
		}
		return ""; // Retourne une chaîne vide si les indices sont invalides
	}
}