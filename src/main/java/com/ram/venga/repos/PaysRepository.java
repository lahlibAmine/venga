package com.ram.venga.repos;

import com.ram.venga.domain.Pays;
import com.ram.venga.domain.OrigineEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


public interface PaysRepository extends JpaRepository<Pays, Long> {

    boolean existsByCodeIsoIgnoreCase(String codeIso);

    boolean existsByNomIgnoreCase(String nom);

    @Query("select p from Pays p where p.id not in (select distinct(o.pays.id) from OrigineEmission o ) order by p.dateCreated")
    List<Pays> getPaysNotInOrigin();

}
