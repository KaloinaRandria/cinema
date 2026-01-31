package mg.working.cinema.repository.pub;

import mg.working.cinema.model.pub.PaiementPub;
import mg.working.cinema.repository.facturation.SocieteTotalPaidProjection;
import mg.working.cinema.repository.report.SocieteMontantProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PaiementPubRepo extends JpaRepository<PaiementPub, String> {

    @Query("""
        SELECT COALESCE(SUM(p.montant), 0)
        FROM PaiementPub p
        WHERE p.societePub.id = :idSociete
          AND p.datePaiement >= :start
          AND p.datePaiement < :end
    """)
    double sumPaiementsBySocieteBetween(
            @Param("idSociete") String idSociete,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<PaiementPub> findBySocietePub_IdAndDatePaiementBetweenOrderByDatePaiementDesc(
            String idSociete,
            LocalDateTime start,
            LocalDateTime end
    );

    // ✅ AJOUTER ÇA (projection)
    @Query(value = """
        select
            p.id_societe_pub as idSociete,
            coalesce(sum(p.montant), 0) as totalPaid
        from paiement_pub p
        where p.date_paiement >= :start
          and p.date_paiement <  :end
        group by p.id_societe_pub
        """, nativeQuery = true)
    List<SocieteTotalPaidProjection> sumTotalPaidBySociete(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
    select
        p.id_societe_pub as idSociete,
        coalesce(sum(p.montant), 0) as montant
    from paiement_pub p
    where p.date_paiement >= :start
      and p.date_paiement <  :end
    group by p.id_societe_pub
    """, nativeQuery = true)
    List<mg.working.cinema.repository.report.SocieteMontantProjection> totalPaidBySocieteBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );



    /**
     * ✅ Total payé pub par société sur la période (GROUP BY)
     */
    @Query(value = """
        select
            p.id_societe_pub as idSociete,
            coalesce(sum(p.montant), 0) as montant
        from paiement_pub p
        where p.date_paiement >= :start
          and p.date_paiement <  :end
        group by p.id_societe_pub
    """, nativeQuery = true)
    List<SocieteMontantProjection> totalPaidPubBySocieteBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


}
