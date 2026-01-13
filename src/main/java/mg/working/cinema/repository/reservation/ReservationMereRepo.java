package mg.working.cinema.repository.reservation;

import mg.working.cinema.model.reservation.ReservationMere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationMereRepo extends JpaRepository<ReservationMere,String> {
}
