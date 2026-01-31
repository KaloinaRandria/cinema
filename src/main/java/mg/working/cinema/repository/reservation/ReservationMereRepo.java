package mg.working.cinema.repository.reservation;

import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.reservation.ReservationMere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationMereRepo extends JpaRepository<ReservationMere,String> {
        @Query("select coalesce(sum(r.montantTotal), 0) " +
                "from ReservationMere r " +
                "where r.seance = :seance")
        double findMontantTotalBySeance(@Param("seance") Seance seance);
}
