package com.ram.venga.rest;

import com.ram.venga.model.VilleDTO;
import com.ram.venga.service.VilleService;
import javax.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/villes", produces = "application/json")
public class VilleResource {

    private final VilleService villeService;

    public VilleResource(final VilleService villeService) {
        this.villeService = villeService;
    }

    @GetMapping
    public ResponseEntity<List<VilleDTO>> getAllVilles() {
        return ResponseEntity.ok(villeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VilleDTO> getVille(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(villeService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createVille(@RequestBody @Valid final VilleDTO villeDTO) {
        final Long createdId = villeService.create(villeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateVille(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final VilleDTO villeDTO) {
        villeService.update(id, villeDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVille(@PathVariable(name = "id") final Long id) {
        villeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

