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
@Table(name = "role")
@SequenceGenerator(
        name = "s_role",
        sequenceName = "s_role",
        allocationSize = 1)
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "s_role") @Column(name = "id_role")
    String id;
    @Column(nullable = false)
    String libelle;
    String code;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("ROL", "s_role");
    }
}
