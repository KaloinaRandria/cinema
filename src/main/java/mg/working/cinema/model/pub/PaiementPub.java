package mg.working.cinema.model.pub;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paiement_pub")
public class PaiementPub {


    @Id
    @Column(name = "id_paiement_pub", length = 20)
    private String idPaiementPub;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Column(name = "montant", nullable = false)
    private double montant;

    // FK -> societe_pub(id_societe_pub)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_societe_pub", nullable = false)
    private SocietePub societePub;

    @Column(name = "reference", length = 100)
    private String reference;
}
