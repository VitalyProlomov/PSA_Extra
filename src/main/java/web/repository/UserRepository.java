package web.repository;

import web.persistence.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Find user by username
     * @param username username to search
     * @return Optional containing user if found
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Find user by email
     * @param email email to search
     * @return Optional containing user if found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check if username exists
     * @param username username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * @param email email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);
}