package com.ram.venga.service;

import com.ram.venga.domain.Image;
import com.ram.venga.domain.Offre;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.mapper.ImageMapper;
import com.ram.venga.model.ImageDTO;
import com.ram.venga.model.ImageOrigineDTO;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.ProfilEnum;
import com.ram.venga.repos.ImageRepository;
import com.ram.venga.repos.OffreRepository;
import com.ram.venga.repos.OrigineEmissionRepository;
import com.ram.venga.repos.UtilisateurRepository;
import com.ram.venga.util.NotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final OffreRepository offreRepository;
    private final ImageMapper imageMapper;
    private final UtilisateurRepository utilisateurRepository;

    private final OffreService offreService;
    private final OrigineEmissionRepository origineEmissionRepository;
    private final KeycloackService keycloackService;

    public ImageService(final ImageRepository imageRepository,
                        final OffreRepository offreRepository, ImageMapper imageMapper, UtilisateurRepository utilisateurRepository, OffreService offreService, OrigineEmissionRepository origineEmissionRepository, KeycloackService keycloackService) {
        this.imageRepository = imageRepository;
        this.offreRepository = offreRepository;
        this.imageMapper = imageMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.offreService = offreService;
        this.origineEmissionRepository = origineEmissionRepository;
        this.keycloackService = keycloackService;
    }

   /* public List<ImageDTO> findAll() {
        final List<Image> images = imageRepository.findAll(Sort.by("id"));
        return images.stream()
                .map(image -> imageMapper.toDto(image))
                .toList();
    }
*/
    public ImageDTO get(final Long id) {
        return imageRepository.findById(id)
                .map(image -> imageMapper.toDto(image))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ImageDTO imageDTO) {
        final Image image = new Image();
        imageMapper.toEntity(imageDTO);
        return imageRepository.save(image).getId();
    }

    public void update(final Long id, final ImageDTO imageDTO) {
        final Image image = imageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        imageMapper.toEntity(imageDTO);
        imageRepository.save(image);
    }

    public void delete(final Long id) {
        imageRepository.deleteById(id);
    }

    private ImageDTO mapToDTO(final Image image, final ImageDTO imageDTO) {
        imageDTO.setId(image.getId());
        imageDTO.setUrl(image.getUrl());
        imageDTO.setOffre(image.getOffre() == null ? null : image.getOffre().getId());
        return imageDTO;
    }

    private Image mapToEntity(final ImageDTO imageDTO, final Image image) {
        image.setUrl(imageDTO.getUrl());
        final Offre offre = imageDTO.getOffre() == null ? null : offreRepository.findById(imageDTO.getOffre())
                .orElseThrow(() -> new NotFoundException("offre not found"));
        image.setOffre(offre);
        return image;
    }


    public ResponseEntity<?> saveImage(List<MultipartFile> imageData, Long idOrigine,String description) throws IOException {
        Offre offreRest = offreRepository.findByOrigineEmissionId(idOrigine);
        Offre offre = new Offre();
        if(offreRest == null){

            OrigineEmission origineEmission =  origineEmissionRepository.findById(idOrigine).get();
            offre.setOrigineEmission(origineEmission);
            offreRepository.save(offre);
        }else{
            offre = offreRest;
        }


        List<Long> listId = new ArrayList<>();
        for(MultipartFile image : imageData){
            byte[] bytes = image.getBytes();
            Image newImage = new Image();
            newImage.setImageData(bytes);
            newImage.setOffre(offre);
            newImage.setNomFichier(image.getOriginalFilename());
            newImage.setDescription(description);
            Long id = imageRepository.save(newImage).getId();
            listId.add(id);
        }
        return ResponseEntity.ok(listId);
    }

    public byte[] getImageBytes(Long imageId) {
        Optional<Image> imageOptional = imageRepository.findById(imageId);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            return image.getImageData();
        } else {
            throw new IllegalArgumentException("Image not found with ID: " + imageId);
        }
    }

    public MediaType getImageMediaTypeByFileName(Long imageId) {
        Optional<Image> imageOptional = imageRepository.findById(imageId);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            String fileName = image.getNomFichier().toLowerCase();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return MediaType.IMAGE_JPEG;
            } else if (fileName.endsWith(".png")) {
                return MediaType.IMAGE_PNG;
            } else if (fileName.endsWith(".webp")) {
                return MediaType.valueOf("image/webp");
            }else if (fileName.endsWith(".gif")) {
                return MediaType.IMAGE_GIF;
            }


            // Add more conditions for other image types if needed
        }
        throw new IllegalArgumentException("Image not found with ID: " + imageId);
    }

    public ResponseEntity getAllByOrigine() {
       String isUser = keycloackService.getIdUserToken();
        Utilisateur utilisateur = utilisateurRepository.findByRefKUser(isUser).get();
        List<ImageDTO> image = new ArrayList<>();
        if(utilisateur.getProfil().equals(ProfilEnum.SUPER_ADMIN)){
            image =   imageRepository.findByOffreOrigine(null);

        }else{
            OrigineEmission origineEmission = utilisateur.getCollaborateur().getEntite().getOrigineEmission();
            image =   imageRepository.findByOffreOrigine(origineEmission);

        }

            // List<ImageDTO> imageDTO =imageMapper.toDto(image);
            return ResponseEntity.ok(image);
    }

}
