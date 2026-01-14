package mg.working.cinema.service.film;

import mg.working.cinema.model.film.Film;
import mg.working.cinema.repository.film.FilmRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    @Autowired
    FilmRepo filmRepo;

    public List<Film> getAllFilms() {
        return filmRepo.findAll();
    }

    public Optional<Film> getFilmById(String id) {
        return filmRepo.findById(id);
    }
}
