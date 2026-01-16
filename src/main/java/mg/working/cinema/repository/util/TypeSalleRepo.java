package mg.working.cinema.repository.util;

import mg.working.cinema.model.util.TypeSalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeSalleRepo extends JpaRepository<TypeSalle,String> {
}
