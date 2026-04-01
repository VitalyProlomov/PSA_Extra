package pokerlibrary.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pokerlibrary.utils.Money;
import java.text.DecimalFormat;
import java.util.*;
import static pokerlibrary.models.PositionType.*;
/**
 * Class used for storing information about one street - all the action,
 * cards that came.
 */
public class StreetDescription {
    @Getter
    private Money potAfterBetting;
    // Will be null for Pre-flop
    private Board board;
    private HashMap<PositionType, PlayerInGame> playersAfterBetting = new HashMap<>();
    private ArrayList<Action> allActions = new ArrayList<>();
    @Setter
    @Getter
    private boolean isAllIn = false;

    /**
     * Constructs a StreetDescription with given parameters (Money version)
     * @param potAfterBetting pot size after betting as Money
     * @param board board of given StreetDescription
     * @param playersAfterBetting players who did not fold after betting
     * @param allActions all actions of all players that took place during this StreetDescription
     */
    public StreetDescription(Money potAfterBetting, Board board, Collection<PlayerInGame> playersAfterBetting, ArrayList<Action> allActions) {
        this.potAfterBetting = potAfterBetting != null ? potAfterBetting : Money.of("0");
        if (board != null) {
            this.board = new Board(board);
        } else {
            this.board = null;
        }
        for (PlayerInGame p : playersAfterBetting) {
            this.playersAfterBetting.put(p.getPosition(), new PlayerInGame(p));
        }
        this.allActions = new ArrayList<>(allActions);
    }
    /**
     * Constructs a new StreetDescription by copying values of all the fields of give StreetDescription
     * @param strCopy given StreetDescription
     */
    public StreetDescription(StreetDescription strCopy) {
        this.potAfterBetting = strCopy.potAfterBetting != null ? new Money(strCopy.potAfterBetting.toBigDecimal()) : Money.of("0");
        this.playersAfterBetting = new HashMap<>();
        for (PlayerInGame p : strCopy.getPlayersAfterBetting()) {
            this.playersAfterBetting.put(p.getPosition(), new PlayerInGame(p));
        }
        this.allActions = new ArrayList<>(strCopy.allActions);
        if (strCopy.board != null) {
            this.board = new Board(strCopy.board);
        } else {
            this.board = null;
        }
        this.isAllIn = strCopy.isAllIn;
    }
    /**
     * Constructs empty StreetDescription
     */
    public StreetDescription() {
        this.potAfterBetting = Money.of("0");
    }
    /**
     * @return list of all actions
     */
    public ArrayList<Action> getAllActions() {
        return new ArrayList<>(allActions);
    }
    /**
     * adds Action to the list of all actions
     * @param action action to add
     */
    private void addAction(Action action) {
        allActions.add(action);
    }
    /**
     * Adds action to the action list of the street description and changes the balance of the given player.
     *
     * @param action action to add
     * @param decrAmount amount that will be subtracted from the balance of acting player
     */
    public void addActionAndUpdateBalances(Action action, Money decrAmount) {
        addActionAndUpdateBalancesMoney(action, Money.of(String.valueOf(decrAmount)));
    }
    /**
     * Adds action to the action list of the street description and changes the balance of the given player.
     *
     * @param action action to add
     * @param decrAmount amount that will be subtracted from the balance of acting player
     */
    public void addActionAndUpdateBalancesMoney(Action action, Money decrAmount) {
        if (decrAmount == null || decrAmount.compareTo(Money.of("0")) < 0) {
            throw new IllegalArgumentException("decrement amount can not be less than 0.");
        }
        for (PlayerInGame p : playersAfterBetting.values()) {
            if (p.getId().equals(action.getPlayerId())) {
                if (decrAmount.compareTo(p.getBalance()) > 0) {
                    throw new IllegalArgumentException("decrement amount can not be less than player balance");
                }
                p.setBalance(p.getBalance().subtract(decrAmount));
            }
        }
        addAction(action);
    }
    /**
     * returns amount of uncalled bet to the players balance
     * @param id id of the player that needs a return
     * @param returnedAmount amount of returned money in dollars
     */
    public void returnUncalledChips(String id, Money returnedAmount) {
        returnUncalledChipsMoney(id, Money.of(String.valueOf(returnedAmount)));
    }
    /**
     * returns amount of uncalled bet to the players balance
     * @param id id of the player that needs a return
     * @param returnedAmount amount of returned money as Money
     */
    public void returnUncalledChipsMoney(String id, Money returnedAmount) {
        if (returnedAmount == null || returnedAmount.compareTo(Money.of("0")) < 0) {
            throw new IllegalArgumentException("returned amount can not be less than 0.");
        }
        for (PlayerInGame p : playersAfterBetting.values()) {
            if (p.getId().equals(id)) {
                p.setBalance(p.getBalance().add(returnedAmount));
            }
        }
        potAfterBetting = potAfterBetting.subtract(returnedAmount);
    }
    /**
     * @return an ArrayList of all the players that didn't fold after all the actions
     */
    public ArrayList<PlayerInGame> getPlayersAfterBetting() {
        return new ArrayList<>(playersAfterBetting.values());
    }
    /**
     * @return Hash of the last aggressor of this betting round (the last player to raise).
     * If no one bet (everybody called ar checked), null is returned.
     */
    @JsonIgnore
    public String getLastAggressorHash() {
        for (int i = allActions.size() - 1; i >= 0; --i) {
            if (allActions.get(i).getActionType().equals(Action.ActionType.RAISE) ||
                    allActions.get(i).getActionType().equals(Action.ActionType.BET)) {
                return allActions.get(i).getPlayerId();
            }
        }
        return null;
    }
//    /**
//     * Sets players after betting
//     * @param playersAfterBetting players to set
//     */
//    public void setPlayersAfterBetting(ArrayList<PlayerInGame> playersAfterBetting) {
//        this.playersAfterBetting = new HashMap<>();
//        for (PlayerInGame p : playersAfterBetting) {
//            this.playersAfterBetting.put(p.getPosition(), new PlayerInGame(p));
//        }
//    }
    /**
     * Sets players after betting
     * @param playersAfterBetting players to set
     */
    public void setPlayersAfterBetting(Collection<PlayerInGame> playersAfterBetting) {
        this.playersAfterBetting = new HashMap<>();
        for (PlayerInGame p : playersAfterBetting) {
            this.playersAfterBetting.put(p.getPosition(), new PlayerInGame(p));
        }
    }
    /**
     * adds a single player to the players after betting
     * @param player player to add
     */
// I may make it return boolean to show weather the player was added or not
    public void addPlayerAfterBetting(PlayerInGame player) {
        if (!this.playersAfterBetting.containsKey(player.getPosition())) {
            this.playersAfterBetting.put(player.getPosition(), new PlayerInGame(player));
        }
    }
    /**
     * Removes a single player from players after betting
     * @param player player to remove (position is looked at when the removal is occurred)
     */
    public void removePlayerAfterBetting(PlayerInGame player) {
        this.playersAfterBetting.remove(player.getPosition());
    }
    /**
     * @return the board of this Street
     */
    public Board getBoard() {
        if (board == null) {
            return null;
        }
        return new Board(board);
    }
    /**
     * Sets board
     * @param board board to set
     */
    public void setBoard(Board board) {
        if (board == null) {
            this.board = null;
            return;
        }
        this.board = new Board(board);
    }

    /**
     * @return pot after betting as Money
     */
    public Money getPotAfterBettingMoney() {
        return potAfterBetting != null ? potAfterBetting : Money.of("0");
    }
    /**
     * Sets pot after betting (from Money)
     * @param potAfterBetting pot to set
     */
    public void setPotAfterBetting(Money potAfterBetting) {
        this.potAfterBetting = Money.of(String.valueOf(potAfterBetting));
    }
    /**
     * Sets pot after betting (from Money)
     * @param potAfterBetting pot to set
     */
    public void setPotAfterBettingMoney(Money potAfterBetting) {
        this.potAfterBetting = potAfterBetting != null ? potAfterBetting : Money.of("0");
    }
    /**
     * StreetDescription is equal to another object only if this object is StreetDescription.
     * Two StreetDescriptions are only equal if boards, pot after betting,
     * all actions (order also matters) and players after betting are equal
     * @param obj compared object
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == StreetDescription.class) {
            StreetDescription st = (StreetDescription) obj;
            if (this.board == null && st.board != null) {
                return false;
            }
            return (this.potAfterBetting == null ? st.potAfterBetting == null : this.potAfterBetting.compareTo(st.potAfterBetting) == 0) &&
                    this.allActions.equals(st.allActions) &&
                    this.playersAfterBetting.equals((st.playersAfterBetting)) &&
                    ((this.board == null && st.board == null) || this.board.equals(st.board));
        }
        return false;
    }
    /**
     * @return hashcode of the StreetDescription using potAfterBetting, board, playersAfterBetting, allActions.
     */
    @Override
    public int hashCode() {
        return Objects.hash(potAfterBetting, board, playersAfterBetting, allActions);
    }
    /**
     * @return String representation of this StreetDescription with its board,
     * players after betting, pot after betting and all actions.
     */
    @Override
    public String toString() {
        ArrayList<PlayerInGame> orderedPlayers = new ArrayList<>();
        ArrayList<PositionType> orderPos = new ArrayList<>(List.of(UTG, UTG_1, UTG_2, SB, BB, LJ, HJ, CO, BTN));
        for (PositionType orderPo : orderPos) {
            PlayerInGame p = playersAfterBetting.get(orderPo);
            if (p != null) {
                orderedPlayers.add(p);
            }
        }
        return "(StreetDescription| Board: " + board +
                ", pot after betting: " + (potAfterBetting != null ? potAfterBetting : Money.of("0")) +
                ", Players after betting: " + orderedPlayers +
                ", Actions: " + allActions;
    }
}