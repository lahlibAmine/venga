package com.ram.venga.unitaire;
import com.ram.venga.domain.*;
import com.ram.venga.mapper.JournalRapprochementMapper;
import com.ram.venga.mapper.JournalRapprochementViewMapper;
import com.ram.venga.mapper.RecetteBruteMapper;
import com.ram.venga.mapper.VenteMapper;
import com.ram.venga.repos.*;
import com.ram.venga.service.CollaborateurService;
import com.ram.venga.service.JournalRapprochementService;
import com.ram.venga.service.KeycloackService;
import com.ram.venga.service.ViewRefreshService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TestRaprochement {
    @InjectMocks
    private JournalRapprochementService journalRapprochementService; // Remplacez VotreClasse par le nom de votre classe

    // Mocks pour les dépendances nécessaires
    @Mock
    private JournalRapprochementRepository journalRapprochementRepository;
    @Mock
    private RecetteBruteRepository recetteBruteRepository;
    @Mock
    private JournalRapprochementMapper journalRapprochementMapper;
    @Mock
    private  VenteRepository  venteRepository;
    @Mock
    private VenteMapper venteMapper;
    @Mock
    private RecetteBruteMapper recetteBruteMapper;

    @Mock
    private CollaborateurRepository collaborateurRepository;
    @Mock
    private HauteSaisonRepository hauteSaisonRepository;
    @Mock
    private CollaborateurService collaborateurService;
    @Mock
    private ClasseProduitRepository classeProduitRepository;
    @Mock
    private PrimeRepository primeRepository;
    @Mock
    private OrigineEmissionRepository origineEmissionRepository;
    @Mock
    private ConcoursRepository concoursRepository;
    @Mock
    private SegmentRepository segmentRepository;
    @Mock
    private BonCommandeRepository bonCommandeRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private OpperationRepository opperationRepository;
    @Mock
    private KeycloackService keycloackService;
    @Mock
    private EntiteRepository entiteRepository;
    @Mock
    private ClasseReservationRepository classeReservationRepository;
    @Mock
    private JournalRapprochementViewRepository journalRapprochementViewRepository;

    @Mock
    private JournalRapprochementViewMapper journalRapprochementViewMapper;

    @Mock
    private ViewRefreshService viewRefreshService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        journalRapprochementService = new JournalRapprochementService(journalRapprochementMapper,journalRapprochementRepository,
                venteRepository,recetteBruteRepository, collaborateurRepository, collaborateurService, primeRepository, bonCommandeRepository, utilisateurRepository, opperationRepository, journalRapprochementViewMapper, viewRefreshService, venteMapper,
                recetteBruteMapper, hauteSaisonRepository, classeProduitRepository, origineEmissionRepository, concoursRepository, segmentRepository, keycloackService, entiteRepository,classeReservationRepository,journalRapprochementViewRepository); // Create an instance of the class being tested
    }

    @Test
    public void testCreateSenario1() {
        // Créez des objets factices (ventes, recetteBrutes, etc.) pour vos tests
        List<HauteSaison> hauteSaisons = new ArrayList<>();
        // Mock les appels aux méthodes de repository et configurez-les selon vos besoins
        when(venteRepository.findAllByIntegre()).thenReturn(TestData.createSampleVenteListSenario1());
        when(recetteBruteRepository.findAllByIntegre()).thenReturn(TestData.createSampleRecetteListSenario1());
        when(collaborateurRepository.findAllWithCollaborateur()).thenReturn(TestData.createSampleCollaborateurListSenario1());
        when(primeRepository.findByOrigineEmissionIdAndclassProduitAndSegement(any(), any(), any())).thenReturn(TestData.createSamplePrimeListSenario1());
        when(hauteSaisonRepository.findAll()).thenReturn(TestData.createSampleHauteSaisonListSenario1());
        when(journalRapprochementRepository.findByRecetteWithVente(any())).thenReturn(Optional.empty());
        when(journalRapprochementRepository.findByVenteAndRecette(any(), any())).thenReturn(Optional.empty());
        when(origineEmissionRepository.findByNom(any())).thenReturn(TestData.createSampleOrigineEmission());
        when(concoursRepository.findByClasseProduitLibelle(any(),any(),any(),any())).thenReturn(TestData.createSampleConcour());
        // Appelez la méthode à tester
        ResponseEntity<?> responseEntity = journalRapprochementService.create();

        // Effectuez des assertions pour vérifier le comportement attendu
        // par exemple, vérifiez si le statut de la réponse est OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Vous pouvez également vérifier si les méthodes de repository ont été appelées avec les bons arguments
        verify(venteRepository, times(1)).findAllByIntegre();
        verify(recetteBruteRepository, times(1)).findAllByIntegre();
        // ... d'autres vérifications

        // Récupérez les ventes après l'exécution de create()
        List<Vente> ventesApres = venteRepository.findAllByIntegre();

        List<RecetteBrute> recette = recetteBruteRepository.findAllByIntegre();
        // Créez un ensemble de ventes attendues avec des valeurs spécifiques pour chaque champ
        List<Vente> ventesAttendues = TestData.createSampleVenteListSenario1(); // Remplacez par vos données de test spécifiques

        // Vérifiez chaque vente individuellement
        for (int i = 0; i < ventesApres.size(); i++) {
            Vente venteApres = ventesApres.get(i);
            Vente venteAttendue = ventesAttendues.get(i);
            // Comparez les champs de la vente après avec les champs de la vente attendue
            assertEquals(2 , venteApres.getNbrCouponNonRapprocher());
            assertEquals(false , venteApres.getVenteRapproche());

            // Effectuez d'autres vérifications pour d'autres champs
        }
    }

    @Test
    public void testCreateSenario2() {
        // Créez des objets factices (ventes, recetteBrutes, etc.) pour vos tests
        List<HauteSaison> hauteSaisons = new ArrayList<>();
        // Mock les appels aux méthodes de repository et configurez-les selon vos besoins
        when(venteRepository.findAllByIntegre()).thenReturn(TestData.createSampleVenteListSenario1());
        when(recetteBruteRepository.findAllByIntegre()).thenReturn(TestData.createSampleRecetteListSenario2());
        when(collaborateurRepository.findAllWithCollaborateur()).thenReturn(TestData.createSampleCollaborateurListSenario1());
        when(primeRepository.findByOrigineEmissionIdAndclassProduitAndSegement(any(), any(), any())).thenReturn(TestData.createSamplePrimeListSenario1());
        when(hauteSaisonRepository.findAll()).thenReturn(TestData.createSampleHauteSaisonListSenario1());
        when(journalRapprochementRepository.findByRecetteWithVente(any())).thenReturn(Optional.empty());
        when(journalRapprochementRepository.findByVenteAndRecette(any(), any())).thenReturn(Optional.empty());
        when(origineEmissionRepository.findByNom(any())).thenReturn(TestData.createSampleOrigineEmission());
        when(concoursRepository.findByClasseProduitLibelle(any(),any(),any(),any())).thenReturn(TestData.createSampleConcour());
        // Appelez la méthode à tester
        ResponseEntity<?> responseEntity = journalRapprochementService.create();

        // Effectuez des assertions pour vérifier le comportement attendu
        // par exemple, vérifiez si le statut de la réponse est OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Vous pouvez également vérifier si les méthodes de repository ont été appelées avec les bons arguments
        verify(venteRepository, times(1)).findAllByIntegre();
        verify(recetteBruteRepository, times(1)).findAllByIntegre();
        // ... d'autres vérifications

        // Récupérez les ventes après l'exécution de create()
        List<Vente> ventesApres = venteRepository.findAllByIntegre();

        List<RecetteBrute> recette = recetteBruteRepository.findAllByIntegre();
        // Créez un ensemble de ventes attendues avec des valeurs spécifiques pour chaque champ
        List<Vente> ventesAttendues = TestData.createSampleVenteListSenario1(); // Remplacez par vos données de test spécifiques

        // Vérifiez chaque vente individuellement
        for (int i = 0; i < ventesApres.size(); i++) {
            Vente venteApres = ventesApres.get(i);
            Vente venteAttendue = ventesAttendues.get(i);
            // Comparez les champs de la vente après avec les champs de la vente attendue
            assertEquals(0 , venteApres.getNbrCouponNonRapprocher());
            assertEquals(true , venteApres.getVenteRapproche());

            // Effectuez d'autres vérifications pour d'autres champs
        }
    }

    @Test
    public void testCreateSenario3() {
        // Créez des objets factices (ventes, recetteBrutes, etc.) pour vos tests
        List<HauteSaison> hauteSaisons = new ArrayList<>();
        // Mock les appels aux méthodes de repository et configurez-les selon vos besoins
        when(venteRepository.findAllByIntegre()).thenReturn(TestData.createSampleVenteListSenario1());
        when(recetteBruteRepository.findAllByIntegre()).thenReturn(TestData.createSampleRecetteListSenario3());
        when(collaborateurRepository.findAllWithCollaborateur()).thenReturn(TestData.createSampleCollaborateurListSenario1());
        when(primeRepository.findByOrigineEmissionIdAndclassProduitAndSegement(any(), any(), any())).thenReturn(TestData.createSamplePrimeListSenario1());
        when(hauteSaisonRepository.findAll()).thenReturn(TestData.createSampleHauteSaisonListSenario1());
        when(journalRapprochementRepository.findByRecetteWithVente(any())).thenReturn(Optional.empty());
        when(journalRapprochementRepository.findByVenteAndRecette(any(), any())).thenReturn(Optional.empty());
        when(origineEmissionRepository.findByNom(any())).thenReturn(TestData.createSampleOrigineEmission());
        when(concoursRepository.findByClasseProduitLibelle(any(),any(),any(),any())).thenReturn(TestData.createSampleConcour());
        // Appelez la méthode à tester
        ResponseEntity<?> responseEntity = journalRapprochementService.create();

        // Effectuez des assertions pour vérifier le comportement attendu
        // par exemple, vérifiez si le statut de la réponse est OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Vous pouvez également vérifier si les méthodes de repository ont été appelées avec les bons arguments
        verify(venteRepository, times(1)).findAllByIntegre();
        verify(recetteBruteRepository, times(1)).findAllByIntegre();
        // ... d'autres vérifications

        // Récupérez les ventes après l'exécution de create()
        List<Vente> ventesApres = venteRepository.findAllByIntegre();

        List<RecetteBrute> recette = recetteBruteRepository.findAllByIntegre();
        // Créez un ensemble de ventes attendues avec des valeurs spécifiques pour chaque champ
        List<Vente> ventesAttendues = TestData.createSampleVenteListSenario1(); // Remplacez par vos données de test spécifiques

        // Vérifiez chaque vente individuellement
        for (int i = 0; i < ventesApres.size(); i++) {
            Vente venteApres = ventesApres.get(i);
            Vente venteAttendue = ventesAttendues.get(i);
            // Comparez les champs de la vente après avec les champs de la vente attendue
            assertEquals(3 , venteApres.getNbrCouponNonRapprocher());
            assertEquals(false , venteApres.getVenteRapproche());

            // Effectuez d'autres vérifications pour d'autres champs
        }
    }

    @Test
    public void testTraitementPoint() {
        // Mock data for vente and recetteBrute
        Vente vente = new Vente();
        vente.setSignatureAgent("agent123");

        RecetteBrute recetteBrute = new RecetteBrute();
        recetteBrute.setEscaleDepart("A");
        recetteBrute.setEscaleArrivee("B");
        recetteBrute.setOrigineEmission("C");
        recetteBrute.setClasseProduit("D");
        recetteBrute.setMontantBrut(50);

        List<Collaborateur> collaborators = new ArrayList<>();
        Collaborateur collaborateur = new Collaborateur();
        collaborateur.setSignature("agent123");
        collaborateur.setSoldePoint(0);
        collaborateur.setChiffreAffaire(0);
        collaborators.add(collaborateur);

        // Mock repository calls
        when(collaborateurService.isRecetteInHauteSaison(Mockito.any(), Mockito.any())).thenReturn(false);
        doNothing().when(Mockito.mock(JournalRapprochementService.class)).traitementPoint(vente,recetteBrute);
        when(collaborateurRepository.findAllWithCollaborateur()).thenReturn(collaborators);
        when(segmentRepository.findByEscalDepartAndEscalArriver(any(), any())).thenReturn(TestData.createSampleSegment());
        when(origineEmissionRepository.findByNom(recetteBrute.getOrigineEmission())).thenReturn(TestData.createSampleOrigineEmission());
        when(primeRepository.findByOrigineEmissionIdAndclassProduitAndSegement(any(), any(), any())).thenReturn(TestData.createSamplePrimeListSenario1());
        // Call the traitementPoint method
        journalRapprochementService.traitementPoint(vente, recetteBrute);

        // Assertions for the collaborator object after traitementPoint
        assertEquals(100, collaborateur.getSoldePoint());
        assertEquals(50, collaborateur.getChiffreAffaire());
    }

    // Add more test cases as needed

}
