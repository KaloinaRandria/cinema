package mg.working.cinema.model.reservation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.model.Siege;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation_fille")
@SequenceGenerator(
        name = "s_reservation_fille",
        sequenceName = "s_reservation_fille",
        allocationSize = 1)
public class ReservationFille {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_reservation_fille") @Column(name = "id_reservation_fille")
    String id;
    double prix;
    @ManyToOne @JoinColumn(name = "id_reservation_mere", referencedColumnName = "id_reservation_mere", nullable = false)
    ReservationMere reservationMere;
    @ManyToOne @JoinColumn(name = "id_siege", referencedColumnName = "id_siege", nullable = false)
    Siege siege;
}
