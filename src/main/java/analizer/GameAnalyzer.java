package analizer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.Action;
import models.Game;
import models.PlayerInGame;
import models.StreetDescription;

import java.util.ArrayList;
import java.util.HashSet;

import static models.Action.ActionType.*;

public class GameAnalyzer {
    /**
     * @return if Hero did NOT fold on preflop,
     * false otherwise
     */
    @JsonIgnore
    public static boolean isHeroPostFlop(Game game) {
        return game.getPreFlop().getPlayersAfterBetting().contains(new PlayerInGame("Hero"));
    }


    /**
     * Checks, if preflop caller has check-raised a bet on the flop.
     * It might be a lead from another caller or a c-bet by raiser.
     *
     * @param game observed game
     * @return true, if such check-raised has occurred; false, otherwise
     */
    public static boolean isFlopCheckRaisedByCaller(Game game) {
        String pfrHash = getPFRHash(game);
        if (pfrHash == null) {
            return false;
        }
        if (game.getFlop() == null) {
            return false;
        }

        HashSet<String> checkedPlayers = new HashSet<String>();
        for (Action action : game.getFlop().getAllActions()) {
            if (!action.getPlayerId().equals(pfrHash) && action.getActionType().equals(CHECK)) {
                checkedPlayers.add(action.getPlayerId());
            }
            if (!action.getPlayerId().equals(pfrHash) && action.getActionType().equals(RAISE) &&
                    checkedPlayers.contains(action.getPlayerId())) {
                for (String hash : checkedPlayers) {
                    // Important to be sure, that the raise was not of the caller AFTER THE PF RAISER,
                    // because then it will be just a raise, not a check-raise
                    // (this check actually checks if currently observed player has checked or not).
                    if (hash.equals(action.getPlayerId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * counts amount of preflop raises (EXcludes blind raises).
     */
    private static int countPreFlopRaises(Game g) {
        StreetDescription pf = g.getPreFlop();
        if (pf == null) {
            throw new RuntimeException("Preflop was null");
        }
        int preFlopRaisesAmount = 0;
        for (int i = 0; i < pf.getAllActions().size(); ++i) {
            if (pf.getAllActions().get(i).getActionType().equals(RAISE)) {
                ++preFlopRaisesAmount;
            }
        }
        return preFlopRaisesAmount;
    }

    @JsonIgnore
    public static boolean isPotUnRaised(Game game) {
        return countPreFlopRaises(game) == 0;
    }

    /**
     * @return true, if the pot is single raised (only one raise took pace during preflop),
     * false otherwise
     */
    @JsonIgnore
    public static boolean isPotSingleRaised(Game game) {
        return countPreFlopRaises(game) == 1;
    }

    /**
     * @return true, if the pot is 3 bet (2 raises took pace during preflop),
     * false otherwise
     */
    @JsonIgnore
    public static boolean isPot3Bet(Game game) {
        return countPreFlopRaises(game) == 2;
    }

    /**
     * @return true, if the pot is 4 bet (3 raises took pace during preflop),
     * false otherwise
     */
    @JsonIgnore
    public static boolean isPot4Bet(Game game) {
        return countPreFlopRaises(game) == 3;
    }

    /**
     * @return true, if the pot is 5+ bet (4 or more raises took pace during preflop),
     * false otherwise
     */
    @JsonIgnore
    public static boolean isPot5PlusBet(Game game) {
        return countPreFlopRaises(game) >= 4;
    }

    /**
     * @return true if more than 2 players were active (didn't fold) after preflop,
     * false otherwise.
     */
    @JsonIgnore
    public static boolean isPotMultiWay(Game game) {
        return game.getPreFlop().getPlayersAfterBetting().size() > 2;
    }

    /**
     * @return true if everybody but winner of the game folded on preflop,
     * false otherwise
     */
    @JsonIgnore
    public static boolean isGameFoldedPreFlop(Game game) {
        return game.getPreFlop().getPlayersAfterBetting().size() == 1;
    }

    /**
     * @return true if Hero is in the game
     */
    @JsonIgnore
    public static boolean isHeroInGame(Game game) {
        return game.getPlayer("Hero") != null;
    }


    @JsonIgnore
    public static boolean didPlayerLimp(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        for (Action a : game.getPreFlop().getAllActions()) {
            if (a.getActionType().equals(RAISE) || a.getActionType().equals(BET)) {
                return false;
            }
            if (a.getPlayerId().equals("hash")) {
                if (!a.getActionType().equals(ANTE) && !a.getActionType().equals(BLIND) &&
                        !a.getActionType().equals(MISSED_BLIND)) {
                    return a.getActionType().equals(CALL) || a.getActionType().equals(CHECK);
                }
            }
        }
        return false;
    }

    /**
     * Checks if player with given hash has called a single raise in given (observed) game.
     * Player doesn't have to be post flop after this.
     * SRPC doesn't necessarily have to be his final role either.
     *
     * @param game observed game
     * @param hash hash of the given player
     * @return true, if player has called single raise; false, otherwise.
     */
    @JsonIgnore
    public static boolean didPlayerCallRFI(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        ArrayList<Action> actions = game.getPreFlop().getAllActions();
        int raisesCount = 0;
        for (Action act : actions) {
            if (act.getActionType() == RAISE) {
                ++raisesCount;
                if (raisesCount > 1) {
                    return false;
                }
            }
            if (act.getActionType() == CALL && act.getPlayerId().equals(hash) && raisesCount == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean didPlayerRFI(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        ArrayList<Action> actions = game.getPreFlop().getAllActions();
        for (Action a : actions) {
            // Looking for 1st raise
            if (a.getActionType().equals(RAISE)) {
                return a.getPlayerId().equals(hash);
            }
        }
        return false;
    }

    public static boolean didPlayerCall3Bet(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        ArrayList<Action> actions = game.getPreFlop().getAllActions();
        int raiseCounter = 0;
        for (Action a : actions) {
            if (a.getActionType().equals(RAISE)) {
                ++raiseCounter;
            }
            if (a.getActionType().equals(CALL) && a.getPlayerId().equals(hash) && raiseCounter == 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if player with given hash has 3-bet in given (observed) game.
     * Player doesn't have to be post flop after this.
     * 3bR doesn't necessarily have to be his final role either.
     * *Straddle doesn`t count as a raise.
     *
     * @param game observed game
     * @param hash hash of the given player
     * @return true, if player has 3-bet in observed game; false, otherwise.
     */
    @JsonIgnore
    public static boolean didPlayer3Bet(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        boolean was1RaiseFound = false;
        for (int i = 0; i < game.getPreFlop().getAllActions().size(); ++i) {
            if (game.getPreFlop().getAllActions().get(i).getActionType().equals(RAISE)) {
                if (was1RaiseFound) {
                    return game.getPreFlop().getAllActions().get(i).getPlayerId().equals(hash);
                }
                was1RaiseFound = true;
            }
        }
        return false;
    }

    public static boolean didPlayer4Bet(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        int raisesAmount = 0;
        for (int i = 0; i < game.getPreFlop().getAllActions().size(); ++i) {
            if (game.getPreFlop().getAllActions().get(i).getActionType().equals(RAISE)) {
                if (raisesAmount == 2) {
                    return game.getPreFlop().getAllActions().get(i).getPlayerId().equals(hash);
                }
                ++raisesAmount;
            }
        }
        return false;
    }

    public static boolean HasPlayerInitiallyFolded(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        for (Action a : game.getPreFlop().getAllActions()) {
            if (a.getPlayerId().equals(hash) && a.getActionType() != ANTE &&
                    a.getActionType() != MISSED_BLIND && a.getActionType() != BLIND) {
                return a.getActionType() == FOLD;
            }
        }
        return false;
    }


    /**
     * Checks if player with given hash is a limper lin given (observed) game.
     * The player must be post flop with his role for the role to be FINAL and
     * for the method to return true.
     * If player has checked in unraised pot (on BB or with a missed blind), method
     * will return TRUE.
     *
     * @param game observed (analyzed) game
     * @param hash players HASH CODE (not nickname)
     * @return true if player with given hash is limper as their final role in the given game
     * or has checked (not raised).
     */
    @JsonIgnore
    public static boolean isPlayerLimper(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        if (game.getGameId().equals("HD1567698525")) {
            System.out.println("A");
        }
        if (!isPotUnRaised(game)) {
            return false;
        }
        for (PlayerInGame p : game.getPreFlop().getPlayersAfterBetting()) {
            if (p.getId().equals(hash)) {
                return true;
            }
        }
        return false;
    }

    /**
     * In this method and all methods that are named isPlayer[ROLE]() we assume this role to
     * be final. So, if player called single raise, but then someone 3bet and player has
     * folded (or called), then he IS NOT a single raise pot caller (SRPC).
     * The player must be post flop with his role for the role to be final and
     * for the method to return true.
     *
     * @param game observed (analyzed) game
     * @param hash players HASH CODE (not nickname)
     * @return true if player with given hash has a single-raise-pot caller final role in the given game.
     */
    @JsonIgnore
    public static boolean isPlayerSRPC(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        if (!isPotSingleRaised(game)) {
            return false;
        }

        if (getPFRHash(game).equals(hash)) {
            return false;
        }

        boolean isPlayerPostFlop = false;
        for (PlayerInGame p : game.getPreFlop().getPlayersAfterBetting()) {
            if (p.getId().equals(hash)) {
                isPlayerPostFlop = true;
            }
        }

        return isPlayerPostFlop;
    }


    /**
     * Checks if the player (by the given HASH - in game identification) is the preflop raiser
     * in the give game (pot is not necessarily single raised).
     *
     * @param game observed game
     * @param hash players HASH CODE (not nickname)
     * @return true if given player in observed game is the preflop raiser
     */
    @JsonIgnore
    public static boolean isPlayerPFR(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }

        for (int i = game.getPreFlop().getAllActions().size() - 1; i >= 0; --i) {
            if (game.getPreFlop().getAllActions().get(i).getActionType().equals(RAISE)) {
                return game.getPreFlop().getAllActions().get(i).getPlayerId().equals(hash);
            }
        }
        return false;
    }

    /**
     * Checks if the player (by the given HASH - in game identification) is the
     * preflop singled raised pot raiser in the given game.
     *
     * @param game observed game
     * @param hash players HASH CODE (not nickname)
     * @return true if given player in observed game is the preflop single raised pot raiser
     */
    @JsonIgnore
    public static boolean isPlayerSRPR(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        if (!isPotSingleRaised(game)) {
            return false;
        }
        ArrayList<Action> allActions = game.getPreFlop().getAllActions();
        for (int i = allActions.size() - 1; i >= 0; --i) {
            if (allActions.get(i).getActionType() == RAISE) {
                return allActions.get(i).getPlayerId().equals(hash);
            }
        }
        return false;
    }


    /**
     * In this method and all methods that are named isPlayer[ROLE]() we assume this role to
     * be final. So, if player 3 bet, but then someone 4bet and player has
     * folded (or called), then he IS NOT a 3-bet pot raiser (3bR).
     * The player must be post flop with his role for the role to be final and
     * for the method to return true.
     *
     * @param game observed (analyzed) game
     * @return true if player with given hash has a 3-bet raiser final role in the given game.
     */
    @JsonIgnore
    public static boolean isPlayer3BetRaiser(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        if (!isPot3Bet(game)) {
            return false;
        }


        boolean was1stRaiseFound = false;
        ArrayList<Action> actions = game.getPreFlop().getAllActions();

        for (int i = 0; i < actions.size(); ++i) {
            if (actions.get(i).getActionType() == RAISE) {
                if (was1stRaiseFound) {
                    return actions.get(i).getPlayerId().equals(hash);
                }
                was1stRaiseFound = true;
            }
        }
        return false;
    }

    /**
     * In this method and all methods that are named isPlayer[ROLE]() we assume this role to
     * be final. So, if player has called a 3bet, but then someone 4bet and player has
     * folded (or called), then he IS NOT a 3-bet caller (3bC).
     * The player must be post flop with his role for the role to be final and
     * for the method to return true.
     *
     * @param game observed (analyzed) game
     * @return true if player with given hash has a 3-bet caller final role in the given game.
     */
    public static boolean isPlayer3BetCaller(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        if (!isPot3Bet(game)) {
            return false;
        }

        ArrayList<Action> actions = game.getPreFlop().getAllActions();
        int index = 0;
        int raiseCounter = 0;
        while (raiseCounter < 2) {
            if (actions.get(index).getActionType() == RAISE) {
                ++raiseCounter;
            }
            ++index;
        }

        while (index < actions.size()) {
            if (actions.get(index).getPlayerId().equals(hash)) {
                return actions.get(index).getActionType() == CALL;
            }
            ++index;
        }
        return false;
    }


    // CHECK IF VALID OR NOT
//    /**
//     * Checks if player with given hash has 4-bet in given (observed) game.
//     * Player doesn't have to be post flop after this.
//     * 3bR doesn't necessarily have to be his final role either.
//     * *Straddle doesn`t count as a raise.
//     *
//     * @param game observed game
//     * @param hash hash of the given player
//     * @return true, if player has 3-bet in observed game; false, otherwise.
//     */
    @JsonIgnore
    public static boolean isPlayer4BetRaiser(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        if (!isPot4Bet(game)) {
            return false;
        }
        if (!isPlayerPFR(game, hash)) {
            return false;
        }
        int raisesAmount = 0;
        for (int i = game.getPreFlop().getAllActions().size() - 1; i >= 0; --i) {
            if (game.getPreFlop().getAllActions().get(i).getActionType() == RAISE) {
                if (!game.getPreFlop().getAllActions().get(i).getPlayerId().equals(hash)) {
                    return false;
                }
            }
        }
        for (int i = 0; i < game.getPreFlop().getAllActions().size(); ++i) {
            if (game.getPreFlop().getAllActions().get(i).getActionType().equals(RAISE)) {
                if (raisesAmount == 2) {
                    return game.getPreFlop().getAllActions().get(i).getPlayerId().equals(hash);
                }
                ++raisesAmount;
            }
        }
        return false;
    }

    public static boolean isPlayer4BetCaller(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        if (!isPot4Bet(game)) {
            return false;
        }
        ArrayList<Action> allActions = game.getPreFlop().getAllActions();
        for (int i = allActions.size() - 1; i >= 0; --i) {
            if (allActions.get(i).getPlayerId().equals(hash)) {
                return allActions.get(i).getActionType() == CALL;
            }
        }
        return false;
    }


    @JsonIgnore
    public static boolean isPlayer5BetRaiser(Game game, String hash) {
        if (game == null || game.getPreFlop() == null || game.getPlayer(hash) == null) {
            return false;
        }
        int raisesAmount = 0;
        for (int i = 0; i < game.getPreFlop().getAllActions().size(); ++i) {
            if (game.getPreFlop().getAllActions().get(i).getActionType().equals(RAISE)) {
                if (raisesAmount == 3) {
                    return game.getPreFlop().getAllActions().get(i).getPlayerId().equals(hash);
                }
                ++raisesAmount;
            }
        }
        return false;
    }

    // TODO 5bC

    @JsonIgnore
    public static String getPFRHash(Game game) {
        if (isPotUnRaised(game)) {
            return null;
        }

        return game.getPreFlop().getLastAggressorHash();
    }

    @JsonIgnore
    public static boolean didCBetFlop(Game game, String hash) {
        if (game.getFlop() == null) {
            return false;
        }
        if (!isPlayerPFR(game, hash)) {
            return false;
        }
        int i = 0;
        while (i < game.getFlop().getAllActions().size() && game.getFlop().getAllActions().get(i).getActionType().equals(BET)) {
            ++i;
        }
        if (i >= game.getFlop().getAllActions().size()) {
            return false;
        }
        return game.getFlop().getAllActions().get(i).getPlayerId().equals(hash);
    }

    @JsonIgnore
    public static boolean didPFRCheckFlop(Game game, String hash) {
        if (game.getFlop() == null) {
            return false;
        }
        if (!isPlayerPFR(game, hash)) {
            return false;
        }
        int i = 0;
        while (i < game.getFlop().getAllActions().size() && !game.getFlop().getAllActions().get(i).getPlayerId().equals(hash)) {
            ++i;
        }
        return game.getFlop().getAllActions().get(i).getActionType().equals(CHECK);
    }


    @JsonIgnore
    public static boolean didCheckRaiseFlop(Game game, String hash) {
        if (game.getFlop() == null) {
            return false;
        }
        boolean wasChecked = false;
        for (int i = 0; i < game.getFlop().getAllActions().size(); ++i) {
            if (game.getFlop().getAllActions().get(i).getPlayerId().equals(hash)) {
                if (wasChecked) {
                    return game.getFlop().getAllActions().get(i).getActionType().equals(RAISE);
                }
                wasChecked = true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didCallCBetFlop(Game game, String hash) {
        if (game.getFlop() == null) {
            return false;
        }
        if (isPlayerPFR(game, hash)) {
            return false;
        }
        for (int i = 0; i < game.getFlop().getAllActions().size(); ++i) {
            if (game.getFlop().getAllActions().get(i).getPlayerId().equals(hash) &&
                    game.getFlop().getAllActions().get(i).getActionType().equals(CALL)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didCallerFoldFlop(Game game, String hash) {
        if (game.getFlop() == null) {
            return false;
        }
        if (isPlayerPFR(game, hash)) {
            return false;
        }
        for (int i = 0; i < game.getFlop().getAllActions().size(); ++i) {
            if (game.getFlop().getAllActions().get(i).getPlayerId().equals(hash) &&
                    game.getFlop().getAllActions().get(i).getActionType().equals(FOLD)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean wasFlopChecked(Game game) {
        for (Action a : game.getFlop().getAllActions()) {
            if (a.getActionType() != CHECK) {
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    public static boolean didRaiseFlop(Game game, String hash) {
        if (game.getFlop() == null) {
            return false;
        }
        for (int i = 0; i < game.getFlop().getAllActions().size(); ++i) {
            if (game.getFlop().getAllActions().get(i).getPlayerId().equals(hash) &&
                    game.getFlop().getAllActions().get(i).getActionType().equals(RAISE)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didCBetTurn(Game game, String hash) {
        if (game.getTurn() == null || game.getTurn().getAllActions().isEmpty()) {
            return false;
        }
        // turn c-bet occur without flop c-bet
//        if (!didCBetFlop(game, hash)) {
//            return false;
//        }
        for (int i = 0; i < game.getTurn().getAllActions().size(); ++i) {
            if (game.getTurn().getAllActions().get(i).getActionType().equals(BET) &&
                    game.getTurn().getAllActions().get(i).getPlayerId().equals(hash)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didPFRCheckTurn(Game game, String hash) {
        if (game.getTurn() == null || game.getTurn().getAllActions().isEmpty()) {
            return false;
        }
        if (!isPlayerPFR(game, hash)) {
            return false;
        }
        int i = 0;
        while (i < game.getTurn().getAllActions().size() && !game.getTurn().getAllActions().get(i).getPlayerId().equals(hash)) {
            ++i;
        }
        try {
            return game.getTurn().getAllActions().get(i).getActionType().equals(CHECK);
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @JsonIgnore
    public static boolean didCheckRaiseTurn(Game game, String hash) {
        if (game.getTurn() == null || game.getTurn().getAllActions().isEmpty()) {
            return false;
        }

        boolean wasChecked = false;
        for (int i = 0; i < game.getTurn().getAllActions().size(); ++i) {
            if (game.getTurn().getAllActions().get(i).getPlayerId().equals(hash)) {
                if (wasChecked) {
                    return game.getTurn().getAllActions().get(i).getActionType().equals(RAISE);
                }
                wasChecked = true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didCallCBetTurn(Game game, String hash) {
        if (game.getTurn() == null || game.getTurn().getAllActions().isEmpty()) {
            return false;
        }
        if (isPlayerPFR(game, hash)) {
            return false;
        }
        for (int i = 0; i < game.getTurn().getAllActions().size(); ++i) {
            if (game.getTurn().getAllActions().get(i).getPlayerId().equals(hash) &&
                    game.getTurn().getAllActions().get(i).getActionType().equals(CALL)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didRaiseTurn(Game game, String hash) {
        if (game.getTurn() == null || game.getTurn().getAllActions().isEmpty()) {
            return false;
        }
        for (int i = 0; i < game.getTurn().getAllActions().size(); ++i) {
            if (game.getTurn().getAllActions().get(i).getPlayerId().equals(hash) &&
                    game.getTurn().getAllActions().get(i).getActionType().equals(RAISE)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didCallerFoldTurn(Game game, String hash) {
        if (game.getTurn() == null || game.getTurn().getAllActions().isEmpty()) {
            return false;
        }
        if (isPlayerPFR(game, hash)) {
            return false;
        }
        for (int i = 0; i < game.getTurn().getAllActions().size(); ++i) {
            if (game.getTurn().getAllActions().get(i).getPlayerId().equals(hash) &&
                    game.getTurn().getAllActions().get(i).getActionType().equals(FOLD)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didCBetRiver(Game game, String hash) {
        if (game.getRiver() == null) {
            return false;
        }
        if (!didCBetTurn(game, hash)) {
            return false;
        }
        for (int i = 0; i < game.getRiver().getAllActions().size(); ++i) {
            if (game.getRiver().getAllActions().get(i).getActionType().equals(BET) &&
                    game.getRiver().getAllActions().get(i).getPlayerId().equals(hash)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didLeadRiver(Game game, String hash) {
        if (game.getRiver() == null) {
            return false;
        }
        if (didCBetTurn(game, hash)) {
            return false;
        }
        for (int i = 0; i < game.getRiver().getAllActions().size(); ++i) {
            if (game.getRiver().getAllActions().get(i).getActionType().equals(BET) &&
                    game.getRiver().getAllActions().get(i).getPlayerId().equals(hash)) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public static boolean didWinAtShowdownRiver(Game game, String hash) {
        if (game.getRiver() == null) {
            return false;
        }
        if (game.getRiver().getPlayersAfterBetting().contains(new PlayerInGame(hash))) {
            return (game.getWinners().containsKey(hash));
        }
        return false;
    }

    @JsonIgnore
    public static boolean didCallRiver(Game game, String hash) {
        if (game.getRiver() == null) {
            return false;
        }
        for (int i = 0; i < game.getRiver().getAllActions().size(); ++i) {
            if (game.getRiver().getAllActions().get(i).getPlayerId().equals(hash)) {
                return game.getRiver().getAllActions().get(i).getActionType().equals(CALL);
            }
        }
        return false;
    }
}
