package mg.working.cinema.repository.reservation;

import mg.working.cinema.model.reservation.ReservationFille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationFilleRepo extends JpaRepository<ReservationFille, String> {

    @Query("""
        select rf.siege.id
        from ReservationFille rf
        where rf.reservationMere.seance.id = :seanceId
    """)
    List<String> findOccupiedSeatIdsBySeance(@Param("seanceId") String seanceId);

    @Query("""
        select count(rf) > 0
        from ReservationFille rf
        where rf.reservationMere.seance.id = :seanceId
          and rf.siege.id = :siegeId
    """)
    boolean existsBySeanceAndSiege(@Param("seanceId") String seanceId,
                                   @Param("siegeId") String siegeId);
}
