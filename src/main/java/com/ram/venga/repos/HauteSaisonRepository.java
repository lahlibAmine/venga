package com.ram.venga.repos;

import com.ram.venga.domain.HauteSaison;
import com.ram.venga.domain.OrigineEmission;
import liquibase.pro.packaged.Q;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface HauteSaisonRepository extends JpaRepository<HauteSaison, Long> {

    boolean existsByLibelleIgnoreCase(String libelle);

    @Query("select HS from HauteSaison HS " +
            "where (:keyword = '' or :keyword is null) or " +
            "(:keyword <> '' and " +
            "(HS.libelle like %:keyword% )) " +
            "order by HS.dateCreated desc")
    Page<HauteSaison> findAllByKeyword(String keyword, Pageable pageable);
    @Query("select hs from HauteSaison hs where hs.origineEmission.id = :origineEmissionId")
    List<HauteSaison> findAllByOrigineEmission(Long origineEmissionId);
}
