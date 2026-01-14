package mg.working.cinema.service.user;

import mg.working.cinema.model.user.Utilisateur;
import mg.working.cinema.repository.user.UtilisateurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {
    @Autowired
    UtilisateurRepo utilisateurRepo;

    public Utilisateur getUtilisateurByMail(String mail) {
        return utilisateurRepo.findByMail(mail);
    }


}
