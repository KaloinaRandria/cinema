package mg.working.cinema.service.film;

import mg.working.cinema.repository.film.FilmRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmService {
    @Autowired
    FilmRepo filmRepo;
}
