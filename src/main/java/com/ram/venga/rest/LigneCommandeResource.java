package com.ram.venga.rest;

import com.ram.venga.model.LigneCommandeCreateDto;
import com.ram.venga.model.LigneCommandeDto;
import com.ram.venga.model.LigneCommandeValidateurDto;
import com.ram.venga.model.enumeration.StatutBAEnum;
import com.ram.venga.service.LigneCommandeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/ligneCommande", produces = "application/json")
public class LigneCommandeResource {
    private final LigneCommandeService ligneCommandeService;

    public LigneCommandeResource(LigneCommandeService ligneCommandeService) {
        this.ligneCommandeService = ligneCommandeService;
    }

    @GetMapping
    public Page<LigneCommandeDto> getLigneCommande( Pageable pageable,
           @RequestParam String fournisseur
            , @RequestParam String cadeau , @RequestParam String dateDebut ,
                                                 @RequestParam String dateFin , @RequestParam Integer pointDebut ,
                                                 @RequestParam Integer pointFin) {
        return ligneCommandeService.getLigneCommande(pageable,cadeau,dateDebut,dateFin,pointDebut,pointFin,fournisseur);
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> createLigneCommande(@RequestBody List<LigneCommandeDto> ligneCommandeDtos) throws MessagingException, UnsupportedEncodingException {
        return ligneCommandeService.create(ligneCommandeDtos);

    }

    @GetMapping("/validateur")
    public Page<LigneCommandeValidateurDto> getLigneCommandeValidateur(Pageable pageable,
                                                                       @RequestParam String fournisseur,
                                                                       @RequestParam String cadeau ,
                                                                       @RequestParam String dateDebut ,
                                                                       @RequestParam String dateFin ,
                                                                       @RequestParam Integer pointDebut ,
                                                                       @RequestParam Integer pointFin,
                                                                       @RequestParam String agent,
                                                                     //@RequestParam String signature,
                                                                       @RequestParam String agence) {
        return ligneCommandeService.getLigneCommandeValidateur(pageable,cadeau,dateDebut,dateFin,pointDebut,pointFin,fournisseur,agent/*,signature*/,agence);
    }

    @PostMapping("/update/{id}")
    public void updateStatus(@PathVariable(name = "id") final Long id, @RequestParam StatutBAEnum etat) {
         ligneCommandeService.updateStatus(id,etat);
    }

    @GetMapping("/validateur/export")
    public void getLigneCommandeValidateur(HttpServletResponse response,
                                                                       @RequestParam String fournisseur,
                                                                       @RequestParam String cadeau ,
                                                                       @RequestParam String dateDebut ,
                                                                       @RequestParam String dateFin ,
                                                                       @RequestParam Integer pointDebut ,
                                                                       @RequestParam Integer pointFin,
                                                                       @RequestParam String agent,
                                                                       //@RequestParam String signature,
                                                                       @RequestParam String agence) {
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=commande.xlsx");
         ligneCommandeService.getLigneCommandeValidateurExport(response,cadeau,dateDebut,dateFin,pointDebut,pointFin,fournisseur,agent/*,signature*/,agence);
    }


}
