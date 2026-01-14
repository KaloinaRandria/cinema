package mg.working.cinema.repository;

import mg.working.cinema.model.Siege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiegeRepo extends JpaRepository<Siege,String> {
    @Query("""
        SELECT s
        FROM Siege s
        WHERE s.salle.id = :idSalle
    """)
    List<Siege> findSiegeBySalle(String idSalle);
}
