package com.ram.venga.rest;

import com.ram.venga.model.SegmentDTO;
import com.ram.venga.service.SegmentService;
import javax.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/segments",produces = "application/json")
public class SegmentResource {

    private final SegmentService segmentService;

    public SegmentResource(final SegmentService segmentService) {
        this.segmentService = segmentService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllSegments(@RequestParam(required = false , defaultValue = "false")  Boolean pageCheck,@RequestParam(required = false) String keyword, Pageable pageable) {
        return ResponseEntity.ok(segmentService.findAll(pageCheck,pageable,keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SegmentDTO> getSegment(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(segmentService.get(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSegment(@RequestBody @Valid final SegmentDTO segmentDTO) {
        return segmentService.create(segmentDTO);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateSegment(
            @RequestBody  SegmentDTO segmentDTO) {
        return  segmentService.update( segmentDTO);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteSegment(@PathVariable(name = "id") final Long id) {

        return  segmentService.delete(id);
    }

}
