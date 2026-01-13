package mg.working.cinema.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.model.util.Role;
import mg.working.cinema.service.util.IdGenerator;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "utilisateur")
@SequenceGenerator(
        name = "s_utilisateur",
        sequenceName = "s_utilisateur",
        allocationSize = 1)
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "s_utilisateur") @Column(name = "id_utilisateur")
    String id;
    String nom;
    String prenom;
    String mail;
    String mdp;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilisateur_role",
            joinColumns = @JoinColumn(name = "id_utilisateur"),
            inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    Set<Role> roles = new HashSet<>();

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("USR", "s_utilisateur");
    }
}
