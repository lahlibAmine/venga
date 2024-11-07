package com.ram.venga.rest;

import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.ExcelImportResponse;
import com.ram.venga.model.PrimeDTO;
import com.ram.venga.model.PrimeIdSegmentDTO;
import com.ram.venga.model.enumeration.ImportStatus;
import com.ram.venga.repos.UtilisateurRepository;
import com.ram.venga.service.KeycloackService;
import com.ram.venga.service.PrimeService;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;



@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/primes", produces = "application/json")
public class PrimeResource {

    private final PrimeService primeService;
    private final KeycloackService keycloackService;
    private final UtilisateurRepository utilisateurRepository;

    public PrimeResource(final PrimeService primeService, KeycloackService keycloackService, UtilisateurRepository utilisateurRepository) {
        this.primeService = primeService;
        this.keycloackService = keycloackService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping
    public ResponseEntity<Page<PrimeDTO>> getAllPrimes(Pageable pageable, @RequestParam String origin, @RequestParam String classeProduit/*,@RequestParam String segment*/,@RequestParam String keyword ,@RequestParam String depart ,  @RequestParam String destination) {
        return ResponseEntity.ok(primeService.findAll(pageable,origin,classeProduit/*,segment*/,keyword,depart,destination));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrimeDTO> getPrime(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(primeService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createPrime(@RequestBody @Valid final PrimeIdSegmentDTO primeDTO) {
        final Long createdId = primeService.create(primeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updatePrime(@RequestBody @Valid final PrimeIdSegmentDTO primeDTO) {
        primeService.update( primeDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public void deletePrime(@PathVariable(name = "id") Long id) {
        primeService.delete(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<ExcelImportResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") Long id,
            @RequestParam("fileId") String fileId) throws Exception {
        // Get the user's ID and details
        String idUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(idUser)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExcelImportResponse(0, 0,"Le fichier est vide. Aucune donnée à importer."));
        }
        ExcelImportResponse importResponse = new ExcelImportResponse();
        try {
            ExcelImportResponse response = primeService.importPrimesViaExcel(file, id,importResponse,fileId);
            primeService.importPrimesScheduled(utilisateur);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExcelImportResponse(0, 0,"Quelque chose s'est mal passé lors du processus d'importation, vérifiez votre boîte mail."));
        }
    }
        @GetMapping(value = "/check-primes-importation-progress")
    public ResponseEntity<?> PrimesInsertionProgress(@RequestParam String fileId,@RequestParam String totalRowsPersisted) throws IOException {
        return primeService.checkPrimesInsertionProgress(fileId,totalRowsPersisted);
    }

}
