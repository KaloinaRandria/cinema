package mg.working.cinema.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.user.Utilisateur;
import mg.working.cinema.service.SiegeService;
import mg.working.cinema.service.film.SeanceService;
import mg.working.cinema.service.reservation.ReservationServiceImpl;
import mg.working.cinema.service.user.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final SeanceService seanceService;
    private final SiegeService siegeService;
    private final ReservationServiceImpl reservationService;
    private final UtilisateurService utilisateurService;
    private final ObjectMapper objectMapper;

    public ReservationController(SeanceService seanceService,
                                 SiegeService siegeService,
                                 ReservationServiceImpl reservationService,
                                 UtilisateurService utilisateurService,
                                 ObjectMapper objectMapper) {
        this.seanceService = seanceService;
        this.siegeService = siegeService;
        this.reservationService = reservationService;
        this.utilisateurService = utilisateurService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/new/{idSeance}")
    public String newReservation(@PathVariable String idSeance, Model model, HttpServletRequest request)
            throws JsonProcessingException {

        Seance seance = seanceService.getById(idSeance)
                .orElseThrow(() -> new IllegalArgumentException("Séance introuvable : " + idSeance));

        var salle = seance.getSalle();

        // Tous les sièges de la salle (plan)
        var sieges = siegeService.getSiegesBySalle(salle.getId());

        // Sièges déjà occupés pour CETTE séance
        List<String> occupiedSeatIds = reservationService.getOccupiedSeatIds(idSeance);

        // JSON pour le front (évite #objects.toJson)
        String siegesJson = objectMapper.writeValueAsString(sieges);
        String occupiedJson = objectMapper.writeValueAsString(occupiedSeatIds);

        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("seance", seance);
        model.addAttribute("salle", salle);
        model.addAttribute("sieges", sieges);

        model.addAttribute("siegesJson", siegesJson);
        model.addAttribute("occupiedJson", occupiedJson);

        return "reservation/reservation-saisie"; // templates/reservation/reservation-saisie.html
    }

    @PostMapping("/create")
    public String createReservation(@RequestParam("idSeance") String idSeance,
                                    @RequestParam(value = "seatIds[]", required = false) List<String> seatIds,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (seatIds == null || seatIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("ko", "Veuillez sélectionner au moins un siège.");
                return "redirect:/reservation/new/" + idSeance;
            }

            String email = authentication.getName();
            Utilisateur utilisateur = utilisateurService.getUtilisateurByMail(email);

            reservationService.createReservation(idSeance, utilisateur, seatIds);

            redirectAttributes.addFlashAttribute("ok", "Réservation confirmée avec succès !");
            return "redirect:/seance/" + idSeance;

        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("ko", ex.getMessage());
            return "redirect:/reservation/new/" + idSeance;

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("ko", "Erreur lors de la réservation : " + ex.getMessage());
            return "redirect:/reservation/new/" + idSeance;
        }
    }
}
