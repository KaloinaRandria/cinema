package mg.working.cinema.service;

import mg.working.cinema.model.Siege;
import mg.working.cinema.repository.SiegeRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiegeService {

    private final SiegeRepo siegeRepository;

    public SiegeService(SiegeRepo siegeRepository) {
        this.siegeRepository = siegeRepository;
    }

    /**
     * Retourne tous les sièges d’une salle (plan de salle)
     */
    public List<Siege> getSiegesBySalle(String idSalle) {

        if (idSalle == null || idSalle.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de la salle est obligatoire");
        }

        return siegeRepository.findSiegeBySalle(idSalle);
    }

    /**
     * Retourne un siège par ID
     */
    public Siege getById(String idSiege) {
        return siegeRepository.findById(idSiege)
                .orElseThrow(() ->
                        new IllegalArgumentException("Siège introuvable : " + idSiege)
                );
    }
}
