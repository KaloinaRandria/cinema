package mg.working.cinema.repository.facturation;

import java.time.LocalDateTime;

public interface FacturationLineProjection {
    String getIdSociete();
    String getNomSociete();
    String getIdSeance();
    LocalDateTime getDebut();
    String getTitre();
    Integer getNbDiffusions();
    Double getTotalDiffusion();
}
