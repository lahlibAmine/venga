package com.ram.venga.rest;

import com.ram.venga.domain.BonCommande;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.model.BonCommandeDTO;
import com.ram.venga.model.LigneCommandeDto;
import com.ram.venga.service.BonCommandeService;
import javax.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/bonCommandes", produces = "application/json")
public class BonCommandeResource {

    private final BonCommandeService bonCommandeService;

    public BonCommandeResource(final BonCommandeService bonCommandeService) {
        this.bonCommandeService = bonCommandeService;
    }

    @GetMapping
    public ResponseEntity<List<BonCommandeDTO>> getAllBonCommandes() {
        return ResponseEntity.ok(bonCommandeService.findAll());
    }

    @GetMapping("/{idBonCommande}")
    public BonCommande getBonCommande(@PathVariable(name = "idBonCommande") final Long id) {
        return bonCommandeService.getByCollaborateur(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createBonCommande(
            @RequestBody @Valid final BonCommandeDTO bonCommandeDTO) {
        final Long createdId = bonCommandeService.create(bonCommandeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateBonCommande(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final BonCommandeDTO bonCommandeDTO) {
        bonCommandeService.update(id, bonCommandeDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBonCommande(@PathVariable(name = "id") final Long id) {
        bonCommandeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /*  @GetMapping("/{idCollaborateur}")
    public List<BonCommande> getBonCommande(@PathVariable(name = "idCollaborateur") final Long id) {
        return bonCommandeService.getCommandeByUser(id);
    }
*/
}
