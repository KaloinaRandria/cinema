package mg.working.cinema.service.reservation;

import java.util.List;

public interface ReservationService {
    List<String> getOccupiedSeatIds(String idSeance);
}

