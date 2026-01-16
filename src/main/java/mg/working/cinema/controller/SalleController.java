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

        // key = libelle du type (Premium/Vip/Standard/...)
        Map<String, mg.working.cinema.dto.TypeSiegeStats> statsMap = new HashMap<>();

        for (Siege s : sieges) {
            if (s.getTypeSiege() == null || s.getTypeSiege().getLibelle() == null) continue;

            String libelle = s.getTypeSiege().getLibelle();
            double prix = s.getTypeSiege().getPrix();

            statsMap.putIfAbsent(libelle, new mg.working.cinema.dto.TypeSiegeStats(libelle, prix));
            statsMap.get(libelle).increment();
        }

        // Valeur max = somme des valeursTotales
        double valeurMax = statsMap.values().stream()
                .mapToDouble(mg.working.cinema.dto.TypeSiegeStats::getValeurTotale)
                .sum();

        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("salle", salle);
        model.addAttribute("sieges", sieges);

        // ✅ liste dynamique pour Thymeleaf
        model.addAttribute("statsTypes", statsMap.values());
        model.addAttribute("valeurMax", valeurMax);

        return "salle/salle-fiche";
    }


}
