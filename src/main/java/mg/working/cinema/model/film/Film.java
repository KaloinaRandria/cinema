package mg.working.cinema.model.film;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.model.util.Genre;
import mg.working.cinema.service.util.IdGenerator;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "film")
@SequenceGenerator(
        name = "s_film",
        sequenceName = "s_film",
        allocationSize = 1)
public class Film {
    @Id @Column(name = "id_film")
    String id;
    String titre;
    String description;
    double duree;
    @ManyToOne @JoinColumn(name = "id_genre", referencedColumnName = "id_genre", nullable = false)
    Genre genre;
    @Column(name = "date_sortie")
    LocalDate dateSortie;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("FIL", "s_film");
    }
}
