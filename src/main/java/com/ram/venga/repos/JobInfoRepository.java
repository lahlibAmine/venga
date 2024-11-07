package com.ram.venga.repos;

import com.ram.venga.domain.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobInfoRepository extends JpaRepository<JobInfo, Long> {

	JobInfo findByFileName(String fileName);
}
