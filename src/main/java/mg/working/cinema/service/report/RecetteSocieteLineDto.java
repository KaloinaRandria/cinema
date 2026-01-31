package mg.working.cinema.service.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecetteSocieteLineDto {
    private String idSociete;
    private String nomSociete;

    private double pubSocieteSeance;

    // payé/rest calculés PAR SEANCE (société×séance)
    private double pubPayeSocieteSeance;
    private double pubResteSocieteSeance;

    // optionnel
    private double tauxSociete;
}
