package mg.working.cinema.repository.report;

import mg.working.cinema.model.film.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeanceRecetteRepo extends JpaRepository<Seance, String> {

    @Query(value = """
        select
            s.id_seance as id,
            f.titre as titre,
            s.debut as debut,

            coalesce(dp.pub, 0) as pub,
            coalesce(rm.resa, 0) as resa,
            (coalesce(dp.pub, 0) + coalesce(rm.resa, 0)) as total,

            dp.id_societe_pub as idSociete,
            sp.nom as nomSociete

        from seance s
        join film f on f.id_film = s.id_film

        left join (
            select
                id_seance,
                id_societe_pub,
                sum(montant_total) as pub
            from diffusion_pub
            where date_diffusion >= :start
              and date_diffusion <  :end
            group by id_seance, id_societe_pub
        ) dp on dp.id_seance = s.id_seance

        left join societe_pub sp on sp.id_societe_pub = dp.id_societe_pub

        left join (
            select
                id_seance,
                sum(montant_total) as resa
            from reservation_mere
            group by id_seance
        ) rm on rm.id_seance = s.id_seance

        where s.debut >= :start
          and s.debut <  :end

        order by s.debut desc, sp.nom asc
        """, nativeQuery = true)
    List<SeanceRecetteProjection> getRecettesParSeance(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Total pub par société (dû)
    @Query(value = """
        select
            dp.id_societe_pub as idSociete,
            coalesce(sum(dp.montant_total), 0) as montant
        from diffusion_pub dp
        where dp.date_diffusion >= :start
          and dp.date_diffusion <  :end
        group by dp.id_societe_pub
        """, nativeQuery = true)
    List<SocieteMontantProjection> totalPubDueBySocieteBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Total pub global
    @Query(value = """
        select coalesce(sum(dp.montant_total), 0)
        from diffusion_pub dp
        where dp.date_diffusion >= :start
          and dp.date_diffusion <  :end
        """, nativeQuery = true)
    double totalPubBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Total resa global
    @Query(value = """
        select coalesce(sum(rm.montant_total), 0)
        from reservation_mere rm
        join seance s on s.id_seance = rm.id_seance
        where s.debut >= :start
          and s.debut <  :end
        """, nativeQuery = true)
    double totalResaBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    /**
     * Lignes: 1 ligne = (Séance × Société) pour la pub
     * - Séances sans pub => 1 ligne avec idSociete null et pubSocieteSeance = 0
     * - On filtre par période sur s.debut ET dp.date_diffusion (si présent)
     */
    @Query(value = """
        select
            s.id_seance as idSeance,
            f.titre as titre,
            s.debut as debut,

            rm.montant_total as resa,

            coalesce(sum(dp.montant_total), 0) as pubSocieteSeance,

            sp.id_societe_pub as idSociete,
            sp.nom as nomSociete

        from seance s
        join reservation_mere rm on rm.id_seance = s.id_seance
        join film f on f.id_film = s.id_film

        left join diffusion_pub dp
               on dp.id_seance = s.id_seance
              and dp.date_diffusion >= :start
              and dp.date_diffusion <  :end

        left join societe_pub sp
               on sp.id_societe_pub = dp.id_societe_pub

        where s.debut >= :start
          and s.debut <  :end

        group by
            s.id_seance, f.titre, s.debut, rm.montant_total,
            sp.id_societe_pub, sp.nom

        order by s.debut desc, sp.nom asc nulls last
    """, nativeQuery = true)
    List<SeanceRecetteLineProjection> getRecettesParSeanceLines(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Total dû pub par société sur la période (SUM diffusion_pub.montant_total).
     */
    @Query(value = """
        select
            dp.id_societe_pub as idSociete,
            coalesce(sum(dp.montant_total), 0) as montant
        from diffusion_pub dp
        where dp.date_diffusion >= :start
          and dp.date_diffusion <  :end
        group by dp.id_societe_pub
    """, nativeQuery = true)
    List<SocieteMontantProjection> totalDuePubBySocieteBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
