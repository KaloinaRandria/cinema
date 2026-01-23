package mg.working.cinema.repository.facturation;

import mg.working.cinema.model.film.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FacturationDiffusionRepo extends JpaRepository<Seance, String> {

    // Lignes: Société + Séance (sur période via date_diffusion)
    @Query(value = """
        select
            sp.id_societe_pub as idSociete,
            sp.nom as nomSociete,
            s.id_seance as idSeance,
            s.debut as debut,
            f.titre as titre,
            coalesce(sum(dp.nb_diffusions), 0) as nbDiffusions,
            coalesce(sum(dp.montant_total), 0) as totalDiffusion
        from diffusion_pub dp
        join societe_pub sp on sp.id_societe_pub = dp.id_societe_pub
        join seance s on s.id_seance = dp.id_seance
        join film f on f.id_film = s.id_film
        where dp.date_diffusion >= :start
          and dp.date_diffusion <  :end
        group by sp.id_societe_pub, sp.nom, s.id_seance, s.debut, f.titre
        order by sp.nom asc, s.debut desc
        """, nativeQuery = true)
    List<FacturationLineProjection> findFacturationLines(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Total dû par société sur période
    @Query(value = """
        select
            dp.id_societe_pub as idSociete,
            coalesce(sum(dp.montant_total), 0) as totalDue
        from diffusion_pub dp
        where dp.date_diffusion >= :start
          and dp.date_diffusion <  :end
        group by dp.id_societe_pub
        """, nativeQuery = true)
    List<SocieteTotalDueProjection> sumTotalDueBySociete(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
