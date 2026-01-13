package mg.working.cinema.repository.util;

import mg.working.cinema.model.util.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role,String> {
}
