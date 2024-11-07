package com.ram.venga.rest;

import com.ram.venga.model.*;
import com.ram.venga.model.enumeration.CategorieEntiteEnum;
import com.ram.venga.service.EntiteService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/entites",produces = "application/json")
public class EntiteResource {

    private final EntiteService entiteService;

    public EntiteResource(final EntiteService entiteService) {
        this.entiteService = entiteService;
    }

    @GetMapping
    public ResponseEntity<List<EntiteDTO>> getAllEntites() {
        return ResponseEntity.ok(entiteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntiteDTO> getEntite(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(entiteService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEntite(@RequestBody @Valid final EntiteDTO entiteDTO) {
        ResponseEntity<?> response = entiteService.create(entiteDTO);
        return response;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEntite(
            @RequestBody @Valid final EntiteDTO entiteDTO) {
       return entiteService.update(entiteDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteEntite(@PathVariable(name = "id") final Long id) {
        return entiteService.delete(id);
    }

    @GetMapping("/categorie")
    public Page<EntiteDTO> findByCategorie(@RequestParam CategorieEntiteEnum categorie,@RequestParam String keyword, Pageable pageable){
        return entiteService.findByCategorie(categorie,keyword,pageable);
    }

    @GetMapping("/categorie-list")
    public List<EntiteDTO> findAllByCategorie(@RequestParam CategorieEntiteEnum categorie,@RequestParam(required = false,defaultValue = "false") Boolean isCreate){
        return entiteService.finAllByCategorie(categorie,isCreate);
    }

    @GetMapping("/agence")
    public Page<EntiteDTO> findByCategorie(@RequestParam String codeIata,
                                           @RequestParam String nomAgence,
                                           @RequestParam String originEmission,
                                           @RequestParam String porteFeuille,
                                           @RequestParam String representation,
                                           Pageable pageable){

        return entiteService.agenceFilter(codeIata,nomAgence,originEmission,porteFeuille,representation,pageable);
    }

    @GetMapping("/list")
    public List<EntiteNomUdpadeDto> findAll(){
        return entiteService.findAllEntite();
    }

    @GetMapping("/{id}/parent")
    public List<EntiteCreateListDTO> portfeuilleByRepresentation(@PathVariable(name = "id") Long idRepresentation){
        return entiteService.portfeuilleByRepresentation(idRepresentation);
    }
    @GetMapping("/categorie-list-search")
    public Map<String,List<EntiteDTO>> findAllByCategorieSearc(@RequestParam String portfeuille, @RequestParam String representation){
        return entiteService.finAllByCategorieSearch(portfeuille,representation);
    }

    @GetMapping("/rattache/agences")
    public Page<EntiteDTO> getAllAgencesByAuthenticatedRattache(@RequestParam String keyword, Pageable pageable){
        return entiteService.agencesByAuthenticatedRattache(keyword, pageable);
    }

    @GetMapping("/consultant/agences")
    public Page<EntiteDTO> getAllAgencesByAuthenticatedConsultant(@RequestParam String keyword, Pageable pageable){
        return entiteService.agencesByAuthenticatedConsultant(keyword, pageable);
    }

    @GetMapping("/agences/export")
    public void getAllAgencesByAuthenticatedConsultantExport(@RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) String codeIata,
                                                             @RequestParam(required = false) String nomAgence,
                                                             @RequestParam(required = false) String originEmission,
                                                             @RequestParam(required = false) String porteFeuille,
                                                             @RequestParam(required = false) String representation, HttpServletResponse response, Pageable pageable){
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Agence.xlsx");

        entiteService.agencesByAuthenticatedConsultantExport(response,keyword,codeIata,nomAgence,originEmission,porteFeuille,representation, pageable);
    }


}

