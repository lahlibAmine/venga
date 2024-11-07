package com.ram.venga.batch;

import com.ram.venga.domain.JobInfo;
import com.ram.venga.repos.JobInfoRepository;
import com.ram.venga.rest.BatchResource;
import com.ram.venga.service.BatchService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@EnableBatchProcessing
@Component
public class TransportJob {

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	BatchResource batchResource;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private CustomTransportProcessor customTransportProcessor;

	@Autowired
	private JobInfoRepository jobInfoRepository;
	
	 @Value("${app.resources}")
	  String resourcePath;
	

	public Job createTransportJob(String fileName, JobInfo jobInfo) throws Exception {

		return jobBuilderFactory.get("transportJob").start(transportStep(fileName, jobInfo)).build();

	}

	private ItemReader<String> transportReader(String fileName, JobInfo jobInfo) {
		return new CustomTransportReader(resourcePath+"/"+fileName, jobInfo);
	}

	private Step transportStep(String fileName, JobInfo jobInfo) throws Exception {
		return stepBuilderFactory.get("step2").<String, String>chunk(10).reader(transportReader(fileName, jobInfo))
				.processor(transportProcessor()).writer(transportWriter())
				.listener(new UpdateLastProcessedLineTransportWriteListener(jobInfo, jobInfoRepository))
				.listener(new LastProcessedLineItemReadListener(jobInfo)).build();
	}

	public ItemProcessor<String, String> transportProcessor() {
		return customTransportProcessor;
	};

	public ItemWriter<String> transportWriter() {
		return items -> {
		//	batchProcessService.writeTransportObject(items);
			ResponseEntity<Object> response = null;
			// Count the number of objects in the JSON array
				if( items.size() ==1 ) {

					batchResource.saveProcessedTransportObjectOne(items.get(0));
				}
				else if(items.size() > 1){
					batchResource.saveProcessedTransportObject(items);
				}

		};
	}

}
