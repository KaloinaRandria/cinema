package mg.working.cinema.repository.user;

import mg.working.cinema.model.user.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepo extends JpaRepository<Utilisateur,String> {
}
