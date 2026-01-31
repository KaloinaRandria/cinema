package mg.working.cinema.model.reservation;

import jakarta.persistence.*;
import lombok.*;
import mg.working.cinema.service.util.IdGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation_extra")
@SequenceGenerator(
        name = "s_reservation_extra",
        sequenceName = "s_reservation_extra",
        allocationSize = 1
)
public class ReservationExtra {

    @Id
    @Column(name = "id_reservation_extra")
    String id;

    @ManyToOne
    @JoinColumn(name = "id_reservation_mere", referencedColumnName = "id_reservation_mere", nullable = false)
    ReservationMere reservationMere;

    @Column(nullable = false)
    String libelle; // "POPCORN"

    @Column(nullable = false)
    int quantite;   // ex: 1, 2...

    @Column(name = "prix_unitaire", nullable = false)
    double prixUnitaire; // 10000

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("REX", "s_reservation_extra");
    }
}
