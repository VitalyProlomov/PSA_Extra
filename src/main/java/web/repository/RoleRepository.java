package web.repository;

import web.persistence.models.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Find role by name
     * @param name role name (e.g., "ROLE_USER")
     * @return Optional containing role if found
     */
    Optional<RoleEntity> findByName(String name);
}