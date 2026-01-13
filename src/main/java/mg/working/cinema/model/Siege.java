package mg.working.cinema.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.service.util.IdGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "siege")
@SequenceGenerator(
        name = "s_siege",
        sequenceName = "s_siege",
        allocationSize = 1
)
public class Siege {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_siege") @Column(name = "id_siege")
    String id;
    String rangee;
    int numero;
    @ManyToOne @JoinColumn(name = "id_salle", referencedColumnName = "id_salle", nullable = false)
    Salle salle;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("SIG", "s_siege");
    }
}
