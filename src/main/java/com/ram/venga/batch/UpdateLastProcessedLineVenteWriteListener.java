package com.ram.venga.batch;

import com.ram.venga.domain.JobInfo;
import com.ram.venga.repos.JobInfoRepository;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class UpdateLastProcessedLineVenteWriteListener implements ItemWriteListener<Object> {

	private JobInfoRepository jobInfoRepository;

	private JobInfo jobInfo;

	public UpdateLastProcessedLineVenteWriteListener(JobInfo jobInfo, JobInfoRepository jobInfoRepository) {
		this.jobInfo = jobInfo;
		this.jobInfoRepository = jobInfoRepository;
	}

	@Override
	public void afterWrite(List<? extends Object> items) {

		// Mettez à jour lastProcessedLine avec la valeur appropriée

		jobInfoRepository.save(jobInfo);

	}

	@Override
	public void beforeWrite(List<? extends Object> items) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWriteError(Exception exception, List<? extends Object> items) {
		// TODO Auto-generated method stub

	}

	// ...
}
