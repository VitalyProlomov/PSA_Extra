package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pokerlibrary.analizer.GameAnalyzer;
import pokerlibrary.exceptions.IncorrectBoardException;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.exceptions.IncorrectHandException;
import pokerlibrary.models.Game;
import pokerlibrary.models.Hand;
import pokerlibrary.models.PlayerInGame;
import pokerlibrary.parsers.gg.GGPokerokHoldem9MaxParser;
import web.persistence.models.GameEntity;
import web.persistence.models.UserEntity;
import web.repository.GameRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GGPokerokHoldem9MaxParser parser;

    @Transactional
    public GameEntity saveGame(UserEntity user, String rawText) throws IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        // Parse the text to extract key fields
        Game pokerGame = parser.parseGame(rawText);

        // Check if game already exists
        if (gameRepository.existsByUserAndGameId(user, pokerGame.getGameId())) {
            throw new IllegalArgumentException("Game already exists: " + pokerGame.getGameId());
        }

        // Create entity
        GameEntity entity = new GameEntity();
        entity.setUser(user);
        entity.setGameId(pokerGame.getGameId());
        entity.setRawHandText(rawText);  // ✅ Store raw text

        // Extract key fields for quick table display
        entity.setDatePlayed(pokerGame.getDate() != null ?
                LocalDateTime.ofInstant(pokerGame.getDate().toInstant(),
                        java.time.ZoneId.systemDefault()) : null);
        entity.setPotType(calculatePotType(pokerGame));
        entity.setHeroWinloss(BigDecimal.valueOf(pokerGame.getHeroWinloss()));
        entity.setFinalPot(BigDecimal.valueOf(pokerGame.getFinalPot()));
        entity.setBigBlindSize(BigDecimal.valueOf(pokerGame.getBigBlindSize$()));
        entity.setGameType(pokerGame.getGameType() != null ?
                pokerGame.getGameType().name() : "HOLDEM_9MAX");

        // ✅ Extract hero's cards for table display
        extractHeroCards(pokerGame, entity);

        return gameRepository.save(entity);
    }

    /**
     * Extract hero's card ranks and suits for quick table display
     */
    private void extractHeroCards(Game pokerGame, GameEntity entity) {
        try {
            PlayerInGame hero = pokerGame.getPlayer("Hero");
            if (hero != null && hero.getHand() != null) {
                Hand hand = hero.getHand();
                if (hand.getCard1() != null) {
                    entity.setHeroCard1Rank(hand.getCard1().getRank().name());
                    entity.setHeroCard1Suit(hand.getCard1().getSuit().name());
                }
                if (hand.getCard2() != null) {
                    entity.setHeroCard2Rank(hand.getCard2().getRank().name());
                    entity.setHeroCard2Suit(hand.getCard2().getSuit().name());
                }
            }
        } catch (Exception e) {
            // Hero cards not available - leave as null
            entity.setHeroCard1Rank(null);
            entity.setHeroCard1Suit(null);
            entity.setHeroCard2Rank(null);
            entity.setHeroCard2Suit(null);
        }
    }

    @Transactional(readOnly = true)
    public Game parseGameForReplay(GameEntity entity) throws IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        return parser.parseGame(entity.getRawHandText());
    }

    @Transactional(readOnly = true)
    public List<GameEntity> findAllByUser(UserEntity user) {
        return gameRepository.findByUserOrderByDateCreatedDesc(user);
    }

    @Transactional(readOnly = true)
    public GameEntity findByUserAndGameId(UserEntity user, String gameId) {
        return gameRepository.findByUserAndGameId(user, gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));
    }

    @Transactional
    public void deleteGame(UserEntity user, Long gameId) {
        GameEntity game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Not authorized to delete this game");
        }

        gameRepository.delete(game);
    }

    private String calculatePotType(Game game) {
        try {
            if (GameAnalyzer.isPotUnRaised(game)) return "unraised";
            if (GameAnalyzer.isPotSingleRaised(game)) return "single raised";
            if (GameAnalyzer.isPot3Bet(game)) return "3bet";
            if (GameAnalyzer.isPot4Bet(game)) return "4bet";
            if (GameAnalyzer.isPot5PlusBet(game)) return "5+ bet";
        } catch (Exception e) {
            // Handle gracefully
        }
        return "undefined";
    }
}