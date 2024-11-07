package com.ram.venga.rest;

import com.ram.venga.domain.Vente;
import com.ram.venga.model.JournalRapprochementDTO;
import com.ram.venga.model.enumeration.StatutRapprochementEnum;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import com.ram.venga.service.JournalRapprochementService;
import javax.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/journalRapprochements", produces = "application/json")
public class JournalRapprochementResource {

    private final JournalRapprochementService journalRapprochementService;

    public JournalRapprochementResource(
            final JournalRapprochementService journalRapprochementService) {
        this.journalRapprochementService = journalRapprochementService;
    }

    @GetMapping
        public ResponseEntity<Page<JournalRapprochementDTO>> getAllJournalRapprochements(@RequestParam String numBillet, @RequestParam(defaultValue = "") StatutRapprochementEnum statut, @RequestParam String dateDebut, @RequestParam String dateFin, @RequestParam(required = false) StatutVenteEnum rapprocher,@RequestParam(required = false,defaultValue = "false") Boolean isArchived, Pageable pageable) {
        return ResponseEntity.ok(journalRapprochementService.findAll(numBillet,statut,dateDebut,dateFin,rapprocher,isArchived,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalRapprochementDTO> getJournalRapprochement(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(journalRapprochementService.get(id));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateJournalRapprochement(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final JournalRapprochementDTO journalRapprochementDTO) {
        journalRapprochementService.update(id, journalRapprochementDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteJournalRapprochement(
            @PathVariable(name = "id") final Long id) {
        journalRapprochementService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
