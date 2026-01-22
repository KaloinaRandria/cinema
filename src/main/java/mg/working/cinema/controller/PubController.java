package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.service.film.SeanceService;
import mg.working.cinema.service.pub.DiffusionPubService;
import mg.working.cinema.service.pub.OffrePubService;
import mg.working.cinema.service.pub.SocietePubService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pub")
public class PubController {

    private final DiffusionPubService diffusionPubService;
    private final SocietePubService societePubService;
    private final OffrePubService offrePubService;
    private final SeanceService seanceService;

    public PubController(
            DiffusionPubService diffusionPubService,
            SocietePubService societePubService,
            OffrePubService offrePubService,
            SeanceService seanceService
    ) {
        this.diffusionPubService = diffusionPubService;
        this.societePubService = societePubService;
        this.offrePubService = offrePubService;
        this.seanceService = seanceService;
    }

    // LISTE DES DIFFUSIONS
    @GetMapping("/diffusions")
    public String listDiffusions(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("diffusions", diffusionPubService.getAll());
        return "pub/diffusion-liste";
    }

    // FORMULAIRE AJOUT
    @GetMapping("/diffusions/add")
    public String addDiffusion(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());

        model.addAttribute("seances", seanceService.listerSeances());
        model.addAttribute("societes", societePubService.getAll());
        model.addAttribute("offres", offrePubService.getAll());

        return "pub/diffusion-saisie";
    }

    // CREATE
    @PostMapping("/diffusions/create")
    public String createDiffusion(
            @RequestParam("idSeance") String idSeance,
            @RequestParam("idSociete") String idSociete,
            @RequestParam("idOffre") String idOffre,
            @RequestParam("nbDiffusions") int nbDiffusions,
            RedirectAttributes ra
    ) {
        try {
            diffusionPubService.creerDiffusion(idSeance, idSociete, idOffre, nbDiffusions);
            ra.addFlashAttribute("ok", "Diffusion pub enregistrée avec succès.");
            return "redirect:/pub/diffusions";
        } catch (Exception e) {
            ra.addFlashAttribute("ko", "Erreur : " + e.getMessage());
            return "redirect:/pub/diffusions/add";
        }
    }

    // CA PAR MOIS
    @GetMapping("/ca")
    public String caMensuel(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("currentUri", request.getRequestURI());

        int y = (year == null ? 2025 : year);
        int m = (month == null ? 12 : month);

        double ca = diffusionPubService.calculerCA(y, m);

        model.addAttribute("year", y);
        model.addAttribute("month", m);
        model.addAttribute("ca", ca);

        return "pub/ca";
    }
}
