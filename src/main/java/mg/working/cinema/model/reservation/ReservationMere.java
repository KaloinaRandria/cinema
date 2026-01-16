package mg.working.cinema.model.reservation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.model.film.Seance;
import mg.working.cinema.model.user.Utilisateur;
import mg.working.cinema.service.util.IdGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation_mere")
@SequenceGenerator(
        name = "s_reservation_mere",
        sequenceName = "s_reservation_mere",
        allocationSize = 1)
public class ReservationMere {
    @Id @Column(name = "id_reservation_mere")
    String id;
    String reference;
    @Column(name = "date_reservation")
    LocalDateTime dateReservation;
    @ManyToOne @JoinColumn(name = "id_utilisateur", referencedColumnName = "id_utilisateur", nullable = false)
    Utilisateur utilisateur;
    @ManyToOne @JoinColumn(name = "id_seance", referencedColumnName = "id_seance", nullable = false)
    Seance seance;

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("REM", "s_reservation_mere");
    }
}
