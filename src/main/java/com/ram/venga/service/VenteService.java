package com.ram.venga.service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import com.mysema.commons.lang.Pair;
import com.ram.venga.domain.*;
import com.ram.venga.mapper.CollaborateurMapper;
import com.ram.venga.mapper.ExportEmissionViewMapper;
import com.ram.venga.model.*;
import com.ram.venga.model.enumeration.*;
import com.ram.venga.projection.*;
import com.ram.venga.repos.*;
import com.ram.venga.util.ExcelGeneratorUtility;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.VenteMapper;
import com.ram.venga.util.NotFoundException;


@Service
public class VenteService {

    private final VenteMapper venteMapper;
    private final CollaborateurMapper collaborateurMapper;
    private final VenteRepository venteRepository;
    private final KeycloackService keycloackService;
    private final CollaborateurRepository collaborateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EntiteRepository entiteRepository;
    private final ExcelExportService excelExportService;
    private final SegmentRepository segmentRepository;
    private final AuditUserActionService auditUserActionService;
    private final UtilisateurService utilisateurService;
    private final OpperationRepository opperationRepository;

    private final ViewRefreshService viewRefreshService;
    private final DemandeInscriptionRepository demandeInscriptionRepository;
    private final RecetteBruteRepository recetteBruteRepository;
    private final ExportEmissionRepository exportEmissionRepository;

    private final ExportEmissionViewMapper exportEmissionViewMapper;
    private final ClasseReservationRepository classeReservationRepository;
    private final QueryEmission queryEmission;
    private final HauteSaisonRepository hauteSaisonRepository;
    private final HauteSaisonService hauteSaisonService;
    private final CollaborateurService collaborateurService;

    public VenteService(final VenteMapper venteMapper,
                        ExportEmissionViewMapper exportEmissionViewMapper,
                        CollaborateurMapper collaborateurMapper, final VenteRepository venteRepository,
                        final OrigineEmissionRepository origineEmissionRepository,
                        final KeycloackService keycloackService,
                        final CollaborateurRepository collaborateurRepository,
                        final UtilisateurRepository utilisateurRepository,
                        final EntiteRepository entiteRepository,
                        final ExcelExportService excelExportService,
                        final SegmentRepository segmentRepository,
                        final JournalRapprochementRepository journalRapprochementRepository,
                        final DemandeInscriptionRepository demandeInscriptionRepository,
                        final RecetteBruteRepository recetteBruteRepository,
                        final AuditUserActionService auditUserActionService,
                        final UtilisateurService utilisateurService, OpperationRepository opperationRepository, ViewRefreshService viewRefreshService, ExportEmissionRepository exportEmissionRepository, ClasseReservationRepository classeReservationRepository, QueryEmission queryEmission, HauteSaisonRepository hauteSaisonRepository, HauteSaisonService hauteSaisonService, CollaborateurService collaborateurService) {
        this.venteMapper = venteMapper;
        this.exportEmissionViewMapper = exportEmissionViewMapper;
        this.collaborateurMapper = collaborateurMapper;
        this.venteRepository = venteRepository;
        this.keycloackService = keycloackService;
        this.collaborateurRepository = collaborateurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.entiteRepository = entiteRepository;
        this.excelExportService = excelExportService;
        this.segmentRepository = segmentRepository;
        this.demandeInscriptionRepository = demandeInscriptionRepository;
        this.recetteBruteRepository = recetteBruteRepository;
        this.auditUserActionService = auditUserActionService;
        this.utilisateurService = utilisateurService;
        this.opperationRepository = opperationRepository;
        this.viewRefreshService = viewRefreshService;
        this.exportEmissionRepository = exportEmissionRepository;
        this.classeReservationRepository = classeReservationRepository;
        this.queryEmission = queryEmission;
        this.hauteSaisonRepository = hauteSaisonRepository;
        this.hauteSaisonService = hauteSaisonService;
        this.collaborateurService = collaborateurService;
    }

    public List<VenteDTO> findAll() {
        final List<Vente> ventes = venteRepository.findAll(Sort.by("dateCreated").descending());
        return ventes.stream()
                .map(vente -> venteMapper.toDto(vente))
                .toList();
    }

    public VenteDTO get(final Long id) {
        return venteRepository.findById(id)
                .map(vente -> venteMapper.toDto(vente))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VenteDTO venteDTO) {
        final Vente vente = new Vente();
        venteMapper.toEntity(venteDTO);
        return venteRepository.save(vente).getId();
    }

    public void update(final Long id, final VenteDTO venteDTO) {
        final Vente vente = venteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        venteMapper.toEntity(venteDTO);
        venteRepository.save(vente);
    }

    public void delete(final Long id) {
        venteRepository.deleteById(id);
    }


    public Map<Collaborateur,java.lang.Integer> TotalVenteByAllCollaborateur() {
        List<Vente> ventes = venteRepository.findAll();
        return ventes.stream()
                .collect(Collectors.groupingBy(Vente::getCollaborateur,
                        Collectors.summingInt(Vente::getNbrPoint)));
    }
   // @Transactional
    public Page<VenteDTO> getEmission(String dateDebut, String dateFin, String origine, String destination, String classeR, String montantDebut, String montantFin, String agence, List<String> portfeuille, List<String> representation, List<StatutVenteEnum> rapprocher, Pageable pageable, String signature, String numBillet, Boolean isArchived) throws ParseException {
        LocalDateTime dateTimeDebut = null;
        LocalDateTime dateTimeFin = null;
        Double montantDebutDouble = 0.0;
        Double montantFinDouble = 0.0;
      //  String motif = null;
        List<StatutVenteEnum> venteEnum = rapprocher;
        // Prétraitement des paramètres
        if (!montantDebut.isEmpty()) {
            montantDebutDouble = Double.valueOf(montantDebut);
        }
        if (!montantFin.isEmpty()) {
            montantFinDouble = Double.valueOf(montantFin);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (!dateFin.isEmpty()) {
            dateTimeFin = LocalDate.parse(dateFin, formatter).atTime(23, 59, 59);
        }
        if (!dateDebut.isEmpty()) {
            dateTimeDebut = LocalDate.parse(dateDebut, formatter).atStartOfDay();
        }

        // Nettoyage des paramètres vides
        origine = origine.isEmpty() ? null : origine;
        destination = destination.isEmpty() ? null : destination;
        classeR = classeR.isEmpty() ? null : classeR;
        numBillet = numBillet.isEmpty() ? null : numBillet;
        String motif1 = null;
        String motif2 = null;
        String motif3 = null;
        String motif4 = null;

        Set<StatutVenteEnum> statutVenteEnumsQuery = new HashSet<>();
        if(rapprocher.isEmpty()){
            List<StatutVenteEnum> isAllData = new ArrayList<>();
            isAllData.add(StatutVenteEnum.Rapproche);
            isAllData.add(StatutVenteEnum.Rapproche_Partiellement);
            isAllData.add(StatutVenteEnum.Non_Integere);
            statutVenteEnumsQuery.addAll(isAllData);

        }
        // Gestion des motifs
            for (StatutVenteEnum rapprocher1 : venteEnum) {
           // String motif = null;
            switch (rapprocher1) {
                case Attente_recette:
                    motif1 = "en attente de code iata du transport";
                  //  statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                case Agence_manquante:
                    motif2 = "Agence inexistante avec le code";
                  //  statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                case CL_manquante:
                    motif3 = "cette classe de réservation inexistante";
                  //  statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                case CL_recette_vide:
                    motif4 = "classe de réservation vide";
                 //   statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                default:
                    statutVenteEnumsQuery.add(rapprocher1);
            }

            }
        List<String> motifList = Arrays.asList(motif1, motif2, motif3, motif4);
        List<String> lowerMotif = motifList != null && !motifList.isEmpty()
                ? motifList.stream()
                .filter(Objects::nonNull) // Filter out null values
                .map(String::toLowerCase) // Convert to lowercase
                .toList()
                : Collections.emptyList();
        if(statutVenteEnumsQuery.isEmpty()){
            statutVenteEnumsQuery = null;
        }
        if(portfeuille.isEmpty()){
            portfeuille = null;
        }
        if(representation.isEmpty()){
            representation = null;
        }
        //  Page<Vente> ventesP = venteRepository.findAll(pageable);

       // pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("dateCreated").descending());
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElseThrow(() -> new RuntimeException("User not found"));
        Page<VenteDTO> ventes;

        if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
            Long entite = utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            List<Long> idEntite = entiteRepository.findByParentId(entite);
            ventes = queryEmission.getEmissionVenteWithDynamicMotifs(dateTimeDebut, dateTimeFin, agence,
                    portfeuille != null ? portfeuille.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,
                    representation != null ? representation.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,idEntite, Optional.ofNullable(statutVenteEnumsQuery)
                            .map(list -> list.stream().toList())
                            .orElse(null), numBillet, classeR, origine, destination, montantDebutDouble, montantFinDouble, signature, false ,lowerMotif, pageable);
        } else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            List<Long> idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
            ventes = queryEmission.getEmissionVenteWithDynamicMotifs(dateTimeDebut, dateTimeFin, agence,
                    portfeuille != null ? portfeuille.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,
                    representation != null ? representation.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,idEntite, Optional.ofNullable(statutVenteEnumsQuery)
                            .map(list -> list.stream().toList())
                            .orElse(null), numBillet, classeR, origine, destination, montantDebutDouble, montantFinDouble, signature, false ,lowerMotif, pageable);
        } else {

            ventes = queryEmission.getEmissionVenteWithDynamicMotifs(dateTimeDebut, dateTimeFin, agence,
                    portfeuille != null ? portfeuille.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,
                    representation != null ? representation.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                                .toList() : null,null, Optional.ofNullable(statutVenteEnumsQuery)
                            .map(list -> list.stream().toList())
                            .orElse(null), numBillet, classeR, origine, destination, montantDebutDouble, montantFinDouble, signature, isArchived ,lowerMotif, pageable);
        }

        // Création de variables locales finales pour les utiliser dans la lambda
        final String finalClasseR = classeR;
        final String finalOrigine = origine;
        final String finalDestination = destination;
        final Double finalMontantDebutDouble = montantDebutDouble;
        final Double finalMontantFinDouble = montantFinDouble;

        // Étape 1 : Récupérer tous les numBillet de ventes
        List<String> numBilletList = ventes.stream()
                .map(VenteDTO::getNumBillet)
                .collect(Collectors.toList());

// Étape 2 : Récupérer tous les SegmentRecetteDTO en une seule requête
        List<SegmentRecetteDTO> allRecettes = recetteBruteRepository.findAllByNumBilletListByFilter(
                numBilletList, finalClasseR, finalOrigine, finalDestination, finalMontantDebutDouble, finalMontantFinDouble
        );

// Étape 3 : Regrouper les recettes par numBillet
        Map<String, List<SegmentRecetteDTO>> recettesMap = allRecettes.stream()
                .collect(Collectors.groupingBy(SegmentRecetteDTO::getNumBillet));

// Étape 4 : Assigner les recettes aux ventes
        ventes.stream().parallel().forEach(vente -> {
            List<SegmentRecetteDTO> recetteBrutes = recettesMap.getOrDefault(vente.getNumBillet(), Collections.emptyList());
            vente.setSegmentDTOList(recetteBrutes);
        });
        return ventes;
    }


    public Page<CollaborateurEntiteDTO> getChiffreAffaire(String dateDebut, String dateFin, String agence,String agent, String portfeuille, String representation,Pageable pageable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTimeDebut = dateDebut.isEmpty() ? null : LocalDate.parse(dateDebut, formatter).atStartOfDay();
        LocalDateTime dateTimeFin = dateFin.isEmpty() ? null : LocalDate.parse(dateFin, formatter).atStartOfDay();
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElseThrow(() -> new RuntimeException("User not found."));
        if(agence.isEmpty()){
            agence=null;
        }
        if(agent.isEmpty()){
            agent=null;
        }

        String agencePattern = agence!= null ?  agence.toLowerCase()  : null;
        String agentPattern = agent != null ?  agent  : null;
        Page<Collaborateur> collaborateurPage = null;
        if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
            Long parent =utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            List<Long> idEntite = entiteRepository.findByParentId(parent);
            collaborateurPage = collaborateurRepository.findAllByAgentAndAgenceWithEntite(agentPattern,idEntite,agencePattern,dateTimeDebut,dateTimeFin,portfeuille,representation,pageable);
        }
        else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            List<Long> idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);
            collaborateurPage = collaborateurRepository.findAllByAgentAndAgenceWithEntite(agentPattern,idEntite,agencePattern,dateTimeDebut,dateTimeFin,portfeuille,representation,pageable);
        }
        else if (utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)) {
            collaborateurPage = collaborateurRepository.findAllByAgentAndAgence(agentPattern,agencePattern,dateTimeDebut,dateTimeFin,portfeuille,representation,pageable);
        } else {
            throw new RuntimeException("Invalid user profile.");
        }
        Page<Collaborateur> collaborateurs = collaborateurPage.map(collaborateur ->{
            Double chiffreAfaire =opperationRepository.findBySignatureWithFilterDate(collaborateur.getSignature(),dateTimeDebut,dateTimeFin);

            collaborateur.setChiffreAffaire(chiffreAfaire != null ? chiffreAfaire.intValue() : 0);

            return collaborateur;
        });
       // List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByRapprocher(dateTimeDebut,dateTimeFin);
       /* Map<String, Double> montantSumByNumbillet = recetteBrutes.stream()
                .collect(Collectors.groupingBy(RecetteBrute::getNumBillet,
                        Collectors.summingDouble(RecetteBrute::getMontantBrut)));*/

        Page<CollaborateurEntiteDTO> collaborateurEntiteDTO = collaborateurs.map(collaborateurMapper::toDtoCollaborateurEntite);

        return collaborateurEntiteDTO;
    }
    @Transactional
    public Page<VenteDTO> checkSignature( String origineEmission,String codeIata, String numBillet,String signature,Long representationId, Long portefeuilleId, Pageable pageable) {
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElse(null);

        if(origineEmission.isEmpty()){
            origineEmission = null;
        }
        if(codeIata.isEmpty()){
            codeIata = null;
        }
        if(numBillet.isEmpty()){
            numBillet= null;
        }
        if(signature.isEmpty()){
            signature= null;
        }
        if (utilisateur != null) {
            Page<Vente> venteRequest = null;
            Page<VenteDTO> venteDTOS;


            if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
                Long entite = utilisateur.getCollaborateur().getEntite().getId();
                List<Long> idEntite = entiteRepository.findByParentId(entite);
                venteRequest = venteRepository.findByVenteRapprocherAttacher(origineEmission,codeIata,numBillet,signature,idEntite, pageable);
            }
            if (utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN) || utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL)) {
                venteRequest = venteRepository.findByVenteRapprocher(origineEmission,codeIata,numBillet,signature,representationId,portefeuilleId, pageable);
            }

            venteDTOS = venteRequest.map(vente -> {
                        VenteDTO venteDTO = venteMapper.toDto(vente);
                        Double sumSolde = opperationRepository.sumSolde(vente.getNumBillet());
                        venteDTO.setNbrPoint(sumSolde != null ? sumSolde.longValue() : 0);
                        Entite entite = entiteRepository.findByCode(vente.getCodeIATA());
                        venteDTO.setAdresseAgence(entite != null ? entite.getAdresse() : "" );
                        venteDTO.setNomAgence(entite != null ? entite.getNom() : "" );
                        venteDTO.setEmailAgence(entite != null ? entite.getEmail() : "" );
                        venteDTO.setTeleAgence(entite != null ? entite.getTelephone() : "" );
                        venteDTO.setNomPortfeuille(entite != null && entite.getParent() != null ? entite.getParent().getNom() : "" );
                        venteDTO.setNomRepresentation(entite != null && entite.getParent() != null &&  entite.getParent().getParent() != null ? entite.getParent().getParent().getNom() : "" );

                        // Assuming these variables are declared and initialized somewhere in your code
                        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByNumBilletList(vente.getNumBillet());

                        List<SegmentRecetteDTO> segmentRecetteDTOS = new ArrayList<>();
                        if(!recetteBrutes.isEmpty()){
                            segmentRecetteDTOS =   recetteBrutes.stream()
                                    .map(recetteBrute -> {
                                        SegmentRecetteDTO segmentRecetteDTO = new SegmentRecetteDTO();
                                        Opperation opperation = opperationRepository.findByRecetteBruteId(recetteBrute.getId());
                                        segmentRecetteDTO.setEscaleDepart(recetteBrute.getEscaleDepart());
                                        segmentRecetteDTO.setEscaleDestination(recetteBrute.getEscaleArrivee());
                                        segmentRecetteDTO.setNumCoupon(recetteBrute.getNumCoupon());
                                        segmentRecetteDTO.setDateTransport(recetteBrute.getDateTransport());
                                        segmentRecetteDTO.setRecetteIntegre(recetteBrute.getRecetteIntegre());
                                        segmentRecetteDTO.setRecetteRapproche(recetteBrute.getRecetteRapproche());
                                        segmentRecetteDTO.setMontantBrut(recetteBrute.getMontantBrut());
                                        segmentRecetteDTO.setNumVol(recetteBrute.getCieVol());
                                        segmentRecetteDTO.setClassReservation(recetteBrute.getClasseReservation());
                                        segmentRecetteDTO.setPointGagne(opperation!=null ? opperation.getDebit() : 0);
                                        segmentRecetteDTO.setMotif(recetteBrute.getMotif());
                                        segmentRecetteDTO.setCodeIATA(recetteBrute.getCodeIATA());
                                        segmentRecetteDTO.setClassProduit(recetteBrute.getClasseProduit());
                                        return segmentRecetteDTO;
                                    })
                                    .collect(Collectors.toList());
                        }

                        venteDTO.setSegmentDTOList(segmentRecetteDTOS);
                        return venteDTO;
                    })
                    ;

            return venteDTOS;
        }

        return Page.empty();
    }

    public ResponseEntity updateSignature(Long id, SignatureDto signature)  {
        Vente vente = venteRepository.findById(id).get();
        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByNumBilletList(vente.getNumBillet());
        Collaborateur collaborateur = collaborateurRepository.findBySignature(signature.getSignature());
        Map<String,String> map = new HashMap<>();
        List<Opperation> opperation = opperationRepository.findByRecetteBrutes(recetteBrutes);

        if(vente != null){
            if(collaborateur == null){
                map.put("message","cette signature est erroné, merci de ressayer !!!!");

                return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

            }
            String oldSignaturedAgent = vente.getSignatureAgent();

            vente.setSignatureAgent(signature.getSignature());
            vente.setCollaborateur(collaborateur);
            if(vente.getStatutVente().equals(StatutVenteEnum.Rapproche_En_Instance)){
                vente.setStatutVente(StatutVenteEnum.Rapproche);
            }
            if(vente.getStatutVente().equals(StatutVenteEnum.Rapproche_Partiellement_EI)){
                vente.setStatutVente(StatutVenteEnum.Rapproche_Partiellement);
            }
           /* opperation.stream().forEach(
                    opperation1 -> opperation1.setSignature(signature.getSignature())
            );*/

                    Integer soldePoint =  opperation.stream().mapToInt(Opperation::getDebit).sum();
                    collaborateur.setSoldePoint(collaborateur.getSoldePoint()+soldePoint);
                    //opperationRepository.saveAll(opperation);
                    collaborateurRepository.save(collaborateur);
                    venteRepository.save(vente);

            Utilisateur currentUser = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).get();
            // Audit signature change event
            auditUserActionService.create(
            	AuditUserActionDTO.getInstance(
            		AuditActionEnum.CHANGEMENT_SIGNATURE, vente.getId(),
            		currentUser!=null ? currentUser.getId(): null,
            		currentUser!=null ? currentUser.getLogin(): null,
            		oldSignaturedAgent, vente.getSignatureAgent()
            	)

            );
        } else {
            map.put("message","aucune vente avec l'id " + id);
            return  new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(vente.getId());
    }
    @Transactional
    public void getEmissionList(String dateDebut, String dateFin, String origine, String destination, String classeR,
                                String montantDebut, String montantFin, String agence, List<String> portfeuille,
                                List<String> representation, HttpServletResponse response, List<StatutVenteEnum> rapprocher,
                                String signature, String numBillet) throws ParseException, IOException {

        // Parse date strings into LocalDateTime
        LocalDateTime dateTimeDebut = Optional.ofNullable(parseDate(dateDebut, "yyyy-MM-dd"))
                .map(date -> date.toLocalDate().atStartOfDay())
                .orElse(null);

        LocalDateTime dateTimeFin = Optional.ofNullable(parseDate(dateFin, "yyyy-MM-dd"))
                .map(date -> date.toLocalDate().atTime(23, 59, 59))
                .orElse(null);

        // Check and set empty strings to null
        destination = checkEmpty(destination);
        origine = checkEmpty(origine);
        classeR = checkEmpty(classeR);
        String motif1 = null;
        String motif2 = null;
        String motif3 = null;
        String motif4 = null;

        Set<StatutVenteEnum> statutVenteEnumsQuery = new HashSet<>();
        if(rapprocher.isEmpty()){
            List<StatutVenteEnum> isAllData = new ArrayList<>();
            isAllData.add(StatutVenteEnum.Rapproche);
            isAllData.add(StatutVenteEnum.Rapproche_Partiellement);
            isAllData.add(StatutVenteEnum.Non_Integere);
            statutVenteEnumsQuery.addAll(isAllData);

        }
        // Gestion des motifs
        for (StatutVenteEnum rapprocher1 : rapprocher) {
            // String motif = null;
            switch (rapprocher1) {
                case Attente_recette:
                    motif1 = "en attente de code iata du transport";
                    //  statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                case Agence_manquante:
                    motif2 = "Agence inexistante avec le code";
                    //  statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                case CL_manquante:
                    motif3 = "cette classe de réservation inexistante";
                    //  statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                case CL_recette_vide:
                    motif4 = "classe de réservation vide";
                    //   statutVenteEnumsQuery.add(StatutVenteEnum.Non_Integere);
                    break;
                default:
                    statutVenteEnumsQuery.add(rapprocher1);
            }

        }
        List<String> motifList = Arrays.asList(motif1, motif2, motif3, motif4);
        List<String> lowerMotif = motifList != null && !motifList.isEmpty()
                ? motifList.stream()
                .filter(Objects::nonNull) // Filter out null values
                .map(String::toLowerCase) // Convert to lowercase
                .toList()
                : Collections.emptyList();
        if(rapprocher.isEmpty()){
            rapprocher = null;
        }
        if(portfeuille.isEmpty()){
            portfeuille = null;
        }
        if(representation.isEmpty()){
            representation = null;
        }

        // Parse montantDebut and montantFin into Double
        Double montantDebutInteger = parseDouble(montantDebut);
        Double montantFinInteger = parseDouble(montantFin);

        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"N DE BILLET","NOMBRE COUPON","DATE EMISSION","PNR", "CODE IATA","AGENCE EMETTRICE","SIGNATEURE AGENT","NOM AGENT","NOM AGENCE","POINT ATTRIBUÉS",
                "STATUT VENTE","MOBILE AGENT","EMAIL AGENT", "NUMÉRO VOL", "NUMÉRO COUPON", "DATE TRANSPORT",
                 "ESCALE DÉPART", "ESCALE ARRIVÉE","CLASSE RESERVATION","MONTANT BRUT","REMARQUE VENTE","REMARQUE RECETTE"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        viewRefreshService.refreshExportEmissionView();


        // Fetch vente data based on user profile in batches
        List<ExportEmissionView> exportEmissionViews;
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (utilisateur.getProfil() == ProfilEnum.RATTACHE_COMMERCIAL) {
            Long idEntiteRa = utilisateur.getCollaborateur().getEntite().getId();
            List<Long> idEntite = entiteRepository.findByParentId(idEntiteRa);
            exportEmissionViews = queryEmission.getEmissionVenteWithDynamicMotifsList(dateTimeDebut, dateTimeFin,agence, portfeuille != null ? portfeuille.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,
                    representation != null ? representation.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,idEntite,rapprocher,numBillet,classeR,
                    origine, destination, montantDebutInteger, montantFinInteger
                    , signature,lowerMotif);
        }else if (utilisateur.getProfil() == ProfilEnum.CONSULTANT) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            List<Long> entite = entiteRepository.findByRepresentationAgence(idEntiteRa);
            exportEmissionViews =queryEmission.getEmissionVenteWithDynamicMotifsList(dateTimeDebut, dateTimeFin,agence, portfeuille != null ? portfeuille.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,
                    representation != null ? representation.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,entite,rapprocher,numBillet,classeR,
                    origine, destination, montantDebutInteger, montantFinInteger
                    , signature,lowerMotif);
        }
        else {
            exportEmissionViews = queryEmission.getEmissionVenteWithDynamicMotifsList(dateTimeDebut, dateTimeFin,agence, portfeuille != null ? portfeuille.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,
                    representation != null ? representation.stream()
                            .filter(Objects::nonNull)
                            .map(String::toLowerCase)
                            .toList() : null,null,rapprocher,numBillet,classeR,
                    origine, destination, montantDebutInteger, montantFinInteger
                    , signature,lowerMotif);
        }

        // Process vente data and populate the sheet in batches
        try {
            int rowNum = 1;

                for (ExportEmissionView exportEmissionView : exportEmissionViews) {
                    ExportEmissionDTO exportEmissionDTO = exportEmissionViewMapper.toDto(exportEmissionView);
                    Row row = sheet.createRow(rowNum++);
                    populateRowWithData(row, exportEmissionDTO);

            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Liste_Emission.xlsx");

            // Write workbook content to response
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String checkEmpty(String value) {
        return value.isEmpty() ? null : value;
    }
    private LocalDateTime parseDate(String dateString, String pattern) {
        return dateString.isEmpty() ? null : LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern)).atStartOfDay();
    }

    private Double parseDouble(String doubleString) {
        return doubleString.isEmpty() ? 0 : Double.valueOf(doubleString);
    }

    private void populateRowWithData(Row row, ExportEmissionDTO exportEmissionDTO) {
            // Create a DateTimeFormatter for the desired date format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            row.createCell(0).setCellValue(exportEmissionDTO.getNumBillet());//ok
            row.createCell(1).setCellValue(exportEmissionDTO.getNbrCoupon() != null ? exportEmissionDTO.getNbrCoupon() : 0);//ok
            LocalDate dateEmissionL =exportEmissionDTO.getDateEmission() != null ? exportEmissionDTO.getDateEmission().toLocalDate() : null;
            String dateEmission = (dateEmissionL != null) ? dateEmissionL.format(formatter) : null;
            row.createCell(2).setCellValue(dateEmission); //ok
            row.createCell(3).setCellValue(exportEmissionDTO.getPnr() != null ? exportEmissionDTO.getPnr() : "");//ok
            row.createCell(4).setCellValue(exportEmissionDTO.getCodeIATA() != null ? exportEmissionDTO.getCodeIATA(): "");//ok
            row.createCell(5).setCellValue(exportEmissionDTO.getNomEntite()!= null ? exportEmissionDTO.getNomEntite() : "");//ok
            row.createCell(6).setCellValue(exportEmissionDTO.getSignature() != null ? exportEmissionDTO.getSignature() : "");//ok
            row.createCell(7).setCellValue(exportEmissionDTO.getNomCollaborateur() != null ? exportEmissionDTO.getNomCollaborateur() : "");//ok
            row.createCell(8).setCellValue(exportEmissionDTO.getCollaborateur() != null && exportEmissionDTO.getCollaborateur().getEntite() != null ? exportEmissionDTO.getCollaborateur().getEntite().getNom() : "");//ok
            row.createCell(9).setCellValue(exportEmissionDTO.getDebit()!= null ? exportEmissionDTO.getDebit(): 0);
            row.createCell(10).setCellValue(exportEmissionDTO.getStatutVente()!= null ? exportEmissionDTO.getStatutVente().toString(): "");
            row.createCell(11).setCellValue(exportEmissionDTO.getCollaborateur() != null ? exportEmissionDTO.getCollaborateur().getMobile(): "");
            row.createCell(12).setCellValue(exportEmissionDTO.getCollaborateur() != null ? exportEmissionDTO.getCollaborateur().getEmail(): "");
            row.createCell( 13).setCellValue(exportEmissionDTO.getCieVol()!= null ? exportEmissionDTO.getCieVol(): "");
            row.createCell( 14).setCellValue(exportEmissionDTO.getNumCoupon()!= null ? exportEmissionDTO.getNumCoupon(): "");
            // Assuming exportEmissionDTO.getDateTransport() returns a LocalDate
            LocalDate dateTransportL = exportEmissionDTO.getDateTransport() != null ? exportEmissionDTO.getDateTransport().toLocalDate() : null ;
            // Format the LocalDate to a string using the formatter
            String dateTransport = (dateTransportL != null) ? dateTransportL.format(formatter) : null;
            // Set the formatted date string to the Excel cell
            row.createCell(15).setCellValue(dateTransport);
            row.createCell( 16).setCellValue(exportEmissionDTO.getEscaleDepart()!= null ? exportEmissionDTO.getEscaleDepart(): "" );
            row.createCell( 17).setCellValue(exportEmissionDTO.getEscaleArrivee()!= null ? exportEmissionDTO.getEscaleArrivee(): "" );
            row.createCell( 18).setCellValue(exportEmissionDTO.getClasseReservation()!= null ? exportEmissionDTO.getClasseReservation(): "" );
            row.createCell( 19).setCellValue(exportEmissionDTO.getMontantBrut()!= null ? exportEmissionDTO.getMontantBrut(): 0 );
            row.createCell( 20).setCellValue(exportEmissionDTO.getMotif_vente()!= null ? exportEmissionDTO.getMotif_vente(): "" );
            row.createCell( 21).setCellValue(exportEmissionDTO.getMotif_recette()!= null ? exportEmissionDTO.getMotif_recette(): "" );

    }



    public Set<String> getOrigine() {
        return segmentRepository.findByEscalDepart();
    }

    public Set<String> getDestination() {

        return segmentRepository.findByEscalArriver();

    }

    public HandlerDto checkSignatureAgent(SignatureDto signature) {
        String idUser = keycloackService.getIdUserToken();
        HandlerDto dto = new HandlerDto();
       Map<Boolean,String> map = new HashMap<>();
        Collaborateur collaborateur;
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).get();
       if(utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)){
           Long entite = utilisateur.getCollaborateur().getEntite().getId();
           List<Long> idEntite = entiteRepository.findByParentId(entite);

           collaborateur = collaborateurRepository.findBySignatureWithEntite(signature.getSignature(),idEntite);
            if(collaborateur == null){
                dto.setStatus(HandlerEnum.refusé);
                dto.setMessage("cette signature ne correspond a aucun agent");
            }else{
                dto.setStatus(HandlerEnum.validé);
                dto.setMessage("Le nombre de points lié à ce billet sera automatiquement attribué à l’agent : "+collaborateur.getNom());
            }
        }else{
            collaborateur = collaborateurRepository.findBySignature(signature.getSignature());
            if(collaborateur == null){
                dto.setStatus(HandlerEnum.refusé);
                dto.setMessage("Cette signature ne correspond a aucun agent");

            }else{
                dto.setStatus(HandlerEnum.validé);
                dto.setMessage("Le nombre de points lié à ce billet sera automatiquement attribué à l’agent : "+collaborateur.getNom());
            }
        }

        return dto;
    }

    public void getChiffreAffaireExport(String dateDebut, String dateFin, String agence,String agent, String portfeuille, String representation,HttpServletResponse response) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTimeDebut = dateDebut.isEmpty() ? null : LocalDate.parse(dateDebut, formatter).atStartOfDay();
        LocalDateTime dateTimeFin = dateFin.isEmpty() ? null : LocalDate.parse(dateFin, formatter).atStartOfDay();
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElseThrow(() -> new RuntimeException("User not found."));
        if(agence.isEmpty()){
            agence=null;
        }
        if(agent.isEmpty()){
            agent=null;
        }
        String agencePattern = agence!= null ?  agence.toLowerCase()  : null;
        String agentPattern = agent != null ?  agent  : null;
        List<CollaborateursChiffreAffaireReportProjection> collaborateurPage = null;
        if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
            Long parent =utilisateur.getCollaborateur() != null && utilisateur.getCollaborateur().getEntite() != null ? utilisateur.getCollaborateur().getEntite().getId() : null;
            List<Long> idEntite = entiteRepository.findByParentId(parent);
            collaborateurPage = collaborateurRepository.findAllByAgentAndAgenceWithEntiteList(agentPattern,idEntite,agencePattern,dateTimeDebut,dateTimeFin,portfeuille,representation);
        } else if (utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL) || utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)) {
            collaborateurPage = collaborateurRepository.findAllByAgentAndAgenceList(agentPattern,agencePattern,dateTimeDebut,dateTimeFin,portfeuille,representation);
        } else if (ProfilEnum.CONSULTANT.equals(utilisateur.getProfil())) {
            Set<Entite> idEntiteRa = utilisateur.getCollaborateur().getEntites();
            List<Long> idEntite = entiteRepository.findByRepresentationAgence(idEntiteRa);

            collaborateurPage = collaborateurRepository.findAllByAgentAndAgenceWithEntiteList(agentPattern,idEntite,agencePattern,dateTimeDebut,dateTimeFin,portfeuille,representation);
        }
        else {
            throw new RuntimeException("Invalid user profile.");
        }

        exporChiffreAffaireToExcelAdmins(collaborateurPage,response);
         }

 /*   private void populateRowWithDataCollaborateur(Row row, CollaborateurDTO collaborateurDTO) {
        row.createCell(0).setCellValue(collaborateurDTO.getCode()!=null ? collaborateurDTO.getCode() : "");
        row.createCell(1).setCellValue(collaborateurDTO.getNomAgence() != null ? collaborateurDTO.getNomAgence() : "");
        row.createCell(2).setCellValue(collaborateurDTO.getNomAgent() != null ? collaborateurDTO.getNomAgent() : "");
        row.createCell(3).setCellValue(collaborateurDTO.getSignature() != null ? collaborateurDTO.getSignature() : "");
        row.createCell(4).setCellValue(collaborateurDTO.getChiffreAffaire() != null ? collaborateurDTO.getChiffreAffaire() : 0);
        row.createCell(5).setCellValue(collaborateurDTO.getAdresseAgence() != null ? collaborateurDTO.getAdresseAgence() : "");
        row.createCell(6).setCellValue(collaborateurDTO.getEmailAgence()!= null ? collaborateurDTO.getEmailAgence() : "");
        row.createCell(7).setCellValue(collaborateurDTO.getEmailAgent()!= null ? collaborateurDTO.getEmailAgent(): "");
        row.createCell(8).setCellValue(collaborateurDTO.getTeleAgence()!= null ? collaborateurDTO.getTeleAgence(): "");
        row.createCell( 9).setCellValue(collaborateurDTO.getMobile()!= null ? collaborateurDTO.getMobile(): "");
    }*/

    public void exporChiffreAffaireToExcelAdmins(List<CollaborateursChiffreAffaireReportProjection> collaborateurs , HttpServletResponse response) {
        List<Pair<String, Function<CollaborateursChiffreAffaireReportProjection, String>>> pairs = new ArrayList<>();
        pairs.add(Pair.of("Signature", CollaborateursChiffreAffaireReportProjection::getSignature));
        pairs.add(Pair.of("Nom", CollaborateursChiffreAffaireReportProjection::getNom));
        pairs.add(Pair.of("Prenom", CollaborateursChiffreAffaireReportProjection::getPrenom));
        pairs.add(Pair.of("Email Agences", CollaborateursChiffreAffaireReportProjection::getEmailAgences));
        pairs.add(Pair.of("Email Agent", CollaborateursChiffreAffaireReportProjection::getEmailAgent));
        pairs.add(Pair.of("Adresse Agence", CollaborateursChiffreAffaireReportProjection::getAdressAgence));
        pairs.add(Pair.of("Agence", CollaborateursChiffreAffaireReportProjection::getAgence));
        pairs.add(Pair.of("Code IATA", CollaborateursChiffreAffaireReportProjection::getCodeIATA));
        pairs.add(Pair.of("Portefeuille", CollaborateursChiffreAffaireReportProjection::getPortefeuille));
        pairs.add(Pair.of("Representation", CollaborateursChiffreAffaireReportProjection::getRepresentation));
        pairs.add(Pair.of("Chiffre D'affaires", proj -> proj.getChiffreAffaires() != null ? proj.getChiffreAffaires().toString() : ""));
        pairs.add(Pair.of("Telephone Agence", CollaborateursChiffreAffaireReportProjection::getTeleAgence));
        pairs.add(Pair.of("Mobile Agent", CollaborateursChiffreAffaireReportProjection::getMobileAgent));
        ExcelGeneratorUtility.generateExcelReport(response, collaborateurs, pairs);
    }

    public ResponseEntity<?> updateSignatureGrouped(SignatureDto signature) {
        List<Vente> ventes = venteRepository.findBySignatureNotInCollaborateur(signature.getOldSignature());
        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByNumBilletListIn(
                ventes.stream().map(Vente::getNumBillet).toList()
        );
        Collaborateur collaborateur = collaborateurRepository.findBySignature(signature.getSignature());
        Map<String, String> map = new HashMap<>();

        if (collaborateur == null) {
            map.put("message", "Cette signature est erronée, merci de réessayer !!!!");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        List<Opperation> opperations = opperationRepository.findByRecetteBrutes(recetteBrutes);
        int soldePoint = opperations.stream().mapToInt(Opperation::getDebit).sum();
        collaborateur.setSoldePoint(collaborateur.getSoldePoint() + soldePoint);

        for (Vente vente : ventes) {
            if (vente == null) {
                map.put("message", "Aucune vente avec l'id " + vente.getId());
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }

            String oldSignaturedAgent = vente.getSignatureAgent();

            vente.setSignatureAgent(signature.getSignature());
            vente.setCollaborateur(collaborateur);

            if (vente.getStatutVente().equals(StatutVenteEnum.Rapproche_En_Instance)) {
                vente.setStatutVente(StatutVenteEnum.Rapproche);
            } else if (vente.getStatutVente().equals(StatutVenteEnum.Rapproche_Partiellement_EI)) {
                vente.setStatutVente(StatutVenteEnum.Rapproche_Partiellement);
            }


            // Uncomment and save operations if needed
            // opperationRepository.saveAll(opperations);

            collaborateurRepository.save(collaborateur);
            venteRepository.save(vente);

            Utilisateur currentUser = utilisateurService.findByKckRefUser(keycloackService.getIdUserToken()).orElse(null);

            // Audit signature change event
            auditUserActionService.create(
                    AuditUserActionDTO.getInstance(
                            AuditActionEnum.CHANGEMENT_SIGNATURE, vente.getId(),
                            currentUser!=null ? currentUser.getId(): null,
                            currentUser!=null ? currentUser.getLogin(): null,
                            oldSignaturedAgent, vente.getSignatureAgent()
                    )

            );
        }

        return ResponseEntity.ok("Signature updated successfully.");
    }

    public void exportCheckSignature( HttpServletResponse response,String origineEmission,String codeIata, String numBillet,String signature,Long representationId, Long portefeuilleId) {
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser).orElse(null);

        if(origineEmission.isEmpty()){
            origineEmission = null;
        }
        if(codeIata.isEmpty()){
            codeIata = null;
        }
        if(numBillet.isEmpty()){
            numBillet= null;
        }
        if(signature.isEmpty()){
            signature= null;
        }
        if (utilisateur != null) {
            List<CheckSignatureVenteRepost> venteRequest = null;


            if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
                Long entite = utilisateur.getCollaborateur().getEntite().getId();
                venteRequest = venteRepository.findByVenteRapprocherAttacherList(origineEmission,codeIata,numBillet,signature,entite);
            }
            if (utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN) || utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL)) {
                venteRequest = venteRepository.findByVenteRapprocherList(origineEmission,codeIata,numBillet,signature,representationId,portefeuilleId);
            }
            List<Pair<String, Function<CheckSignatureVenteRepost, String>>> pairs = new ArrayList<>();
            pairs.add(Pair.of("Origine Emission", CheckSignatureVenteRepost::getOrigineEmission));
            pairs.add(Pair.of("code iata", CheckSignatureVenteRepost::getCodeIata));
            pairs.add(Pair.of("Num Billet", CheckSignatureVenteRepost::getNumBillet));
            pairs.add(Pair.of("Nbr Coupon", CheckSignatureVenteRepost::getNbrCoupon));
            pairs.add(Pair.of("Nbr Point", CheckSignatureVenteRepost::getNbrPoint));
            pairs.add(Pair.of("Signature", CheckSignatureVenteRepost::getSignature));
            pairs.add(Pair.of("Representation", CheckSignatureVenteRepost::getRepresentation));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            pairs.add(Pair.of("Date Emission", proj -> proj.getDateEmission().format(formatter)));
            pairs.add(Pair.of("Portfeuille",CheckSignatureVenteRepost::getPortfeuille ));

            ExcelGeneratorUtility.generateExcelReport(response, venteRequest, pairs);


        }
    }
}