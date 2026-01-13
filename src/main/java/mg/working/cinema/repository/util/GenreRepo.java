package mg.working.cinema.repository.util;

import mg.working.cinema.model.util.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepo extends JpaRepository<Genre,String> {
}
