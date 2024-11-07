package com.ram.venga.rest;

import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.model.CadeauBaPost;
import com.ram.venga.model.CadeauxBADTO;
import com.ram.venga.model.LigneCommandeDto;
import com.ram.venga.service.CadeauxBAService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/cadeaux-ba", produces = "application/json")
public class CadeauxBAResource {

    private final CadeauxBAService cadeauxBAService;

    public CadeauxBAResource(final CadeauxBAService cadeauxBAService) {
        this.cadeauxBAService = cadeauxBAService;
    }

    @GetMapping
    public ResponseEntity<List<CadeauxBADTO>> getAllCadeauxBAs() {
        return ResponseEntity.ok(cadeauxBAService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CadeauxBADTO> getCadeauxBA(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(cadeauxBAService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCadeauxBA(
            @RequestBody @Valid final CadeauBaPost cadeauxBADTO) {
        return cadeauxBAService.create(cadeauxBADTO);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateCadeauxBA(
            @RequestBody @Valid final CadeauBaPost cadeauxBADTO) {
        return cadeauxBAService.update( cadeauxBADTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteCadeauxBA(@PathVariable(name = "id") final Long id) {
        return cadeauxBAService.delete(id);
    }

    @GetMapping("/fournisseur")
    public Map<String,List<CadeauxBA>> getLigneCommandebyFournisseur() {
        return cadeauxBAService.getLigneCommandeByFournisseur();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CadeauxBADTO>> getAllCadeauxBAsByKeyword(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(cadeauxBAService.findByKeyWord(keyword,pageable));
    }

}