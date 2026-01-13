package mg.working.cinema.repository.reservation;

import mg.working.cinema.model.reservation.ReservationFille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationFilleRepo extends JpaRepository<ReservationFille,String> {
}
