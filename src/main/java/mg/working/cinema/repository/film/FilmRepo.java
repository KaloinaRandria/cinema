package mg.working.cinema.repository.film;

import mg.working.cinema.model.film.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepo extends JpaRepository<Film, String> {
}
