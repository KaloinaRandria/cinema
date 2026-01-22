package mg.working.cinema.repository.pub;

import mg.working.cinema.model.pub.SocietePub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocietePubRepo extends JpaRepository<SocietePub, String> {
    SocietePub findByNomIgnoreCase(String nom);
    boolean existsByNomIgnoreCase(String nom);
}
