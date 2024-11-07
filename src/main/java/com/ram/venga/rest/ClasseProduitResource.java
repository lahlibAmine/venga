package com.ram.venga.rest;

import com.ram.venga.model.CadeauxBADTO;
import com.ram.venga.model.ClasseProduitDTO;
import com.ram.venga.service.ClasseProduitService;
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
@RequestMapping(value = "/api/v1/classeProduits", produces = "application/json")
public class ClasseProduitResource {

    private final ClasseProduitService classeProduitService;

    public ClasseProduitResource(final ClasseProduitService classeProduitService) {
        this.classeProduitService = classeProduitService;
    }

    @GetMapping
    public ResponseEntity<List<ClasseProduitDTO>> getAllClasseProduits() {
        return ResponseEntity.ok(classeProduitService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasseProduitDTO> getClasseProduit(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(classeProduitService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClasseProduit(
            @RequestBody @Valid final ClasseProduitDTO classeProduitDTO) {
        return classeProduitService.create(classeProduitDTO);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateClasseProduit(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ClasseProduitDTO classeProduitDTO) {
        return classeProduitService.update(id, classeProduitDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteClasseProduit(@PathVariable(name = "id") final Long id) {
        return classeProduitService.delete(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ClasseProduitDTO>> getAllClasseProduitByKeyword(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(classeProduitService.findByKeyWord(keyword,pageable));
    }

}

