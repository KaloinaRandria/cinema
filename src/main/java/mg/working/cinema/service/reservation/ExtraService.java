package mg.working.cinema.service.reservation;

import mg.working.cinema.repository.reservation.ReservationExtraRepo;
import org.springframework.stereotype.Service;

@Service
public class ExtraService {

    private final ReservationExtraRepo extraRepo;

    public ExtraService(ReservationExtraRepo extraRepo) {
        this.extraRepo = extraRepo;
    }

    public double caPopcornMensuel(int month, int year) {
        Double res = extraRepo.caPopcornMensuel(month, year);
        return (res == null) ? 0 : res;
    }
}
