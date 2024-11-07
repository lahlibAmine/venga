package com.ram.venga.unitaire;

import com.ram.venga.domain.*;
import com.ram.venga.mapper.BonCommandeMapper;
import com.ram.venga.mapper.OpperationMapper;
import com.ram.venga.mapper.VenteMapper;
import com.ram.venga.model.BonCommandeDTO;
import com.ram.venga.model.OpperationDTO;
import com.ram.venga.repos.*;
import com.ram.venga.service.KeycloackService;
import com.ram.venga.service.OpperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import static org.mockito.Mockito.*;

import java.util.*;

public class TestOperation {
/*

        @Mock
        private CollaborateurRepository collaborateurRepository;

        @Mock
        private VenteRepository venteRepository;

        @Mock
        private UtilisateurRepository utilisateurRepository;

        @Mock
        private BonCommandeRepository bonCommandeRepository;

        @Mock
        private OpperationRepository opperationRepository;

        @Mock
        private OpperationMapper opperationMapper;

        @Mock
        private BonCommandeMapper bonCommandeMapper;
        @Mock
        private VenteMapper venteMapper;
        @Mock
        private ClasseReservationRepository classeReservationRepository;
    @Mock
    private KeycloackService keycloackService;
    @Mock
    private RecetteBruteRepository recetteBruteRepository;
        @InjectMocks
        private OpperationService yourClassInstance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        yourClassInstance = new OpperationService(opperationRepository,venteRepository,
                bonCommandeRepository,collaborateurRepository,opperationMapper,
                classeReservationRepository, keycloackService, recetteBruteRepository, venteMapper, bonCommandeMapper,bonCommandeRepository,utilisateurRepository); // Create an instance of the class being tested
    }
        @Test
        void testGet() {
            // Mock data
            Long id = 1L;
            Collaborateur collaborateur = new Collaborateur();
            collaborateur.setId(id);

            List<Vente> ventes = new ArrayList<>();
            // Add some Vente objects to the ventes list

            Utilisateur utilisateur = new Utilisateur();
            // Set up utilisateur object

            List<BonCommande> commande = new ArrayList<>();
            // Add some BonCommande objects to the commande list

            List<Opperation> opperations = new ArrayList<>();
            // Add some Opperation objects to the opperations list

            List<OpperationDTO> opperationDTOS = new ArrayList<>();
            // Add some OpperationDTO objects to the opperationDTOS list

            List<BonCommandeDTO> bonCommandeDTOS = new ArrayList<>();
            // Add some BonCommandeDTO objects to the bonCommandeDTOS list

            Pageable pageable = mock(Pageable.class);

            // Set up mock repository method calls and mapper
            when(collaborateurRepository.findById(id)).thenReturn(Optional.of(collaborateur));
            when(venteRepository.findByCollaborateurId(collaborateur.getId())).thenReturn(ventes);
            when(utilisateurRepository.findByEmail(collaborateur.getEmail())).thenReturn(utilisateur);
            when(bonCommandeRepository.findByAgentCommercial_Id(utilisateur.getId())).thenReturn(commande);
            when(opperationRepository.findByRe(ventes)).thenReturn(opperations);
            when(opperationMapper.toDto(opperations)).thenReturn(opperationDTOS);
            when(bonCommandeMapper.toDto(commande)).thenReturn(bonCommandeDTOS);

            // Call the method
       //     Page<OpperationTraiterDto> result = yourClassInstance.get( pageable);

            // Perform assertions on the result
            // Add your assertions based on the expected behavior of the method

            // Verify mock repository method calls and mapper invocations
            verify(collaborateurRepository).findById(id);
            verify(venteRepository).findByCollaborateurId(collaborateur.getId());
            verify(utilisateurRepository).findByEmail(collaborateur.getEmail());
            verify(bonCommandeRepository).findByAgentCommercial_Id(utilisateur.getId());
            verify(opperationRepository).findByVenteIsIn(ventes);
            verify(opperationMapper).toDto(opperations);
            verify(bonCommandeMapper).toDto(commande);

            // Add more assertions based on the expected behavior of the method

        }*/

      /*  @Test
        void testTraiterDtos() {
            // Mock data
            List<OpperationDTO> opperationDTOS = new ArrayList<>();
            // Add some OpperationDTO objects to the opperationDTOS list

            List<BonCommandeDTO> bonCommandeDTOS = new ArrayList<>();
            // Add some BonCommandeDTO objects to the bonCommandeDTOS list
            List<VenteDTO> venteDTOS = new ArrayList<>();

            Pageable pageable = mock(Pageable.class);

            // Call the method
            Page<OpperationTraiterDto> result = yourClassInstance.traiterDtos(opperationDTOS, bonCommandeDTOS, ,pageable);

            // Perform assertions on the result
            // Add your assertions based on the expected behavior of the method

            // Example assertion
            Assertions.assertNotNull(result);
            // Add more assertions based on the expected behavior of the method

        }*/
    }
