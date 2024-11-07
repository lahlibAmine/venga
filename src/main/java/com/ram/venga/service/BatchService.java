package com.ram.venga.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ram.venga.domain.*;
import com.ram.venga.model.enumeration.StatutBilletEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import com.ram.venga.repos.*;
import lombok.extern.slf4j.Slf4j;
import net.snowflake.client.jdbc.internal.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BatchService {

    @Autowired
    VenteRepository venteRepository;

    @Autowired
    CollaborateurRepository collaborateurRepository;
    @Autowired
    OrigineEmissionRepository origineEmissionRepository;

    @Autowired
    RecetteBruteRepository recetteBruteRepository;

    @Autowired
    EntiteRepository entiteRepository;
    @Autowired
    ClasseReservationRepository classeReservationRepository;

    public Map<String, String[]> constructVenteElementToExtract() {
        System.out.println("start extract element");
        Map<String, String[]> map = new HashMap<String, String[]>();

        String[] pnr = {"ForPnrHandling", "activePNRimage", "pnrHeader", "reservationInfo", "reservation",
                "controlNumber"};
        String[] dateEmission;
        String[] timeEmission = {"ForPnrHandling", "activePNRimage", "pnrHeader", "reservationInfo", "reservation",
                "time"};
        String[] dateEmission1 = {"ForPnrHandling", "activePNRimage", "pnrHeader", "reservationInfo", "reservation",
                "date"};

        String[] anneeEmission = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsStruct", "ticketDocumentData",
                "ticketingDate","dateTime","year"};
        String[] moisEmission = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsStruct", "ticketDocumentData",
                "ticketingDate","dateTime","month"};
        String[] jourEmission = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsStruct", "ticketDocumentData",
                "ticketingDate","dateTime","day"};
        String[] verificationCode = {"ForPnrHandling", "activePNRimage", "pnrHeader", "sbrPosDetails", "sbrUserIdentification",
                "originatorTypeCode"};
        String[] signatureAgent = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsIndiv",
                "otherDataFreetext", "longFreetext"};
        String[] numBillet = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsStruct",
                "ticketDocumentData", "tktNumber", "documentDetails", "number"};
        String[] companyDetail = {"ForPnrHandling", "activePNRimage", "originDestinationDetails", "itineraryInfo",
                "travelProduct", "companyDetail", "identification"};
        String[] productDetails = {"ForPnrHandling", "activePNRimage", "originDestinationDetails", "itineraryInfo",
                "travelProduct", "productDetails", "identification"};
        String[] officeId = {"ForPnrHandling",  "activePNRimage", "pnrHeader",
                "sbrPosDetails", "sbrUserIdentification", "originIdentification", "inHouseIdentification1"};
      /*  String[] officeId = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsStruct",
                "ticketDocumentData", "officeInfo", "originatorDetails", "inHouseIdentification1"};*/
        String[] nbrCoupon = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsStruct",
                "ticketDocumentData"};
        String[] firstNumBillet = {"ForPnrHandling", "activePNRimage", "dataElementsMaster", "dataElementsStruct",
                "ticketDocumentData", "numAirlineCode", "companyIdentification", "otherCompany"};
        String[] classeReservation = {"ForPnrHandling", "activePNRimage", "originDestinationDetails",
                "itineraryInfo", "travelProduct", "productDetails", "classOfService"};
       /* String annee=null;
        String mois=null;
        String jour= null;

        if(anneeEmission!= null && anneeEmission.length > 0 && moisEmission!= null && moisEmission.length > 0 && jourEmission!= null && jourEmission.length > 0){
            annee=   Arrays.stream(anneeEmission).findFirst().get().substring(2,2);
            mois=   Arrays.stream(moisEmission).findFirst().get();
            jour=   Arrays.stream(jourEmission).findFirst().get();
        }

        String dateEmissionConcatenated = jour+mois+annee;
        dateEmission = new String[]{dateEmissionConcatenated};*/

        // Now, store this concatenated array in the map
        map.put("companyDetail", companyDetail);
        map.put("dateEmission1",dateEmission1);
        map.put("productDetails", productDetails);
        map.put("pnr", pnr);
        map.put("anneeEmission", anneeEmission);
        map.put("moisEmission", moisEmission);
        map.put("jourEmission", jourEmission);
        map.put("timeEmission", timeEmission);
        map.put("signatureAgent", signatureAgent);
        map.put("numBillet", numBillet);
        map.put("officeId", officeId);
        map.put("nbrCoupon", nbrCoupon);
        map.put("verificationCode", verificationCode);
        map.put("firstNumBillet", firstNumBillet);
        map.put("classeReservation", classeReservation);
        return map;

    }

    public List<String> sendProcessedVenteObject(String mapString) throws IOException, ParserConfigurationException, SAXException {

        System.out.println("start vente venga");
        log.info("object recuperer "+ mapString);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Register JavaTimeModule for LocalDateTime support
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: use ISO-8601 format
        Map<String, List<String>> map = objectMapper.readValue(mapString, new TypeReference<Map<String, List<String>>>() {
        });


        List<String> extractedPnr = null;
        List<String> extractedAllSignatureAgent = new ArrayList<String>();
        List<String> extractedNumBillet = null;
        List<String> extractedDateEmission = null;
        List<String> extractedDateEmission1 = null;
        List<String> extractedTimeEmission = null;
        List<String> extractedCieVol = null;
        List<String> extractedOfficeId = null;
        List<String> extractedNbrCoupon = null;
        List<String> extractedCompanyDetail = null;
        List<String> extractedProductDetails = null;
        List<String> extractedFirstNumBillet = null;

        List<String> concatenatedList = new ArrayList<>();
        List<String> concatenatedNumBillet = new ArrayList<>();
        List<String> extractedClasseReservation = null;

        if (map.containsKey("pnr"))
            extractedPnr = map.get("pnr");
          if (map.containsKey("companyDetail"))
            extractedCompanyDetail = map.get("companyDetail");

        if (map.containsKey("firstNumBillet"))
            extractedFirstNumBillet = map.get("firstNumBillet");

        if (map.containsKey("productDetails"))
            extractedProductDetails = map.get("productDetails");

        if (map.containsKey("signatureAgent"))
            extractedAllSignatureAgent = map.get("signatureAgent");

        if (map.containsKey("numBillet"))
            extractedNumBillet = map.get("numBillet");

        if (map.containsKey("dateEmission"))
            extractedDateEmission = map.get("dateEmission");

        if (map.containsKey("timeEmission"))
            extractedTimeEmission = map.get("timeEmission");

        if (map.containsKey("cieVol"))
            extractedCieVol = map.get("cieVol");

        if (map.containsKey("officeId"))
            extractedOfficeId = map.get("officeId");

        if (map.containsKey("nbrCoupon"))
            extractedNbrCoupon = map.get("nbrCoupon");

        if (map.containsKey("classeReservation"))
            extractedClasseReservation = map.get("classeReservation");
        if(map.containsKey("dateEmission1"))
            extractedDateEmission1 = map.get("dateEmission1");

        boolean found = false;
       // boolean foundCode = false;

        /*List<String> finalExtractedPartyCodeQualifier = extractedPartyCodeQualifier;
        int indexUPD = IntStream.range(0, extractedPartyCodeQualifier.size())
                .filter(i -> finalExtractedPartyCodeQualifier.get(i).contains("UPD"))
                .findFirst()
                .orElse(-1);

        int indexCRE = IntStream.range(0, extractedPartyCodeQualifier.size())
                .filter(i -> finalExtractedPartyCodeQualifier.get(i).contains("CRE"))
                .findFirst()
                .orElse(-1);

        int indexOWN = IntStream.range(0, extractedPartyCodeQualifier.size())
                .filter(i -> finalExtractedPartyCodeQualifier.get(i).contains("OWN"))
                .findFirst()
                .orElse(-1);

         */

        if (extractedCompanyDetail != null && extractedProductDetails != null) {
            int minLength = Math.min(extractedCompanyDetail.size(), extractedProductDetails.size());
            for (int i = 0; i < minLength; i++) {
                concatenatedList.add(extractedCompanyDetail.get(i) + extractedProductDetails.get(i));
            }
        }

        List<String> extractedSignatureAgent = new ArrayList<String>();
        List<String> extractedCodeIataAgent = new ArrayList<String>();
        for (String signature : extractedAllSignatureAgent) {
            String filteredSignature = signature.replaceAll("\\s", "");
            if (filteredSignature.toUpperCase().contains("AINCAS") && signature.length() <= 64) {
                found = true;
                // Add the filtered signature (without any whitespace)
                extractedSignatureAgent.add(filteredSignature);
                log.info("La signature est : " + filteredSignature);
            }
        }

        if (found == false)
            return null;

        List<String> ventes = new ArrayList<>();
        if (extractedNumBillet != null) {
          //  int minLength = Math.min(extractedNumBillet.size(), extractedFirstNumBillet.size());
            for (int i = 0; i < extractedNumBillet.size(); i++) {
                Vente vente = new Vente();
                String firstnumBillet = (extractedFirstNumBillet != null && !extractedFirstNumBillet.isEmpty())
                        ? extractedFirstNumBillet.get(0)
                        : "147";

                String numBillet = (extractedNumBillet != null && !extractedNumBillet.isEmpty())
                        ? extractedNumBillet.get(i)
                        : "error get numbillet not found";

                concatenatedNumBillet.add(firstnumBillet + "-" + numBillet);

                Entite entite = null;
                 vente.setStatutVente(StatutVenteEnum.Non_Rapproche);
                log.info("status non rapprocher ");
                Collaborateur collaborateur = collaborateurRepository.findBySignature(extractedSignatureAgent.get(0));
//		System.out.println("entite_origine out if : "+ entite.getOrigineEmission().getNom());

                if (collaborateur != null) {
                    vente.setIdCollaborateur(collaborateur.getId());
                    log.info("collaborateur est trouves "+collaborateur.getId());
                }

                if (entite != null && collaborateur == null ) {
                    if (entite.getOrigineEmission() != null) {
                        vente.setVenteIntgre(true);
                        vente.setVenteRapproche(false);
                        vente.setStatutVente(StatutVenteEnum.Non_Integre_En_Instance);
                        log.info("vente est non integere ");

                    }
                }else if(entite != null && collaborateur != null){
                    if (entite.getOrigineEmission() != null) {
                        vente.setVenteIntgre(true);
                        vente.setVenteRapproche(false);
                        vente.setStatutVente(StatutVenteEnum.Integre_En_Instance);
                        log.info("vente est non integere ");

                    }
                }
                else {
                    vente.setVenteIntgre(false);
                    vente.setVenteRapproche(false);
                    vente.setStatutVente(StatutVenteEnum.Non_Integere);
                    vente.setMotif("en attente de code iata du transport ");

                    log.info("en attente de code iata du transport ");

                }


                if (!extractedPnr.isEmpty())
                    vente.setPnr(extractedPnr.get(0));

                if (!extractedSignatureAgent.isEmpty())
                    vente.setSignatureAgent(extractedSignatureAgent.get(0));
                if (extractedClasseReservation != null && !extractedClasseReservation.isEmpty()) {
                    vente.setClasseReservation(extractedClasseReservation.get(0));
                }


                if(!concatenatedNumBillet.isEmpty()){
                   if(venteRepository.existsByNumBillet(concatenatedNumBillet.get(i))){
                        return null;
                    }
                }
                if(!concatenatedNumBillet.isEmpty()){
                    vente.setNumBillet(concatenatedNumBillet.get(i));
                    log.info("le numero billet est "+concatenatedNumBillet.get(i));

                }
                if (!extractedDateEmission.isEmpty() && extractedDateEmission.get(0).matches("\\d+")) {
                    // Define the date and time format patterns
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyy");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

                    // Parse the date and time strings into LocalDate and LocalTime objects
                    LocalDateTime dateTime = LocalDateTime.of(LocalDate.parse(extractedDateEmission.get(0), dateFormatter),
                            LocalTime.parse(extractedTimeEmission.get(0), timeFormatter));

                    vente.setDateEmission(dateTime);
                       } else if (!extractedDateEmission1.isEmpty() && extractedDateEmission1.get(0).matches("\\d{6}")) {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("ddMMyy");

                    // Parse uniquement en LocalDate
                    LocalDate date = LocalDate.parse(extractedDateEmission1.get(0), dateFormatter);

                    // Convertir LocalDate en LocalDateTime à minuit
                    LocalDateTime dateTime = date.atStartOfDay();

                    vente.setDateEmission(dateTime);
                }
                else{
                    LocalDateTime veille = LocalDateTime.now().minusDays(1);
                    vente.setDateEmission(veille);
                }

                if(!concatenatedList.isEmpty()){
                    vente.setCieVol(concatenatedList.get(0));
                }
              /*  if (!extractedOfficeId.isEmpty())
                    vente.setOfficeId(extractedOfficeId.get(indexUPD));*/

                if (!extractedNbrCoupon.isEmpty()) {

                    int size =0;
                    //	vente.setNumCoupon(extractedNbrCoupon.get(0));
                    if(!extractedNbrCoupon.isEmpty()){
                        if( extractedNbrCoupon.get(0) != null){
                            String xmlString = extractedNbrCoupon.get(0);
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();

                            // Create a Document from the XML string
                            ByteArrayInputStream input = new ByteArrayInputStream(xmlString.getBytes());
                            Document document = builder.parse(input);

                            // Get the NodeList of couponsInfo elements
                            NodeList couponsInfoList = document.getElementsByTagName("couponsInfo");

                            // Get the size of couponsInfo
                           size =  couponsInfoList.getLength();
                        }

                    }
                    vente.setNbrCoupon(size);
                    vente.setNbrCouponNonRapprocher(size);
                    log.info("nombre coupon est  size");

                }
                vente.setStatutBillet(StatutBilletEnum.EMIS);
                log.info("vente est emis");


                ventes.add(objectMapper.writeValueAsString(vente));
            }
        }else{
            return null;
        }
        log.info("List des ventes a saver" +ventes);
        return ventes;

    }

       public Object saveVente(List<Vente> ventes) {
        log.info("Start saving ventes");


        // Create an ObjectMapper for deserialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        // Initialize a list to store the deserialized Vente objects
        List<Vente> listeVentes = new ArrayList<>();

        // Loop through each JSON string in the list and deserialize it
        for (Vente vente : ventes) {
           // log.info("Deserializing vente: " + );
            try {
             //   Vente vente = objectMapper.readValue(v, Vente.class);

                OrigineEmission origineEmission = null;
                Collaborateur collaborateur = null;

                if (vente.getNumBillet() != null) {
                    if (vente.getIdOrigineEmission() != null) {
                        origineEmission = origineEmissionRepository.findById(vente.getIdOrigineEmission()).orElse(null);
                    }
                    if (vente.getIdCollaborateur() != null) {
                        collaborateur = collaborateurRepository.findById(vente.getIdCollaborateur()).orElse(null);
                    }
                    if (collaborateur != null) {
                        vente.setCollaborateur(collaborateur);
                    }
                    if (origineEmission != null) {
                        vente.setOrigineEmission(origineEmission);
                    }
                    if (!venteRepository.existsByNumBillet(vente.getNumBillet())) {
                        venteRepository.save(vente);
                        listeVentes.add(vente);
                    }
                }
            } catch (Exception e) {
                log.error("Exception during processing vente: " + e.getMessage(), e);
            }
        }

        return listeVentes;
    }


    public Object saveVenteOne(Vente vente) {
        log.info("Start saving ventes");
        // Créez un objet ObjectMapper pour la désérialisation
    //    ObjectMapper objectMapper = new ObjectMapper();
      //  objectMapper.registerModule(new JavaTimeModule());

        // Initialisez une liste pour stocker les objets RecetteBrute désérialisés
        List<Vente> listeVentes = new ArrayList<>();

        // Parcourez chaque chaîne JSON dans le tableau et désérialisez-la
    //    log.info("Start saving ventes" + items);
        //    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        //  Vente vente = objectMapper.readValue(items, Vente.class);
        if (vente.getNumBillet() != null ) {
            if (vente.getIdCollaborateur() != null) {
                Collaborateur collaborateur = collaborateurRepository.findById(vente.getIdCollaborateur()).get();
                vente.setCollaborateur(collaborateur);
            }
            if (vente.getIdOrigineEmission() != null) {
                OrigineEmission origineEmission = origineEmissionRepository.findById(vente.getIdOrigineEmission()).get();
                vente.setOrigineEmission(origineEmission);
            }
            listeVentes.add(vente);
        }
        venteRepository.saveAll(listeVentes);

        return listeVentes;
    }

    public Map<String, Long[]> constructTransportElementToExtract() {

        // Définissez les informations sur l'emplacement et la taille
        Long[] cieTitre = {0L, 3L};
        Long[] numBillet = {19L, 10L};
        Long[] numCoupon = {29L, 1L};
        Long[] cieVol = {3L, 4L};
        Long[] dateTransport = {8L, 8L};
        Long[] escaleDepart = {52L, 3L};
        Long[] escaleArrivee = {55L, 3L};
        Long[] classeProduit = {58L, 1L};
        Long[] classeReservation = {30L, 4L};
        Long[] montantBrut = {88L, 16L};
        Long[] codeIATA = {268L, 9L};

        Map<String, Long[]> map = new HashMap<String, Long[]>();

        map.put("cieTitre", cieTitre);
        map.put("numBillet", numBillet);
        map.put("numCoupon", numCoupon);
        map.put("cieVol", cieVol);
        map.put("dateTransport", dateTransport);
        map.put("escaleDepart", escaleDepart);
        map.put("escaleArrivee", escaleArrivee);
        map.put("classeProduit", classeProduit);
        map.put("classeReservation", classeReservation);
        map.put("montantBrut", montantBrut);
        map.put("codeIATA", codeIATA);

        return map;
    }

    public String sendProcessedTransportObject(Map<String, String> map) throws JsonProcessingException {

        String cieTitre = null;
        String numBillet = null;
        String numCoupon = null;
        String cieVol = null;
        String dateTransport = null;
        String escaleDepart = null;
        String escaleArrivee = null;
        String classeProduit = null;
        String classeReservation = "";
        String montantBrut = null;
        String codeIATA = null;
        String codeIATAVente = null;
        String officeId = null;
        boolean foundCode = false;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Register JavaTimeModule for LocalDateTime support
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: use ISO-8601 format

        if (map.containsKey("cieTitre"))
            cieTitre = map.get("cieTitre");

        if (map.containsKey("numBillet"))
            numBillet = map.get("numBillet");

        if (map.containsKey("numCoupon"))
            numCoupon = map.get("numCoupon");

        if (map.containsKey("cieVol"))
            cieVol = map.get("cieVol");

        if (map.containsKey("dateTransport"))
            dateTransport = map.get("dateTransport");

        if (map.containsKey("escaleDepart"))
            escaleDepart = map.get("escaleDepart");

        if (map.containsKey("escaleArrivee"))
            escaleArrivee = map.get("escaleArrivee");

        if (map.containsKey("classeProduit"))
            classeProduit = map.get("classeProduit");

        if (map.containsKey("classeReservation"))
            classeReservation = map.get("classeReservation");

        if (map.containsKey("montantBrut"))
            montantBrut = map.get("montantBrut");

        if (map.containsKey("codeIATA"))
            codeIATA = map.get("codeIATA");

        RecetteBrute recetteBrutte = new RecetteBrute();
        String[] classReservationExtract = {null}; 
        classReservationExtract[0] = !classeReservation.equals(" ")? classeReservation.substring(0,1) : null; //" "
        /*
         * RecetteBrute existingRecetteBrute =
         * recetteBruteRepository.findByNumBillet(numBillet);
         *
         * if(existingRecetteBrute != null) recetteBrutte = existingRecetteBrute;
         */
        Vente vente = venteRepository.findByNumBillet(cieTitre+"-"+numBillet);
        codeIATAVente =  vente!= null ? vente.getCodeIATA() : null;
        officeId = vente != null ? vente.getOfficeId() : null;

        Entite entite =null;
        if(codeIATA!= null){
            if (codeIATA.contains("P")) {
                // Si la chaîne contient "P", extrayez les caractères à partir de la deuxième position
                recetteBrutte.setCodeIATA(codeIATA.substring(1)); // Commence à la deuxième position jusqu'à la fin
            } else {
                // Si la chaîne ne contient pas "P", extrayez les sept premiers caractères
                if (codeIATA.length() >= 7) {
                    recetteBrutte.setCodeIATA(codeIATA.substring(0, 7).trim());
                    entite =  entiteRepository.findByCode(codeIATA.substring(0, 7).trim());// Les sept premiers caractères
                    String finalClasseReservation1 = classeReservation;
                    Optional.ofNullable(entite)
                            .map(Entite::getVille)
                            .map(Ville::getPays)
                            .map(Pays::getNom)
                            .filter("Tunisie"::equals)
                            .ifPresentOrElse(
                                    nom -> {
                                        if ("YGIT".equals(finalClasseReservation1)) {
                                            classReservationExtract[0] = "G";
                                        }
                                    },
                                    () -> {
                                        // Code to execute if the Optional is not present
                                        // Example: set classReservationExtract to a default value
                                    //    classReservationExtract[0] = finalClasseReservation1.substring(0,1);
                                    }
                            );


                } else {
                    // Si la chaîne est plus courte que sept caractères, utilisez-la telle quelle
                    recetteBrutte.setCodeIATA(codeIATA.trim());
                }
            }
        }
        if(codeIATAVente!= null){
            recetteBrutte.setCodeIATAVente(codeIATAVente);
        }

        if(!venteRepository.existsByNumBillet(cieTitre+"-"+numBillet)){
            return null;
        }
        String motif = null;

        if(classReservationExtract[0] == null){
            motif = "classe de réservation vide " + classReservationExtract[0];
        }

        if(recetteBruteRepository.findByNumBilletAndCoupon(numCoupon,cieTitre+"-"+numBillet).isPresent()){
            return null;
        }

        ClasseReservation classeReservationCheck = classeReservationRepository.findByCode(classReservationExtract[0]);
        if (classeReservationCheck != null)
            foundCode = true;

        if (entite != null) {
            if (entite.getOrigineEmission() != null) {
                recetteBrutte.setOrigineEmission(entite.getOrigineEmission().getNom());
                recetteBrutte.setRecetteIntegre(true);
                recetteBrutte.setRecetteRapproche(false);
            }

        } else {
            recetteBrutte.setRecetteIntegre(false);
            recetteBrutte.setRecetteRapproche(false);
            String codeIataErr = (recetteBrutte.getCodeIATA() != null ? recetteBrutte.getCodeIATA() : "");
            if(motif != null){
                motif = motif.concat(" , Agence inexistante avec le code " + codeIataErr);
            }
            else{
                motif = "Agence inexistante avec le code " + codeIataErr;
            }
        }

        if (!foundCode ){
            recetteBrutte.setRecetteIntegre(false);
                if(motif != null){
                    motif = motif.concat(" , cette classe de réservation inexistante " + classReservationExtract[0]);
                }
                else{
                    motif = "cette classe de réservation inexistante " + classReservationExtract[0];
                }
           
        }

        recetteBrutte.setCieTitre(cieTitre);
        recetteBrutte.setNumBillet(cieTitre+"-"+numBillet);
        recetteBrutte.setNumCoupon(numCoupon);
        recetteBrutte.setCieVol(cieVol);

        // Créez un formateur de date personnalisé pour le format "ddMMyyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        // Analysez la chaîne en un objet LocalDateTime en utilisant le formateur
        LocalDate localDate = LocalDate.parse(dateTransport, formatter);

        LocalTime localTime = LocalTime.of(0, 0);

        // Convertissez LocalDate en LocalDateTime en utilisant les valeurs de LocalDate
        // et LocalTime
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        recetteBrutte.setDateTransport(localDateTime);
        recetteBrutte.setEscaleDepart(escaleDepart);
        recetteBrutte.setEscaleArrivee(escaleArrivee);
        recetteBrutte.setClasseProduit(classeProduit);
        recetteBrutte.setClasseReservation(classReservationExtract[0]);
        recetteBrutte.setMontantBrut(Double.parseDouble(montantBrut));
        recetteBrutte.setMotif(motif != null ? motif : "");

        return  objectMapper.writeValueAsString(recetteBrutte);
    }

    public Object saveTransport(List<? extends String> items) {

        // Créez un objet ObjectMapper pour la désérialisation
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Initialisez une liste pour stocker les objets RecetteBrute désérialisés
        List<RecetteBrute> listeRecettes = new ArrayList<>();

        // Parcourez chaque chaîne JSON dans le tableau et désérialisez-la
        for (String recetteBrute : items) {
            try {
                RecetteBrute recette = objectMapper.readValue(recetteBrute, RecetteBrute.class);
                Entite entite = null;
                if(recette.getCodeIATA()!=null){
                    Vente vente = venteRepository.findByNumBillet(recette.getNumBillet());
                    recette.setDateEmission(vente.getDateEmission());
                    if(!recette.getCodeIATA().equals(vente.getCodeIATA())){
                        vente.setCodeIATA(recette.getCodeIATA());
                        //OrigineEmission origineEmission = entiteRepository.findByCode(recette.getCodeIATA()).getOrigineEmission();
                       // vente.setOrigineEmission(origineEmission);
                        vente.setMotif("");
                        vente.setQualification("update_recette");
                        entite = entiteRepository.findByCode(recette.getCodeIATA() != null ? recette.getCodeIATA().toUpperCase():null);
                        if (entite != null) {
                            if (entite.getOrigineEmission() != null) {
                                vente.setOrigineEmission(entite.getOrigineEmission());
                            }
                        }
                        vente.setStatutVente(StatutVenteEnum.Non_Rapproche);
                        Collaborateur collaborateur = collaborateurRepository.findBySignature(vente.getSignatureAgent());
//		System.out.println("entite_origine out if : "+ entite.getOrigineEmission().getNom());

                        if (entite != null) {
                            if (entite.getOrigineEmission() != null) {
                                vente.setVenteIntgre(true);
                                vente.setVenteRapproche(false);
                            }
                            if(collaborateur == null ){
                                vente.setStatutVente(StatutVenteEnum.Integre_En_Instance);
                            }
                        } else {
                            vente.setVenteIntgre(false);
                            vente.setVenteRapproche(false);
                            vente.setStatutVente(StatutVenteEnum.Non_Integere);
                            vente.setMotif("Agence inexistante avec le code " + (recette.getCodeIATA() != null ? recette.getCodeIATA() : ""));
                        }
                        venteRepository.save(vente);
                    }
                    listeRecettes.add(recette);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Gérez les exceptions en cas d'échec de la désérialisation
            }
        }

        recetteBruteRepository.saveAll(listeRecettes);

        return listeRecettes;

    }

    public Object saveTransportOne(String items) {

        // Créez un objet ObjectMapper pour la désérialisation
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Initialisez une liste pour stocker les objets RecetteBrute désérialisés
        List<RecetteBrute> listeRecettes = new ArrayList<>();

        // Parcourez chaque chaîne JSON dans le tableau et désérialisez-la
            try {
                objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                RecetteBrute recette = objectMapper.readValue(items, RecetteBrute.class);
                Entite entite = null;
                if(recette.getCodeIATA()!=null){
                    Vente vente = venteRepository.findByNumBillet(recette.getNumBillet());
                    recette.setDateEmission(vente.getDateEmission());
                    if(!recette.getCodeIATA().equals(vente.getCodeIATA())){
                        vente.setCodeIATA(recette.getCodeIATA());
                     //   OrigineEmission origineEmission = entiteRepository.findByCode(recette.getCodeIATA().toUpperCase()) != null ? entiteRepository.findByCode(recette.getCodeIATA().toUpperCase()).getOrigineEmission() : null;
                     //   vente.setOrigineEmission(origineEmission);
                        vente.setQualification("update_recette");
                        vente.setMotif("");
                        entite = entiteRepository.findByCode(recette.getCodeIATA() != null ? recette.getCodeIATA().toUpperCase():null);
                        if (entite != null) {
                            if (entite.getOrigineEmission() != null) {
                                vente.setOrigineEmission(entite.getOrigineEmission());
                            }
                        }
                        vente.setStatutVente(StatutVenteEnum.Non_Rapproche);
                        Collaborateur collaborateur = collaborateurRepository.findBySignature(vente.getSignatureAgent());
//		System.out.println("entite_origine out if : "+ entite.getOrigineEmission().getNom());

                        if (entite != null) {
                            if (entite.getOrigineEmission() != null) {
                                vente.setVenteIntgre(true);
                                vente.setVenteRapproche(false);
                            }
                            if(collaborateur == null ){
                                vente.setStatutVente(StatutVenteEnum.Integre_En_Instance);
                            }
                        } else {
                            vente.setVenteIntgre(false);
                            vente.setVenteRapproche(false);
                            vente.setStatutVente(StatutVenteEnum.Non_Integere);
                            vente.setMotif("Agence inexistante avec le code " + (recette.getCodeIATA() != null ? recette.getCodeIATA() : ""));
                        }
                        venteRepository.save(vente);
                    }
                    listeRecettes.add(recette);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Gérez les exceptions en cas d'échec de la désérialisation
            }

        recetteBruteRepository.saveAll(listeRecettes);

        return listeRecettes;

    }

}

