package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Class that describes the single action in game by a player.
 * Holds info about position, type of action (check, fold, bet, call, and others.)
 * and (where applicable) the amount of bet.
 */
public class Action {
    /**
     * Lists all the Action types
     */
    public enum ActionType {
        CHECK,
        BET,
        FOLD,
        CALL,
        RAISE,
        BLIND,
        ANTE,
        STRADDLE,
        MISSED_BLIND
    }

    private final ActionType actionType;
    private final double amount;
    private final String playerInGameId;
    private final double potBeforeAction;

    /**
     * Constructs new Action using given ActionType, playerInGame, amount contributed in pot,
     * and pot before this action.
     *
     * @param actionType      Type of action (ex: CHECK, FOLD, RAISE)
     * @param playerInGameId  ID of the player that is doing the action
     * @param amount          amount (in dollars), contributed to the pot by this action (will be 0 for FOLD and CHECK),
     *                        can NOT be <= 0 for CALL, BET and RAISE types
     * @param potBeforeAction amount of pot in dollars before this action
     * @throws IllegalArgumentException is thrown if amount is <= 0 for CALL, BET, or RAISE Action types
     *                                  or because pot before action is < 0.
     */
    @JsonCreator
    public Action(@JsonProperty("actionType") ActionType actionType,
                  @JsonProperty("playerInGameId") String playerInGameId,
                  @JsonProperty("amount") double amount,
                  @JsonProperty("potBeforeAction") double potBeforeAction) {
        this.actionType = actionType;
        this.playerInGameId = playerInGameId;
        if (actionType == ActionType.FOLD || actionType == ActionType.CHECK) {
            this.amount = 0;
        } else {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount, contributed to the pot can not be equal or less than 0 " +
                        "for any action type other than FOLD and CHECK");
            }

            this.amount = amount;
        }

        if (potBeforeAction < 0) {
            throw new IllegalArgumentException("Pot before action can not be less than 0.");
        }
        this.potBeforeAction = potBeforeAction;
    }


    /**
     * @return ActionType of this action
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * @return amount contributed to the pot by this action (in dollars)
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return amount in dollars in the pot before this action.
     */
    public double getPotBeforeAction() {
        return potBeforeAction;
    }

    /**
     * @return copy of the playerInGame in this Action
     */
    @JsonProperty("playerInGameId")
    public String getPlayerId() {
        return playerInGameId;
    }

    /**
     * Action is equal to another object only if it is another Action and the following
     * fields are equal: actionType, potBeforeBetting, amount and playerInGame
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == Action.class) {
            Action ac = (Action) obj;
            return this.actionType == ac.actionType &&
                    Math.abs(this.potBeforeAction - ac.potBeforeAction) < 0.01 &&
                    Math.abs(this.amount - ac.amount) < 0.01 &&
                    this.playerInGameId.equals(ac.playerInGameId);
        }
        return false;
    }


    /**
     * @return hash function of this Action, using actionType, amount,
     * playerInGameId and potBeforeAction fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(actionType, amount, playerInGameId, potBeforeAction);
    }

    /**
     * Returns string representation of this action as such
     * a) if (Action is CHECK or FOLD) => "(Action| Type: t, PotBeforeAction: p, pg)"
     * b) if (Action is CALL, BET, or RAISE) => "(Action| Type: t, Amount: a, Pot before action: p, Player Id: pg)",
     * where t = actionType, a = amount, p = potBeforeAction, pg = playerInGameId
     *
     * @return String representation of this Action
     */
    @Override
    public String toString() {
        String repr = "(Action| Type: " + actionType;
        if (actionType != ActionType.FOLD && actionType != ActionType.CHECK) {
            DecimalFormat dcf = new DecimalFormat("##0.00");
            repr += ", Amount: " + dcf.format(amount).replace(',', '.');
        }
        DecimalFormat dcf = new DecimalFormat("##0.00");
        String potRep = dcf.format(potBeforeAction);
        potRep = potRep.replace(",", ".");
        repr += ", Pot before action: " + potRep + ", Player Id: " + playerInGameId + ")";
        return repr;
    }
}
