package com.ram.venga.repos;

import com.ram.venga.domain.DemandeInscription;
import com.ram.venga.domain.Devise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DevisesRepository  extends JpaRepository<Devise, Long> {
    @Query("select D from Devise D join OrigineEmission O on O.devise.id = D.id where D.id = :id")
    Devise existsByIdOrigineEmission(Long id);

    @Query("SELECT D FROM Devise D " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(:keyword <> '' AND " +
            "(LOWER(D.devise) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "ORDER BY D.dateCreated DESC")
    Page<Devise> findAllByKeyword(String keyword,Pageable pageable);
}
