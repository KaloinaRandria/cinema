package mg.working.cinema.service.facturation;

import mg.working.cinema.model.pub.PaiementPub;
import mg.working.cinema.repository.facturation.FacturationDiffusionRepo;
import mg.working.cinema.repository.facturation.FacturationLineProjection;
import mg.working.cinema.repository.facturation.SocieteTotalDueProjection;
import mg.working.cinema.repository.facturation.SocieteTotalPaidProjection;
import mg.working.cinema.repository.pub.PaiementPubRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacturationPubService {

    private final FacturationDiffusionRepo facturationDiffusionRepo;
    private final PaiementPubRepo paiementPubRepo;

    public FacturationPubService(FacturationDiffusionRepo facturationDiffusionRepo,
                                 PaiementPubRepo paiementPubRepo) {
        this.facturationDiffusionRepo = facturationDiffusionRepo;
        this.paiementPubRepo = paiementPubRepo;
    }

    public LocalDateTime startOf(int year, int month) {
        return YearMonth.of(year, month).atDay(1).atStartOfDay();
    }

    public LocalDateTime endOf(int year, int month) {
        return YearMonth.of(year, month).plusMonths(1).atDay(1).atStartOfDay();
    }

    public List<FacturationLineDto> getFacturation(int year, int month) {
        LocalDateTime start = startOf(year, month);
        LocalDateTime end = endOf(year, month);

        List<FacturationLineProjection> lines = facturationDiffusionRepo.findFacturationLines(start, end);

        // total dû par société
        Map<String, Double> dueBySociete = facturationDiffusionRepo.sumTotalDueBySociete(start, end)
                .stream()
                .collect(Collectors.toMap(
                        SocieteTotalDueProjection::getIdSociete,
                        p -> p.getTotalDue() == null ? 0d : p.getTotalDue()
                ));

        // total payé par société
        Map<String, Double> paidBySociete = paiementPubRepo.sumTotalPaidBySociete(start, end)
                .stream()
                .collect(Collectors.toMap(
                        SocieteTotalPaidProjection::getIdSociete,
                        p -> p.getTotalPaid() == null ? 0d : p.getTotalPaid()
                ));

        // taux = payé / dû (par société)
        Map<String, Double> tauxBySociete = new HashMap<>();
        for (String idSoc : dueBySociete.keySet()) {
            double due = dueBySociete.getOrDefault(idSoc, 0d);
            double paid = paidBySociete.getOrDefault(idSoc, 0d);
            double taux = (due <= 0) ? 0d : (paid / due);
            // option: limiter à 1 si tu ne veux pas d'avance
            if (taux > 1) taux = 1;
            if (taux < 0) taux = 0;
            tauxBySociete.put(idSoc, taux);
        }

        // build DTO final
        List<FacturationLineDto> out = new ArrayList<>();
        for (FacturationLineProjection p : lines) {
            FacturationLineDto dto = new FacturationLineDto();
            dto.setIdSociete(p.getIdSociete());
            dto.setNomSociete(p.getNomSociete());
            dto.setIdSeance(p.getIdSeance());
            dto.setDebut(p.getDebut());
            dto.setTitre(p.getTitre());

            dto.setNbDiffusions(p.getNbDiffusions() == null ? 0 : p.getNbDiffusions());
            double totalLine = p.getTotalDiffusion() == null ? 0d : p.getTotalDiffusion();
            dto.setTotalDiffusion(totalLine);

            double taux = tauxBySociete.getOrDefault(p.getIdSociete(), 0d);
            dto.setTauxPaiement(taux);

            double reste = totalLine - (totalLine * taux);
            dto.setResteAPayer(reste);

            out.add(dto);
        }
        return out;
    }

    public double totalDueSociete(List<FacturationLineDto> rows, String idSociete) {
        return rows.stream()
                .filter(r -> Objects.equals(r.getIdSociete(), idSociete))
                .mapToDouble(FacturationLineDto::getTotalDiffusion)
                .sum();
    }

    public double totalPaidSociete(int year, int month, String idSociete) {
        LocalDateTime start = startOf(year, month);
        LocalDateTime end = endOf(year, month);
        return paiementPubRepo.sumPaiementsBySocieteBetween(idSociete, start, end);
    }

    public List<PaiementPub> historiquePaiements(int year, int month, String idSociete) {
        LocalDateTime start = startOf(year, month);
        LocalDateTime end = endOf(year, month);
        return paiementPubRepo.findBySocietePub_IdAndDatePaiementBetweenOrderByDatePaiementDesc(idSociete, start, end);
    }
}
