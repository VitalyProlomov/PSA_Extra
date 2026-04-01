package pokerlibrary.models;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.utils.Money;
import java.util.*;
import static pokerlibrary.models.PositionType.*;
/**
 * Class defining the entity of a game (1 hand played among players).
 * Contains information about cards, pot size, hands dealt to players
 * (if revealed) and other.
 */
public class Game {
    // Unique (At least for PokerOk)
    @Getter
    private final String gameId;
    /**
     * Contains the table index information.
     */
    @Getter
    @Setter
    private String table;
    public enum GameType {
        HOLDEM_9MAX,
        HOLDEM_RNC_6MAX,
        UNKNOWN
    }
    @Getter
    private GameType gameType = GameType.UNKNOWN;
    public void setGameType(GameType gameType) {
        if (this.gameType == GameType.UNKNOWN) {
            this.gameType = gameType;
        }
    }
    /**
     * Map containing pairs hash - player, that are present in given Game
     */
    @JsonProperty(value = "players", required = true)
    private HashMap<String, PlayerInGame> players = new HashMap<>();
    private final HashMap<String, Money> initialBalances = new HashMap<>();
    /**
     * -- GETTER --
     *
     * @return extra cash amount
     */
// Amount of dollars as a cash drop (or 0 if there is no cash drop)
    @Getter
    private Money extraCashAmount = Money.of("0");
    /**
     * -- GETTER --
     *
     *
     * -- SETTER --
     *  sets the date of the game
     *
     @return Date when the game took place.
      * @param date given date to set (no setting conditions)
     */
    @Setter
    @Getter
    private Date date;
    /**
     * -- GETTER --
     *
     * @return Big Blind (BB) size in dollars.
     */
    @Getter
    private final Money bigBlindSize$;
    private StreetDescription preFlop;
    private StreetDescription flop;
    private StreetDescription turn;
    private StreetDescription river;
    @JsonProperty(value = "allWinners", required = true)
    private HashMap<String, Money> allWinners = new HashMap<>();
    @Setter
    @Getter
    private Money rake = Money.of("0");

    /**
     * Contains all the single shown cards assigned to the players that showed them.
     */
    private final HashMap<String, Card> shownOneCards = new HashMap<>();


    /**
     * Constructs a new game with given ID and BB (given as Money) and players.
     */
    @JsonIgnore
    public Game(String gameId, Money bigBlindSize$, Collection<PlayerInGame> players) {
        this.gameId = gameId;
        this.bigBlindSize$ = bigBlindSize$ != null ? bigBlindSize$ : Money.of("0");
        if (players != null) {
            for (PlayerInGame p : players) {
                this.players.put(p.getId(), p);
            }
            for (PlayerInGame p : players) {
                this.initialBalances.put(p.getId(), p.getBalance());
            }
        }
    }
    /**
     * Constructs a new game with given ID and BB (given in dollars)
     *
     * @param gameId        ID of the game from PokerCraft parsed text view of the game
     * @param bigBlindSize$ value of 1 big blind in dollars
     */
    @JsonCreator
    public Game(@JsonProperty("gameId") String gameId,
                @JsonProperty("bigBlindSize$") Money bigBlindSize$,
                @JsonProperty("players") HashMap<String, PlayerInGame> players,
                @JsonProperty("initialBalances") HashMap<String, Money> initialBalances) {
        this.gameId = gameId;
        this.bigBlindSize$ = bigBlindSize$ != null ? bigBlindSize$ : Money.of("0");
        if (players != null) {
            this.players.putAll(players);
        }
        if (initialBalances != null) {
            for (String id : initialBalances.keySet()) {
                this.initialBalances.put(id, initialBalances.get(id));
            }
        }
    }
    /**
     * Adds information about one shown card (needed when player shows just one of his cards, not both).
     *
     * @param playerId id of hte player, that showed card.
     * @param card     card that was shown
     * @return true, if the player with given id is present in that game and information is added;
     * false, otherwise.
     */
    public boolean addShownOneCard(String playerId, Card card) {
        if (this.players.get(playerId) != null) {
            shownOneCards.put(playerId, card);
            return true;
        }
        return false;
    }
    /**
     * @return hashMap that contains all the single shown cards assigned to players who showed them
     */
    public HashMap<String, Card> getShownOneCards() {
        return new HashMap<>(shownOneCards);
    }
    @JsonIgnore
    public Money getHeroWinloss() {
        if (players.get("Hero") != null) {
            return players.get("Hero").getBalance().subtract(initialBalances.getOrDefault("Hero", Money.of("0")));
        }
        return Money.ZERO;
    }
    /**
     * If player with given hash doesn't exist in this game, returns 0.
     * @param hash hash of player, whose winloss will be counted
     * @return winloss of player with given hash
     */
    @JsonIgnore
    public Money getPlayerWinloss(String hash) {
        if (players.get(hash) != null) {
            return players.get(hash).getBalance().subtract(initialBalances.getOrDefault(hash, Money.of("0")));
        }
        return Money.ZERO;
    }
    /**
     * @return a Map of the hash - player pairs in game (copy, not a link)
     */
    public Map<String, PlayerInGame> getPlayers() {
        if (players == null) {
            return null;
        }
        return new HashMap<>(players);
    }
    /**
     * @return a Map of the position - player pairs in game (copy, not a link)
     */
    @JsonIgnore
    public HashMap<PositionType, PlayerInGame> getPosPlayersMap() {
        if (players == null) {
            return null;
        }
        HashMap<PositionType, PlayerInGame> posP = new HashMap<>();
        for (PlayerInGame p : players.values()) {
            posP.put(p.getPosition(), p);
        }
        return posP;
    }
    /**
     * Returns the copy player in the game with the corresponding hash. If there is
     * no player in game with such hash, null is returned
     *
     * @param id id of the PlayerInGame to get
     * @return player in game with same hash. Or null if no such player is found
     */
    public PlayerInGame getPlayer(String id) {
        try {
            return new PlayerInGame(players.get(id));
        } catch (Exception ex) {
            return null;
        }
    }
    /**
     * Sets the hand of the player on given position to the given hand
     *
     * @param pos  position of the player
     * @param hand Hand to set
     * @return true if player with such id was found and the hand was added. False, otherwise
     */
    public boolean setPlayerHand(PositionType pos, Hand hand) {
        for (PlayerInGame p : players.values()) {
            if (pos == p.getPosition()) {
                p.setHand(hand);
                return true;
            }
        }
        return false;
    }
    /**
     * Sets the hand of the player with given id to the given hand.
     *
     * @param id   id of the player
     * @param hand Hand to set
     * @return true if player with such id was found and the hand was added. False, otherwise
     */
    public boolean setPlayerHand(String id, Hand hand) {
        if (players.get(id) != null) {
            players.get(id).setHand(hand);
            return true;
        }
        return false;
    }
    /**
     * Sets Hero`s hand to the given hand
     *
     * @param hand hand to set
     * @return true if player with such id was found and the hand was added. False, otherwise
     */
    public boolean setHeroHand(Hand hand) {
        return setPlayerHand("Hero", hand);
    }
    /**
     * Sets players with given map of (ID -> PlayerInGame) pairs and updates the initial balances (inside the Game).
     *
     * @param playersMap players Map (id -> PlayerInGame) to set
     */
    public void setPlayers(HashMap<String, PlayerInGame> playersMap) {
        for (String id : playersMap.keySet()) {
            players.put(id, playersMap.get(id));
        }
//        this.players = new HashMap<>(playersMap);
        HashMap<String, Money> initB = new HashMap<>();
        for (PlayerInGame p : playersMap.values()) {
            initialBalances.put(p.getId(), p.getBalance());
        }
        setInitialBalances(initB);
    }
    /**
     * Sets players and updates the initial balances (inside the Game)
     *
     * @param players players to set
     */
    @JsonIgnore
    public void setPlayers(Collection<PlayerInGame> players) {
// Should think about working w nulls.
        this.players = new HashMap<>();
        for (PlayerInGame p : players) {
            this.players.put(p.getId(), p);
        }
        HashMap<String, Money> initB = new HashMap<>();
        for (PlayerInGame p : players) {
            initialBalances.put(p.getId(), p.getBalance());
        }
        setInitialBalances(initB);
    }
    /**
     * Subtracts given amount from the balance of the player with given ID.
     *
     * @param id         ID of player whose balance is needed to be decreased
     * @param decrAmount amount that will be decreased from given player`s balance
     * @throws IllegalArgumentException if {@code decrAmount} is less than players balance or decrAmount is less than 0
     */
    public void decrementPlayersBalance(String id, Money decrAmount) {
        decrementPlayersBalanceMoney(id, Money.of(String.valueOf(decrAmount)));
    }
    /**
     * Subtracts given Money amount from the balance of the player with given ID.
     *
     * @param id         ID of player whose balance is needed to be decreased
     * @param decrAmount amount that will be decreased from given player`s balance
     * @throws IllegalArgumentException if {@code decrAmount} is less than players balance or decrAmount is less than 0
     */
    public void decrementPlayersBalanceMoney(String id, Money decrAmount) {
        if (decrAmount == null || decrAmount.compareTo(Money.of("0")) < 0) {
            throw new IllegalArgumentException("Decrement amount must be positive (ypu can not add chips to player`s balance during hand");
        }
        PlayerInGame p = players.get(id);
        if (p != null) {
            Money balance = p.getBalance();
            if (decrAmount.compareTo(balance) > 0) {
                throw new IllegalArgumentException("Decrement amount must be less or equal to the balance of the player");
            }
            p.setBalance(balance.subtract(decrAmount));
        }
    }
    /**
     * Returns the given amount to the player`s balance (if bet was uncalled)
     *
     * @param playerId     id of the player to return chips
     * @param returnAmount amount to return to player`s balance
     */
    public void returnUncalledChips(String playerId, Money returnAmount) {
        returnUncalledChipsMoney(playerId, Money.of(String.valueOf(returnAmount)));
    }
    /**
     * Returns the given Money amount to the player`s balance (if bet was uncalled)
     *
     * @param playerId     id of the player to return chips
     * @param returnAmount amount to return to player`s balance
     */
    public void returnUncalledChipsMoney(String playerId, Money returnAmount) {
        PlayerInGame p = players.get(playerId);
        if (p != null) {
            p.setBalance(p.getBalance().add(returnAmount));
        }
    }
    /**
     * @return true if extra cash is more than zero,
     * false otherwise.
     */
    @JsonIgnore
    public boolean isExtraCash() {
        return extraCashAmount != null && !extraCashAmount.isZero();
    }
    /**
     * Sets extra cash (can not be less than zero)
     *
     * @param amount amount ot set
     * @throws IllegalArgumentException if amount is less than zero
     */
    public void setExtraCash(Money amount) {
        setExtraCashMoney(Money.of(String.valueOf(amount)));
    }
    /**
     * Sets extra cash (can not be less than zero)
     *
     * @param amount amount ot set
     * @throws IllegalArgumentException if amount is less than zero
     */
    public void setExtraCashMoney(Money amount) {
        if (amount == null || amount.compareTo(Money.of("0")) < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        extraCashAmount = amount;
    }
    /**
     * Calculates the small blind size (as for the 29.03.2023, all the sizes on
     * PokerOk, if BB size is even, SB = 0.5 BB, if BB is an odd amount, then
     * SB = 0.4 BB).
     *
     * @return Small blind amount in dollars
     */
    @JsonIgnore
    public Money getSB() {
        return getSbMoney();
    }
    /**
     * Calculates the small blind size (as for the 29.03.2023, all the sizes on
     * PokerOk, if BB size is even, SB = 0.5 BB, if BB is an odd amount, then
     * SB = 0.4 BB).
     *
     * @return Small blind amount as Money
     */
    @JsonIgnore
    public Money getSbMoney() {
        Money bbCents = bigBlindSize$.multiply(new java.math.BigDecimal("100"));
        if (bbCents.toBigDecimal().remainder(new java.math.BigDecimal("2")).compareTo(java.math.BigDecimal.ZERO) == 0) {
            return bigBlindSize$.multiply(new java.math.BigDecimal("0.5"));
        }
        return bigBlindSize$.multiply(new java.math.BigDecimal("0.4"));
    }
    /**
     * @return copy of the preflop StreetDescription
     */
    public StreetDescription getPreFlop() {
        if (this.preFlop == null) {
            return null;
        }
        return new StreetDescription(preFlop);
    }
    /**
     * Sets preflop (sets a copy of given StreetDescription, not a link)
     *
     * @param preFlop given StreetDescription
     */
    public void setPreFlop(StreetDescription preFlop) {
        if (preFlop == null) {
            this.preFlop = null;
            return;
        }
        this.preFlop = new StreetDescription(preFlop);
    }
    /**
     * @return copy of the flop StreetDescription
     */
    public StreetDescription getFlop() {
        if (flop == null) {
            return null;
        }
        return new StreetDescription(flop);
    }
    /**
     * Sets flop (sets a copy of given StreetDescription, not a link)
     *
     * @param flop given StreetDescription
     */
    public void setFlop(StreetDescription flop) {
        if (flop == null) {
            this.flop = null;
            return;
        }
        this.flop = new StreetDescription(flop);
    }
    /**
     * @return copy of the turn StreetDescription
     */
    public StreetDescription getTurn() {
        if (turn == null) {
            return null;
        }
        return new StreetDescription(turn);
    }
    /**
     * Sets turn (sets a copy of given StreetDescription, not a link)
     *
     * @param turn given StreetDescription
     */
    public void setTurn(StreetDescription turn) {
        if (turn == null) {
            this.turn = null;
        } else {
            this.turn = new StreetDescription(turn);
        }
    }
    /**
     * @return copy of the river StreetDescription
     */
    public StreetDescription getRiver() {
        if (this.river == null) {
            return null;
        }
        return new StreetDescription(river);
    }
    /**
     * Sets river (sets a copy of given StreetDescription, not a link)
     *
     * @param river given StreetDescription
     */
    public void setRiver(StreetDescription river) {
        if (river == null) {
            this.river = null;
            return;
        }
        this.river = new StreetDescription(river);
    }
    /**
     * @return the HashMap of winners in this game, containing id`s and
     * amount won assigned to the player with given id
     */
    public HashMap<String, Money> getWinners() {
        HashMap<String, Money> result = new HashMap<>();
        if (allWinners != null) {
            for (String key : allWinners.keySet()) {
                result.put(key, allWinners.get(key));
            }
        }
        return result;
    }
    public void setWinners(HashMap<String, Money> map) {
        if (map == null) {
            this.allWinners = null;
            return;
        }
        for (String key : map.keySet()) {
            addWinner(key, map.get(key));
        }
    }
    /**
     * Adds a winner and assigns amount on to the winner hash set.
     *
     * @param winnerId hash of the player who has won the pot
     * @param amount amount of the final pot that the player has taken.
     * @return true if the winner is in the game, and the adding is successful,
     * false otherwise (if there is no player with such hash in game,
     * so winner is not added)
     */
    public boolean addWinner(String winnerId, Money amount) {
        return addWinnerMoney(winnerId, Money.of(String.valueOf(amount)));
    }
    /**
     * Adds a winner and assigns amount on to the winner hash set.
     *
     * @param winnerId hash of the player who has won the pot
     * @param amount amount of the final pot that the player has taken.
     * @return true if the winner is in the game, and the adding is successful,
     * false otherwise (if there is no player with such hash in game,
     * so winner is not added)
     */
    public boolean addWinnerMoney(String winnerId, Money amount) {
        if (players.get(winnerId) == null) {
            return false;
        }
        players.get(winnerId).setBalance(players.get(winnerId).getBalance().add(amount));
        allWinners.merge(winnerId, amount, Money::add);
        return true;
    }
    /**
     * @return final pot of the game
     */
    @JsonIgnore
    public Money getFinalPot() {
        return getFinalPotMoney();
    }
    /**
     * @return final pot of the game as Money
     */
    @JsonIgnore
    public Money getFinalPotMoney() {
        if (river != null) {
            return river.getPotAfterBettingMoney();
        } else if (turn != null) {
            return turn.getPotAfterBettingMoney();
        } else if (flop != null) {
            return flop.getPotAfterBettingMoney();
        } else if (preFlop != null) {
            return preFlop.getPotAfterBettingMoney();
        }
        throw new RuntimeException("Final pot was not counted.");
    }
    /**
     * @return HashMap of all the initial balances of players in game
     */
    public HashMap<String, Money> getInitialBalancesMoney() {
        return new HashMap<>(initialBalances);
    }
    /**
     * Legacy getter for backward compatibility
     */
    public HashMap<String, Money> getInitialBalances() {
        HashMap<String, Money> result = new HashMap<>();
        for (String key : initialBalances.keySet()) {
            result.put(key, initialBalances.get(key));
        }
        return result;
    }
    /**
     * Sets initial balances of the players
     *
     * @param players hashMap of balances
     */
    private void setInitialBalances(Map<String, Money> players) {
        initialBalances.putAll(players);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj.getClass() != Game.class) {
            return false;
        }
        return this.gameId.equals(((Game) obj).gameId);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(gameId);
    }
    /**
     * @return string representation of the game, that contains id of the game, list of players
     * and all StreetsDescriptions
     */
    @Override
    public String toString() {
        ArrayList<PlayerInGame> orderedPlayers = new ArrayList<>();
        ArrayList<PositionType> orderPos = new ArrayList<>(List.of(SB, BB, TB, UTG, UTG_1, UTG_2, LJ, HJ, CO, BTN));
        for (PositionType orderPo : orderPos) {
            PlayerInGame p = null;
            for (PlayerInGame iterP : players.values()) {
                if (iterP.getPosition() == orderPo) {
                    p = iterP;
                }
            }
            if (p != null)
                orderedPlayers.add(new PlayerInGame(p));
        }
        for (PlayerInGame p : orderedPlayers) {
            p.setBalance(this.initialBalances.get(p.getId()));
        }
        return "(Game| Game Id: " + gameId +
                ", Players: " + orderedPlayers +
                ", Preflop: " + preFlop +
                ", Flop: " + flop +
                ", Turn: " + turn +
                ", River: " + river + ")";
    }
}