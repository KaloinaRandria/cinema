package mg.working.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import mg.working.cinema.model.film.Film;
import mg.working.cinema.service.film.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
