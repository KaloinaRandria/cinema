package mg.working.cinema.service.report;

import mg.working.cinema.repository.report.SeanceRecetteProjection;
import mg.working.cinema.repository.report.SeanceRecetteRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeanceRecetteService {

    private final SeanceRecetteRepo seanceRecetteRepo;

    public SeanceRecetteService(SeanceRecetteRepo seanceRecetteRepo) {
        this.seanceRecetteRepo = seanceRecetteRepo;
    }

    public List<SeanceRecetteProjection> getRecettesParSeance() {
        return seanceRecetteRepo.getRecettesParSeance();
    }

    public double totalPub(List<SeanceRecetteProjection> rows) {
        return rows.stream()
                .mapToDouble(r -> r.getPub() == null ? 0 : r.getPub())
                .sum();
    }

    public double totalResa(List<SeanceRecetteProjection> rows) {
        return rows.stream()
                .mapToDouble(r -> r.getResa() == null ? 0 : r.getResa())
                .sum();
    }

    public double totalGeneral(List<SeanceRecetteProjection> rows) {
        return rows.stream()
                .mapToDouble(r -> r.getTotal() == null ? 0 : r.getTotal())
                .sum();
    }
}
