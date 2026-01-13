package mg.working.cinema.repository;

import mg.working.cinema.model.Siege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiegeRepo extends JpaRepository<Siege,String> {
}
