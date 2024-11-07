package com.ram.venga.service;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.ram.venga.domain.*;
import com.ram.exception.UserAlreadyExistException;
import com.ram.venga.model.prime.PrimeExtractorPojo;
import com.ram.venga.model.ExcelImportResponse;
import com.ram.venga.model.PrimeIdSegmentDTO;
import com.ram.venga.model.prime.response.ImportedPrimesResponseDto;

import com.ram.venga.model.enumeration.ImportStatusEnum;
import com.ram.venga.model.enumeration.ScheduleStatus;
import com.ram.venga.repos.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.io.IOException;


import com.ram.venga.mapper.PrimeMapper;
import com.ram.venga.model.PrimeDTO;
import com.ram.venga.util.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class PrimeService {

    @Value("${directories.excelFilesPath}")
    private String excelFilesPath;
    private final Validator validator;

	private final PrimeMapper primeMapper;
    private final PrimeRepository primeRepository;
    private final ImportedPrimeRepository importedPrimeRepository;
    private final SegmentRepository segmentRepository;
    private final MailService mailService;
    private final SegmentService segmentService;
    private final ClasseProduitService classeProduitService;
    private final OrigineEmissionService origineEmissionService;

    public Page<PrimeDTO> findAll(Pageable pageable,String origin,String classeProduit/*,String segment*/,String keyword,String origineS,String destinationS) {
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateCreated").ascending()
        );
        final Page<Prime> primes = primeRepository.findAllByKeyWord(pageable,origin,classeProduit/*,segment*/,keyword,origineS,destinationS);
        return primes.map(primeMapper::toDto);
    }
    public PrimeDTO get(final Long id) {
        return primeRepository.findById(id)
        		.map(primeMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PrimeIdSegmentDTO primeDTO) {
        Segment segment = segmentRepository.findByEscalDepartAndEscalArriver(primeDTO.getEscalDepart(),primeDTO.getEscalArriver());
        if(segment == null){
            throw new UserAlreadyExistException("segment inexistant .");
        }
        Prime primes =  primeRepository.findByOrigineEmissionIdAndclassProduitIdAndSegement(primeDTO.getOrigineEmission(),primeDTO.getClasseProduit(),segment.getId());
        if(primes != null){
           throw new UserAlreadyExistException("ce prime existe deja");
        }
       Prime prime= primeMapper.toEntitWithId(primeDTO);
        prime.setSegment(segment);
        return primeRepository.save(prime).getId();
    }

    public void update(final PrimeIdSegmentDTO primeDTO) {
        primeRepository.findById(primeDTO.getId())
                .orElseThrow(NotFoundException::new);
        primeRepository.save(primeMapper.toEntitWithId(primeDTO));
    }


    public ExcelImportResponse importPrimesViaExcel(MultipartFile multipartFile, long id, ExcelImportResponse response,String fileId) throws IOException {
        OrigineEmission origineEmission = origineEmissionService.findById(id);
        AtomicInteger totalRowsPersisted = new AtomicInteger(0);
        List<PrimeExtractorPojo> primes = Poiji.fromExcel(multipartFile.getInputStream(), PoijiExcelType.XLSX, PrimeExtractorPojo.class);
        // Fetch all existing imported primes in one go
        List<ImportedPrimes> existingImportedPrimes = importedPrimeRepository.findByOrigineEmissionId(origineEmission.getId());
        // Create a map for quick lookup
        Map<String, ImportedPrimes> importedPrimesMap = existingImportedPrimes.stream()
                .collect(Collectors.toMap(
                        prime -> prime.getClasseProduit() + "-" + prime.getSegment(),
                        prime -> prime
                ));

        List<ImportedPrimes> primesList = new ArrayList<>();
        primes.stream().forEach(prime -> {
            if (validator.validate(prime).isEmpty() || origineEmission.getNom().equalsIgnoreCase(prime.getOrigineEmission())) {
                String key = prime.getClasseProduit() + "-" + prime.getSegment();
                ImportedPrimes savedPrime = importedPrimesMap.getOrDefault(key, new ImportedPrimes());
                if (savedPrime.getId() == null) {
                    // New prime, set the origin emission and key attributes
                    savedPrime.setOrigineEmission(origineEmission);
                    savedPrime.setClasseProduit(prime.getClasseProduit());
                    savedPrime.setSegment(prime.getSegment());
                }
                savedPrime.setNbrPoint(Integer.valueOf(prime.getNbrPoint()));
                savedPrime.setSchedule_status(ScheduleStatus.NOT_YET);
                savedPrime.setProcessing_status(null);
                savedPrime.setFileId(fileId);
                totalRowsPersisted.incrementAndGet();
                primesList.add(savedPrime);
            }
        });
        importedPrimeRepository.saveAll(primesList);
        // Calculate the number of rows actually persisted
        String message = totalRowsPersisted.get() == primes.size() ? "Toutes les lignes sont persistées." : "Les lignes sont partiellement persistées.";
        response.setDescription(message);
        response.setTotalRowsProcessed(primes.size());
        //response.setTotalRowsPersisted((int)rowsPersisted);
        response.setTotalRowsPersisted((int)importedPrimeRepository.countImportedPrimesByFileId(fileId));
        return response;
    }

    private void populateRowWithDataPrime(Row row, ImportedPrimes importedPrime) {
        row.createCell(0).setCellValue(importedPrime.getOrigineEmission()!=null ? importedPrime.getOrigineEmission().getNom() : "");
        row.createCell(1).setCellValue(importedPrime.getSegment() != null ? importedPrime.getSegment() : "");
        row.createCell(2).setCellValue(importedPrime.getClasseProduit() != null ? importedPrime.getClasseProduit() : "");
        row.createCell(3).setCellValue(importedPrime.getNbrPoint() != null ? importedPrime.getNbrPoint().toString() : "");
    }

    @Async
    public void importPrimesScheduled(Utilisateur utilisateur) throws Exception {
        List<ImportedPrimes> importedPrimes = importedPrimeRepository.findAllNotTreated();
        String fileName = "Rapport_Echec_Import_Primes.xlsx";
        Path excelFolderPath = Paths.get(excelFilesPath);
        if (!Files.exists(excelFolderPath)){
            Files.createDirectories(excelFolderPath);
        }
        String parentFolderPath = excelFolderPath.resolve(fileName).getParent().toString().concat("\\");
        String filePath = excelFolderPath.resolve(fileName).toString();
        Workbook workbook = createExcelFile(importedPrimes);
        if (workbook != null){
            try(FileOutputStream fileOut = new FileOutputStream(filePath)){
                workbook.write(fileOut);
            }
            workbook.close();
            List<String> emails = Collections.singletonList(utilisateur.getEmail());
            Resource resource = readFile(parentFolderPath);
            if (resource.exists()){
                mailService.sendEmailWithAttachment(emails, "Rapport d'échec d'importation des primes importées.", resource,fileName);
                Files.delete(Paths.get(filePath));
            }
        }
    }

    private Workbook createExcelFile(List<ImportedPrimes> importedPrimes) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");
        int rowsCount = 0;
        // Create headers for the Excel sheet
        String[] headers = {"Origine d'émission", "Segment", "Classe de Produit", "Nombre de points"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        // Populate data into the Excel sheet
        int currentRowNum = 1;
        for (ImportedPrimes importedPrime : importedPrimes) {
            try {
                // Update status of imported prime
                importedPrime.setSchedule_status(ScheduleStatus.TREATED);
                importedPrime.setTreated_at(OffsetDateTime.now());
                Optional<Prime> prime = this.findByOrigineEmissionIdAndClassProduitCodeAndSegementCode(
                        importedPrime.getOrigineEmission().getId(),
                        importedPrime.getClasseProduit(),
                        importedPrime.getSegment()
                );
                // Save or update prime details
                if (prime.isPresent()) {
                    Prime existingPrime = prime.get();
                    existingPrime.setNbrPoint(importedPrime.getNbrPoint());
                    primeRepository.save(existingPrime);
                } else {
                    Prime newPrime = Prime.builder()
                            .origineEmission(importedPrime.getOrigineEmission())
                            .nbrPoint(importedPrime.getNbrPoint())
                            .segment(segmentService.findByCode(importedPrime.getSegment()))
                            .classeProduit(classeProduitService.findByCode(importedPrime.getClasseProduit()))
                            .build();
                    primeRepository.save(newPrime);
                }
                importedPrime.setProcessing_status(ImportStatusEnum.Success);
            } catch (Exception e) {
                // Handle failure
                importedPrime.setProcessing_status(ImportStatusEnum.Failure);
                Row row = sheet.createRow(currentRowNum++);
                populateRowWithDataPrime(row, importedPrime);
                rowsCount++;
            }
            // Save the updated imported prime
            importedPrimeRepository.save(importedPrime);
        }
        // Return null if no rows were added to the sheet
        if (rowsCount <= 0) {
            return null;
        }
        return workbook;
    }

    public void delete(final Long id) {

        if(!primeRepository.existsById(id))
            throw new NotFoundException("No Prime with this Id");

        primeRepository.deleteById(id);

    }

    public Resource readFile(String path) {
        File dir = new File(path);
        // Validate directory
        if (!dir.exists() || !dir.isDirectory()) {
            // Handle invalid directory case, e.g., throw an exception or return null
            return null;
        }
        // Filter files
        File[] matchingFiles = dir.listFiles((directory, name) -> name.contains("Rapport_Echec_Import"));

        // Check if any matching files were found
        if (matchingFiles != null && matchingFiles.length > 0) {
            // Return the first matching file
            return new FileSystemResource(matchingFiles[0]);
        } else {
            // Handle case where no matching files were found, e.g., throw an exception or return null
            return null;
        }
    }

    public Prime findById(Long id){
        return primeRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("une prime avec l'identifiant %s n'a pas été trouvée.",id)));
    }

    public Optional<Prime> findByOrigineEmissionIdAndClassProduitCodeAndSegementCode(Long origineEmissionId, String classProduit, String segment){
        return primeRepository.findByOrigineEmissionIdAndClasseProduitCodeAndSegmentCode(origineEmissionId, classProduit, segment);
    }
        public ResponseEntity<?> checkPrimesInsertionProgress(String fileId,String totalRowsPersisted) throws IOException {
        Long treatedPrimes = importedPrimeRepository.countImportedPrimesByScheduleStatusAndFileId(ScheduleStatus.TREATED, fileId);

        if (Long.parseLong(totalRowsPersisted) == 0) {
            return ResponseEntity.badRequest().body("Aucune prime n'a été importée.");
        } else {
            double progress = (treatedPrimes / (double) Long.parseLong(totalRowsPersisted)) * 100;
            if (progress < 100) {
                return ResponseEntity.ok(progress );
            } else {
                List<ImportedPrimes> failureImportedPrimes = importedPrimeRepository.findAllFailureImportedPrimes(fileId);
                List<ImportedPrimesResponseDto> failureImportedPrimesList = failureImportedPrimes.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
                if (!failureImportedPrimesList.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.OK).body(failureImportedPrimesList);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("importation terminée avec succès.");
                }
            }
        }
    }
    private ImportedPrimesResponseDto toDto(ImportedPrimes importedPrimes) {
        return ImportedPrimesResponseDto.builder()
                .origineEmission(importedPrimes.getOrigineEmission().getNom())
                .segment(importedPrimes.getSegment())
                .classeProduit(importedPrimes.getClasseProduit())
                .nbrPoint(importedPrimes.getNbrPoint())
                .build();
    }
}
