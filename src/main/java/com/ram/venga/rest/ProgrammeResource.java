package com.ram.venga.rest;

import com.ram.venga.model.ProgrammeDTO;
import com.ram.venga.service.ProgrammeService;
import javax.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/programmes",produces = "application/json")
public class ProgrammeResource {

    private final ProgrammeService programmeService;

    public ProgrammeResource(final ProgrammeService programmeService) {
        this.programmeService = programmeService;
    }

    @GetMapping
    public ResponseEntity<Page<ProgrammeDTO>> getAllProgrammes(Pageable pageable) {
        return ResponseEntity.ok(programmeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgrammeDTO> getProgramme(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(programmeService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createProgramme(
            @RequestBody @Valid final ProgrammeDTO programmeDTO) {
        final Long createdId = programmeService.create(programmeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateProgramme(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ProgrammeDTO programmeDTO) {
        programmeService.update(id, programmeDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProgramme(@PathVariable(name = "id") final Long id) {
        programmeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
