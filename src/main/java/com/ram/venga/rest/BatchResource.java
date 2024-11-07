package com.ram.venga.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.venga.batch.TransportJob;
import com.ram.venga.batch.VenteJob;
import com.ram.venga.domain.JobInfo;
import com.ram.venga.domain.Vente;
import com.ram.venga.repos.JobInfoRepository;
import com.ram.venga.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.xml.sax.SAXException;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/batch", produces = "application/json")
@Slf4j
@EnableBatchProcessing
public class BatchResource {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	private JobInfoRepository jobInfoRepository;

	@Autowired
	private BatchService batchService;
	@Autowired
	@Lazy
	private VenteJob venteJob;

	@Autowired
	@Lazy
	TransportJob transportJob;

	@Value("${app.resources}")
	String resourcePath;
	@Value("${app.ArchivePath}")
	String ArchivePath;

	private final JournalRapprochementService journalRapprochementService;
	private final MailService mailService;
	public BatchResource(JournalRapprochementService journalRapprochementService, MailService mailService) {
		this.journalRapprochementService = journalRapprochementService;
		this.mailService = mailService;
	}

	@GetMapping("/process/venteElement")
	public ResponseEntity<Map<String, String[]>> sendElementToExtract() {
		return ResponseEntity.ok(batchService.constructVenteElementToExtract());
	}

	@PostMapping("/process/vente")
	public ResponseEntity<Object> sendProcessedVenteObject(@RequestBody String mapString) throws IOException, ParserConfigurationException, SAXException {
		return ResponseEntity.ok(batchService.sendProcessedVenteObject(mapString));
	}

	@PostMapping("/write/vente")
	public ResponseEntity<Object> saveProcessedVenteObject(@RequestParam List<Vente> ventes) {
		return ResponseEntity.ok(batchService.saveVente(ventes));
	}

	@PostMapping("/write/vente/one")
	public ResponseEntity<Object> saveProcessedVenteObjectOne(@RequestParam Vente vente) {
		return ResponseEntity.ok(batchService.saveVenteOne(vente));
	}

	@GetMapping("/process/transportElement")
	public ResponseEntity<Map<String, Long[]>> sendTransportElementToExtract() {
		return ResponseEntity.ok(batchService.constructTransportElementToExtract());
	}

	@PostMapping("/process/transport")
	public String sendProcessedTransportObject(@RequestParam Map<String, String> map) throws JsonProcessingException {
		return batchService.sendProcessedTransportObject(map);
	}

	@PostMapping("/write/transport")
	public ResponseEntity<Object> saveProcessedTransportObject(@RequestParam List<? extends String> items) {
		return ResponseEntity.ok(batchService.saveTransport(items));
	}

	@PostMapping("/rapprochement")
	public ResponseEntity<?> createJournalRapprochement() {
		return journalRapprochementService.create();
	}

	@PostMapping("/updateVenteNonIntegre")
	public void updateVenteNonIntegre() {
		  journalRapprochementService.updateVenteNonIntegre();
	}
	@PostMapping("/updateRecetteNonIntegre")
	public void updateRecetteNonIntegre() {
		 journalRapprochementService.updateRecetteNonIntegre();
	}

	@PostMapping("/write/transport/one")
	public ResponseEntity<Object> saveProcessedTransportObjectOne(@RequestParam  String items) {
		return ResponseEntity.ok(batchService.saveTransportOne(items));
	}
	@PostMapping("/Vente/nonRapprocherParErreur/update")
	public void updateVenteNonRapprocherParErreur(){
		journalRapprochementService.updateRapprocherParErreur();
	}
	//@Scheduled(cron = "${scheduled.archivage}")
	@PostMapping("/archivage-date-vente/update")
	public void achivageDateVente(){
		log.info("start archivage :");
		journalRapprochementService.achivageDateVente();
	}
	@PostMapping("/send/emailWarning")
	public void sendEmailWarning(@RequestParam  String file) throws MessagingException, UnsupportedEncodingException {
		mailService.sendEmailWarning(file);
	}
/*	@PostMapping("/delete/collaborateur")
	public void deleteCollaborateur(){
		collaborateurService.deleteCollaborateur();
	}*/
	@PostMapping("/job/transport")
//	@Scheduled(cron = "${scheduled.transport}")
	public void transportJob() throws Exception {
		log.info("start transport scheduled");
		File directory = new File(resourcePath);

		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles((dir, name) -> {
				// Vérifier si le nom du fichier contient "rec"
				return name.contains("rec");
			});

			for (File file : files) {
				String fileName = file.getName();
				log.info("file" +fileName);
				JobInfo jobInfo = jobInfoRepository.findByFileName(fileName);

				if (jobInfo == null)
					jobInfo = jobInfoRepository.save(new JobInfo(null, fileName, 0L, LocalDateTime.now(),null));

				JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
						.addString("fileName", fileName).toJobParameters();

				try {
					jobLauncher.run(transportJob.createTransportJob(fileName, jobInfo), jobParameters);
					jobInfo.setEndDay(LocalDateTime.now());
					jobInfoRepository.save(jobInfo);
					createJournalRapprochement();
					// Supprimer le fichier après le traitement si nécessaire
					Path sourcePath = file.toPath();
					Path targetPath = Paths.get(ArchivePath, fileName);

					// Déplacer le fichier après le traitement
					Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);				}
				catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
					   | JobParametersInvalidException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@PostMapping("/job/vente")
//	@Scheduled(cron = "${scheduled.vente}")
	public void venteJob() throws Exception {
		log.info("start vente scheduled");
		File directory = new File(resourcePath);

		if (directory.exists() && directory.isDirectory()) {

			File[] files = directory.listFiles((dir, name) -> name.contains("PRD"));

			if (files != null && files.length == 0) {
				log.info("Aucun fichier trouvé dans le répertoire : " + directory.getAbsolutePath());
			} else if (files == null) {
				log.error("Erreur lors de la lecture des fichiers dans le répertoire : " + directory.getAbsolutePath());
			} else {
				log.info(files.length + " fichier(s) trouvé(s) dans le répertoire : " + directory.getAbsolutePath());
			}

			// Définir le format de date attendu dans le nom du fichier
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

			// Trier les fichiers par leur date de manière ascendante
			Arrays.sort(files, Comparator.comparing(file -> {
				String fileName = file.getName();
				// Extraire la date du nom du fichier
				int startIndex = fileName.indexOf(".SBR.BATCH.D") + ".SBR.BATCH.D".length();
				int endIndex = startIndex + 6; // Assuming the date format is always yyMMdd
				String dateString = fileName.substring(startIndex, endIndex);
				try {
					// Parser la date dans le format attendu
					Date date = dateFormat.parse(dateString);
					return date;
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}));
			for (File file : files) {
				String fileName = file.getName();
				log.info("file" +fileName);
				JobInfo jobInfo = jobInfoRepository.findByFileName(fileName);

				if (jobInfo == null)
					jobInfo = jobInfoRepository.save(new JobInfo(null, fileName, 0L,LocalDateTime.now(),null));

				JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
						.addString("fileName", fileName).toJobParameters();

				try {
					jobLauncher.run(venteJob.createVenteJob(fileName, jobInfo), jobParameters);
					jobInfo.setEndDay(LocalDateTime.now());
					jobInfoRepository.save(jobInfo);
					// Supprimer le fichier après le traitement si nécessaire
					Path sourcePath = file.toPath();
					Path targetPath = Paths.get(ArchivePath, fileName);

					// Déplacer le fichier après le traitement
					Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);				} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
																													 | JobParametersInvalidException e) {
					e.printStackTrace();
				}
			}
		} else {
			log.error("Le répertoire spécifié est invalide ou n'existe pas : " + directory);
		}
	}

	public void deleteFiles(String directoryPath,String key) {
		Path directory = Paths.get(directoryPath);

		if (Files.exists(directory) && Files.isDirectory(directory)) {
			try {
				Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if (file.getFileName().toString().contains(key)) {
							Files.delete(file);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException e) {
				// Handle exceptions here, e.g., log the error or rethrow it.
				e.printStackTrace();
			}
		}
	}
}
