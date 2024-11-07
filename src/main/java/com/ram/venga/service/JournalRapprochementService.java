package com.ram.venga.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ram.venga.domain.*;
import com.ram.venga.mapper.JournalRapprochementViewMapper;
import com.ram.venga.mapper.RecetteBruteMapper;
import com.ram.venga.mapper.VenteMapper;
import com.ram.venga.model.HauteSaisonDTO;
import com.ram.venga.model.RecetteBruteRapprochementDTO;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.model.enumeration.StatutRapprochementEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import com.ram.venga.repos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ram.venga.mapper.JournalRapprochementMapper;
import com.ram.venga.model.JournalRapprochementDTO;
import com.ram.venga.util.NotFoundException;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class JournalRapprochementService {

	private final JournalRapprochementMapper journalRapprochementMapper;
    private final JournalRapprochementRepository journalRapprochementRepository;
    private final VenteRepository venteRepository;
    private final RecetteBruteRepository recetteBruteRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final CollaborateurService collaborateurService;
    private final PrimeRepository primeRepository;
    private final BonCommandeRepository bonCommandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final OpperationRepository opperationRepository;
    private final JournalRapprochementViewMapper journalRapprochementViewMapper;
    @Value("${mois.archivage}")
    private long dateArchive;
    @Autowired
    private final ViewRefreshService viewRefreshService;

    private final VenteMapper venteMapper;
    private final RecetteBruteMapper  recetteBruteMapper;
    private final HauteSaisonRepository hauteSaisonRepository;
    private final ClasseProduitRepository classeProduitRepository;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final ConcoursRepository concoursRepository;
    private final SegmentRepository segmentRepository;
    List<Collaborateur> collaborators = null;
    private final KeycloackService keycloackService;
    private final EntiteRepository entiteRepository;
    private final ClasseReservationRepository classeReservationRepository;
    private final JournalRapprochementViewRepository journalRapprochementViewRepository;



    public JournalRapprochementService(
            final JournalRapprochementMapper journalRapprochementMapper,
            final JournalRapprochementRepository journalRapprochementRepository,
            final VenteRepository venteRepository,
            final RecetteBruteRepository recetteBruteRepository, CollaborateurRepository collaborateurRepository, CollaborateurService collaborateurService, PrimeRepository primeRepository, BonCommandeRepository bonCommandeRepository, UtilisateurRepository utilisateurRepository, OpperationRepository opperationRepository, JournalRapprochementViewMapper journalRapprochementViewMapper, ViewRefreshService viewRefreshService, VenteMapper venteMapper, RecetteBruteMapper recetteBruteMapper, HauteSaisonRepository hauteSaisonRepository, ClasseProduitRepository classeProduitRepository, OrigineEmissionRepository origineEmissionRepository, ConcoursRepository concoursRepository, SegmentRepository segmentRepository, KeycloackService keycloackService, EntiteRepository entiteRepository, ClasseReservationRepository classeReservationRepository, JournalRapprochementViewRepository journalRapprochementViewRepository) {
    	this.journalRapprochementMapper = journalRapprochementMapper;
        this.journalRapprochementRepository = journalRapprochementRepository;
        this.venteRepository = venteRepository;
        this.recetteBruteRepository = recetteBruteRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.collaborators = collaborateurRepository.findAllWithCollaborateur();
        this.collaborateurService = collaborateurService;
        this.primeRepository = primeRepository;
        this.bonCommandeRepository = bonCommandeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.opperationRepository = opperationRepository;
        this.journalRapprochementViewMapper = journalRapprochementViewMapper;
        this.viewRefreshService = viewRefreshService;
        this.venteMapper = venteMapper;
        this.recetteBruteMapper = recetteBruteMapper;
        this.hauteSaisonRepository = hauteSaisonRepository;
        this.classeProduitRepository = classeProduitRepository;
        this.origineEmissionRepository = origineEmissionRepository;
        this.concoursRepository = concoursRepository;
        this.segmentRepository = segmentRepository;
        this.keycloackService = keycloackService;
        this.entiteRepository = entiteRepository;
        this.classeReservationRepository = classeReservationRepository;
        this.journalRapprochementViewRepository = journalRapprochementViewRepository;
    }

    public List<Collaborateur> getCollaborators() {
        return this.collaborators;
    }
    public Page<JournalRapprochementDTO> findAll(
            String numBillet,
            StatutRapprochementEnum statutRapprochementEnum,
            String dateDebut,
            String dateFin,
            StatutVenteEnum rapprocher,
            Boolean isArchived,
            Pageable pageable
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTimeDebut = dateDebut.isEmpty() ? null : LocalDate.parse(dateDebut, formatter);
        LocalDate dateTimeFin = dateFin.isEmpty() ? null : LocalDate.parse(dateFin, formatter);

        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Page<JournalRapprochementView> journalRapprochementPage = null;
        viewRefreshService.refreshJournalRapprochementView();
        List<Long> idEntite = null;
        if (utilisateur.getProfil().equals(ProfilEnum.RATTACHE_COMMERCIAL)) {
            Long idEntiteRa = utilisateur.getCollaborateur().getEntite().getId();
            idEntite = entiteRepository.findByParentId(idEntiteRa);
            journalRapprochementPage = journalRapprochementViewRepository.findDistinctByVente(
                    numBillet,
                    dateTimeDebut,
                    dateTimeFin,
                    rapprocher,
                    //        idEntite,
                    isArchived,
                    pageable
            );
        }

        if (utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN) || utilisateur.getProfil().equals(ProfilEnum.ADMIN_FONCTIONNEL)) {
            journalRapprochementPage = journalRapprochementViewRepository.findDistinctByVente(
                    numBillet,
                    dateTimeDebut,
                    dateTimeFin,
                    rapprocher,
            //        idEntite,
                    isArchived,
                    pageable
            );
        } else {
            throw new RuntimeException("Invalid user profile.");
        }

        Page<JournalRapprochementDTO> journalRapprochementDTOS = journalRapprochementPage.map(journalRapprochementViewMapper::toDto);

          List<String> numBillets = journalRapprochementDTOS.stream()
                .map(JournalRapprochementDTO::getNumBillet)
                .collect(Collectors.toList());

        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByListNumBillet(numBillets);

        for (JournalRapprochementDTO dto : journalRapprochementDTOS.getContent()) {
            List<RecetteBruteRapprochementDTO> tempRecetteBruteRapprochementDTOS = recetteBrutes.stream()
                    .filter(recetteBrute -> recetteBrute.getNumBillet().equals(dto.getNumBillet()))
                    .map(recetteBrute -> {
                        RecetteBruteRapprochementDTO rapprochementDTO = new RecetteBruteRapprochementDTO();
                        rapprochementDTO.setStatut(recetteBrute.getRecetteRapproche());
                        rapprochementDTO.setNumCoupon(recetteBrute.getNumCoupon());
                        return rapprochementDTO;
                    })
                    .collect(Collectors.toList());

            dto.setRecetteRapprochertDto(tempRecetteBruteRapprochementDTOS);
        }

        return journalRapprochementDTOS;

    }
        public JournalRapprochementDTO get(final Long id) {
        return journalRapprochementRepository.findById(id)
        		.map(journalRapprochement -> journalRapprochementMapper.toDto(journalRapprochement))
                .orElseThrow(NotFoundException::new);
    }

    public ResponseEntity<?> create() {
        log.info("Debut rapprochement :");
        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByIntegre();
        log.info("Nombre recette brute intégrée et non rapprochées : "+recetteBrutes.size());
        for (RecetteBrute recetteBrute : recetteBrutes) {
            log.info("Rapprochement recette brute n° " + recetteBrute.getNumBillet());
            try{
                Vente vente = venteRepository.findByNumBilletAndIntegrer(recetteBrute.getNumBillet());
                JournalRapprochement journalRapprochement = new JournalRapprochement();
                journalRapprochement.setDate(LocalDateTime.now());

                if (vente != null) {
                    log.info("Vente id : "+vente.getId());
                    journalRapprochement.setRecette(recetteBrute);
                    journalRapprochement.setStatut(StatutRapprochementEnum.TRAITE);
                    journalRapprochement.setVente(vente);
                    journalRapprochement.setNumBillet(recetteBrute.getNumBillet());
                    journalRapprochementRepository.save(journalRapprochement);

                    traitementPoint(vente, recetteBrute);

                    Integer nbrCouponNonRapprocher = vente.getNbrCouponNonRapprocher();
                    int sum = nbrCouponNonRapprocher != null ? nbrCouponNonRapprocher - 1 : 0;
                    nbrCouponNonRapprocher = sum;
                    vente.setNbrCouponNonRapprocher(sum);
                    recetteBrute.setRecetteRapproche(true);

                    //Update statut vente selon nombre coupon rapproché
                    int nbrCoupon = vente.getNbrCoupon();

                    if (nbrCouponNonRapprocher == 0 && nbrCoupon != 0 && vente.getCollaborateur() != null) {
                        vente.setVenteRapproche(true);
                        vente.setStatutVente(StatutVenteEnum.Rapproche);
                    }
                    else if (nbrCouponNonRapprocher == 0 && nbrCoupon != 0 && vente.getCollaborateur() == null) {
                        vente.setVenteRapproche(true);
                        vente.setStatutVente(StatutVenteEnum.Rapproche_En_Instance);
                    }
                    else if (nbrCouponNonRapprocher != 0 && nbrCouponNonRapprocher < nbrCoupon && vente.getCollaborateur() != null) {
                        vente.setVenteRapproche(false);
                        vente.setStatutVente(StatutVenteEnum.Rapproche_Partiellement);
                    }
                    else if (nbrCouponNonRapprocher != 0 && nbrCouponNonRapprocher < nbrCoupon && vente.getCollaborateur() == null) {
                        vente.setVenteRapproche(false);
                        vente.setStatutVente(StatutVenteEnum.Rapproche_Partiellement_EI);
                    }
                    else {
                        vente.setVenteRapproche(false);
                        vente.setStatutVente(StatutVenteEnum.Non_Rapproche);
                    }
                    venteRepository.save(vente);
                } else {
                    log.info("Vente non trouvé pour n° billet: "+recetteBrute.getNumBillet());
                    journalRapprochement.setVente(null);
                    journalRapprochement.setStatut(StatutRapprochementEnum.REJETE);
                    journalRapprochement.setRecette(recetteBrute);
                    journalRapprochement.setNumBillet(recetteBrute.getNumBillet());
                    journalRapprochementRepository.save(journalRapprochement);
                }
                recetteBruteRepository.save(recetteBrute);
            }catch (Exception e){
                e.printStackTrace();
                log.info("Erreur rapprochement recette brute n° "+recetteBrute.getNumBillet());
            }
        }
        return ResponseEntity.ok().build();
    }


    public void update(final Long id, final JournalRapprochementDTO journalRapprochementDTO) {
        final JournalRapprochement journalRapprochement = journalRapprochementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        journalRapprochementMapper.toEntity(journalRapprochementDTO);
        journalRapprochementRepository.save(journalRapprochement);
    }

    public void delete(final Long id) {
        journalRapprochementRepository.deleteById(id);
    }
    public void traitementPoint(Vente sale, RecetteBrute recetteBrute) {
        if(sale.getDateEmission() == null){
            return;
        }
        Collaborateur collaborateur = sale.getCollaborateur();

        Segment segment = segmentRepository.findByEscalDepartAndEscalArriver(recetteBrute.getEscaleDepart(), recetteBrute.getEscaleArrivee());
        OrigineEmission origineEmission = origineEmissionRepository.findByNom(recetteBrute.getOrigineEmission());
        ClasseReservation classeReservation = classeReservationRepository.findByCode(recetteBrute.getClasseReservation());
        Prime prime = null;
        Concours concours = null;

        if (classeReservation != null && classeReservation.getClasseProduit() != null ) {
            prime = primeRepository.findByOrigineEmissionIdAndclassProduitAndSegement(
                    origineEmission.getId(), classeReservation.getClasseProduit().getCode(), segment.getId());
            if(sale.getDateEmission() != null){
                concours = concoursRepository.findByClasseProduitLibelle(
                        classeReservation.getClasseProduit().getCode().toLowerCase(), recetteBrute.getDateTransport().toLocalDate(),sale.getDateEmission().toLocalDate(),sale.getOrigineEmission().getId());

            }
        }

        int primePoints = (prime != null) ? prime.getNbrPoint() : 0;
        double promotionFactor = (concours != null) ? concours.getFacteurPromotion() : 1;
        double psMajoration = (concours != null) ? concours.getPsMajoration() : 0;
        double result = (promotionFactor * primePoints + psMajoration);
        int currentPoints = 0;
        List<HauteSaison> hauteSaisons = hauteSaisonRepository.findAllByOrigineEmission(sale.getOrigineEmission().getId());
            if ( !collaborateurService.isRecetteInHauteSaison(sale, hauteSaisons)) {
                if(collaborateur != null ){
                    currentPoints = (collaborateur.getSoldePoint() != null) ? collaborateur.getSoldePoint().intValue() : 0;
                double currentPointsChiffre = (collaborateur.getChiffreAffaire() != null) ? collaborateur.getChiffreAffaire().intValue() : 0;

                collaborateur.setSoldePoint((int) result + currentPoints);
                collaborateur.setChiffreAffaire((int) (currentPointsChiffre + recetteBrute.getMontantBrut()));

                Opperation operation = new Opperation();
                operation.setSolde((int) result + currentPoints);
                operation.setBonCommande(null);
                operation.setDate(LocalDateTime.now());
                operation.setDebit((int) result);
                operation.setCredit(0);
                operation.setRecetteBrute(recetteBrute);
                operation.setSignature(sale.getSignatureAgent());
                collaborateurRepository.save(collaborateur);
                opperationRepository.save(operation);
            }
                else {
                    Opperation operation = new Opperation();
                    operation.setSolde((int) result + currentPoints);
                    operation.setBonCommande(null);
                    operation.setDate(LocalDateTime.now());
                    operation.setDebit((int) result);
                    operation.setCredit(0);
                    operation.setRecetteBrute(recetteBrute);
                    operation.setSignature(sale.getSignatureAgent());

                    opperationRepository.save(operation);
                }
        }
    }


    @Override
    public String toString() {
        return "JournalRapprochementService{" +
                "collaborators=" + collaborators +
                '}';
    }

    public void updateVenteNonIntegre() {
        List<Vente> ventes = venteRepository.findVenteNonInteger();
   //     List<Vente> ventesUpdated = new ArrayList<>();
        for(Vente vente :ventes){
            String codeIata = vente.getCodeIATA();
            if(vente.getOrigineEmission()==null && vente.getCodeIATA()!=null){
                List<RecetteBrute> recetteBrute = recetteBruteRepository.findAllRecetteByNumBillet(vente.getNumBillet());
                Entite entite = entiteRepository.findByCode(recetteBrute != null ? recetteBrute.get(0).getCodeIATA() : null);
                if(entite != null && entite.getOrigineEmission() != null){
                    vente.setOrigineEmission(entite.getOrigineEmission());
                    vente.setCodeIATA(recetteBrute.get(0).getCodeIATA() );
                    vente.setVenteIntgre(true);
                    vente.setMotif("la vente de " + codeIata +" a ete modifier par " +vente.getCodeIATA());
                    vente.setStatutVente(StatutVenteEnum.Non_Rapproche);
                    venteRepository.save(vente);
                 //   ventesUpdated.add(vente1);
                    log.info("la vente "+vente.getId()+" à été bien modifier par status Non_Rapprocher et integrer" );
                }
            }
        }
       // return ResponseEntity.ok(ventesUpdated);
    }

    public void updateRecetteNonIntegre() {
        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findRecetteNonInteger();
    //    List<RecetteBrute> recetteUpdated = new ArrayList<>();
        for(RecetteBrute  recetteBrute :recetteBrutes){
            ClasseReservation classeReservation = classeReservationRepository.findByCode(recetteBrute.getClasseReservation());
            Entite entite = null;
            if(recetteBrute.getOrigineEmission()==null && recetteBrute.getCodeIATA()!=null ){
                //codeIATA recette = codeIATA vente car moment intégration les recettes on affecte
                //codeIATA vente à codeIATA recette
                entite =  entiteRepository.findByCode(recetteBrute.getCodeIATA());
                if(entite != null && entite.getOrigineEmission() != null){
                    recetteBrute.setOrigineEmission(entite.getOrigineEmission().getNom());
                }
            }
            if(classeReservation!=null && entite!= null && entite.getOrigineEmission() != null){
                recetteBrute.setRecetteIntegre(true);
                recetteBrute.setMotif("la recette " + recetteBrute.getCodeIATAVente() + " a ete modifier par " + recetteBrute.getCodeIATA());
           //     recetteUpdated.add(recetteBrute);
                log.info("la recette "+recetteBrute.getId()+" à été bien modifier par  recette integrer" );

            }
            recetteBruteRepository.save(recetteBrute);
        }
     //   return ResponseEntity.ok(recetteUpdated);
    }

    public void updateRapprocherParErreur() {
        log.info("start Update status ");
        List<RecetteBrute> recetteBrutesCheck = recetteBruteRepository.findAllRecetteBruteNonInteger();
        log.info("recette Checked size :" + recetteBrutesCheck.size());

        if (!recetteBrutesCheck.isEmpty()) {
            for (RecetteBrute recetteBrute : recetteBrutesCheck) {
                Vente vente = venteRepository.findByNumBilletAndIntegerAndNonRapprocher(recetteBrute.getNumBillet());

                if (vente != null) {
                    vente.setStatutVente(StatutVenteEnum.Non_Rapprocher_Par_Erreur);
                    venteRepository.save(vente);
                } else {
                    log.warn("Vente Not Integer for RecetteBrute with numBillet: " + recetteBrute.getNumBillet());
                }
            }
        }

        log.info("end update.");
    }
    @Transactional
    public void achivageDateVente() {
        // Obtenez la date actuelle
        LocalDateTime dateActuelle = LocalDateTime.now();
        log.info("la date est "+dateActuelle);

        // Calculez la date il y a trois mois
        LocalDateTime DateArchive = dateActuelle.minusMonths(dateArchive);
        log.info("la date a archiver est "+DateArchive);


        venteRepository.updateArchivedVenteRapprocherDates(DateArchive); // modifier les vente avec status Rapprocher
        List<String> numBillet =venteRepository.getNumBilletRapprocherOrNonIntegerAfterMount(DateArchive);

        recetteBruteRepository.updateArchivedRecetteBetweenDates(numBillet);
        journalRapprochementRepository.updateArchivedjournalRapprochementBetweenDates(numBillet);

    }

}


