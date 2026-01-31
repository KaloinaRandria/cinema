package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.dto.SeatDto;
import mg.working.cinema.model.Siege;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.user.Utilisateur;
import mg.working.cinema.service.film.SeanceService;
import mg.working.cinema.service.reservation.ReservationAvailabilityService;
import mg.working.cinema.service.reservation.ReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final SeanceService seanceService;
    private final ReservationAvailabilityService availabilityService;
    private final ReservationService reservationService;

    public ReservationController(SeanceService seanceService,
                                 ReservationAvailabilityService availabilityService,
                                 ReservationService reservationService) {
        this.seanceService = seanceService;
        this.availabilityService = availabilityService;
        this.reservationService = reservationService;
    }

    @GetMapping("/new/{idSeance}")
    public String form(@PathVariable String idSeance, Model model, HttpServletRequest request) {
        Seance seance = seanceService.getById(idSeance)
                .orElseThrow(() -> new IllegalArgumentException("Séance introuvable : " + idSeance));
        List<Siege> availableSeats = availabilityService.getAvailableSeats(idSeance);

        List<SeatDto> seatDtos = availableSeats.stream()
                .map(s -> new SeatDto(
                        s.getId(),
                        s.getRangee(),
                        s.getNumero(),
                        s.getTypeSiege() != null ? s.getTypeSiege().getLibelle() : null
                ))
                .toList();

        model.addAttribute("availableSeats", seatDtos);
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("seance", seance);
        model.addAttribute("salle", seance.getSalle());

        return "reservation/reservation-saisie";
    }

    @PostMapping("/create")
    public String create(@RequestParam("idSeance") String idSeance,
                         @RequestParam(name = "seatIds[]", required = false) List<String> seatIds,
                         HttpServletRequest request,
                         RedirectAttributes ra) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (!(principal instanceof Utilisateur user)) {
            ra.addFlashAttribute("ko", "Utilisateur non authentifié.");
            return "redirect:/reservation/new/" + idSeance;
        }

        // ✅ Extraire seatCategory[SEAT_ID] -> ADULT/CHILD
        Map<String, String> seatCategory = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (key != null && key.startsWith("seatCategory[") && key.endsWith("]")) {
                String seatId = key.substring("seatCategory[".length(), key.length() - 1);
                String val = (values != null && values.length > 0) ? values[0] : null;
                if (val != null) seatCategory.put(seatId, val);
            }
        });

        try {
            String reservationId = reservationService.createReservation(idSeance, user, seatIds, seatCategory);
            ra.addFlashAttribute("ok", "Réservation créée : " + reservationId);
            return "redirect:/seance/" + idSeance;

        } catch (Exception e) {
            ra.addFlashAttribute("ko", e.getMessage());
            return "redirect:/reservation/new/" + idSeance;
        }
    }




}

