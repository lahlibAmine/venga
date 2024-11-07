package com.ram.venga.batch;


import com.ram.venga.domain.JobInfo;
import com.ram.venga.service.MailService;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CustomVenteReader implements ItemReader<String> {

	private List<String> handlers;
	@Autowired
	MailService mailService;
	private int currentIndex;

	private String fileName;


	public void setFileName(String fileName) {
		this.fileName = fileName;

	}
	
	

	public CustomVenteReader(String fileName, JobInfo jobInfo) throws XMLStreamException, MessagingException, UnsupportedEncodingException {

		this.fileName = fileName;
		this.handlers = extractHandlers();

		if (jobInfo != null)
			this.currentIndex = jobInfo.getLastProcessedLine().intValue();
		else
			this.currentIndex = 0;
	}

	@Override
	public String read() {
		if (handlers != null && currentIndex < handlers.size()) {
			return handlers.get(currentIndex++);
		} else {
			return null; // Indique la fin de la lecture
		}
	}

	private List<String> extractHandlers() throws MessagingException, UnsupportedEncodingException {
		List<String> handlerList = new ArrayList<>();
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();

		try {
			File file = new File(fileName);
			 FileInputStream fileInputStream = new FileInputStream(file);
			XMLStreamReader reader = inputFactory.createXMLStreamReader(fileInputStream);
			StringBuilder handlerContent = null;
			int depth = 0;

			while (reader.hasNext()) {
				int event = reader.next();

				if (event == XMLStreamConstants.START_ELEMENT && "ForPnrHandling".equals(reader.getLocalName())) {
					if (depth == 0) {
						handlerContent = new StringBuilder();
						handlerContent.append("<ForPnrHandling>");
					}
					depth++;
				} else if (event == XMLStreamConstants.END_ELEMENT && "ForPnrHandling".equals(reader.getLocalName())) {
					depth--;
					if (depth == 0) {
						handlerContent.append("</ForPnrHandling>");
						handlerList.add(handlerContent.toString());
						handlerContent = null;
					}
				} else if (event == XMLStreamConstants.START_ELEMENT && handlerContent != null) {
					depth++;
					handlerContent.append("<").append(reader.getLocalName()).append(">");
				} else if (event == XMLStreamConstants.END_ELEMENT && handlerContent != null) {
					depth--;
					handlerContent.append("</").append(reader.getLocalName()).append(">");
				} else if (event == XMLStreamConstants.CHARACTERS && handlerContent != null) {
					handlerContent.append(reader.getText());
				}
			}

			return handlerList;
		} catch (Exception e) {
			System.out.println("Error reading XML from classpath: " + fileName);
			e.printStackTrace();
			Path path = Paths.get(fileName);
			String fileName = path.getFileName().toString();
			mailService.sendEmailWarning(fileName);

		}
		return null;
	}

}
