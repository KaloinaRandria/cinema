package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.Salle;
import mg.working.cinema.model.film.Film;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.service.SalleService;
import mg.working.cinema.service.film.FilmService;
import mg.working.cinema.service.film.SeanceService;
import mg.working.cinema.service.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/seance")
public class SeanceController {
    @Autowired
    SeanceService seanceService;
    @Autowired
    FilmService filmService;
    @Autowired
    SalleService salleService;
    @Autowired
    IdGenerator idGenerator;


    @GetMapping("/list")
    public String listSeances(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("seances", seanceService.listerSeances());
        return "seance/seance-liste"; // templates/seance/list.html
    }

    @GetMapping("/{id}")
    public String fiche(@PathVariable String id, Model model, HttpServletRequest request) {
        Seance seance = seanceService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Séance non trouvée avec l'id : " + id));
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("seance", seance );
        return "seance/seance-fiche";
    }

    @GetMapping("/add")
    public String add(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("films", filmService.getAllFilms());
        model.addAttribute("salles", salleService.getAllSalle());
        return "seance/seance-saisie";
    }


    // ✅ Traitement insertion
    @PostMapping("/save")
    public String save(@RequestParam("idFilm") String idFilm,
                       @RequestParam("idSalle") String idSalle,
                       @RequestParam("debut") String debut,   // format: 2026-01-15T10:30
                       @RequestParam("fin") String fin,       // format: 2026-01-15T12:30
                       @RequestParam("prix") double prix,
                       RedirectAttributes redirectAttributes) {
        try {
            // 1) Vérifs simples
            if (prix < 0) {
                throw new IllegalArgumentException("Le prix ne doit pas être négatif");
            }

            LocalDateTime dtDebut = LocalDateTime.parse(debut);
            LocalDateTime dtFin = LocalDateTime.parse(fin);

            // 2) Charger Film & Salle
            Optional<Film> filmOpt = filmService.getFilmById(idFilm);
            Optional<Salle> salleOpt = salleService.getSalleById(idSalle);

            if (filmOpt.isEmpty()) {
                throw new IllegalArgumentException("Film introuvable : " + idFilm);
            }
            if (salleOpt.isEmpty()) {
                throw new IllegalArgumentException("Salle introuvable : " + idSalle);
            }

            // 3) Construire la séance
            Seance seance = new Seance();
            seance.setId(idGenerator);
            seance.setDebut(dtDebut);
            seance.setFin(dtFin);
            seance.setPrix(prix);
            seance.setFilm(filmOpt.get());
            seance.setSalle(salleOpt.get());

            // 4) Appeler la logique métier (fin>début, conflits salle, etc.)
            Seance saved = seanceService.creerSeance(seance);

            redirectAttributes.addFlashAttribute("ok", "Séance créée : " + saved.getId());
            return "redirect:/seance/list";

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("ko", "Erreur : " + ex.getMessage());
            return "redirect:/seance/add";
        }
    }



}
