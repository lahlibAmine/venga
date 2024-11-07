package com.ram.venga.repos;

import com.ram.venga.domain.CadeauxBA;
import com.ram.venga.domain.Devise;
import com.ram.venga.domain.OrigineEmission;
import com.ram.venga.domain.Pays;
import com.ram.venga.model.CadeauxBADTO;
import com.ram.venga.service.CategorieCadeauService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CadeauxBARepository extends JpaRepository<CadeauxBA, Long> {


    @Query("select c " +
            "from CadeauxBA c " +
            "where  LOWER(c.origineEmission.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.fournisseur.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.categorieCadeau.libelle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "order by c.dateCreated desc ")
    Page<CadeauxBA> findByKeyWord(String keyword, Pageable pageable);

    List<CadeauxBA> findByCategorieCadeauId(Long id);

    @Query("select C.origineEmission from CadeauxBA C where C.origineEmission.pays.id = :idPays group by C.origineEmission.pays")
    OrigineEmission findByPays(Long idPays);
}
