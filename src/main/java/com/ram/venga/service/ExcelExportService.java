package com.ram.venga.service;

import com.ram.venga.domain.Vente;
import com.ram.venga.model.VenteDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class ExcelExportService {

    public void exportToExcel(VenteDTO data, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // Créer la première ligne pour les en-têtes
        Row headerRow = sheet.createRow(0);
        String[] headers = {"N DE BILLET", "MONTANT BRUT", "OFFICE ID","PNR","POINT ATTRIBUÉS","NOM AGENT", "SIGNATEURE AGENT","NOM AGENCE","CODE IATA","MOBILE AGENT","DATE EMISSION","EMAIL AGENT","TELEPHONE AGENCE","ADRESSE AGENCE","NUMÉRO VOL","NUMÉRO COUPON","DATE DE TRANSPORT","ESCALE DÉPART","ESCALE ARRIVÉE"}; // Remplacez par vos en-têtes
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Remplir les données
        int rowNum = 1;
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getNumBillet());

            // Check for null before accessing properties
            row.createCell(1).setCellValue(data.getSegmentDTOList()!= null ?data.getSegmentDTOList().get(0).getMontantBrut() : 0);
            row.createCell(2).setCellValue(data.getOfficeId() != null ? data.getOfficeId() : "");
            row.createCell(3).setCellValue(data.getPnr() != null ? data.getPnr() : "");
            row.createCell(4).setCellValue(data.getNbrPoint() != null ? data.getNbrPoint().intValue() : 0);

            // Check for null before accessing Collaborateur and its properties
            if (data.getCollaborateur() != null) {
                row.createCell(5).setCellValue(data.getNomAgent() != null ? data.getNomAgent() : "");
                row.createCell(6).setCellValue(data.getSignatureAgent() != null ? data.getSignatureAgent() : "");
                row.createCell(7).setCellValue(data.getNomAgence() != null ? data.getNomAgence() : "");
                row.createCell(9).setCellValue(data.getMobileAgent() != null ? data.getMobileAgent() : "");
                row.createCell(11).setCellValue(data.getEmailAgent() != null ? data.getEmailAgent() : "");
                row.createCell(12).setCellValue(data.getEmailAgence()!= null ? data.getEmailAgence() : "");
                row.createCell(13).setCellValue(data.getAdresseAgence() != null ? data.getAdresseAgence() : "");
            }

            // Check for null before accessing other properties
            row.createCell(8).setCellValue(data.getCodeIATA() != null ? data.getCodeIATA() : "");
            LocalDateTime dateEmission = data.getDateEmission().toLocalDate().atStartOfDay();
            Date dateEmissionDate = (dateEmission != null) ? Date.from(dateEmission.atZone(ZoneId.systemDefault()).toInstant()) : null;
            row.createCell(10).setCellValue(dateEmissionDate);
            row.createCell(14).setCellValue(data.getCieVol() != null ? data.getCieVol() : "");
            row.createCell(15).setCellValue(data.getNbrCoupon() != null ? data.getNbrCoupon() : 0);
            LocalDateTime dateTransport = data.getDateEmission().toLocalDate().atStartOfDay();
            Date dateTransportDate = (dateTransport != null) ? Date.from(dateEmission.atZone(ZoneId.systemDefault()).toInstant()) : null;

            row.createCell(10).setCellValue(dateTransportDate);
          //  row.createCell(17).setCellValue(data.getEscaleDepart() != null ? data.getEscaleDepart() : "");
          //  row.createCell(18).setCellValue(data.getEscaleArrivee() != null ? data.getEscaleArrivee() : "");

            // Add more cells based on your columns


        // Paramétrer la réponse HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

        // Écrire le contenu dans la réponse
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
