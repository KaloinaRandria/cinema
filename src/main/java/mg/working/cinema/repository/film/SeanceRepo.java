package mg.working.cinema.repository.film;

import mg.working.cinema.model.Siege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeanceRepo extends JpaRepository<Siege,String> {
}
