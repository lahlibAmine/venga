package com.ram.venga.repos;

import com.ram.venga.domain.Entite;
import com.ram.venga.model.enumeration.CategorieEntiteEnum;
import com.ram.venga.projection.AgenceSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EntiteRepository extends JpaRepository<Entite, Long> {

    boolean existsByCodeIgnoreCase(String code);

    @Query("select E from Entite E where upper( E.code) = :code")
    Entite findByCode(String code);
    @Query("SELECT e FROM Entite e " +
            "WHERE e.categorie = :categorie " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR (e.parent IS NOT NULL AND LOWER(:keyword) = (SELECT LOWER(ee.nom) FROM Entite ee WHERE ee.id = e.parent.id ))) " +
            "ORDER BY e.dateCreated desc")
    Page<Entite> findByCategoryOrKeyword(CategorieEntiteEnum categorie, String keyword, Pageable pageable);
    @Query(value = "select e from Entite e where e.categorie = 'AGENCE' and " +
            "(:nomAgence = '' or LOWER(e.nom) LIKE LOWER(CONCAT('%', :nomAgence, '%'))) and " +
            "(:codeIata = '' or lower(:codeIata) = lower(e.code)) and " +
            "(:originEmission = '' or lower(:originEmission) = lower(e.origineEmission.nom)) and " +
            "(:representation = '' or (e.parent is not null and lower(:representation) = (select lower(rep.nom) from Entite rep where rep.id = (select pf.parent.id from Entite pf where pf.id = e.parent.id) )) ) and " +
            "(:porteFeuille = '' or (e.parent is not null and lower(:porteFeuille) = (select lower(ee.nom) from Entite ee where ee.id = e.parent.id)) ) order by e.dateCreated desc ")
    Page<Entite> agenceFilter(String codeIata,
                              String nomAgence,
                              String originEmission,
                              String porteFeuille,
                              String representation,
                              Pageable pageable );

    @Query("select E.id from Entite E where E.parent.id = :idEntite")
    List<Long> findByParentId(Long idEntite);

    @Query("select e from Entite e where e.categorie = :categorie order by e.dateCreated")
    List<Entite> findAllByCategorie(CategorieEntiteEnum categorie);

    @Query("select e " +
            "from Entite e " +
            "left join Collaborateur c on c.entite.id = e.id " +
            "where e.categorie = 'PORTEFEUILLE' " +
            "and c is null "+
            "order by e.dateCreated")
    List<Entite> findUnassignedPortefeuille();

    @Query("select e from Entite e where e.parent.id = :idRepresentation")
    List<Entite> findByRepresentationPortefeuille(Long idRepresentation);

    @Query("select distinct e from Entite e where e.parent.nom = :portfeuille")
    List<Entite> findAgenceByPortfeuille(String portfeuille);

    @Query("select distinct e.parent from Entite e where e.parent.parent.nom = :representation")
    List<Entite> findPortfeuilleByRepresentation(String representation);
    @Query("select distinct e from Entite e where e.parent.parent.nom = :representation")
    List<Entite> findAgenceByRepresentation(String representation);

    @Query("select E from Entite E where upper( E.officeId) = :office")
    Entite findByOfficeId(String office);

    @Query("select E from Entite E where (upper( E.code) = :code or :code is null) and (upper( E.officeId) = :officeId or :officeId  = '')")
    Entite findByCodeOrOfficeId(String code,String officeId);

    @Query("select E.id from Entite E where E.parent.parent in :idEntites")
    List<Long> findByRepresentationAgence(Set<Entite> idEntites);

    @Query("SELECT E " +
            "FROM Entite E " +
            "WHERE E.parent.id = :idPortefeuille " +
            "AND E.categorie = 'AGENCE' " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(E.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(E.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY E.dateCreated desc")
    Page<Entite> findAgencesByPorteuille(Long idPortefeuille,String keyword, Pageable pageable);

    @Query("SELECT E " +
            "FROM Entite E " +
            "WHERE E.parent.parent.id in :ids " +
            "AND E.categorie = 'AGENCE' " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(E.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(E.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY E.dateCreated desc")
    Page<Entite> findAgencesByListOfRepresentationIds(List<Long> ids, String keyword, Pageable pageable);
    @Query("select distinct E.categorie from Entite E where E.parent.id = :idEntite")
    String findByParentIdcategorie(Long idEntite);


    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Collaborateur c " +
            "WHERE c.entite.categorie = 'PORTEFEUILLE' " +
            "AND c.entite.id = :belongTo")
    boolean isPortefeuilleAlreadyAffected(Long belongTo);

    @Query("select E.id from Entite E where E.parent.id in :idEntites")
    List<Long> findByPortfeuilleAgence(List<Long> idEntites);

    @Query("select e from Entite e where (Cast(:categorie as string) = 'PORTEFEUILLE' and e.categorie = :categorie and e.id in :port) or (cast(:categorie as string) = 'REPRESENTATION' and cast(e.categorie as string) = :categorie and e.id in :Rep)  order by e.dateCreated")
    List<Entite> findAllByCategorieByRepresentation(CategorieEntiteEnum categorie, List<Long> Rep, List<Long> port);
    @Query("select E.id from Entite E where E.parent.id in :idEntiteRepresentation")
    List<Long> findByRepresentationPortfeuille(List<Long> idEntiteRepresentation);
    @Query("SELECT E.code as codeIata, E.nom as nom " +
            ", E.ville.nom as ville , E.origineEmission.nom as origine" +
            ",E.parent.nom as portfeuille , E.parent.parent.nom as representation ," +
            " E.codePostal as  codePostal , E.adresse as adresse , E.email as email ," +
            "E.telephone as tele , E.fax as fax " +
            "FROM Entite E " +
            "WHERE E.parent.parent.id in :idRepresentation " +
            "AND E.categorie = 'AGENCE' " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(E.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(E.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY E.dateCreated desc")
    List<AgenceSearchProjection> findAgencesByListOfRepresentationIdsList(List<Long> idRepresentation, String keyword);
    @Query("SELECT E.code as codeIata, E.nom as nom " +
            ", E.ville.nom as ville , E.origineEmission.nom as origine" +
            ",E.parent.nom as portfeuille , E.parent.parent.nom as representation ," +
            " E.codePostal as  codePostal , E.adresse as adresse , E.email as email ," +
            "E.telephone as tele , E.fax as fax " +
            "FROM Entite E " +
            "WHERE E.parent.id = :idPortfeuille " +
            "AND E.categorie = 'AGENCE' " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(E.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(E.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY E.dateCreated desc")
    List<AgenceSearchProjection> findAgencesByListOfPortfeuilleList(Long idPortfeuille, String keyword);

    @Query("SELECT E.code as codeIata, E.nom as nom " +
            ", E.ville.nom as ville , E.origineEmission.nom as origine" +
            ",E.parent.nom as portfeuille , E.parent.parent.nom as representation ," +
            " E.codePostal as  codePostal , E.adresse as adresse , E.email as email ," +
            "E.telephone as tele , E.fax as fax " +
            "FROM Entite E " +
            "WHERE  E.categorie = 'AGENCE' and " +
            "(:nomAgence = '' or LOWER(E.nom) LIKE LOWER(CONCAT('%', :nomAgence, '%'))) and " +
                    "(:codeIata = '' or lower(:codeIata) = lower(E.code)) and " +
                    "(:originEmission = '' or lower(:originEmission) = lower(E.origineEmission.nom)) and " +
                    "(:representation = '' or (E.parent is not null and lower(:representation) = (select lower(rep.nom) from Entite rep where rep.id = (select pf.parent.id from Entite pf where pf.id = E.parent.id) )) ) and " +
                    "(:porteFeuille = '' or (E.parent is not null and lower(:porteFeuille) = (select lower(ee.nom) from Entite ee where ee.id = E.parent.id)) ) "+
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(E.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(E.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY E.dateCreated desc")
    List<AgenceSearchProjection> findAgencesByList( String keyword,String codeIata,
                                                    String nomAgence,
                                                    String originEmission,
                                                    String porteFeuille,
                                                    String representation);
}
