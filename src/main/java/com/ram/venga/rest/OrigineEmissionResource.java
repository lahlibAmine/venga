package com.ram.venga.rest;

import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.model.*;
import com.ram.venga.service.OrigineEmissionService;
import javax.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/origineEmissions", produces = "application/json")
public class OrigineEmissionResource {

    private final OrigineEmissionService origineEmissionService;

    public OrigineEmissionResource(final OrigineEmissionService origineEmissionService) {
        this.origineEmissionService = origineEmissionService;
    }

    @GetMapping
    public ResponseEntity<Page<OrigineEmissionDeviseDTO>> getAllOrigineEmissions(@RequestParam(required = false) String keyword,Pageable pageable) {
        return ResponseEntity.ok(origineEmissionService.findAll(keyword,pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrigineEmissionDTO> getOrigineEmission(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(origineEmissionService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrigineEmission(
            @RequestBody @Valid final OrigineEmissionDeviseAddDto origineEmissionDTO) {
        return origineEmissionService.create(origineEmissionDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrigineEmission(
            @RequestBody @Valid final OrigineEmissionDeviseAddDto origineEmissionDTO) {
        return origineEmissionService.update( origineEmissionDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrigineEmission(@PathVariable(name = "id") final Long id) {
       return origineEmissionService.delete(id);
    }

    @GetMapping("/unpaged")
    public ResponseEntity<List<OrigineEmissionDTO>> getAllOrigineEmissions() {
        return ResponseEntity.ok(origineEmissionService.findAll().getContent());
    }

    @PostMapping("/devise")
    public ResponseEntity<?> createOrigineEmissionDevise(
            @RequestBody @Valid final OrigineEmissionDeviseAddDto origineEmissionDTO) {
        return origineEmissionService.createDevise(origineEmissionDTO);
    }

    @GetMapping("/devise")
    public ResponseEntity<Page<OrigineEmissionDeviseDTO>> getAllOrigineEmissionsDevise(@RequestParam(required = false) String keyword,Pageable pageable) {
        return ResponseEntity.ok(origineEmissionService.findAllByDevise(keyword,pageable));
    }
    @PostMapping("/devise/update")
    public ResponseEntity<?> updateOrigineEmissionDevise(
            @RequestBody @Valid final OrigineEmissionDeviseAddDto origineEmissionDTO) {
        return origineEmissionService.updateDevise( origineEmissionDTO);
    }

    @PostMapping("/{id}/nbrPoint/delete")
    public ResponseEntity<?> deleteNbrPoint(@PathVariable(name = "id") final Long id) {
        return origineEmissionService.deleteNbrPoint(id);
    }

    @GetMapping("/nbrPoint")
    public ResponseEntity<Page<OrigineEmissionDeviseDTO>> getAllOrigineEmissionsNbr(@RequestParam(required = false) String keyword,Pageable pageable) {
        return ResponseEntity.ok(origineEmissionService.findAllNbrPoint(keyword,pageable));
    }

}
