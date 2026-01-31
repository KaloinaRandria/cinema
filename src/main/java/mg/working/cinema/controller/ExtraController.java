package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.reservation.ReservationExtra;
import mg.working.cinema.model.reservation.ReservationMere;
import mg.working.cinema.repository.reservation.ReservationExtraRepo;
import mg.working.cinema.repository.reservation.ReservationMereRepo;
import mg.working.cinema.service.util.IdGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/extras")
public class ExtraController {

    private final ReservationExtraRepo extraRepo;
    private final ReservationMereRepo reservationMereRepo;
    private final IdGenerator idGenerator;

    public ExtraController(ReservationExtraRepo extraRepo,
                           ReservationMereRepo reservationMereRepo,
                           IdGenerator idGenerator) {
        this.extraRepo = extraRepo;
        this.reservationMereRepo = reservationMereRepo;
        this.idGenerator = idGenerator;
    }

    /**
     * Page d'ajout d'extra (simulation de données)
     */
    @GetMapping("/add")
    public String addExtra(Model model,
                           @RequestParam(required = false) String success,  HttpServletRequest request) {

        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("reservations", reservationMereRepo.findAll());
        model.addAttribute("success", success != null);

        return "extra/extra-saisie";
    }

    /**
     * Enregistrement de l'extra
     */
    @PostMapping("/save")
    public String saveExtra(@RequestParam("reservationMereId") String reservationMereId,
                            @RequestParam("libelle") String libelle,
                            @RequestParam("quantite") int quantite,
                            @RequestParam("prixUnitaire") double prixUnitaire , RedirectAttributes redirectAttributes) {

        // Sécurité minimale
        if (quantite <= 0 || prixUnitaire < 0) {
            redirectAttributes.addFlashAttribute("ko","Extra invalide");
            return "redirect:/extras/add";
        }

        ReservationMere reservationMere = reservationMereRepo
                .findById(reservationMereId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

        ReservationExtra extra = new ReservationExtra();
        extra.setId(idGenerator);
        extra.setReservationMere(reservationMere);
        extra.setLibelle(libelle.trim().toUpperCase()); // POPCORN
        extra.setQuantite(quantite);
        extra.setPrixUnitaire(prixUnitaire);

        extraRepo.save(extra);
        redirectAttributes.addFlashAttribute("ok","Extra inserer");

        return "redirect:/report/recettes-seances";
    }
}
