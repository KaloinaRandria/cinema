package mg.working.cinema.model.pub;

import jakarta.persistence.*;
import lombok.*;
import mg.working.cinema.service.util.IdGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "societe_pub")
@SequenceGenerator(
        name = "s_societe_pub",
        sequenceName = "s_societe_pub",
        allocationSize = 1
)
public class SocietePub {

    @Id
    @Column(name = "id_societe_pub")
    private String id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String contact; // optionnel (tel/email/etc)

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("SOP", "s_societe_pub");
    }

    public SocietePub(String id) {
        this.id = id;
    }
}
