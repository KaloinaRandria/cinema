package mg.working.cinema.repository.report;

import java.time.LocalDateTime;

public interface SeanceRecetteProjection {
    String getId();            // id_seance
    String getTitre();         // film titre
    LocalDateTime getDebut();  // seance debut

    Double getPub();           // pub total (sur seance + societe)
    Double getResa();          // resa total (sur seance)
    Double getTotal();         // pub + resa

    String getIdSociete();     // societe_pub id (peut être null si pas de pub)
    String getNomSociete();    // societe_pub nom (peut être null)
}
