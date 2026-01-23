package mg.working.cinema.service.report;

import mg.working.cinema.repository.pub.PaiementPubRepo;
import mg.working.cinema.repository.report.SeanceRecetteLineProjection;
import mg.working.cinema.repository.report.SeanceRecetteRepo;
import mg.working.cinema.repository.report.SocieteMontantProjection;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class SeanceRecetteService {

    private final SeanceRecetteRepo seanceRecetteRepo;
    private final PaiementPubRepo paiementPubRepo;

    public SeanceRecetteService(SeanceRecetteRepo seanceRecetteRepo,
                                PaiementPubRepo paiementPubRepo) {
        this.seanceRecetteRepo = seanceRecetteRepo;
        this.paiementPubRepo = paiementPubRepo;
    }

    // -------- Période --------
    public LocalDateTime startOf(int year, int month) {
        return YearMonth.of(year, month).atDay(1).atStartOfDay();
    }

    public LocalDateTime endOf(int year, int month) {
        return YearMonth.of(year, month).plusMonths(1).atDay(1).atStartOfDay();
    }

    /**
     * ✅ Nouvelle structure: liste groupée par séance, avec sous-lignes sociétés.
     */
    public List<RecetteSeanceGroupDto> getRecettesParSeance(int year, int month) {
        LocalDateTime start = startOf(year, month);
        LocalDateTime end = endOf(year, month);

        // 1) Lignes base: (séance × société)
        List<SeanceRecetteLineProjection> lines = seanceRecetteRepo.getRecettesParSeanceLines(start, end);

        // 2) Dû par société (pub)
        Map<String, Double> dueBySociete = new HashMap<>();
        List<SocieteMontantProjection> dueRows = seanceRecetteRepo.totalPubDueBySocieteBetween(start, end);
        for (SocieteMontantProjection p : dueRows) {
            if (p.getIdSociete() != null) {
                dueBySociete.put(p.getIdSociete(), p.getMontant() == null ? 0d : p.getMontant());
            }
        }

        // 3) Payé par société (pub)
        Map<String, Double> paidBySociete = new HashMap<>();
        List<SocieteMontantProjection> paidRows = paiementPubRepo.totalPaidBySocieteBetween(start, end);
        for (SocieteMontantProjection p : paidRows) {
            if (p.getIdSociete() != null) {
                paidBySociete.put(p.getIdSociete(), p.getMontant() == null ? 0d : p.getMontant());
            }
        }

        // 4) taux société = payé/dû (borné 0..1)
        Map<String, Double> tauxBySociete = new HashMap<>();
        for (String idSoc : dueBySociete.keySet()) {
            double due = dueBySociete.getOrDefault(idSoc, 0d);
            double paid = paidBySociete.getOrDefault(idSoc, 0d);

            double taux = (due <= 0) ? 0d : (paid / due);
            if (taux > 1) taux = 1; // pas d'avance
            if (taux < 0) taux = 0;

            tauxBySociete.put(idSoc, taux);
        }

        // 5) Groupement par séance
        Map<String, RecetteSeanceGroupDto> bySeance = new LinkedHashMap<>();

        for (SeanceRecetteLineProjection p : lines) {
            String idSeance = p.getIdSeance();

            RecetteSeanceGroupDto g = bySeance.get(idSeance);
            if (g == null) {
                g = new RecetteSeanceGroupDto();
                g.setIdSeance(idSeance);
                g.setTitre(p.getTitre());
                g.setDebut(p.getDebut());

                double resa = p.getResa() == null ? 0d : p.getResa();
                g.setResa(resa);

                g.setPubTotalSeance(0d);
                g.setPubPayeTotalSeance(0d);
                g.setPubResteTotalSeance(0d);

                g.setTotalSeance(resa);

                bySeance.put(idSeance, g);
            }

            // Séance sans pub => idSociete null : on ne crée pas de sous-ligne société
            if (p.getIdSociete() == null) {
                continue;
            }

            double pubSocieteSeance = p.getPubSocieteSeance() == null ? 0d : p.getPubSocieteSeance();
            String idSoc = p.getIdSociete();

            double taux = tauxBySociete.getOrDefault(idSoc, 0d);

            double payeSocieteSeance = pubSocieteSeance * taux;
            double resteSocieteSeance = pubSocieteSeance - payeSocieteSeance;
            if (resteSocieteSeance < 0) resteSocieteSeance = 0;

            // Sous-ligne société×séance
            RecetteSocieteLineDto l = new RecetteSocieteLineDto();
            l.setIdSociete(idSoc);
            l.setNomSociete(p.getNomSociete());
            l.setPubSocieteSeance(pubSocieteSeance);
            l.setTauxSociete(taux);
            l.setPubPayeSocieteSeance(payeSocieteSeance);
            l.setPubResteSocieteSeance(resteSocieteSeance);

            g.getSocietes().add(l);

            // Totaux séance
            g.setPubTotalSeance(g.getPubTotalSeance() + pubSocieteSeance);
            g.setPubPayeTotalSeance(g.getPubPayeTotalSeance() + payeSocieteSeance);
            g.setPubResteTotalSeance(g.getPubResteTotalSeance() + resteSocieteSeance);

            // total séance = resa + pub total séance
            g.setTotalSeance(g.getResa() + g.getPubTotalSeance());
        }

        return new ArrayList<>(bySeance.values());
    }

    // -------- Totaux global page --------
    public double totalPub(List<RecetteSeanceGroupDto> seances) {
        return seances.stream().mapToDouble(RecetteSeanceGroupDto::getPubTotalSeance).sum();
    }

    public double totalResa(List<RecetteSeanceGroupDto> seances) {
        return seances.stream().mapToDouble(RecetteSeanceGroupDto::getResa).sum();
    }

    public double totalGeneral(List<RecetteSeanceGroupDto> seances) {
        return seances.stream().mapToDouble(RecetteSeanceGroupDto::getTotalSeance).sum();
    }

    public double totalPubPaye(List<RecetteSeanceGroupDto> seances) {
        return seances.stream().mapToDouble(RecetteSeanceGroupDto::getPubPayeTotalSeance).sum();
    }

    public double totalPubReste(List<RecetteSeanceGroupDto> seances) {
        return seances.stream().mapToDouble(RecetteSeanceGroupDto::getPubResteTotalSeance).sum();
    }
}
