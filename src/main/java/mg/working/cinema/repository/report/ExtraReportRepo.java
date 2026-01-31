package mg.working.cinema.repository.report;

import mg.working.cinema.model.reservation.ReservationExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExtraReportRepo extends JpaRepository<ReservationExtra, String> {

    @Query("""
        SELECT r.seance.id as idSeance,
               COALESCE(SUM(e.quantite * e.prixUnitaire), 0) as montant
        FROM ReservationExtra e
        JOIN e.reservationMere r
        WHERE r.dateReservation >= :start
          AND r.dateReservation < :end
        GROUP BY r.seance.id
    """)
    List<SeanceExtraProjection> totalExtraBySeanceBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
