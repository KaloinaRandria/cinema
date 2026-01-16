package mg.working.cinema.repository;

import mg.working.cinema.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalleRepo extends JpaRepository<Salle,String> {
}
