package mg.working.cinema.repository.report;

import mg.working.cinema.model.film.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeanceRecetteRepo extends JpaRepository<Seance, String> {

    @Query(value = """
    select
        s.id_seance as id,
        f.titre as titre,
        s.debut as debut,
        coalesce(sum(dp.montant_total), 0) as pub,
        rm.montant_total as resa,
        (coalesce(sum(dp.montant_total), 0) + rm.montant_total) as total
    from seance s
    left join public.diffusion_pub dp on s.id_seance = dp.id_seance
    join public.reservation_mere rm on s.id_seance = rm.id_seance
    join film f on s.id_film = f.id_film
    group by s.id_seance, s.debut, f.titre, rm.montant_total
    order by s.debut desc
    """, nativeQuery = true)
    List<SeanceRecetteProjection> getRecettesParSeance();

}
