package com.ram.venga.rest;

import com.ram.config.Roles;
import com.ram.venga.model.FournisseurDTO;
import com.ram.venga.service.FournisseurService;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
//@RolesAllowed({"ADMIN"})
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/fournisseurs", produces = "application/json")
public class FournisseurResource {

    private final FournisseurService fournisseurService;

    public FournisseurResource(final FournisseurService fournisseurService) {
        this.fournisseurService = fournisseurService;
    }

    @GetMapping("/commande")
    public ResponseEntity<List<FournisseurDTO>> getAllFournisseurs() {
        return ResponseEntity.ok(fournisseurService.findAll());
    }

    @GetMapping("/list")
    public ResponseEntity<List<FournisseurDTO>> getAllFournisseurss() {
        return ResponseEntity.ok(fournisseurService.findAllList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FournisseurDTO> getFournisseur(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(fournisseurService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFournisseur(
            @RequestBody @Valid final FournisseurDTO fournisseurDTO) {
        return fournisseurService.create(fournisseurDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateFournisseur(
            @RequestBody @Valid final FournisseurDTO fournisseurDTO) {
        return fournisseurService.update(fournisseurDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteFournisseur(@PathVariable(name = "id") final Long id) {
        return fournisseurService.delete(id);
    }
    @GetMapping()
    public ResponseEntity<Page<FournisseurDTO>> getAllFournisseursWithPage(@RequestParam String keyword,Pageable pageable) {
        return ResponseEntity.ok(fournisseurService.findAllWithPage(keyword,pageable));
    }

}
