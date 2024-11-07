package com.ram.venga.service;

import com.ram.venga.domain.*;
import com.ram.venga.mapper.BonCommandeMapper;
import com.ram.venga.mapper.OpperationMapper;
import com.ram.venga.mapper.VenteMapper;
import com.ram.venga.model.*;
import com.ram.venga.repos.*;
import com.ram.venga.util.NotFoundException;

import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class OpperationService {

    private final OpperationRepository opperationRepository;
    private final VenteRepository venteRepository;
    private final BonCommandeRepository bonCommandeRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final OpperationMapper opperationMapper;
    private final ClasseReservationRepository classeReservationRepository;
    private final KeycloackService keycloackService;

    private final RecetteBruteRepository recetteBruteRepository;


    private final VenteMapper venteMapper;

    private final BonCommandeMapper bonCommandeMapper;
    private final BonCommandeRepository getBonCommandeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public OpperationService(final OpperationRepository opperationRepository,
                             final VenteRepository venteRepository,
                             final BonCommandeRepository bonCommandeRepository, CollaborateurRepository collaborateurRepository, OpperationMapper opperationMapper, ClasseReservationRepository classeReservationRepository, KeycloackService keycloackService, RecetteBruteRepository recetteBruteRepository, VenteMapper venteMapper, BonCommandeMapper bonCommandeMapper, BonCommandeRepository getBonCommandeRepository, UtilisateurRepository utilisateurRepository) {
        this.opperationRepository = opperationRepository;
        this.venteRepository = venteRepository;
        this.bonCommandeRepository = bonCommandeRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.opperationMapper = opperationMapper;
        this.classeReservationRepository = classeReservationRepository;
        this.keycloackService = keycloackService;
        this.recetteBruteRepository = recetteBruteRepository;
        this.venteMapper = venteMapper;
        this.bonCommandeMapper = bonCommandeMapper;
        this.getBonCommandeRepository = getBonCommandeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<OpperationDTO> findAll() {
        final List<Opperation> opperations = opperationRepository.findAll(Sort.by("dateCreated").descending());
        return opperations.stream()
                .map(opperation -> mapToDTO(opperation, new OpperationDTO()))
                .toList();
    }
    public Page<OpperationTraiterDto> get(Pageable pageable) throws ExecutionException, InterruptedException {
        String userToken = keycloackService.getIdUserToken();
        List<Opperation> opperationList = new ArrayList<>();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(userToken).get();
        List<Vente> ventes =  venteRepository.findByCollaborateurId(utilisateur.getCollaborateur().getId());
        List<String> numBillet = ventes.stream().map(Vente::getNumBillet).collect(Collectors.toList());
        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByListNumBilletWithDebit(numBillet);
     //   List<BonCommande> bonCommandeList =  bonCommandeRepository.findByAgentCommercial_Id(utilisateur.getId());
        List<Opperation> opperationsRapprocher =opperationRepository.findByRecetteBruteIsIn(recetteBrutes);

        //    List<Opperation> opperationsAcceuil =opperationRepository.findByBonCommandeAgentCommercialId(utilisateur.getCollaborateur().getId());
        //   opperationList.addAll(opperationsAcceuil);
        opperationList.addAll(opperationsRapprocher);
        //  List<OpperationDTO> opperationRapprocher = opperationMapper.toDto(opperationsRapprocher);
        List<OpperationDTO> opperationDTOS = opperationMapper.toDto(opperationList);
        opperationDTOS.stream().forEach(o -> {
            LocalDate vente = venteRepository.findBySignatureAndRecette(o.getSignature(), o.getRecetteBrute())
                    .stream()
                    .map(Vente::getDateEmission)
                    .filter(Objects::nonNull) // Filter out any null values
                    .findFirst()
                    .map(LocalDateTime::toLocalDate) // Map to LocalDate if present
                    .orElse(null); // Return null or another default value if not present

            o.setDateEmission(vente);
        });
        return traiterDtos(opperationDTOS,pageable,ventes.stream().map(Vente::getDateEmission).collect(Collectors.toList()));
    }

    private Page<OpperationTraiterDto> traiterDtos(List<OpperationDTO> opperationDTOS, Pageable pageable, List<LocalDateTime> dateEmission) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<OpperationTraiterDto> result = new ArrayList<>();

        // Create a map of YearMonth from dateEmission for grouping
        Map<YearMonth, List<LocalDateTime>> yearMonthEmissionMap = Optional.ofNullable(dateEmission)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull) // Filter out null values
                .collect(Collectors.groupingBy(YearMonth::from));

        // Group OpperationDTO by YearMonth from the emission dates
        Map<YearMonth, List<OpperationDTO>> opperationByEmission = opperationDTOS.stream()
                .collect(Collectors.groupingBy(opperation -> yearMonthEmissionMap.keySet()
                        .stream()
                        .filter(ym -> YearMonth.from(opperation.getDateEmission()).equals(ym))
                        .findFirst()
                        .orElse(YearMonth.from(opperation.getDateEmission()))));

        // Group BonCommande by YearMonth from the emission dates
      /*  Map<YearMonth, List<BonCommande>> bonCommandeByEmission = bonCommandeList.stream()
                .filter(item -> item.getDate() != null) // Filter out items with null date
                .collect(Collectors.groupingBy(commande -> yearMonthEmissionMap.keySet()
                        .stream()
                        .filter(ym -> YearMonth.from(commande.getDate()).equals(ym))
                        .findFirst()
                        .orElse(YearMonth.from(commande.getDate()))));
*/
        // Prepare the result
        List<YearMonth> yearMonths = new ArrayList<>(yearMonthEmissionMap.keySet());
        int totalElements = yearMonths.size();

        if (startItem >= totalElements) {
            return new PageImpl<>(result, pageable, totalElements);
        }

        int toIndex = Math.min(startItem + pageSize, totalElements);

        // Iterate over the YearMonth and build the result list
        for (YearMonth yearMonth : yearMonths.subList(startItem, toIndex)) {
            OpperationTraiterDto opperationTraiterDto = new OpperationTraiterDto();
            opperationTraiterDto.setYearMonth(yearMonth);

            // Set the first OpperationDTO id or null if not found
            List<OpperationDTO> opperationDTOsForMonth = opperationByEmission.getOrDefault(yearMonth, Collections.emptyList());
            opperationTraiterDto.setId(opperationDTOsForMonth.stream().map(OpperationDTO::getId).findFirst().orElse(null));

            // Calculate the sum of credits and debits
            int sumCredit = opperationDTOsForMonth.stream().mapToInt(OpperationDTO::getDebit).sum();
            opperationTraiterDto.setSumCredit(sumCredit);

          /*  int sumDebit = bonCommandeByEmission.getOrDefault(yearMonth, Collections.emptyList())
                    .stream()
                    .mapToInt(BonCommande::getNbrPointCredit)
                    .sum();
            opperationTraiterDto.setSumDebit(sumDebit);*/

            // Set the list of OpperationDTOs for the month
            opperationTraiterDto.setOpperationDTOS(opperationDTOsForMonth);

            if (!opperationTraiterDto.getOpperationDTOS().isEmpty()) {
                result.add(opperationTraiterDto);
            }
        }

        // Sort the results by YearMonth in descending order
        result.sort(Comparator.comparing(OpperationTraiterDto::getYearMonth).reversed());

        return new PageImpl<>(result, pageable, result.size());
    }

    public Long create(final OpperationDTO opperationDTO) {
        final Opperation opperation = new Opperation();
        mapToEntity(opperationDTO, opperation);
        return opperationRepository.save(opperation).getId();
    }

    public void update(final Long id, final OpperationDTO opperationDTO) {
        final Opperation opperation = opperationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(opperationDTO, opperation);
        opperationRepository.save(opperation);
    }

    public void delete(final Long id) {
        opperationRepository.deleteById(id);
    }

    private OpperationDTO mapToDTO(final Opperation opperation, final OpperationDTO opperationDTO) {
        opperationDTO.setId(opperation.getId());
        opperationDTO.setDate(opperation.getDate().toLocalDate());
        opperationDTO.setDebit(opperation.getDebit());
        opperationDTO.setCredit(opperation.getCredit());
        opperationDTO.setRecetteBrute(opperation.getRecetteBrute() == null ? null : opperation.getRecetteBrute().getId());
        opperationDTO.setBonCommande(opperation.getBonCommande() == null ? null : opperation.getBonCommande().getId());
        return opperationDTO;
    }

    private Opperation mapToEntity(final OpperationDTO opperationDTO, final Opperation opperation) {
        opperation.setDate(opperationDTO.getDate().atStartOfDay());
        opperation.setDebit(opperationDTO.getDebit());
        opperation.setCredit(opperationDTO.getCredit());
        final RecetteBrute recetteBrute = opperationDTO.getRecetteBrute() == null ? null : recetteBruteRepository.findById(opperationDTO.getRecetteBrute())
                .orElseThrow(() -> new NotFoundException("vente not found"));
        opperation.setRecetteBrute(recetteBrute);
        final BonCommande bonCommande = opperationDTO.getBonCommande() == null ? null : bonCommandeRepository.findById(opperationDTO.getBonCommande())
                .orElseThrow(() -> new NotFoundException("bonCommande not found"));
        opperation.setBonCommande(bonCommande);
        return opperation;
    }

    public List<OpperationTraiterGraphDto> getGraphe() throws ExecutionException, InterruptedException {
        String userToken = keycloackService.getIdUserToken();
        List<Opperation> opperationList = new ArrayList<>();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(userToken).get();
        List<Vente> ventes =  venteRepository.findByCollaborateurId(utilisateur.getCollaborateur().getId());
        List<String> numBillet = ventes.stream().map(Vente::getNumBillet).collect(Collectors.toList());
        List<RecetteBrute> recetteBrutes = recetteBruteRepository.findAllByListNumBilletWithDebit(numBillet);
   //     List<BonCommande> bonCommandeList =  bonCommandeRepository.findByAgentCommercial_Id(utilisateur.getId());
        List<Opperation> opperationsRapprocher =opperationRepository.findByRecetteBruteIsIn(recetteBrutes);

        //    List<Opperation> opperationsAcceuil =opperationRepository.findByBonCommandeAgentCommercialId(utilisateur.getCollaborateur().getId());
        //   opperationList.addAll(opperationsAcceuil);
        opperationList.addAll(opperationsRapprocher);
        //  List<OpperationDTO> opperationRapprocher = opperationMapper.toDto(opperationsRapprocher);
        List<OpperationDTO> opperationDTOS = opperationMapper.toDto(opperationList);
        opperationDTOS.stream().forEach(o -> {
            LocalDate vente = venteRepository.findBySignatureAndRecette(o.getSignature(), o.getRecetteBrute())
                    .stream()
                    .map(Vente::getDateEmission)
                    .filter(Objects::nonNull) // Filter out any null values
                    .findFirst()
                    .map(LocalDateTime::toLocalDate) // Map to LocalDate if present
                    .orElse(null); // Return null or another default value if not present

            o.setDateEmission(vente);
        });
        List<LocalDateTime> localDateTimes = new ArrayList<>();
        localDateTimes.add(LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        // Subtract 11 months from the current date
        LocalDateTime modifiedDate = now.minusMonths(11);
        localDateTimes.add(modifiedDate);

        // Call the traiterDtosGraph method to get the graph data
        return traiterDtosGraph(opperationDTOS,localDateTimes);
    }


    private List<OpperationTraiterGraphDto> traiterDtosGraph(List<OpperationDTO> opperationDTOS, List<LocalDateTime> dateEmission) {
        List<OpperationTraiterGraphDto> result = new ArrayList<>();

        // Trouver la date maximale et son mois
        Optional<LocalDateTime> maxDateOptional = dateEmission.stream().max(Comparator.naturalOrder());

        if (maxDateOptional.isEmpty()) {
            return result; // Si pas de dates, retourner une liste vide
        }

        LocalDateTime maxDate = maxDateOptional.get();
        Year maxYear = Year.from(maxDate);
      //  Month maxMonth = maxDate.getMonth();

        // Calculer l'année précédente
        Year previousYear = maxYear.minusYears(1);

        // Déterminer le mois actuel
        Month currentMonth = LocalDateTime.now().getMonth();

        // Create a list for months from the current month of the current year to the same month of the next year
        List<Month> monthsToProcess = new ArrayList<>();
        for (int i = 0; i < 13; i++) { // Add 13 months from the current month
            Month month = currentMonth.plus(i); // Get the month
            if (month.getValue() > 12) {
                month = Month.of(month.getValue() - 12); // Wrap around to January if it goes past December
            }
            monthsToProcess.add(month);
        }

        for (Month month : Month.values()) {
            if (month.equals(currentMonth)) {
                // On ne répète pas le mois actuel
                break;
            }
            monthsToProcess.add(month);
        }

        // Définir les années à filtrer
        Set<Year> filteredYears = new HashSet<>();
        filteredYears.add(maxYear);
        filteredYears.add(previousYear);

        // Map chaque OpperationDTO à son année filtrée
        Map<OpperationDTO, Year> dtoToYearMap = IntStream.range(0, opperationDTOS.size())
                .boxed()
                .filter(i -> {
                    Year yearFromDate = Year.from(opperationDTOS.get(i).getDateEmission());
                    return filteredYears.contains(yearFromDate);
                })
                .collect(Collectors.toMap(opperationDTOS::get, i -> Year.from(opperationDTOS.get(i).getDateEmission())));

        // Regrouper par année puis par mois
        Map<Year, Map<Month, List<OpperationDTO>>> map = dtoToYearMap.entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.groupingBy(entry -> {
                                    OpperationDTO operationDTO = entry.getKey();
                                    LocalDate date = operationDTO.getDateEmission(); // Convertir LocalDateTime à LocalDate
                                    return date.getMonth(); // Obtenir le mois de la date
                                },
                                Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                        )));

        // Parcourir toutes les années et mois pour les traiter
        for (Year year : filteredYears) {
            // Obtenir la map des mois pour cette année
            Map<Month, List<OpperationDTO>> monthMap = map.getOrDefault(year, new HashMap<>());

            OpperationTraiterGraphDto opperationTraiterDto = new OpperationTraiterGraphDto();
            Map<Month, CalculatorDto> monthCalculMap = new LinkedHashMap<>(); // Utiliser LinkedHashMap pour préserver l'ordre

            // Itérer uniquement sur les mois à traiter
            for (Month month : monthsToProcess) {
                List<OpperationDTO> monthOperations = monthMap.getOrDefault(month, new ArrayList<>());

                // Créer le CalculatorDto avec somme des débits (ou 0 s'il n'y a pas d'opérations)
                CalculatorDto calculatorDto = new CalculatorDto();
                int sumDebit = monthOperations.stream().mapToInt(OpperationDTO::getDebit).sum();
                calculatorDto.setSumDebit(sumDebit);
                calculatorDto.setMonth(month);

                // Ajouter à la map
                monthCalculMap.put(month, calculatorDto);
            }

            // Assigner à l'objet DTO
            opperationTraiterDto.setYear(year);
            opperationTraiterDto.setData(new ArrayList<>(monthCalculMap.values()));

            // Ajouter l'objet au résultat final
            result.add(opperationTraiterDto);
        }

        return result;
    }



}
