package web.repository;

import web.persistence.models.GameEntity;
import web.persistence.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {

    /**
     * Find all games by user, ordered by date created (newest first)
     * @param user user to search games for
     * @return list of games
     */
    List<GameEntity> findByUserOrderByDateCreatedDesc(UserEntity user);

    /**
     * Find game by user and game ID
     * @param user user who owns the game
     * @param gameId poker hand ID
     * @return Optional containing game if found
     */
    Optional<GameEntity> findByUserAndGameId(UserEntity user, String gameId);

    /**
     * Check if game exists for user
     * @param user user to check
     * @param gameId poker hand ID
     * @return true if game exists
     */
    boolean existsByUserAndGameId(UserEntity user, String gameId);

    /**
     * Delete all games by user
     * @param user user whose games to delete
     */
    void deleteByUser(UserEntity user);
}