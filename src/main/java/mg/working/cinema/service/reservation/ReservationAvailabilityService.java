package mg.working.cinema.service.reservation;

import mg.working.cinema.model.Siege;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.repository.SiegeRepo;
import mg.working.cinema.repository.reservation.ReservationFilleRepo;
import mg.working.cinema.service.film.SeanceService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationAvailabilityService {

    private final SeanceService seanceService;
    private final SiegeRepo siegeRepository;
    private final ReservationFilleRepo reservationFilleRepository;

    public ReservationAvailabilityService(SeanceService seanceService,
                                          SiegeRepo siegeRepository,
                                          ReservationFilleRepo reservationFilleRepository) {
        this.seanceService = seanceService;
        this.siegeRepository = siegeRepository;
        this.reservationFilleRepository = reservationFilleRepository;
    }

    public List<Siege> getAvailableSeats(String seanceId) {
        Seance seance = seanceService.getById(seanceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid seance ID: " + seanceId));
        String salleId = seance.getSalle().getId();

        List<Siege> all = siegeRepository.findBySalleOrdered(salleId);
        Set<String> occupied = new HashSet<>(reservationFilleRepository.findOccupiedSeatIdsBySeance(seanceId));

        System.out.println("seanceId=" + seanceId);
        System.out.println("salleId=" + salleId);
        System.out.println("all seats=" + all.size());
        System.out.println("occupied seats=" + occupied.size());

        return all.stream()
                .filter(s -> !occupied.contains(s.getId()))
                .collect(Collectors.toList());
    }

}
