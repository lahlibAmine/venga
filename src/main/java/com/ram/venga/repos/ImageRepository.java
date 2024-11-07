package com.ram.venga.repos;

import com.ram.venga.domain.Image;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.model.ImageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT new com.ram.venga.model.ImageDTO(I.id,I.url, I.offre.id, I.description, I.offre.origineEmission.nom) FROM Image I WHERE I.offre.origineEmission = :origineEmission OR :origineEmission IS NULL")
    List<ImageDTO> findByOffreOrigine(OrigineEmission origineEmission);
}
