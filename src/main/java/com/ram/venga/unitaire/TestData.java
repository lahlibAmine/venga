package com.ram.venga.unitaire;

import com.ram.venga.domain.*;
import com.ram.venga.model.enumeration.CategorieCollaborateurEnum;
import com.ram.venga.model.enumeration.CiviliteEnum;
import com.ram.venga.model.enumeration.StatutBilletEnum;
import com.ram.venga.model.enumeration.StatutRapprochementEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestData {

        public static List<Vente> createSampleVenteListSenario1() {
            List<Vente> ventes = new ArrayList<>();

            Vente vente1 = new Vente();
            vente1.setNumBillet("BILLET001");
            vente1.setStatutBillet(StatutBilletEnum.EMIS);
            vente1.setCodeIATA("IATA001");
            vente1.setSignatureAgent("AINCAS20");
            vente1.setNbrCoupon(3);
            vente1.setNbrCouponNonRapprocher(3);
            // Remplissez d'autres champs selon votre modèle de données
            vente1.setDateEmission(LocalDateTime.now());
            vente1.setVenteIntgre(true);
            vente1.setVenteRapproche(false);
            vente1.setCollaborateur(createSampleCollaborateurListSenario1().get(0));
            // ...

            // ...

            // Ajoutez d'autres ventes au besoin

            ventes.add(vente1);

            return ventes;
        }
    public static List<RecetteBrute> createSampleRecetteListSenario1() {
        List<RecetteBrute> recettes = new ArrayList<>();

        RecetteBrute recette1 = new RecetteBrute();
        recette1.setNumBillet("BILLET001");
        recette1.setOrigineEmission("ORIGINE001");
        recette1.setCodeIATA("IATA001");
        recette1.setSignatureAgent("AGENT001");
        recette1.setDateEmission(LocalDateTime.now());
        recette1.setDateTransport(LocalDateTime.now());
        recette1.setEscaleDepart("ABJ");
        recette1.setEscaleArrivee("CMN");
        recette1.setRecetteRapproche(false);
        recette1.setRecetteIntegre(true);
        // Remplissez d'autres champs selon votre modèle de données
        recette1.setCieTitre("CIE_TITRE001");
        recette1.setNumCoupon("1");
        recette1.setOrigineEmission("Egypte");
        recette1.setClasseProduit("P");
        // ...

        RecetteBrute recette2 = new RecetteBrute();
        recette2.setNumBillet("BILLET002");
        recette2.setOrigineEmission("ORIGINE002");
        recette2.setCodeIATA("IATA002");
        recette2.setSignatureAgent("AGENT002");
        recette2.setDateEmission(LocalDateTime.now());
        recette2.setDateTransport(LocalDateTime.now());
        recette2.setEscaleDepart("ABJ");
        recette2.setEscaleArrivee("CMN");
        recette2.setRecetteRapproche(false);
        recette2.setRecetteIntegre(true);
        // Remplissez d'autres champs selon votre modèle de données
        recette2.setCieTitre("CIE_TITRE002");
        recette2.setNumCoupon("COUPON002");
        recette2.setOrigineEmission("Egypte");
        recette2.setClasseProduit("P");
        // ...

        // Ajoutez d'autres recettes au besoin

        recettes.add(recette1);
        recettes.add(recette2);

        return recettes;
    }

    public static List<Collaborateur> createSampleCollaborateurListSenario1() {
        List<Collaborateur> collaborateurs = new ArrayList<>();

        Collaborateur collaborateur1 = new Collaborateur();
        collaborateur1.setId(1L);
        collaborateur1.setCode("CODE001");
        collaborateur1.setNom("Nom001");
        collaborateur1.setPrenom("Prenom001");
        collaborateur1.setCivilite(CiviliteEnum.M);
        collaborateur1.setSignature("AINCAS20");
        collaborateur1.setCategorie(CategorieCollaborateurEnum.COMMERCIAL);
        collaborateur1.setAdresse("Adresse001");
        collaborateur1.setCodePostal("CodePostal001");
        collaborateur1.setDateNaissance(LocalDate.now());
        collaborateur1.setMobile("Mobile001");
        collaborateur1.setTelephone("Telephone001");
        collaborateur1.setFonction("Fonction001");
        collaborateur1.setSoldePoint(0);
        collaborateur1.setEmail("email001@example.com");
        collaborateur1.setChiffreAffaire(10000);
        // Remplissez d'autres champs selon votre modèle de données

        collaborateurs.add(collaborateur1);

        return collaborateurs;
    }

    public static List<HauteSaison> createSampleHauteSaisonListSenario1() {
        List<HauteSaison> hauteSaisons = new ArrayList<>();

        HauteSaison saison1 = new HauteSaison();
        saison1.setLibelle("Haute Saison 2021");
        saison1.setDateDebut(LocalDate.of(2021, 6, 1));
        saison1.setDateFin(LocalDate.of(2021, 8, 31));
        // Remplissez d'autres champs selon votre modèle de données

        HauteSaison saison2 = new HauteSaison();
        saison2.setLibelle("Haute Saison 2022");
        saison2.setDateDebut(LocalDate.of(2022, 6, 1));
        saison2.setDateFin(LocalDate.of(2022, 8, 31));
        // Remplissez d'autres champs selon votre modèle de données

        // Ajoutez d'autres saisons hautes au besoin

        hauteSaisons.add(saison1);
        hauteSaisons.add(saison2);

        return hauteSaisons;
    }

    public static List<RecetteBrute> createSampleRecetteListSenario2() {
        List<RecetteBrute> recettes = new ArrayList<>();

        RecetteBrute recette1 = new RecetteBrute();
        recette1.setNumBillet("BILLET001");
        recette1.setOrigineEmission("ORIGINE001");
        recette1.setCodeIATA("IATA001");
        recette1.setSignatureAgent("AGENT001");
        recette1.setDateEmission(LocalDateTime.now());
        recette1.setDateTransport(LocalDateTime.now());
        recette1.setEscaleDepart("DEPART001");
        recette1.setEscaleArrivee("ARRIVEE001");
        recette1.setRecetteRapproche(false);
        recette1.setRecetteIntegre(true);
        // Remplissez d'autres champs selon votre modèle de données
        recette1.setCieTitre("CIE_TITRE001");
        recette1.setNumCoupon("1");
        // ...

        RecetteBrute recette2 = new RecetteBrute();
        recette2.setNumBillet("BILLET001");
        recette2.setOrigineEmission("ORIGINE002");
        recette2.setCodeIATA("IATA002");
        recette2.setSignatureAgent("AGENT002");
        recette2.setDateEmission(LocalDateTime.now());
        recette2.setDateTransport(LocalDateTime.now());
        recette2.setEscaleDepart("DEPART002");
        recette2.setEscaleArrivee("ARRIVEE002");
        recette2.setRecetteRapproche(false);
        recette2.setRecetteIntegre(true);
        // Remplissez d'autres champs selon votre modèle de données
        recette2.setCieTitre("CIE_TITRE002");
        recette2.setNumCoupon("COUPON002");
        // ...
        RecetteBrute recette3 = new RecetteBrute();
        recette3.setNumBillet("BILLET001");
        recette3.setOrigineEmission("ORIGINE002");
        recette3.setCodeIATA("IATA003");
        recette3.setSignatureAgent("AGENT003");
        recette3.setDateEmission(LocalDateTime.now());
        recette3.setDateTransport(LocalDateTime.now());
        recette3.setEscaleDepart("DEPART003");
        recette3.setEscaleArrivee("ARRIVEE003");
        recette3.setRecetteRapproche(false);
        recette3.setRecetteIntegre(true);
        // Remplissez d'autres champs selon votre modèle de données
        recette3.setCieTitre("CIE_TITRE002");
        recette3.setNumCoupon("COUPON002");
        // ...

        // Ajoutez d'autres recettes au besoin

        recettes.add(recette1);
        recettes.add(recette2);
        recettes.add(recette3);
        return recettes;
    }

    public static List<Collaborateur> createSampleCollaborateurListSenario2() {
        List<Collaborateur> collaborateurs = new ArrayList<>();

        Collaborateur collaborateur1 = new Collaborateur();
        collaborateur1.setCode("CODE001");
        collaborateur1.setNom("Nom001");
        collaborateur1.setPrenom("Prenom001");
        collaborateur1.setCivilite(CiviliteEnum.M);
        collaborateur1.setSignature("SIGNATURE001");
        collaborateur1.setCategorie(CategorieCollaborateurEnum.COMMERCIAL);
        collaborateur1.setAdresse("Adresse001");
        collaborateur1.setCodePostal("CodePostal001");
        collaborateur1.setDateNaissance(LocalDate.now());
        collaborateur1.setMobile("Mobile001");
        collaborateur1.setTelephone("Telephone001");
        collaborateur1.setFonction("Fonction001");
        collaborateur1.setSoldePoint(100);
        collaborateur1.setEmail("email001@example.com");
        collaborateur1.setChiffreAffaire(10000);
        // Remplissez d'autres champs selon votre modèle de données

        Collaborateur collaborateur2 = new Collaborateur();
        collaborateur2.setCode("CODE002");
        collaborateur2.setNom("Nom002");
        collaborateur2.setPrenom("Prenom002");
        collaborateur2.setCivilite(CiviliteEnum.MME);
        collaborateur2.setSignature("SIGNATURE002");
        collaborateur2.setCategorie(CategorieCollaborateurEnum.COMMERCIAL);
        collaborateur2.setAdresse("Adresse002");
        collaborateur2.setCodePostal("CodePostal002");
        collaborateur2.setDateNaissance(LocalDate.now());
        collaborateur2.setMobile("Mobile002");
        collaborateur2.setTelephone("Telephone002");
        collaborateur2.setFonction("Fonction002");
        collaborateur2.setSoldePoint(200);
        collaborateur2.setEmail("email002@example.com");
        collaborateur2.setChiffreAffaire(20000);
        // Remplissez d'autres champs selon votre modèle de données

        // Ajoutez d'autres collaborateurs au besoin

        collaborateurs.add(collaborateur1);
        collaborateurs.add(collaborateur2);

        return collaborateurs;
    }

    public static List<HauteSaison> createSampleHauteSaisonListSenario2() {
        List<HauteSaison> hauteSaisons = new ArrayList<>();

        HauteSaison saison1 = new HauteSaison();
        saison1.setLibelle("Haute Saison 2021");
        saison1.setDateDebut(LocalDate.of(2021, 6, 1));
        saison1.setDateFin(LocalDate.of(2021, 8, 31));
        // Remplissez d'autres champs selon votre modèle de données

        HauteSaison saison2 = new HauteSaison();
        saison2.setLibelle("Haute Saison 2022");
        saison2.setDateDebut(LocalDate.of(2022, 6, 1));
        saison2.setDateFin(LocalDate.of(2022, 8, 31));
        // Remplissez d'autres champs selon votre modèle de données

        // Ajoutez d'autres saisons hautes au besoin

        hauteSaisons.add(saison1);
        hauteSaisons.add(saison2);

        return hauteSaisons;
    }

    public static Prime createSamplePrimeListSenario1() {
        Prime prime = new Prime();
        prime.setNbrPoint(100); // Exemple de nombre de points
        prime.setOrigineEmission(createSampleOrigineEmission()); // Remplacez par une instance réelle si nécessaire
        prime.setSegment(createSampleSegment()); // Remplacez par une instance réelle si nécessaire
        prime.setClasseProduit(createSampleClasseProduit()); // Remplacez par une instance réelle si nécessaire
        prime.setDateCreated(OffsetDateTime.now()); // Exemple de date de création actuelle
        return prime;
    }

    public static OrigineEmission createSampleOrigineEmission() {
        OrigineEmission origineEmission = new OrigineEmission();
        origineEmission.setNom("ExempleOrigine"); // Exemple de nom
     //   origineEmission.setNbrPointBienvenue(50); // Exemple de nombre de points de bienvenue
        origineEmission.setCadeauxBAs(new HashSet<>()); // Initialisez avec une liste vide ou ajoutez des cadeaux si nécessaire
        origineEmission.setPays(new Pays()); // Remplacez par une instance réelle si nécessaire
        origineEmission.setDateCreated(LocalDate.now()); // Exemple de date de création actuelle
        origineEmission.setLastUpdated(LocalDate.now()); // Exemple de date de mise à jour actuelle
        origineEmission.setDevise(new Devise()); // Remplacez par une instance réelle si nécessaire
        return origineEmission;
    }

    public static Segment createSampleSegment() {
        Segment segment = new Segment();
        segment.setId(1L);
        segment.setCode("ABC123"); // Exemple de code
        segment.setEscaleDepart("VilleA"); // Exemple d'escale de départ
        segment.setEscaleDestination("VilleB"); // Exemple d'escale de destination
        segment.setDateCreated(OffsetDateTime.now()); // Exemple de date de création actuelle
        segment.setLastUpdated(OffsetDateTime.now()); // Exemple de date de mise à jour actuelle
        return segment;
    }

    public static ClasseProduit createSampleClasseProduit() {
        ClasseProduit classeProduit = new ClasseProduit();
        classeProduit.setCode("ABC123"); // Exemple de code
        classeProduit.setLibelle("Article de test"); // Exemple de libellé
        classeProduit.setDateCreated(OffsetDateTime.now()); // Exemple de date de création actuelle
        classeProduit.setLastUpdated(OffsetDateTime.now()); // Exemple de date de mise à jour actuelle
        return classeProduit;
    }


    public static Concours createSampleConcour() {
        Concours concours = new Concours();
        concours.setLibelle("concours Standard"); // Exemple de libellé
        concours.setDateDebut(LocalDate.of(2023, 1, 1)); // Exemple de date de début
        concours.setDateFin(LocalDate.of(2023, 12, 31)); // Exemple de date de fin
        concours.setOrigineEmission(new OrigineEmission()); // Remplacez par une instance réelle si nécessaire
        concours.setDateCreated(OffsetDateTime.now()); // Exemple de date de création actuelle
        concours.setLastUpdated(OffsetDateTime.now()); // Exemple de date de mise à jour actuelle
        concours.setFacteurPromotion(1f); // Exemple de facteur de promotion
        concours.setPsMajoration(2f); // Exemple de majoration PS
            return concours;

    }

    public static List<RecetteBrute> createSampleRecetteListSenario3() {

        List<RecetteBrute> recettes = new ArrayList<>();

        RecetteBrute recette1 = new RecetteBrute();
        recette1.setNumBillet("BILLET004");
        recette1.setOrigineEmission("ORIGINE001");
        recette1.setCodeIATA("IATA001");
        recette1.setSignatureAgent("AGENT001");
        recette1.setDateEmission(LocalDateTime.now());
        recette1.setDateTransport(LocalDateTime.now());
        recette1.setEscaleDepart("DEPART001");
        recette1.setEscaleArrivee("ARRIVEE001");
        recette1.setRecetteRapproche(false);
        recette1.setRecetteIntegre(true);
        // Remplissez d'autres champs selon votre modèle de données
        recette1.setCieTitre("CIE_TITRE001");
        recette1.setNumCoupon("1");
        recettes.add(recette1);
        return recettes;
    }
}


