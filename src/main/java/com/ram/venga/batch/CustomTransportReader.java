package com.ram.venga.batch;

import com.ram.venga.domain.JobInfo;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class CustomTransportReader extends FlatFileItemReader<String> {
	
	 @Value("${app.resources}")
	 String resourcePath;

	public CustomTransportReader(String filePath, JobInfo jobInfo) {
		// Create a resource from the file path
		Resource resource = new FileSystemResource(filePath);
		this.setResource(resource);
		this.setLineMapper(new PassThroughLineMapper());

		if (jobInfo != null)
			this.setLinesToSkip(jobInfo.getLastProcessedLine() != null ? jobInfo.getLastProcessedLine().intValue() : 0);

		// Configurez ici d'autres propriétés du lecteur (delimiter, encoding, etc.)
	}

}
