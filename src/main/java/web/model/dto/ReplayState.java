package web.model.dto;

import lombok.NoArgsConstructor;
import pokerlibrary.models.Card;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class ReplayState {
    public String curStreetStr = "preflop";
    public int curActionIndex = -1;
    public String prevActionPlayerId = null;
    public String prevActionType = null;
    public Double prevActionAmount = null;
    public String shownCardsStreet = null;
    public Map<String, Double> playerBalances = new HashMap<>();
    public Map<String, Double> initialBalances = new HashMap<>();
    public Double currentPot = 0.0;
    public boolean isComplete = false;
    public String message = "";

    // Card display info per player
    public Map<String, PlayerCardDisplay> playerCardDisplays = new HashMap<>();
    public CommunityCardsDisplay communityCards = new CommunityCardsDisplay();

    public ReplayState(String preflop, int i, HashMap<String, Double> stringDoubleHashMap, HashMap<String, Double> stringDoubleHashMap1) {

    }

    public static class PlayerCardDisplay {
        public boolean showCards = false;
        public boolean isFolded = false;
        public Card card1 = null;
        public Card card2 = null;
        public String actionType = null;
        public Double actionAmount = null;
    }

    public static class CommunityCardsDisplay {
        public Card flopCard1 = null;
        public Card flopCard2 = null;
        public Card flopCard3 = null;
        public Card turnCard = null;
        public Card riverCard = null;
        public boolean flopVisible = false;
        public boolean turnVisible = false;
        public boolean riverVisible = false;
    }
}