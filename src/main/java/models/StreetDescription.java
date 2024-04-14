package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.DecimalFormat;
import java.util.*;

import static models.PositionType.*;

/**
 * Class used for storing information about one street - all the action,
 * cards that came.
 */
public class StreetDescription {
    private double potAfterBetting;
    // Will be null for Pre-flop
    private Board board;
    private HashMap<PositionType, PlayerInGame> playersAfterBetting = new HashMap<>();
    private ArrayList<Action> allActions = new ArrayList<>();
    private boolean isAllIn = false;

    /**
     * Constructs a StreetDescription with given parameters
     * @param potAfterBetting pot size after betting in dollars
     * @param board board of given StreetDescription
     * @param playersAfterBetting players who did not fold after betting
     * @param allActions all actions of all players that took place during this StreetDescription
     */
    public StreetDescription(double potAfterBetting, Board board, Collection<PlayerInGame> playersAfterBetting, ArrayList<Action> allActions) {
        this.potAfterBetting = potAfterBetting;
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
        this.potAfterBetting = strCopy.potAfterBetting;

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

    }

//    /**
//     * Sets balance of the given player
//     * @param playerId id of the player
//     * @param amount amount to set
//     */
//    public void setPlayerBalance(String playerId, double amount) {
//        for (PositionType pos : playersAfterBetting.keySet()) {
//            if (playersAfterBetting.get(pos).getId().equals(playerId)) {
//                playersAfterBetting.get(pos).setBalance(amount);
//            }
//        }
//    }
//
//    /**
//     * Reduces balance of the player with given id
//     * @param playerId
//     * @param decrAmount
//     */
//    public void decrementPlayerBalance(String playerId, double decrAmount) {
//        for (PositionType pos : playersAfterBetting.keySet()) {
//            if (playersAfterBetting.get(pos).getId().equals(playerId)) {
//                playersAfterBetting.get(pos).setBalance(playersAfterBetting.get(pos).getBalance() - decrAmount);
//            }
//        }
//    }

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
    public void addActionAndUpdateBalances(Action action, double decrAmount) {
        if (decrAmount < 0) {
            throw new IllegalArgumentException("decrement amount can not be less than 0.");
        }
        for (PlayerInGame p : playersAfterBetting.values()) {
            if (p.getId().equals(action.getPlayerId())) {
                if (decrAmount - p.getBalance() > 0.01) {
                    throw new IllegalArgumentException("decrement amount can not be less than player balance");
                }
                p.setBalance(p.getBalance() - decrAmount);
            }
        }
        addAction(action);
    }

    /**
     * returns amount of uncalled bet to the players balance
     * @param id id of the player that needs a return
     * @param returnedAmount amount of returned money in dollars
     */
    public void returnUncalledChips(String id, double returnedAmount) {
        if (returnedAmount < 0) {
            throw new IllegalArgumentException("returned amount can not be less than 0.");
        }
        for (PlayerInGame p : playersAfterBetting.values()) {
            if (p.getId().equals(id)) {
                p.setBalance(p.getBalance() + returnedAmount);
            }
        }

        potAfterBetting -= returnedAmount;
    }

    /**
     * @return an ArrayList of all the players that didnt fold after all the actions
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
     * @return pot after all the actions
     */
    public double getPotAfterBetting() {
        return potAfterBetting;
    }

    /**
     * Sets potAfterBetting
     * @param potAfterBetting pot to set
     */
    public void setPotAfterBetting(double potAfterBetting) {
        this.potAfterBetting = potAfterBetting;
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
     * isAllIn is ture if all players went all-in on this Street
     * (if just one player still has chips and didnt fold,
     * the condition is also met).
     * @return value of isAllIn field
     */
    public boolean isAllIn() {
        return isAllIn;
    }

//    public boolean isCheckRaised()

    /**
     * Sets isAllIn field
     * @param allIn value to set
     */
    public void setAllIn(boolean allIn) {
        isAllIn = allIn;
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
            return Math.abs(this.potAfterBetting - st.potAfterBetting) < 0.01 &&
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
        for (int i = 0; i < orderPos.size(); ++i) {
            PlayerInGame p = playersAfterBetting.get(orderPos.get(i));
            if (p != null) {
                orderedPlayers.add(p);
            }
        }

        DecimalFormat dcf = new DecimalFormat("##0.00");
        String dRep = dcf.format(potAfterBetting);
        return "(StreetDescription| Board: " + board +
                ", pot after betting: " + dRep.replace(',', '.') +
                ", Players after betting: " + orderedPlayers +
                ",\n Actions: " + allActions;
    }
}