package web.persistence.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "games",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_game_id", columnList = "game_id"),
                @Index(name = "idx_date_created", columnList = "date_created")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_games_user"))
    private UserEntity user;

    @Column(name = "game_id", nullable = false, length = 50)
    private String gameId;

    /**
     * RAW text file content that user uploaded
     */
    @Column(name = "raw_hand_text", columnDefinition = "TEXT", nullable = false, length = 50000)
    private String rawHandText;

    @Column(name = "date_played")
    private LocalDateTime datePlayed;

    @Column(name = "pot_type", length = 20)
    private String potType;

    @Column(name = "hero_winloss", precision = 10, scale = 2)
    private BigDecimal heroWinloss;

    @Column(name = "final_pot", precision = 10, scale = 2)
    private BigDecimal finalPot;

    @Column(name = "big_blind_size", precision = 10, scale = 2)
    private BigDecimal bigBlindSize;

    @Column(name = "game_type", length = 30)
    private String gameType;

    // ✅ ADD THESE FIELDS FOR TABLE DISPLAY (extracted during save)
    @Column(name = "hero_card1_rank", length = 10)
    private String heroCard1Rank;

    @Column(name = "hero_card1_suit", length = 10)
    private String heroCard1Suit;

    @Column(name = "hero_card2_rank", length = 10)
    private String heroCard2Rank;

    @Column(name = "hero_card2_suit", length = 10)
    private String heroCard2Suit;

    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    @PrePersist
    protected void onCreate() {
        dateCreated = LocalDateTime.now();
        dateUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = LocalDateTime.now();
    }
}