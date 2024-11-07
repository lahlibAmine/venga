package com.ram.venga.rest;

import com.ram.venga.model.OpperationDTO;
import com.ram.venga.model.OpperationTraiterDto;
import com.ram.venga.model.OpperationTraiterGraphDto;
import com.ram.venga.service.OpperationService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/opperations", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpperationResource {

    private final OpperationService opperationService;

    public OpperationResource(final OpperationService opperationService) {
        this.opperationService = opperationService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OpperationDTO>> getAllOpperations() {
        return ResponseEntity.ok(opperationService.findAll());
    }

    @GetMapping
    public Page<OpperationTraiterDto>  getOpperationByIdCollaborateur(Pageable pageable) throws ExecutionException, InterruptedException {
        return opperationService.get(pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createOpperation(
            @RequestBody @Valid final OpperationDTO opperationDTO) {
        final Long createdId = opperationService.create(opperationDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateOpperation(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final OpperationDTO opperationDTO) {
        opperationService.update(id, opperationDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOpperation(@PathVariable(name = "id") final Long id) {
        opperationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/graph")
    public List<OpperationTraiterGraphDto>  getOpperationByIdCollaborateur() throws ExecutionException, InterruptedException {
        return opperationService.getGraphe();
    }
}



