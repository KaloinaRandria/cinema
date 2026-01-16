package mg.working.cinema.model.film;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.model.Salle;
import mg.working.cinema.service.util.IdGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seance")
@SequenceGenerator(
        name = "s_seance",
        sequenceName = "s_seance",
        allocationSize = 1)
public class Seance {
    @Id @Column(name = "id_seance")
    String id;
    LocalDateTime debut;
    LocalDateTime fin;
    double prix;
    @ManyToOne @JoinColumn(name = "id_film", referencedColumnName = "id_film", nullable = false)
    Film film;
    @ManyToOne @JoinColumn(name = "id_salle", referencedColumnName = "id_salle", nullable = false)
    Salle salle;

    public Seance(String id) {
        this.id = id;
    }
    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("SEA", "s_seance");
    }
}
