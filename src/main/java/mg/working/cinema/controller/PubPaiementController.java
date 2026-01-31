package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.pub.PaiementPub;
import mg.working.cinema.service.pub.DiffusionPubService;
import mg.working.cinema.service.pub.PaiementPubService;
import mg.working.cinema.service.pub.SocietePubService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/pub/paiements")
public class PubPaiementController {

    private final PaiementPubService paiementPubService;
    private final SocietePubService societePubService;
    private final DiffusionPubService diffusionPubService;

    // format HTML input type="datetime-local" => 2025-12-15T10:00
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public PubPaiementController(PaiementPubService paiementPubService,
                                 SocietePubService societePubService,
                                 DiffusionPubService diffusionPubService) {
        this.paiementPubService = paiementPubService;
        this.societePubService = societePubService;
        this.diffusionPubService = diffusionPubService;
    }

    @GetMapping
    public String pagePaiements(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "idSociete", required = false) String idSociete,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentUri", request.getRequestURI());

        int y = (year == null ? 2025 : year);
        int m = (month == null ? 12 : month);

        model.addAttribute("year", y);
        model.addAttribute("month", m);

        // liste sociétés pour le select
        model.addAttribute("societes", societePubService.getAll());

        // si pas de société choisie => on affiche page sans calcul
        if (idSociete == null || idSociete.isBlank()) {
            model.addAttribute("idSociete", "");
            model.addAttribute("totalDu", 0);
            model.addAttribute("totalPaye", 0);
            model.addAttribute("reste", 0);
            model.addAttribute("paiements", List.of());
            return "pub/paiements";
        }

        double totalDu = diffusionPubService.calculerCAParSociete(y, m, idSociete);
        double totalPaye = paiementPubService.totalPayeParSociete(y, m, idSociete);
        double reste = totalDu - totalPaye;

        model.addAttribute("idSociete", idSociete);
        model.addAttribute("totalDu", totalDu);
        model.addAttribute("totalPaye", totalPaye);
        model.addAttribute("reste", reste);

        List<PaiementPub> paiements = paiementPubService.listePaiementsParSociete(y, m, idSociete);
        model.addAttribute("paiements", paiements);

        return "pub/paiements";
    }

    @PostMapping("/create")
    public String createPaiement(
            @RequestParam("idSociete") String idSociete,
            @RequestParam("montant") double montant,
            @RequestParam(value = "datePaiement", required = false) String datePaiementStr,
            @RequestParam(value = "reference", required = false) String reference,
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            RedirectAttributes ra
    ) {
        try {
            LocalDateTime datePaiement = null;
            if (datePaiementStr != null && !datePaiementStr.isBlank()) {
                datePaiement = LocalDateTime.parse(datePaiementStr, dtf);
            }

            paiementPubService.enregistrerPaiement(idSociete, montant, datePaiement, reference);
            ra.addFlashAttribute("ok", "Paiement enregistré avec succès.");
        } catch (Exception e) {
            ra.addFlashAttribute("ko", "Erreur : " + e.getMessage());
        }

        return "redirect:/pub/paiements?year=" + year + "&month=" + month + "&idSociete=" + idSociete;
    }
}
