package mg.working.cinema.repository.report;

import java.time.LocalDateTime;

public interface SeanceRecetteLineProjection {
    String getIdSeance();
    String getTitre();
    LocalDateTime getDebut();

    // réservation (ticket vendu) au niveau séance
    Double getResa();

    // pub pour UNE SOCIETE sur cette séance (SUM dp.montant_total)
    Double getPubSocieteSeance();

    // société (nullable si aucune pub)
    String getIdSociete();
    String getNomSociete();
}
