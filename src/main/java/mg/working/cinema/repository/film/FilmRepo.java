package mg.working.cinema.repository.film;

import mg.working.cinema.model.film.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepo extends JpaRepository<Film, String> {
    @Query("SELECT f FROM Film f WHERE f.id = :id")
    Film findsById(String id);
}
