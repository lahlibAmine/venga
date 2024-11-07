package com.ram.venga.rest;

import com.ram.venga.model.RecetteBruteDTO;
import com.ram.venga.service.RecetteBruteService;
import javax.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/recetteBrutes", produces = "application/json")
public class RecetteBruteResource {

    private final RecetteBruteService recetteBruteService;

    public RecetteBruteResource(final RecetteBruteService recetteBruteService) {
        this.recetteBruteService = recetteBruteService;
    }

    @GetMapping
    public ResponseEntity<List<RecetteBruteDTO>> getAllRecetteBrutes() {
        return ResponseEntity.ok(recetteBruteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetteBruteDTO> getRecetteBrute(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(recetteBruteService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createRecetteBrute(
            @RequestBody @Valid final RecetteBruteDTO recetteBruteDTO) {
        final Long createdId = recetteBruteService.create(recetteBruteDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateRecetteBrute(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final RecetteBruteDTO recetteBruteDTO) {
        recetteBruteService.update(id, recetteBruteDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRecetteBrute(@PathVariable(name = "id") final Long id) {
        recetteBruteService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
