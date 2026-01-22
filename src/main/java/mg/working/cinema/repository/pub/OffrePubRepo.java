package mg.working.cinema.repository.pub;

import mg.working.cinema.model.pub.OffrePub;
import mg.working.cinema.model.pub.SocietePub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffrePubRepo extends JpaRepository<OffrePub,String> {
    OffrePub findByLibelleIgnoreCase(String libelle);
    boolean existsByLibelleIgnoreCase(String libelle);
}
