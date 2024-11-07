package com.ram.venga.rest;

import com.ram.venga.model.HauteSaisonDTO;
import com.ram.venga.service.HauteSaisonService;
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
@RequestMapping(value = "/api/v1/hauteSaisons", produces = "application/json")
public class HauteSaisonResource {

    private final HauteSaisonService hauteSaisonService;

    public HauteSaisonResource(final HauteSaisonService hauteSaisonService) {
        this.hauteSaisonService = hauteSaisonService;
    }

    @GetMapping
    public ResponseEntity<Page<HauteSaisonDTO>> getAllHauteSaisons(@RequestParam String keyword,Pageable pageable) {
        return ResponseEntity.ok(hauteSaisonService.findAll(keyword,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HauteSaisonDTO> getHauteSaison(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(hauteSaisonService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createHauteSaison(
            @RequestBody  HauteSaisonDTO hauteSaisonDTO) {
        final Long createdId = hauteSaisonService.create(hauteSaisonDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateHauteSaison(
            @RequestBody @Valid final HauteSaisonDTO hauteSaisonDTO) {
        hauteSaisonService.update( hauteSaisonDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHauteSaison(@PathVariable(name = "id") final Long id) {
        hauteSaisonService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

