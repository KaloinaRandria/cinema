package mg.working.cinema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.model.util.TypeSalle;
import mg.working.cinema.service.util.IdGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "salle")
@SequenceGenerator(
        name = "s_salle",
        sequenceName = "s_salle",
        allocationSize = 1
)
public class Salle {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_salle") @Column(name = "id_salle")
    String id;
    String nom;
    int capaciteMax;
    @ManyToOne @JoinColumn(name = "id_type_salle", referencedColumnName = "id_type_salle", nullable = false)
    TypeSalle typeSalle;
    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("SAL", "s_salle");
    }
}
