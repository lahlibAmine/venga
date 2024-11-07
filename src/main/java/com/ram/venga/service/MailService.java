package com.ram.venga.service;


import com.ram.config.AppConfig;
import com.ram.venga.domain.Collaborateur;
import com.ram.venga.domain.Utilisateur;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.repos.CollaborateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@Slf4j
@ComponentScan(basePackages = "com.ram.config")
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${app.resources}")
    private String resourcePath;
    @Value("${frontend.server.url}")
    private String frontServerUrl;
    @Value("${spring.mail.username}")
    private String setFrom;

    @Value("${helper.personal}")
    private String personal;
    private final CollaborateurRepository collaborateurRepository;
    private final AppConfig templateEngineConfig;
    public MailService(JavaMailSender mailSender, CollaborateurRepository collaborateurRepository, AppConfig config){
        this.mailSender = mailSender;
        this.collaborateurRepository = collaborateurRepository;
        this.templateEngineConfig = config;
    }

    public void envoyerEmailActivationCompteRattache(String email,String link) throws  UnsupportedEncodingException, javax.mail.MessagingException {
        Context context = new Context();
        context.setVariable("username", email);

        context.setVariable("lienActivation", frontServerUrl+link);
        context.setVariable("image", resourcePath+"\\"+"royalAirMaroc.png");
        envoyerEmailHTML(List.of(email), "Validation de inscription","email-activation-compte-rattache-bootstrapemail", context);
    }
    public void envoyerEmailHTML(List<String> emails, String sujet, String templateHTML, Context context) throws UnsupportedEncodingException, MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");
        helper.setTo(emails.toArray(new String[0]));
        helper.setSubject(sujet);
        helper.setFrom(setFrom, personal);

        String html = templateEngineConfig.templateEngine().process(templateHTML, context);
        helper.setText(html, true);

        mailSender.send(mimeMessage);
    }

    public void sendEmailWithAttachment(List<String> emails, String sujet, Resource file,String attachmentFileName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        helper.setTo(emails.toArray(new String[0]));
        helper.setText(sujet);
        helper.setSubject(sujet);

        // Attach the file
        helper.addAttachment(attachmentFileName,file);
        mailSender.send(mimeMessage);
    }

    public void envoyerEmailActivationCompte(String email,String signature,String nom, String generatedPassword) throws  UnsupportedEncodingException, javax.mail.MessagingException {
        Context context = new Context();
        context.setVariable("username", nom);
        context.setVariable("email", signature);
        context.setVariable("password", generatedPassword);

        context.setVariable("lienActivation",  frontServerUrl+"/validation");

        envoyerEmailHTML(List.of(email), "Venga : Validation of your registration ","email-activation-compte-bootstrapemail", context);
    }

    public void SendOTPEmail(String subject,String actionText,String otpCode,String email,String nom) throws  UnsupportedEncodingException, javax.mail.MessagingException {
        Context context = new Context();
        context.setVariable("username", nom);
        context.setVariable("actionText", actionText);
        context.setVariable("otpCode", otpCode);
        envoyerEmailHTML(List.of(email), subject,"email-otp-code", context);
    }

    public void sendEmailCommandeToAdmin(String email, String nomAgent,String agence) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariable("agent", nomAgent);
        context.setVariable("agence", agence);
        envoyerEmailHTML(List.of(email), "une nouvelle commande :", "email-passer-commande-for-admin", context);
    }

    public void email(String email,String message,String subject) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,false);
        helper.setTo(email);
        helper.setText(message);
        helper.setSubject(subject);
        mailSender.send(mimeMessage);
    }

    public void sendEmailWarning(String file) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

        // Retrieve email addresses from the repository
        List<String> emailAddresses = collaborateurRepository.findAllByCategorie();

        // Set email recipients
        if (emailAddresses != null && !emailAddresses.isEmpty()) {
            Context context = new Context();
            context.setVariable("fileName", file);

            envoyerEmailHTML(emailAddresses, "VENGA : Chargement fichier vente non terminé . ","warning-file", context);

        } else {
            // Handle the case where no email addresses are found
            System.out.println("No email addresses found for the specified category.");
        }
    }
}
