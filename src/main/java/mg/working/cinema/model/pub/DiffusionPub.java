package mg.working.cinema.model.pub;

import jakarta.persistence.*;
import lombok.*;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.service.util.IdGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "diffusion_pub")
@SequenceGenerator(
        name = "s_diffusion_pub",
        sequenceName = "s_diffusion_pub",
        allocationSize = 1
)
public class DiffusionPub {

    @Id
    @Column(name = "id_diffusion_pub")
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_seance", referencedColumnName = "id_seance", nullable = false)
    private Seance seance;

    @ManyToOne
    @JoinColumn(name = "id_societe_pub", referencedColumnName = "id_societe_pub", nullable = false)
    private SocietePub societePub;

    @ManyToOne
    @JoinColumn(name = "id_offre_pub", referencedColumnName = "id_offre_pub", nullable = false)
    private OffrePub offrePub;

    @Column(name = "nb_diffusions", nullable = false)
    private int nbDiffusions = 1;

    @Column(name = "date_diffusion")
    private LocalDateTime dateDiffusion;

    // 🔒 Historisation des prix (conseillé)
    @Column(name = "montant_unitaire", nullable = false)
    private double montantUnitaire;

    @Column(name = "montant_total", nullable = false)
    private double montantTotal;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("DPU", "s_diffusion_pub");
    }

    /**
     * Appelé avant insert/update pour garder la cohérence.
     * Si tu veux toujours prendre le prix de l'offre au moment de l'enregistrement.
     */
    @PrePersist
    @PreUpdate
    public void computeTotals() {
        if (this.offrePub != null) {
            // si montantUnitaire pas renseigné, on prend celui de l'offre
            if (this.montantUnitaire <= 0) {
                this.montantUnitaire = this.offrePub.getPrixUnitaire();
            }
        }
        this.montantTotal = this.nbDiffusions * this.montantUnitaire;
    }

    public DiffusionPub(String id) {
        this.id = id;
    }
}
