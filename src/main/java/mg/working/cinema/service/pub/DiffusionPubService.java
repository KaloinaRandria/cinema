package mg.working.cinema.service.pub;

import jakarta.transaction.Transactional;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.pub.DiffusionPub;
import mg.working.cinema.model.pub.OffrePub;
import mg.working.cinema.model.pub.SocietePub;
import mg.working.cinema.repository.pub.DiffusionPubRepo;
import mg.working.cinema.service.film.SeanceService;
import mg.working.cinema.service.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class DiffusionPubService {

    private final DiffusionPubRepo diffusionPubRepo;
    private final SeanceService seanceService;
    private final SocietePubService societePubService;
    private final OffrePubService offrePubService;
    private final IdGenerator idGenerator;

    public DiffusionPubService(
            DiffusionPubRepo diffusionPubRepo,
            SeanceService seanceService,
            SocietePubService societePubService,
            OffrePubService offrePubService,
            IdGenerator idGenerator
    ) {
        this.diffusionPubRepo = diffusionPubRepo;
        this.seanceService = seanceService;
        this.societePubService = societePubService;
        this.offrePubService = offrePubService;
        this.idGenerator = idGenerator;
    }

    public List<DiffusionPub> getAll() {
        return diffusionPubRepo.findAll();
    }

    public DiffusionPub getById(String id) {
        return diffusionPubRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diffusion pub introuvable : " + id));
    }

    @Transactional
    public DiffusionPub creerDiffusion(String idSeance, String idSociete, String idOffre, int nbDiffusions) {
        if (nbDiffusions <= 0) {
            throw new IllegalArgumentException("Le nombre de diffusions doit être > 0.");
        }

        Seance seance = seanceService.getById(idSeance)
                .orElseThrow(() -> new IllegalArgumentException("Séance introuvable : " + idSeance));

        SocietePub societe = societePubService.getById(idSociete);
        OffrePub offre = offrePubService.getById(idOffre);

        // Exemple de calcul :
        // montantTotal = nbDiffusions * prixUnitaire(offre)
        double montantTotal = nbDiffusions * offre.getPrixUnitaire();

        DiffusionPub d = new DiffusionPub();
        d.setId(idGenerator);
        d.setSeance(seance);
        d.setSocietePub(societe);
        d.setOffrePub(offre);
        d.setNbDiffusions(nbDiffusions);
        d.setMontantTotal(montantTotal);
        d.setDateDiffusion(LocalDateTime.now());

        return diffusionPubRepo.save(d);
    }

    public double calculerCA(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
        return diffusionPubRepo.sumMontantTotalBetween(start, end);
    }

    public double calculerCAParSociete(int year, int month, String idSociete) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
        return diffusionPubRepo.sumMontantTotalBySocieteBetween(idSociete, start, end);
    }

}
