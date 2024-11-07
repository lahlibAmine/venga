package com.ram.venga.rest;

import com.ram.venga.model.ConcoursDTO;
import com.ram.venga.service.ConcoursService;
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
@RequestMapping(value = "/api/v1/concours",produces = "application/json")
public class ConcoursResource {

    private final ConcoursService concoursService;

    public ConcoursResource(final ConcoursService concoursService) {
        this.concoursService = concoursService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ConcoursDTO> getConcours(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(concoursService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createConcours(@RequestBody @Valid final ConcoursDTO concoursDTO) {
        return concoursService.create(concoursDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateConcours(
            @RequestBody @Valid final ConcoursDTO concoursDTO) {
       return concoursService.update( concoursDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteConcours(@PathVariable(name = "id") final Long id) {
        concoursService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ConcoursDTO>> getAllConcoursByKeyword(@RequestParam String origin, @RequestParam List<String> classeProduit,Pageable pageable) {
        return ResponseEntity.ok(concoursService.getAllConcoursByKeyword(origin,classeProduit,pageable));
    }

}

