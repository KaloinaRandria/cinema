package mg.working.cinema.repository.film;

import mg.working.cinema.model.Siege;
import mg.working.cinema.model.film.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeanceRepo extends JpaRepository<Seance,String> {
    @Query("""
        SELECT COUNT(s) > 0
        FROM Seance s
        WHERE s.salle.id = :idSalle
          AND :debut < s.fin
          AND :fin > s.debut
    """)
    boolean existsConflitSalle(
            @Param("idSalle") String idSalle,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    @Query("SELECT s FROM Seance s ORDER BY s.debut DESC")
    List<Seance> findAllOrderByDebutDesc();
}
