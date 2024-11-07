package com.ram.venga.rest;

import com.ram.venga.model.CategorieCadeauDTO;
import com.ram.venga.service.CategorieCadeauService;
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
@RequestMapping(value = "/api/v1/bons-achat",produces = "application/json")
public class CategorieCadeauResource {

    private final CategorieCadeauService categorieCadeauService;

    public CategorieCadeauResource(final CategorieCadeauService categorieCadeauService) {
        this.categorieCadeauService = categorieCadeauService;
    }

    @GetMapping
    public ResponseEntity<Page<CategorieCadeauDTO>> getAllCategorieCadeaus(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(categorieCadeauService.findAll(keyword,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieCadeauDTO> getCategorieCadeau(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(categorieCadeauService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createCategorieCadeau(
            @RequestBody @Valid final CategorieCadeauDTO categorieCadeauDTO) {
        final Long createdId = categorieCadeauService.create(categorieCadeauDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateCategorieCadeau(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final CategorieCadeauDTO categorieCadeauDTO) {
        categorieCadeauService.update(id, categorieCadeauDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategorieCadeau(@PathVariable(name = "id") final Long id) {
        return categorieCadeauService.delete(id);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategorieCadeauDTO>> getAllCategorieCadeausList(Pageable pageable) {
        return ResponseEntity.ok(categorieCadeauService.listAll());
    }

}

