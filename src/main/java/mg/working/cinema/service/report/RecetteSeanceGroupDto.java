package mg.working.cinema.service.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecetteSeanceGroupDto {

    private String idSeance;
    private String titre;
    private LocalDateTime debut;

    private double resa;

    private double pubTotalSeance;

    // ✅ colonnes “par séance”
    private double pubPayeTotalSeance;
    private double pubResteTotalSeance;

    private double totalSeance;
    private double extra;


    // lignes société×séance
    private List<RecetteSocieteLineDto> societes = new ArrayList<>();
}
