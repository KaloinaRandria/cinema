package mg.working.cinema.repository.report;

import java.time.LocalDateTime;

public interface SeanceRecetteProjection {
    String getId();            // s.id_seance
    String getTitre();         // f.titre
    LocalDateTime getDebut();  // s.debut
    Double getPub();           // sum(dp.montant_total)
    Double getResa();          // rm.montant_total
    Double getTotal();         // pub + resa
}
