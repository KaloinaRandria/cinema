package mg.working.cinema.service.pub;

import jakarta.transaction.Transactional;
import mg.working.cinema.model.pub.PaiementPub;
import mg.working.cinema.model.pub.SocietePub;
import mg.working.cinema.repository.pub.PaiementPubRepo;
import mg.working.cinema.service.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class PaiementPubService {

    private final PaiementPubRepo paiementPubRepo;
    private final SocietePubService societePubService;
    private final IdGenerator idGenerator;

    public PaiementPubService(PaiementPubRepo paiementPubRepo,
                              SocietePubService societePubService,
                              IdGenerator idGenerator) {
        this.paiementPubRepo = paiementPubRepo;
        this.societePubService = societePubService;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public PaiementPub enregistrerPaiement(String idSociete,
                                           double montant,
                                           LocalDateTime datePaiement,
                                           String reference) {
        if (montant <= 0) throw new IllegalArgumentException("Le montant du paiement doit être > 0.");

        SocietePub societe = societePubService.getById(idSociete);

        PaiementPub p = new PaiementPub();
        p.setIdPaiementPub(idGenerator);
        p.setSocietePub(societe);
        p.setMontant(montant);
        p.setDatePaiement(datePaiement != null ? datePaiement : LocalDateTime.now());
        p.setReference(reference);

        return paiementPubRepo.save(p);
    }

    public double totalPayeParSociete(int year, int month, String idSociete) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.plusMonths(1).atDay(1).atStartOfDay();
        return paiementPubRepo.sumPaiementsBySocieteBetween(idSociete, start, end);
    }

    public List<PaiementPub> listePaiementsParSociete(int year, int month, String idSociete) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.plusMonths(1).atDay(1).atStartOfDay();
        return paiementPubRepo.findBySocietePub_IdAndDatePaiementBetweenOrderByDatePaiementDesc(idSociete, start, end);
    }
}
