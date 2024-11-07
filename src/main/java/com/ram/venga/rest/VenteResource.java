package com.ram.venga.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.model.*;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import com.ram.venga.projection.ListEmissionProjection;
import com.ram.venga.service.VenteService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/ventes", produces = "application/json")
public class VenteResource {

    private final VenteService venteService;

    public VenteResource(final VenteService venteService) {
        this.venteService = venteService;
    }

    @GetMapping
    public ResponseEntity<List<VenteDTO>> getAllVentes() {
        return ResponseEntity.ok(venteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenteDTO> getVente(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(venteService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createVente(@RequestBody @Valid final VenteDTO venteDTO) {
        final Long createdId = venteService.create(venteDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateVente(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final VenteDTO venteDTO) {
        venteService.update(id, venteDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVente(@PathVariable(name = "id") final Long id) {
        venteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/emission")
    public Page<VenteDTO> getEmission(@RequestParam String dateDebut ,
                                   @RequestParam String dateFin,
                                   @RequestParam String origine,
                                   @RequestParam String destination,
                                   @RequestParam(defaultValue = "") String classeR,
                                   @RequestParam String montantDebut,
                                   @RequestParam String montantFin,
                                   @RequestParam String agence,
                                   @RequestParam List<String> portfeuille,
                                   @RequestParam List<String> representation,
                                   @RequestParam(required = false) List<StatutVenteEnum> rapprocher,
                                   @RequestParam String numBillet,
                                   @RequestParam String signature,
                                   @RequestParam(required = false,defaultValue = "false") Boolean isArchived,
                                   Pageable pageable
                                            ) throws ParseException {
       return venteService.getEmission(dateDebut,dateFin,origine,destination,classeR,montantDebut,montantFin,agence,portfeuille,representation,rapprocher,pageable,signature,numBillet,isArchived);

    }

    @GetMapping("/chiffre-affaire")
    public Page<CollaborateurEntiteDTO>  chiffreAffaire(@RequestParam String dateDebut , @RequestParam String dateFin, @RequestParam String Agence, @RequestParam String agent,@RequestParam String portfeuille,
                                                        @RequestParam String representation, Pageable pageable) {
        return venteService.getChiffreAffaire(dateDebut,dateFin,Agence,agent,portfeuille,representation,pageable);
    }

    @GetMapping("/checkSignature")
    public Page<VenteDTO> checkSignature(
            @RequestParam String origineEmission,
            @RequestParam String codeIata,
            @RequestParam String numBillet,
            @RequestParam String signature,
            @RequestParam(required = false) Long representationId,
            @RequestParam(required = false) Long portefeuilleId,
            Pageable pageable
    ) {
        return venteService.checkSignature(origineEmission,codeIata,numBillet,signature,representationId,portefeuilleId,pageable);
    }

    @PostMapping("/updateSignature/update")
        public ResponseEntity<Long> updateCollaborateur(@RequestParam Long id,@RequestBody SignatureDto signature) throws JsonProcessingException {
       // final Long updateSignature = venteService.updateSignature(id,signature);
        return venteService.updateSignature(id,signature);
    }
    @GetMapping("/emissionExcel")
    public void getEmissionlist(@RequestParam String dateDebut ,
                                @RequestParam String dateFin,
                                @RequestParam String origine,
                                @RequestParam String destination,
                                @RequestParam String classeR,
                                @RequestParam String montantDebut,
                                @RequestParam String montantFin,
                                @RequestParam String agence,
                                @RequestParam List<String> portfeuille,
                                @RequestParam List<String> representation,
                                @RequestParam(required = false) List<StatutVenteEnum> rapprocher,
                                @RequestParam String numBillet,
                                @RequestParam String signature,
                                @RequestParam(required = false) String motif,
                                HttpServletResponse response
    ) throws ParseException, IOException {
        venteService.getEmissionList(dateDebut,dateFin,origine,destination,classeR,montantDebut,montantFin,agence,portfeuille,representation,response,rapprocher,signature,numBillet);
    }

    @GetMapping("/listOrigine")
    public Set<String> origine() {
        return venteService.getOrigine();
    }
    @GetMapping("/listDestination")
    public Set<String>  Destination() {
        return venteService.getDestination();
    }

    @PostMapping("/signature-agent")
    public HandlerDto checkSignatureAgent(@RequestBody SignatureDto signature
    ) {
        return venteService.checkSignatureAgent(signature);

    }

    @GetMapping("/chiffre-affaire-export")
    public void  chiffreAffaireExport(@RequestParam String dateDebut , @RequestParam String dateFin, @RequestParam String Agence, @RequestParam String agent,@RequestParam String portfeuille,
                                                        @RequestParam String representation, HttpServletResponse response) {
        venteService.getChiffreAffaireExport(dateDebut,dateFin,Agence,agent,portfeuille,representation,response);
    }

    @PostMapping("/updateSignature/updateGroup")
    public ResponseEntity<?> updateCollaborateurGrouped(@RequestBody SignatureDto signature) throws JsonProcessingException {
        // final Long updateSignature = venteService.updateSignature(id,signature);
        return venteService.updateSignatureGrouped(signature);
    }
    @GetMapping("/checkSignatureExport")
    public void checkSignatureExport(
            HttpServletResponse response,
            @RequestParam String origineEmission,
            @RequestParam String codeIata,
            @RequestParam String numBillet,
            @RequestParam String signature,
            @RequestParam(required = false) Long representationId,
            @RequestParam(required = false) Long portefeuilleId) {
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Agents.xlsx");

        venteService.exportCheckSignature(response,origineEmission,codeIata,numBillet,signature,representationId,portefeuilleId);
    }
}

