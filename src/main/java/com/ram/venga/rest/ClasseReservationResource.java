package com.ram.venga.rest;

import com.ram.venga.model.ClasseReservationDTO;
import com.ram.venga.service.ClasseReservationService;
import javax.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/classeReservations",produces = "application/json")
public class ClasseReservationResource {

    private final ClasseReservationService classeReservationService;

    public ClasseReservationResource(final ClasseReservationService classeReservationService) {
        this.classeReservationService = classeReservationService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllClasseReservations(@RequestParam(required = false,defaultValue = "false") Boolean pageCheck,@RequestParam(required = false) String keyword, Pageable pageable) {
        return ResponseEntity.ok(classeReservationService.findAll(pageCheck,keyword,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasseReservationDTO> getClasseReservation(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(classeReservationService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClasseReservation(
            @RequestBody @Valid final ClasseReservationDTO classeReservationDTO) {
        return classeReservationService.create(classeReservationDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateClasseReservation(
            @RequestBody  ClasseReservationDTO classeReservationDTO) {
       return classeReservationService.update( classeReservationDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteClasseReservation(@PathVariable(name = "id") final Long id) {
       return classeReservationService.delete(id);
    }

}

