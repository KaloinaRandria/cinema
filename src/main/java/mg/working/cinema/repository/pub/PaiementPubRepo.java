package mg.working.cinema.repository.pub;

import mg.working.cinema.model.pub.PaiementPub;
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
}
