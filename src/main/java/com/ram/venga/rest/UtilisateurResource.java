package com.ram.venga.rest;

import com.ram.venga.model.UtilisateurDTO;
import com.ram.venga.service.UtilisateurService;
import javax.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/utilisateurs", produces = "application/json")
public class UtilisateurResource {

    private final UtilisateurService utilisateurService;

    public UtilisateurResource(final UtilisateurService utilisateurService, PasswordEncoder passwordEncoder) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.findAll());
    }
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getUtilisateur(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(utilisateurService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createUtilisateur(
            @RequestBody @Valid final UtilisateurDTO utilisateurDTO) {
        final Long createdId = utilisateurService.create(utilisateurDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateUtilisateur(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final UtilisateurDTO utilisateurDTO) {
        utilisateurService.update(id, utilisateurDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable(name = "id") final Long id) {
        utilisateurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/Admin")
    public ResponseEntity<Long> createUtilisateurAdmin(
            @RequestBody @Valid final UtilisateurDTO utilisateurDTO) {
        final Long createdId = utilisateurService.create(utilisateurDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @GetMapping("/encoderPassword")
    public String passwordEncoder(@RequestParam String pass){

      return  passwordEncoder.encode(pass);

    }


}
