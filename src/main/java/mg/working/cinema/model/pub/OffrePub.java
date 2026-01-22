package mg.working.cinema.model.pub;

import jakarta.persistence.*;
import lombok.*;
import mg.working.cinema.service.util.IdGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "offre_pub")
@SequenceGenerator(
        name = "s_offre_pub",
        sequenceName = "s_offre_pub",
        allocationSize = 1
)
public class OffrePub {

    @Id
    @Column(name = "id_offre_pub")
    private String id;

    @Column(nullable = false)
    private String libelle;

    @Column(name = "prix_unitaire", nullable = false)
    private double prixUnitaire;

    @Column(nullable = false)
    private boolean actif = true;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("OFP", "s_offre_pub");
    }

    public OffrePub(String id) {
        this.id = id;
    }
}
