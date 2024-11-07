package com.ram.venga.batch;

import com.ram.venga.domain.JobInfo;
import org.springframework.batch.core.ItemReadListener;

public class LastProcessedLineItemReadListener implements ItemReadListener<String> {

	private Long lastProcessedLine; // Initialisez la valeur

	private JobInfo jobInfo;

	public LastProcessedLineItemReadListener(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
		this.lastProcessedLine = jobInfo.getLastProcessedLine();
	}

	@Override
	public void beforeRead() {
		// Ne rien faire avant la lecture
	}

	@Override
	public void afterRead(String item) {
		// Mettez à jour la position du dernier élément lu
		lastProcessedLine++;
		jobInfo.setLastProcessedLine(lastProcessedLine);
	}

	@Override
	public void onReadError(Exception ex) {
		// Gérer les erreurs de lecture si nécessaire
	}

	public Long getLastProcessedLine() {
		return this.lastProcessedLine;
	}
}
