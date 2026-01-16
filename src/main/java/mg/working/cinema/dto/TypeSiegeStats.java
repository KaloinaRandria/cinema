package mg.working.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeSiegeStats {
    private String libelle;
    private double prixUnitaire;
    private double total;
    private double valeurTotale;

    public TypeSiegeStats(String libelle, double prixUnitaire) {
        this.libelle = libelle;
        this.prixUnitaire = prixUnitaire;
    }

    public void increment() {
        this.total++;
        this.valeurTotale += this.prixUnitaire;
    }
}
