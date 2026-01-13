package mg.working.cinema.model.util;

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
@Table(name = "type_salle")
@SequenceGenerator(
        name = "s_type_salle",
        sequenceName = "s_type_salle",
        allocationSize = 1
)
public class TypeSalle {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "s_type_salle") @Column(name = "id_type_salle")
    String id;
    String libelle;
    String code;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("TSL", "s_type_salle");
    }
}
