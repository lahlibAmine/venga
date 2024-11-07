package com.ram.venga.rest;

import com.ram.venga.model.OffreDTO;
import com.ram.venga.service.OffreService;
import javax.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/offres", produces = MediaType.APPLICATION_JSON_VALUE)
public class OffreResource {

    private final OffreService offreService;

    public OffreResource(final OffreService offreService) {
        this.offreService = offreService;
    }

    @GetMapping
    public ResponseEntity<List<OffreDTO>> getAllOffres() {
        return ResponseEntity.ok(offreService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffreDTO> getOffre(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(offreService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createOffre(@RequestBody @Valid final OffreDTO offreDTO) {
        final Long createdId = offreService.create(offreDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateOffre(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final OffreDTO offreDTO) {
        offreService.update(id, offreDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable(name = "id") final Long id) {
        offreService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

