package com.ram.venga.repos;

import com.ram.venga.domain.Offre;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OffreRepository extends JpaRepository<Offre, Long> {
    Offre findByOrigineEmissionId(Long idOrigine);
}
