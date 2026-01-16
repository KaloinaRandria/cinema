package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.film.Film;
import mg.working.cinema.service.film.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/film")
public class FilmController {
    @Autowired
    FilmService filmService;

    @GetMapping("/list")
    public String listFilms(Model model, HttpServletRequest request) {
        List<Film> films = filmService.getAllFilms();
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("films", films);
        return "film/film-liste";
    }

    @GetMapping("/{id}")
    public String ficheFilm(@PathVariable String id, Model model, HttpServletRequest request) {
        Film film = filmService.getFilmById(id)
                .orElseThrow(() -> new IllegalArgumentException("Film non trouvé avec l'id : " + id));
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("film", film);
        return "film/film-fiche"; // ex: templates/film/fiche.html
    }

}
