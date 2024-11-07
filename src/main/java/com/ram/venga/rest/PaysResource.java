package com.ram.venga.rest;

import com.ram.venga.model.PaysDTO;
import com.ram.venga.service.PaysService;
import javax.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/pays", produces = "application/json")
public class PaysResource {

    private final PaysService paysService;

    public PaysResource(final PaysService paysService) {
        this.paysService = paysService;
    }

    @GetMapping
    public ResponseEntity<List<PaysDTO>> getAllPayss() {
        return ResponseEntity.ok(paysService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaysDTO> getPays(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(paysService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createPays(@RequestBody @Valid final PaysDTO paysDTO) {
        final Long createdId = paysService.create(paysDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updatePays(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PaysDTO paysDTO) {
        paysService.update(id, paysDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deletePays(@PathVariable(name = "id") final Long id) {
        paysService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pays-disponible")
    public ResponseEntity<List<PaysDTO>> getPaysNotInOrigin() {
        List<PaysDTO> paysDTOS = paysService.getPaysNotInOrigin();
        return ResponseEntity.ok(paysDTOS);
    }

    @GetMapping("/pays-disponible/{id}")
    public ResponseEntity<List<PaysDTO>> getPaysNotInOriginIncludeCurrent(@PathVariable(name = "id") final Long id) {
        List<PaysDTO> paysDTOS = paysService.getPaysNotInOriginIncludeCurrent(id);
        return ResponseEntity.ok(paysDTOS);
    }

}
