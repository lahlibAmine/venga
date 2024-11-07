package com.ram.venga.batch;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.reflect.TypeToken;
import com.ram.venga.domain.JobInfo;
import com.ram.venga.domain.Vente;
import com.ram.venga.repos.JobInfoRepository;
import com.ram.venga.rest.BatchResource;
import lombok.extern.slf4j.Slf4j;
import net.snowflake.client.jdbc.internal.google.gson.Gson;
import net.snowflake.client.jdbc.internal.google.gson.JsonArray;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@EnableBatchProcessing
@Component
@Slf4j
public class VenteJob {

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Lazy
	private CustomVenteProcessor customVenteProcessor;

	@Autowired
	private JobInfoRepository jobInfoRepository;
	@Autowired
	private BatchResource batchResource;


	 @Value("${app.resources}")
	  String resourcePath;

	public Job createVenteJob(String fileName, JobInfo jobInfo) throws Exception {

		return jobBuilderFactory.get("venteJob").start(transportStep(fileName, jobInfo)).build();

	}

	private ItemReader<String> venteReader(String fileName, JobInfo jobInfo) throws Exception {


		return new CustomVenteReader(resourcePath+"/"+fileName, jobInfo);
	}

	private Step transportStep(String fileName, JobInfo jobInfo) throws Exception {
		return stepBuilderFactory.get("step2").<String, String>chunk(10).reader(venteReader(fileName, jobInfo))
				.processor(venteProcessor()).writer(venteWriter())
				.listener(new UpdateLastProcessedLineVenteWriteListener(jobInfo, jobInfoRepository))
				.listener(new LastProcessedLineItemReadListener(jobInfo)).build();
	}

	public ItemProcessor<String, String> venteProcessor() {
		return customVenteProcessor;
	};

	public ItemWriter<String> venteWriter() {
		return items -> {
			ResponseEntity<Object> response = null;

			// Configure ObjectMapper for Jackson to handle Java 8+ date/time types
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());  // Register JavaTimeModule for LocalDateTime support
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: use ISO-8601 format

			for (String item : items) {
				// Vérifie que "item" est une chaîne JSON valide et non une référence d'objet
				if (item.startsWith("{") || item.startsWith("[")) {
					// Deserialize the JSON string into a JsonNode
					JsonNode jsonNode = objectMapper.readTree(item);  // Parse item into a JsonNode

					if (jsonNode.isArray()) {
						// Deserialize a list of Vente objects from JSON
						List<Vente> venteList = objectMapper.readValue(jsonNode.toString(), new TypeReference<List<Vente>>() {});
						response = batchResource.saveProcessedVenteObject(venteList);
					} else {
						// Deserialize a single Vente object from JSON
						Vente venteObject = objectMapper.treeToValue(jsonNode, Vente.class);
						response = batchResource.saveProcessedVenteObjectOne(venteObject);
					}

					// Check response status
					if (response != null && response.getStatusCodeValue() != 200) {
						throw new Exception("Error: Response returned status code " + response.getStatusCodeValue());
					}
				}
			}
		};
	}


}

