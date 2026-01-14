package mg.working.cinema.repository.reservation;

import mg.working.cinema.model.reservation.ReservationFille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationFilleRepo extends JpaRepository<ReservationFille,String> {
    @Query("""
        SELECT rf.siege.id
        FROM ReservationFille rf
        JOIN rf.reservationMere rm
        WHERE rm.seance.id = :idSeance
    """)
    List<String> findOccupiedSeatIdsBySeance(@Param("idSeance") String idSeance);
}
