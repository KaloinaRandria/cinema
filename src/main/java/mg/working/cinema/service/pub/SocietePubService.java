package mg.working.cinema.service.pub;

import mg.working.cinema.model.pub.SocietePub;
import mg.working.cinema.repository.pub.SocietePubRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocietePubService {

    private final SocietePubRepo societePubRepo;

    public SocietePubService(SocietePubRepo societePubRepo) {
        this.societePubRepo = societePubRepo;
    }

    public List<SocietePub> getAll() {
        return societePubRepo.findAll();
    }

    public SocietePub getById(String id) {
        return societePubRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Société pub introuvable : " + id));
    }

    public SocietePub save(SocietePub societePub) {
        return societePubRepo.save(societePub);
    }
}
