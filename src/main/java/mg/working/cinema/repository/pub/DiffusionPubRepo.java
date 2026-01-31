package mg.working.cinema.repository.pub;

import mg.working.cinema.model.pub.DiffusionPub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DiffusionPubRepo extends JpaRepository<DiffusionPub, String> {

    @Query("""
        SELECT COALESCE(SUM(d.montantTotal), 0)
        FROM DiffusionPub d
        WHERE d.dateDiffusion >= :start AND d.dateDiffusion < :end
    """)
    double sumMontantTotalBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT COALESCE(SUM(d.montantTotal), 0)
    FROM DiffusionPub d
    WHERE d.societePub.id = :idSociete
      AND d.dateDiffusion >= :start
      AND d.dateDiffusion < :end
""")
    double sumMontantTotalBySocieteBetween(String idSociete, LocalDateTime start, LocalDateTime end);

}