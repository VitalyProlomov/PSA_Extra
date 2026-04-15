package web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pokerlibrary.models.Action;
import pokerlibrary.models.Card;
import pokerlibrary.models.PlayerInGame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplayResponse {
    private boolean success;
    private String message;
    private ReplayState replayState;
    private Map<String, PlayerDisplayInfo> players;
    private CommunityCardsDisplay communityCards;
    private Double currentPot;
    private String currentStreet;
    private int actionIndex;
    private boolean isComplete;
    private LastActionInfo lastAction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerDisplayInfo {
        private String playerId;
        private String playerName;
        private String position;
        private Double balance;
        private Integer seatNumber;
        private boolean isHero;
        private boolean showCards;
        private boolean isFolded;
        private Card card1;
        private Card card2;
        private String lastActionType;
        private Double lastActionAmount;
        private boolean hasButton;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastActionInfo {
        private String playerId;
        private String actionType;
        private Double amount;
        private String displayText;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunityCardsDisplay {
        private Card flopCard1;
        private Card flopCard2;
        private Card flopCard3;
        private Card turnCard;
        private Card riverCard;
        private boolean flopVisible;
        private boolean turnVisible;
        private boolean riverVisible;
    }
}