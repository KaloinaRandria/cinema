package mg.working.cinema.controller;

import mg.working.cinema.service.reservation.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seance")
public class SeanceApiController {

    private final ReservationService reservationService;

    public SeanceApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{idSeance}/occupied-seats")
    public List<String> getOccupiedSeats(@PathVariable String idSeance) {
        return reservationService.getOccupiedSeatIds(idSeance);
    }
}

