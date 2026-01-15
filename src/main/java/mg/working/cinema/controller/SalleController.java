package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.Siege;
import mg.working.cinema.service.SalleService;
import mg.working.cinema.service.SiegeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/salle")
public class SalleController {

    private final SalleService salleService;
    private final SiegeService siegeService;

    public SalleController(SalleService salleService, SiegeService siegeService) {
        this.salleService = salleService;
        this.siegeService = siegeService;
    }

    @GetMapping("/list")
    public String listSalle(Model model, HttpServletRequest request) {

        var salles = salleService.getAllSalle();

        // Map: idSalle -> valeurMax
        Map<String, Double> valeurMaxMap = new HashMap<>();
        for (var s : salles) {
            valeurMaxMap.put(s.getId(), salleService.calculerValeurMax(s.getId()));
        }

        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("salles", salles);
        model.addAttribute("valeurMaxMap", valeurMaxMap);

        return "salle/salle-liste";
    }

    /**
     * Fiche salle
     * URL : /salle/{id}
     */
    @GetMapping("/{id}")
    public String ficheSalle(@PathVariable("id") String idSalle,
                             Model model,
                             HttpServletRequest request) {

        var salle = salleService.getSalleById(idSalle)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable : " + idSalle));

        List<Siege> sieges = siegeService.getSiegesBySalle(idSalle);

        int totalPremium = 0;
        int totalStandard = 0;
        int totalVip = 0;

        double valeurPremium = 0.0;
        double valeurStandard = 0.0;
        double valeurVip = 0.0;

        for (Siege s : sieges) {
            if (s.getTypeSiege() == null) continue;
            if (s.getTypeSiege().getLibelle() == null) continue;

            String libelle = s.getTypeSiege().getLibelle();
            double prix = s.getTypeSiege().getPrix();

            if (libelle.equalsIgnoreCase("Premium")) {
                totalPremium++;
                valeurPremium += prix;
            }
            else if (libelle.equalsIgnoreCase("Standard")) {
                totalStandard++;
                valeurStandard += prix;
            } else if (libelle.equalsIgnoreCase("Vip")) {
                totalVip++;
                valeurVip += prix;
            }
        }

        double valeurMax = valeurPremium + valeurStandard + valeurVip;

        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("salle", salle);
        model.addAttribute("sieges", sieges);

        // Totaux sièges
        model.addAttribute("totalPremium", totalPremium);
        model.addAttribute("totalStandard", totalStandard);
        model.addAttribute("totalVip", totalVip);


        // Totaux financiers
        model.addAttribute("valeurPremium", valeurPremium);
        model.addAttribute("valeurStandard", valeurStandard);
        model.addAttribute("valeurMax", valeurMax);
        model.addAttribute("valeurVip", valeurVip);


        return "salle/salle-fiche";
    }

}
