package com.ram.venga.repos;

import com.ram.venga.domain.Ville;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VilleRepository extends JpaRepository<Ville, Long> {

    boolean existsByNomIgnoreCase(String nom);

}
