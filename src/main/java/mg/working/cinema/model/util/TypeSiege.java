package mg.working.cinema.model.util;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mg.working.cinema.repository.film.FilmRepo;
import mg.working.cinema.service.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "type_siege")
@SequenceGenerator(
        name = "s_type_siege",
        sequenceName = "s_type_siege",
        allocationSize = 1
)
public class TypeSiege {
    @Id @Column(name = "id_type_siege")
    String id;
    String libelle;
    double prix;

    public void setPrix(String prix) {
        this.prix = Double.parseDouble(prix);
    }

    public void setId(IdGenerator idGenerator) {
        this.id = idGenerator.generateId("TSI","s_type_siege");
    }
}


