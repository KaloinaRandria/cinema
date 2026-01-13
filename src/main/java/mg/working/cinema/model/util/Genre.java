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
@Table(name = "genre")
@SequenceGenerator(
        name = "s_genre",
        sequenceName = "s_genre",
        allocationSize = 1
)
public class Genre {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "s_genre") @Column(name = "id_genre")
    String id;
    @Column(nullable = false)
    String libelle;
    String code;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("GEN", "s_genre");
    }
}
