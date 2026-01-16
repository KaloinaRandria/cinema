package mg.working.cinema.repository;

import mg.working.cinema.model.Siege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiegeRepo extends JpaRepository<Siege, String> {

    @Query("""
        select s
        from Siege s
        where s.salle.id = :salleId
        order by s.rangee asc, s.numero asc
    """)
    List<Siege> findBySalleOrdered(@Param("salleId") String salleId);
}
