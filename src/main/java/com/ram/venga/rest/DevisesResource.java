package com.ram.venga.rest;


import com.ram.venga.model.DeviseDto;
import com.ram.venga.service.DevisesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/devises",produces = "application/json")
public class DevisesResource {

   private final DevisesService  devisesService;

    public DevisesResource(DevisesService devisesService) {
        this.devisesService = devisesService;
    }

    @GetMapping("/list")
    public List<DeviseDto> getAllDevises(){
        return devisesService.devises();
    }

    @GetMapping
    public Page<DeviseDto> getAllDevisesPage(@RequestParam String keyword, Pageable pageable){
        return devisesService.devisesPage(keyword,pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<?> saveDevisesPage(@RequestBody DeviseDto deviseDto){
        return devisesService.saveDevises(deviseDto);
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateDevisesPage(@RequestBody DeviseDto deviseDto){
        return devisesService.updateDevises(deviseDto);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteDemandeInscription(@PathVariable(name = "id") final Long id) {

        return devisesService.delete(id);
    }
}

