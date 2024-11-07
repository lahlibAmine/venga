package com.ram.venga.rest;

import com.ram.venga.domain.Image;
import com.ram.venga.model.ImageDTO;
import com.ram.venga.model.ImageOrigineDTO;
import com.ram.venga.service.ImageService;
import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/images", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageResource {

    private final ImageService imageService;

    public ImageResource(final ImageService imageService) {
        this.imageService = imageService;
    }

   /* @GetMapping
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        return ResponseEntity.ok(imageService.findAll());
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageBytes(@PathVariable Long id) {
        byte[] imageData = imageService.getImageBytes(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(imageService.getImageMediaTypeByFileName(id));
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createImage(@RequestParam List<MultipartFile> imageFile, @RequestParam Long idOrigine, @RequestParam String description) throws IOException {

        return imageService.saveImage(imageFile,idOrigine,description);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Void> updateImage(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ImageDTO imageDTO) {
        imageService.update(id, imageDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable(name = "id") final Long id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("")
    public ResponseEntity getAllByOrigine() {
        return imageService.getAllByOrigine();
    }
    
}

