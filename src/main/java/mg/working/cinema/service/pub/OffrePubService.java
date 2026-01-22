package mg.working.cinema.service.pub;

import mg.working.cinema.model.pub.OffrePub;
import mg.working.cinema.repository.pub.OffrePubRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OffrePubService {

    private final OffrePubRepo offrePubRepo;

    public OffrePubService(OffrePubRepo offrePubRepo) {
        this.offrePubRepo = offrePubRepo;
    }

    public List<OffrePub> getAll() {
        return offrePubRepo.findAll();
    }

    public OffrePub getById(String id) {
        return offrePubRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Offre pub introuvable : " + id));
    }

    public OffrePub save(OffrePub offrePub) {
        return offrePubRepo.save(offrePub);
    }
}
