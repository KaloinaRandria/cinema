package mg.working.cinema.repository.reservation;

import mg.working.cinema.model.reservation.ReservationExtra;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ReservationExtraRepo extends JpaRepository<ReservationExtra, String> {

    @Query("""
        SELECT COALESCE(SUM(e.quantite * e.prixUnitaire), 0)
        FROM ReservationExtra e
        WHERE MONTH(e.reservationMere.dateReservation) = :month
          AND YEAR(e.reservationMere.dateReservation) = :year
          AND LOWER(e.libelle) = 'popcorn'
    """)
    Double caPopcornMensuel(@Param("month") int month, @Param("year") int year);
}
