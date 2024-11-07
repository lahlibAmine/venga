package com.ram.venga.service;

import com.ram.venga.domain.ExportEmissionView;
import com.ram.venga.model.VenteDTO;
import com.ram.venga.model.enumeration.StatutVenteEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class QueryEmission {

    @Autowired
    private  EntityManager entityManager;

    public  Page<VenteDTO> getEmissionVenteWithDynamicMotifs(LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin,
                                                                   String agence, List<String> portfeuille,
                                                                   List<String> representation,List<Long> idEntite, List<StatutVenteEnum> statutVenteEnum,
                                                                   String numBillet, String classeR, String origine,
                                                                   String destination, Double montantDebut, Double montantFin,
                                                                   String signature, Boolean isArchived, List<String> motifs,
                                                                   Pageable pageable) {

        // Start building the base query
        StringBuilder queryBuilder = new StringBuilder("select distinct new com.ram.venga.model.VenteDTO( " +
                "v.numBillet, v.pnr, v.nbrCoupon, v.dateEmission, " +
                "v.signatureAgent, v.codeIATA, e.nom, " +
                "SUM(CASE WHEN o.debit IS NULL THEN 0 ELSE o.debit END), " +
                "v.venteIntgre, v.venteRapproche, v.collaborateur.nom, " +
                "v.collaborateur.email, v.collaborateur.mobile, v.statutVente, " +
                "e.nom, epp2.nom, ep2.nom, v.dateCreated) " +
                "from Vente v " +
                "left join RecetteBrute rb on rb.numBillet = v.numBillet " +
                "left join Opperation o on o.recetteBrute = rb " +
                "left join Entite e on e.code = v.codeIATA " +
                "left join Entite ep2 on ep2 = e.parent " +
                "left join Entite epp2 on epp2 = e.parent.parent " +
                "left join Entite e1 on e1 = v.collaborateur.entite " +
                "left join Entite ep1 on ep1 = v.collaborateur.entite.parent " +
                "left join Entite epp1 on epp1 = v.collaborateur.entite.parent.parent " +
                "where (rb is null or rb.classeReservation <> ' ') " +
                "and v.collaborateur.categorie = 'COMMERCIAL' " +
                "and v.isArchived = :isArchived and ( v.collaborateur.entite.id in (:idEntite) or (:idEntite) is null )" +
                "and (v.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
                "and (v.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date) is null) " +
                "and (:classeR is null or :classeR = '' or lower(rb.classeReservation) like lower(concat('%', :classeR, '%'))) " +
                "and (:origine is null or :origine = '' or lower(rb.escaleDepart) like lower(concat('%', :origine, '%'))) " +
                "and (:montantDebut = 0.0 or rb.montantBrut >= :montantDebut) " +
                "and (:montantFin = 0.0 or rb.montantBrut <= :montantFin) " +
                "and (:destination is null or :destination = '' or lower(rb.escaleArrivee) like lower(concat('%', :destination, '%'))) " +
                "and (:numBillet is null or :numBillet = '' or v.numBillet like concat('%', :numBillet, '%')) " +
                "and (:agence is null or :agence = '' or lower(e1.nom) like lower(concat('%', :agence, '%'))) " +
                "and ((:portfeuille) is null or lower(ep1.nom) in (:portfeuille)) " +
                "and (:signature is null or :signature = '' or lower(v.collaborateur.signature) = lower(:signature)) " +
                "and ((:representation) is null or lower(epp1.nom) in (:representation)) ");

        // Combine motif and statutVenteEnum conditions using OR logic
        queryBuilder.append("and (");

        // Add statutVenteEnum condition
        queryBuilder.append("((:statutVenteEnum) is not null and v.statutVente in (:statutVenteEnum)) ");

        // Dynamically add LIKE conditions for motifs
        if (motifs != null && !motifs.isEmpty()) {
            queryBuilder.append("or (");
            for (int i = 0; i < motifs.size(); i++) {
                String motif = motifs.get(i);
                if (i > 0) {
                    queryBuilder.append(" or ");
                }
                queryBuilder.append("(lower(rb.motif) like lower(concat('%', :motif").append(i).append(", '%')) or " +
                        "lower(v.motif) like lower(concat('%', :motif").append(i).append(", '%')))");
            }
            queryBuilder.append(") ");
        }

        // Close the OR block
        queryBuilder.append(") ");

        // Add the group by and order by
        queryBuilder.append("group by v.numBillet, v.pnr, v.nbrCoupon, v.dateEmission, " +
                "v.signatureAgent, v.codeIATA, v.venteIntgre, " +
                "v.venteRapproche, v.collaborateur.nom, " +
                "v.collaborateur.email, v.collaborateur.mobile, v.statutVente, e.nom, epp2.nom, ep2.nom, v.dateCreated " +
                "order by v.dateCreated desc");

        // Create a TypedQuery
        TypedQuery<VenteDTO> query = entityManager.createQuery(queryBuilder.toString(), VenteDTO.class);

        // Set parameters
        query.setParameter("dateTimeDebut", dateTimeDebut);
        query.setParameter("dateTimeFin", dateTimeFin);
        query.setParameter("isArchived", isArchived);
        query.setParameter("agence", agence);
        query.setParameter("portfeuille", portfeuille);
        query.setParameter("representation", representation);
        if (statutVenteEnum != null && !statutVenteEnum.isEmpty()) {
            query.setParameter("statutVenteEnum", statutVenteEnum);
        } else {
            query.setParameter("statutVenteEnum", null);
        }
        query.setParameter("numBillet", numBillet);
        query.setParameter("classeR", classeR);
        query.setParameter("origine", origine);
        query.setParameter("destination", destination);
        query.setParameter("montantDebut", 0.0);
        query.setParameter("montantFin", 0.0);
        query.setParameter("signature", signature);
        query.setParameter("idEntite", idEntite);
        // Set other parameters as necessary...
        for (int i = 0; i < motifs.size(); i++) {
            query.setParameter("motif" + i, motifs.get(i));
        }

        // Set motif parameters
        if (motifs != null) {
            for (int i = 0; i < motifs.size(); i++) {
                query.setParameter("motif" + i, motifs.get(i));
            }
        }
        Long count =countTotalResults(dateTimeDebut,dateTimeFin,agence,portfeuille,representation
                ,statutVenteEnum,numBillet,classeR,origine,destination,montantDebut,montantFin,signature,isArchived
                ,motifs,idEntite);
        // Set the pageable
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Execute and return
        return new PageImpl<>(query.getResultList(), pageable, count); // You will need to implement countTotalResults() as well.
    }
    private long countTotalResults(LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin,
                                   String agence, List<String> portfeuille,
                                   List<String> representation, List<StatutVenteEnum> statutVenteEnum,
                                   String numBillet, String classeR, String origine,
                                   String destination, Double montantDebut, Double montantFin,
                                   String signature, Boolean isArchived, List<String> motifs,List<Long> idEntite ) {

        // Build the count query
        StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(DISTINCT v.numBillet) FROM Vente v " +
                "left join RecetteBrute rb on rb.numBillet = v.numBillet " +
                "left join Opperation o on o.recetteBrute = rb " +
                "left join Entite e on e.code = v.codeIATA " +
                "left join Entite ep2 on ep2 = e.parent " +
                "left join Entite epp2 on epp2 = e.parent.parent " +
                "left join Entite e1 on e1 = v.collaborateur.entite " +
                "left join Entite ep1 on ep1 = v.collaborateur.entite.parent " +
                "left join Entite epp1 on epp1 = v.collaborateur.entite.parent.parent " +
                "where (rb is null or rb.classeReservation <> ' ') " +
                "and v.collaborateur.categorie = 'COMMERCIAL' " +
                "and v.isArchived = :isArchived and ( v.collaborateur.entite.id in (:idEntite) or (:idEntite) is null )" +
                "and (v.dateEmission >= :dateTimeDebut or CAST(:dateTimeDebut AS date) is null) " +
                "and (v.dateEmission <= :dateTimeFin or CAST(:dateTimeFin AS date) is null) " +
                "and (:classeR is null or :classeR = '' or lower(rb.classeReservation) like lower(concat('%', :classeR, '%'))) " +
                "and (:origine is null or :origine = '' or lower(rb.escaleDepart) like lower(concat('%', :origine, '%'))) " +
                "and (:montantDebut = 0.0 or rb.montantBrut >= :montantDebut) " +
                "and (:montantFin = 0.0 or rb.montantBrut <= :montantFin) " +
                "and (:destination is null or :destination = '' or lower(rb.escaleArrivee) like lower(concat('%', :destination, '%'))) " +
                "and (:numBillet is null or :numBillet = '' or v.numBillet like concat('%', :numBillet, '%')) " +
                "and (:agence is null or :agence = '' or lower(e1.nom) like lower(concat('%', :agence, '%'))) " +
                "and ((:portfeuille) is null or lower(ep1.nom) in (:portfeuille)) " +
                "and (:signature is null or :signature = '' or lower(v.collaborateur.signature) = lower(:signature)) " +
                "and ((:representation) is null or lower(epp1.nom) in (:representation)) ");

                // Combine motif and statutVenteEnum conditions using OR logic
        countQueryBuilder.append("and (");

        // Add statutVenteEnum condition
        countQueryBuilder.append("((:statutVenteEnum) is not null and v.statutVente in (:statutVenteEnum)) ");

        // Dynamically add LIKE conditions for motifs
        if (motifs != null && !motifs.isEmpty()) {
            countQueryBuilder.append("or (");
            for (int i = 0; i < motifs.size(); i++) {
                String motif = motifs.get(i);
                if (i > 0) {
                    countQueryBuilder.append(" or ");
                }
                countQueryBuilder.append("(lower(rb.motif) like lower(concat('%', :motif").append(i).append(", '%')) or " +
                        "lower(v.motif) like lower(concat('%', :motif").append(i).append(", '%')))");
            }
            countQueryBuilder.append(") ");
        }

        // Close the OR block
        countQueryBuilder.append(") ");
        // Create and set parameters for the TypedQuery
        TypedQuery<Long> countQuery = entityManager.createQuery(countQueryBuilder.toString(), Long.class);
        countQuery.setParameter("dateTimeDebut", dateTimeDebut);
        countQuery.setParameter("dateTimeFin", dateTimeFin);
        countQuery.setParameter("isArchived", isArchived);
        countQuery.setParameter("agence", agence);
        countQuery.setParameter("portfeuille", portfeuille);
        countQuery.setParameter("representation", representation);
        countQuery.setParameter("statutVenteEnum", statutVenteEnum);
        countQuery.setParameter("numBillet", numBillet);
        countQuery.setParameter("classeR", classeR);
        countQuery.setParameter("origine", origine);
        countQuery.setParameter("destination", destination);
        countQuery.setParameter("montantDebut", 0.0);
        countQuery.setParameter("montantFin", 0.0);
        countQuery.setParameter("signature", signature);
        countQuery.setParameter("idEntite", idEntite);

        // Set other parameters as needed, including motifs...

        if (motifs != null) {
            for (int i = 0; i < motifs.size(); i++) {
                countQuery.setParameter("motif" + i, motifs.get(i));
            }
        }

        // Return the total count
        return countQuery.getSingleResult();
    }


    public  List<ExportEmissionView> getEmissionVenteWithDynamicMotifsList(LocalDateTime dateTimeDebut, LocalDateTime dateTimeFin,
                                                                           String agence, List<String> portfeuille,
                                                                           List<String> representation, List<Long> idEntite, List<StatutVenteEnum> statutVenteEnum,
                                                                           String numBillet, String classeR, String origine,
                                                                           String destination, Double montantDebut, Double montantFin,
                                                                           String signature, List<String> motifs) {

        // Start building the base query
        StringBuilder queryBuilder = new StringBuilder("SELECT E FROM ExportEmissionView E " +
                "LEFT JOIN E.entite ee " +
                "LEFT JOIN ee.parent ep " +
                "LEFT JOIN ep.parent epp " +
                "WHERE (:idEntite IS NULL OR E.collaborateur.entite.id IN :idEntite) " +
                "AND E.categorie = 'COMMERCIAL' " +
                "AND (E.dateEmission >= :dateTimeDebut OR CAST(:dateTimeDebut AS date) IS NULL) " +
                "AND (E.dateEmission <= :dateTimeFin OR CAST(:dateTimeFin AS date) IS NULL) " +
                "AND ((:numBillet IS NULL OR :numBillet = '') OR E.numBillet LIKE CONCAT('%', :numBillet, '%')) " +
                "AND (:classeR IS NULL OR :classeR = '' OR LOWER(E.classeReservation) LIKE LOWER(CONCAT('%', :classeR, '%'))) " +
                "AND (:origine IS NULL OR :origine = '' OR LOWER(E.escaleDepart) LIKE LOWER(CONCAT('%', :origine, '%'))) " +
                "AND (E.montantBrut >= :montantDebut OR :montantDebut = 0.0) " +
                "AND (E.montantBrut <= :montantFin OR :montantFin = 0.0) " +
                "AND (:destination IS NULL OR :destination = '' OR LOWER(E.escaleArrivee) LIKE LOWER(CONCAT('%', :destination, '%'))) " +
                "AND (LOWER(E.entite.nom) LIKE LOWER(CONCAT('%', :agence, '%')) OR :agence LIKE '') " +
                "AND (:portfeuille IS NULL OR LOWER(ep.nom) IN (:portfeuille)) " +
                "AND (LOWER(E.signature) = LOWER(:signature) OR :signature = '') " +
                "AND (:representation IS NULL OR LOWER(epp.nom) IN (:representation)) ");


        queryBuilder.append("and (");

        // Add statutVenteEnum condition
        queryBuilder.append("((:statutVenteEnum) is not null and E.statutVente in (:statutVenteEnum)) ");

        // Dynamically add LIKE conditions for motifs
        if (motifs != null && !motifs.isEmpty()) {
            queryBuilder.append("or (");
            for (int i = 0; i < motifs.size(); i++) {
                String motif = motifs.get(i);
                if (i > 0) {
                    queryBuilder.append(" or ");
                }
                queryBuilder.append("(lower(E.motif_recette) like lower(concat('%', :motif").append(i).append(", '%')) or " +
                        "lower(E.motif_vente) like lower(concat('%', :motif").append(i).append(", '%')))");
            }
            queryBuilder.append(") ");
        }

        // Close the OR block
        queryBuilder.append(") ");
        queryBuilder.append("ORDER BY E.numBillet DESC"); // Move ORDER BY here




        // Create a TypedQuery
        TypedQuery<ExportEmissionView> query = entityManager.createQuery(queryBuilder.toString(), ExportEmissionView.class);

        // Set parameters
        query.setParameter("dateTimeDebut", dateTimeDebut);
        query.setParameter("dateTimeFin", dateTimeFin);
        query.setParameter("agence", agence);
        query.setParameter("portfeuille", portfeuille);
        query.setParameter("representation", representation);
        if (statutVenteEnum != null && !statutVenteEnum.isEmpty()) {
            query.setParameter("statutVenteEnum", statutVenteEnum);
        } else {
            query.setParameter("statutVenteEnum", null);
        }
        query.setParameter("numBillet", numBillet);
        query.setParameter("classeR", classeR);
        query.setParameter("origine", origine);
        query.setParameter("destination", destination);
        query.setParameter("montantDebut", montantDebut);
        query.setParameter("montantFin", montantFin);
        query.setParameter("signature", signature);
        query.setParameter("idEntite", idEntite);
        // Set other parameters as necessary...
        for (int i = 0; i < motifs.size(); i++) {
            query.setParameter("motif" + i, motifs.get(i));
        }

        // Set motif parameters
        if (motifs != null) {
            for (int i = 0; i < motifs.size(); i++) {
                query.setParameter("motif" + i, motifs.get(i));
            }
        }
        // Set the pageable
        return query.getResultList(); // You will need to implement countTotalResults() as well.
    }

}
