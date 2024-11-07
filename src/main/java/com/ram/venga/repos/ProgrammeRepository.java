package com.ram.venga.repos;

import com.ram.venga.domain.Programme;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProgrammeRepository extends JpaRepository<Programme, Long> {
}
