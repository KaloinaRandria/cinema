package mg.working.cinema.repository.reservation;

import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.reservation.ReservationMere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationMereRepo extends JpaRepository<ReservationMere,String> {
    @Query(
            "select sum(rm.montantTotal) as ca from ReservationMere rm where rm.seance = :seance"
    )
    double findMontantTotalBySeance(Seance seance);
}
