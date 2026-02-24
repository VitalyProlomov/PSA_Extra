package appinterface.controllers;

import pokerlibrary.analizer.GameAnalyzer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import pokerlibrary.models.*;

import java.text.DecimalFormat;
import java.util.*;

import static pokerlibrary.models.Action.ActionType.*;
import static pokerlibrary.models.PositionType.*;

public class GameDisplay9MaxController {
    private static final int MAX_PLAYERS = 9;
    private static final HashMap<Card.Suit, Color> suitColorMap = new HashMap<>();
    private static final HashMap<Card.Rank, Character> rankCharMap = new HashMap<>();
    private static final HashMap<Action.ActionType, String> actionStringMap = new HashMap<>();
    private final HashMap<String, Integer> hashPlayerIndexMap = new HashMap<>();
    @FXML
    private Label dateLabel;

    @FXML
    private Label flopCard1Label;

    @FXML
    private Rectangle flopCard1Rectangle;

    @FXML
    private Label flopCard2Label;

    @FXML
    private Rectangle flopCard2Rectangle;

    @FXML
    private Label flopCard3Label;

    @FXML
    private Rectangle flopCard3Rectangle;

    @FXML
    private AnchorPane gameDisplayAnchorPane;

    @FXML
    private Label gameIdLabel;

    @FXML
    private Label heroActionLabel;

    @FXML
    private Label heroActionLabel7;

    @FXML
    private Label heroActionLabel8;

    @FXML
    private Label heroBalanceLabel;

    @FXML
    private ImageView heroButtonIcon;

    @FXML
    private Label heroLeftCardLabel;

    @FXML
    private Rectangle heroLeftCardRectangle;

    @FXML
    private Label heroPositionLabel;

    @FXML
    private Label heroRightCardLabel;

    @FXML
    private Rectangle heroRightCardRectangle;

    @FXML
    private Label heroWinLossLabel;

    @FXML
    private Polygon nextActionTriangle;

    @FXML
    private Label player1ActionLabel;

    @FXML
    private Label player1BalanceLabel;

    @FXML
    private ImageView player1ButtonIcon;

    @FXML
    private Label player1LeftCardLabel;

    @FXML
    private Rectangle player1LeftCardRectangle;

    @FXML
    private ImageView player1LeftCardShirt;

    @FXML
    private Label player1PositionLabel;

    @FXML
    private Label player1RightCardLabel;

    @FXML
    private Rectangle player1RightCardRectangle;

    @FXML
    private ImageView player1RightCardShirt;

    @FXML
    private Label player2ActionLabel;

    @FXML
    private Label player2BalanceLabel;

    @FXML
    private ImageView player2ButtonIcon;

    @FXML
    private Label player2LeftCardLabel;

    @FXML
    private Rectangle player2LeftCardRectangle;

    @FXML
    private ImageView player2LeftCardShirt;

    @FXML
    private Label player2PositionLabel;

    @FXML
    private Label player2RightCardLabel;

    @FXML
    private Rectangle player2RightCardRectangle;

    @FXML
    private ImageView player2RightCardShirt;

    @FXML
    private Label player3ActionLabel;

    @FXML
    private Label player3BalanceLabel;

    @FXML
    private ImageView player3ButtonIcon;

    @FXML
    private Label player3LeftCardLabel;

    @FXML
    private Rectangle player3LeftCardRectangle;

    @FXML
    private ImageView player3LeftCardShirt;

    @FXML
    private Label player3PositionLabel;

    @FXML
    private Label player3RightCardLabel;

    @FXML
    private Rectangle player3RightCardRectangle;

    @FXML
    private ImageView player3RightCardShirt;

    @FXML
    private Label player4ActionLabel;

    @FXML
    private Label player4BalanceLabel;

    @FXML
    private ImageView player4ButtonIcon;

    @FXML
    private Label player4LeftCardLabel;

    @FXML
    private Rectangle player4LeftCardRectangle;

    @FXML
    private ImageView player4LeftCardShirt;

    @FXML
    private Label player4PositionLabel;

    @FXML
    private Label player4RightCardLabel;

    @FXML
    private Rectangle player4RightCardRectangle;

    @FXML
    private ImageView player4RightCardShirt;

    @FXML
    private Label player5ActionLabel;

    @FXML
    private Label player5BalanceLabel;

    @FXML
    private ImageView player5ButtonIcon;

    @FXML
    private Label player5LeftCardLabel;

    @FXML
    private Rectangle player5LeftCardRectangle;

    @FXML
    private ImageView player5LeftCardShirt;

    @FXML
    private Label player5PositionLabel;

    @FXML
    private Label player5RightCardLabel;

    @FXML
    private Rectangle player5RightCardRectangle;

    @FXML
    private ImageView player5RightCardShirt;

    @FXML
    private Label player6ActionLabel;

    @FXML
    private Label player6BalanceLabel;

    @FXML
    private ImageView player6ButtonIcon;

    @FXML
    private Label player6LeftCardLabel;

    @FXML
    private Rectangle player6LeftCardRectangle;

    @FXML
    private ImageView player6LeftCardShirt;

    @FXML
    private Label player6PositionLabel;

    @FXML
    private Label player6RightCardLabel;

    @FXML
    private Rectangle player6RightCardRectangle;

    @FXML
    private ImageView player6RightCardShirt;

    @FXML
    private Label player7ActionLabel;

    @FXML
    private Label player7BalanceLabel;

    @FXML
    private ImageView player7ButtonIcon;

    @FXML
    private Label player7LeftCardLabel;

    @FXML
    private Rectangle player7LeftCardRectangle;

    @FXML
    private ImageView player7LeftCardShirt;

    @FXML
    private Label player7PositionLabel;

    @FXML
    private Label player7RightCardLabel;

    @FXML
    private Rectangle player7RightCardRectangle;

    @FXML
    private ImageView player7RightCardShirt;

    @FXML
    private Label player8ActionLabel;

    @FXML
    private Label player8BalanceLabel;

    @FXML
    private ImageView player8ButtonIcon;

    @FXML
    private Label player8LeftCardLabel;

    @FXML
    private Rectangle player8LeftCardRectangle;

    @FXML
    private ImageView player8LeftCardShirt;

    @FXML
    private Label player8PositionLabel;

    @FXML
    private Label player8RightCardLabel;

    @FXML
    private Rectangle player8RightCardRectangle;

    @FXML
    private ImageView player8RightCardShirt;

    @FXML
    private Label playersPostFlopLabel;

    @FXML
    private Label potLabel;

    @FXML
    private Label potTypeLabel;

    @FXML
    private ImageView restartImageView;

    @FXML
    private Label riverCardLabel;

    @FXML
    private Rectangle riverCardRectangle;

    @FXML
    private Ellipse tableEllipse;

    @FXML
    private Label totalPotLabel;

    @FXML
    private Label turnCardLabel;

    @FXML
    private Rectangle turnCardRectangle;

    private Game displayedGame;

    @FXML
    void initialize() {
        nextActionTriangle.setOnMouseClicked(action -> showNextAction());
        restartImageView.setOnMouseClicked(action -> resetAllStates());
        fillMaps();
        // helpImageView.setOnMouseClicked(action -> onHelpImageViewClicked());
    }

    private void setInitialParameters() {
        for (int i = 1; i <= 8; ++i) {
            Label actionLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "ActionLabel");
            Label leftCardLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardLabel");
            Label rightCardLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardLabel");
            Label hashLabel= (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "PositionLabel");
            Label balanceLabel = (Label)  this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "BalanceLabel");

            Rectangle leftCardRectangle = (Rectangle) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardRectangle");
            Rectangle rightCardRectangle = (Rectangle) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardRectangle");
            ImageView leftCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardShirt");
            ImageView rightCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardShirt");

            actionLabel.setText("");
            leftCardLabel.setVisible(false);
            leftCardRectangle.setVisible(false);
            leftCardShirtView.setVisible(false);
            hashLabel.setVisible(false);
            balanceLabel.setVisible(false);

            rightCardLabel.setVisible(false);
            rightCardRectangle.setVisible(false);
            rightCardShirtView.setVisible(false);
        }

        for (int i = 1; i <= 3; ++i) {
            this.gameDisplayAnchorPane.getScene().lookup("#flopCard" + i + "Label").setVisible(false);
            this.gameDisplayAnchorPane.getScene().lookup("#flopCard" + i + "Rectangle").setVisible(false);
        }

        turnCardLabel.setVisible(false);
        turnCardRectangle.setVisible(false);

        riverCardLabel.setVisible(false);
        riverCardRectangle.setVisible(false);

        for (int i : hashPlayerIndexMap.values()) {
            ImageView leftCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardShirt");
            ImageView rightCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardShirt");

            Label hashLabel= (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "PositionLabel");
            Label balanceLabel = (Label)  this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "BalanceLabel");

            leftCardShirtView.setVisible(true);
            rightCardShirtView.setVisible(true);
            hashLabel.setVisible(true);
            balanceLabel.setVisible(true);
        }

    }


    public void setGame(Game game) {
        this.displayedGame = game;
        updateTableInfo();
        resetAllStates();
        setInitialParameters();


        initializeLabels();
    }

    private void updateTableInfo() {
        PlayerInGame hero = displayedGame.getPlayer("Hero");
        this.heroLeftCardRectangle.setFill(suitColorMap.get(hero.getHand().getCard1().getSuit()));
        this.heroLeftCardLabel.setText(String.valueOf(rankCharMap.get(hero.getHand().getCard1().getRank())));

        this.heroRightCardRectangle.setFill(suitColorMap.get(hero.getHand().getCard2().getSuit()));
        this.heroRightCardLabel.setText(String.valueOf(rankCharMap.get(hero.getHand().getCard2().getRank())));

        this.heroPositionLabel.setText(hero.getPosition() + ": Hero");

        double initBalance = displayedGame.getInitialBalances().get("Hero");
        String balanceStr = new DecimalFormat("#0.00").format(initBalance);
        balanceStr = balanceStr.replace(',', '.');
        this.heroBalanceLabel.setText(balanceStr + "$");

        if (hero.getPosition().equals(BTN)) {
            heroButtonIcon.setVisible(true);
        }

//        ArrayList<PlayerInGame> orderedPlayers = new ArrayList<>();
        ArrayList<PositionType> orderedPositions = new ArrayList<>(List.of(
                BTN, SB, BB,
                UTG, UTG_1, UTG_2,
                LJ, HJ, CO));
        while (orderedPositions.size() > displayedGame.getPlayers().size()) {
            orderedPositions.remove(orderedPositions.size() - 1);
        }

        int heroPositionIndex = 0;

        HashMap<PositionType, PlayerInGame> posPlayers = displayedGame.getPosPlayersMap();

        // In parser if there are only 2 players present in the game, then the positions are BTN and BB (not SB)
        if (orderedPositions.size() == 2) {
            orderedPositions.remove(1);
            orderedPositions.add(BB);
        }
        for (int i = 0; i < orderedPositions.size(); ++i) {
//            orderedPlayers.add(posPlayers.get(orderedPositions.get(i)));
            if (posPlayers.get(orderedPositions.get(i)).getId().equals("Hero")) {
                heroPositionIndex = i;
            }
        }

        int heroSeatNumber = displayedGame.getPlayer("Hero").getSeatNumber();
        for (PlayerInGame p : displayedGame.getPlayers().values()) {

            if (p.getId().equals("Hero")) {
                continue;
            }
            int index = p.getSeatNumber() - heroSeatNumber;
            if (index < 0) {
                index += MAX_PLAYERS;
            }
//            PlayerInGame curPlayer = orderedPlayers.get((heroPositionIndex + index) % orderedPlayers.size());
            PlayerInGame curPlayer = p;
            hashPlayerIndexMap.put(curPlayer.getId(), index);
            Label labelToChange = ((Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + index + "BalanceLabel"));

            initBalance = displayedGame.getInitialBalances().get(curPlayer.getId());
            balanceStr = new DecimalFormat("#0.00").format(initBalance).replace(',', '.');
            labelToChange.setText(balanceStr + "$");

            labelToChange = ((Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + index + "PositionLabel"));
            String userName;
            if (curPlayer.getRef() == null) {
                userName = curPlayer.getId();
            } else {
                userName = curPlayer.getRef().getUserName();
            }

            labelToChange.setText(curPlayer.getPosition() + ": " + userName);
            if (curPlayer.getPosition().equals(BTN)) {
                this.gameDisplayAnchorPane.getScene().lookup("#player" + index + "ButtonIcon").setVisible(true);
            }

        }
    }

    private String curStreetStr = "preflop";
    private int curActionIndex = -1;
    private Label prevLabel = null;

    private void showNextAction() {
        ++curActionIndex;
        StreetDescription curStreet = null;
        if (curStreetStr.equals("preflop")) {
            curStreet = displayedGame.getPreFlop();
            if (curStreet.getAllActions().size() <= curActionIndex) {
                curStreetStr = "flop";
                curActionIndex = 0;

                if (displayedGame.getFlop() != null) {
                    curStreet = displayedGame.getFlop();
                    ArrayList<Card> flopCards = curStreet.getBoard().getCards();
                    flopCard1Rectangle.setFill(suitColorMap.get(flopCards.get(0).getSuit()));
                    flopCard2Rectangle.setFill(suitColorMap.get(flopCards.get(1).getSuit()));
                    flopCard3Rectangle.setFill(suitColorMap.get(flopCards.get(2).getSuit()));
                    flopCard1Label.setText(String.valueOf(rankCharMap.get(flopCards.get(0).getRank())));
                    flopCard2Label.setText(String.valueOf(rankCharMap.get(flopCards.get(1).getRank())));
                    flopCard3Label.setText(String.valueOf(rankCharMap.get(flopCards.get(2).getRank())));

                    flopCard1Rectangle.setVisible(true);
                    flopCard2Rectangle.setVisible(true);
                    flopCard3Rectangle.setVisible(true);
                    flopCard1Label.setVisible(true);
                    flopCard2Label.setVisible(true);
                    flopCard3Label.setVisible(true);

                    String balanceStr = new DecimalFormat("#0.00").format(displayedGame.getPreFlop().getPotAfterBetting()).replace(',', '.');
                    potLabel.setText("POT: " + balanceStr + "$");

                    prevLabel.setText("");
                    curActionIndex = -1;
                    return;
                }
            }

        }

        if (curStreetStr.equals("flop")) {
            curStreet = displayedGame.getFlop();
            if (curStreet == null) {
                String balanceStr = new DecimalFormat("#0.00").format(displayedGame.getPreFlop().getPotAfterBetting()).replace(',', '.');
                potLabel.setText("POT: " + balanceStr + "$");

                if (displayedGame.getPreFlop().getPlayersAfterBetting().size() > 1) {
                    displayShownCards(displayedGame.getPreFlop());
                }
                return;
            } else if (curStreet.getAllActions().size() <= curActionIndex) {
                curStreetStr = "turn";
                curActionIndex = 0;

                if (displayedGame.getTurn() != null) {
                    curStreet = displayedGame.getTurn();
                    ArrayList<Card> turnCards = curStreet.getBoard().getCards();

                    turnCardRectangle.setFill(suitColorMap.get(turnCards.get(3).getSuit()));
                    turnCardLabel.setText(String.valueOf(rankCharMap.get(turnCards.get(3).getRank())));

                    turnCardRectangle.setVisible(true);
                    turnCardLabel.setVisible(true);

                    String balanceStr = new DecimalFormat("#0.00").format(displayedGame.getFlop().getPotAfterBetting()).replace(',', '.');
                    potLabel.setText("POT: " + balanceStr + "$");

                    prevLabel.setText("");
                    curActionIndex = -1;
                    return;
                }
            }

        }
        if (curStreetStr.equals("turn")) {
            curStreet = displayedGame.getTurn();
            if (curStreet == null) {
                String balanceStr = new DecimalFormat("#0.00").format(displayedGame.getFlop().getPotAfterBetting()).replace(',', '.');
                potLabel.setText("POT: " + balanceStr + "$");

                if (displayedGame.getFlop().getPlayersAfterBetting().size() > 1) {
                    displayShownCards(displayedGame.getFlop());
                }
                return;
            } else if (curStreet.getAllActions().size() <= curActionIndex) {
                curStreetStr = "river";
                curActionIndex = 0;
                if (displayedGame.getRiver() != null) {
                    curStreet = displayedGame.getRiver();
                    ArrayList<Card> riverCards = curStreet.getBoard().getCards();

                    riverCardRectangle.setFill(suitColorMap.get(riverCards.get(4).getSuit()));
                    riverCardLabel.setText(String.valueOf(rankCharMap.get(riverCards.get(4).getRank())));

                    riverCardRectangle.setVisible(true);
                    riverCardLabel.setVisible(true);

                    String balanceStr = new DecimalFormat("#0.00").format(displayedGame.getRiver().getPotAfterBetting()).replace(',', '.');
                    potLabel.setText("POT: " + balanceStr + "$");

                    prevLabel.setText("");
                    curActionIndex = -1;
                    return;
                }
            }
        }

        if (curStreetStr.equals("river")) {
            curStreet = displayedGame.getRiver();
            if (curStreet == null) {
                String balanceStr = new DecimalFormat("#0.00").format(displayedGame.getTurn().getPotAfterBetting()).replace(',', '.');
                potLabel.setText("POT: " + balanceStr + "$");

                if (displayedGame.getTurn().getPlayersAfterBetting().size() > 1) {
                    displayShownCards(displayedGame.getTurn());
                }
                return;
            }
            if (curStreet.getAllActions().size() <= curActionIndex) {
                if (curStreet.getPlayersAfterBetting().size() > 1) {
                    displayShownCards(curStreet);
                }
                return;
            }

        }

        Action nextAction = curStreet.getAllActions().get(curActionIndex);
        Label curActionLabel;
        if (nextAction.getPlayerId().equals("Hero")) {
            curActionLabel = heroActionLabel;
        } else {
            curActionLabel = (Label) gameDisplayAnchorPane.getScene().lookup(
                    "#player" + hashPlayerIndexMap.get(nextAction.getPlayerId()) + "ActionLabel"
            );
        }

        if (prevLabel != null) {
            prevLabel.setText("");
//            prevLabel.setText(prevLabel.getText().split(" ")[prevLabel.getText().split(" ").length - 1]);
        }
        HashSet<Action.ActionType> nonBetTypes = new HashSet<>(List.of(CALL, FOLD, CHECK));

        curActionLabel.setText(actionStringMap.get(nextAction.getActionType()));

        if (!nonBetTypes.contains(nextAction.getActionType())) {
            String balanceStr = new DecimalFormat("#0.00").format(nextAction.getAmount()).replace(',', '.');
            curActionLabel.setText(curActionLabel.getText() + " " + balanceStr + "$");
        }
        if (nextAction.getActionType().equals(FOLD)) {
            if (nextAction.getPlayerId().equals("Hero")) {
                heroLeftCardRectangle.opacityProperty().set(0.3);
                heroRightCardRectangle.opacityProperty().set(0.3);
            } else {
                int index = hashPlayerIndexMap.get(nextAction.getPlayerId());
//                gameDisplayAnchorPane.getScene().lookup("#player" + index + "LeftCardShirt").setVisible(false);
//                gameDisplayAnchorPane.getScene().lookup("#player" + index + "RightCardShirt").setVisible(false);

                gameDisplayAnchorPane.getScene().lookup("#player" + index + "LeftCardShirt").setOpacity(0.1);
                gameDisplayAnchorPane.getScene().lookup("#player" + index + "RightCardShirt").setOpacity(0.1);

            }
        }

        prevLabel = curActionLabel;
    }

    private String formateDate(Date date) {
        String[] s = date.toString().split(" ");
        return s[1] + " " + s[2] + " " + s[5];
    }

    private void initializeLabels() {
        this.gameIdLabel.setText(gameIdLabel.getText() + displayedGame.getGameId());
        this.dateLabel.setText(dateLabel.getText() + formateDate(displayedGame.getDate()));

        String strRep = new DecimalFormat("#0.00").format(displayedGame.getHeroWinloss()).replace(',', '.');
//        System.out.println(displayedGame.getGameId() + ": " + displayedGame.getHeroWinloss());
//        System.out.println(displayedGame.getHeroWinloss());
        this.heroWinLossLabel.setText(heroWinLossLabel.getText() + strRep + "$");


        String type = "undefined...";
        if (GameAnalyzer.isPotUnRaised(displayedGame)) {
            type = "unraised";
        } else if (GameAnalyzer.isPotSingleRaised(displayedGame)) {
            type = "single raised";
        } else if (GameAnalyzer.isPot3Bet(displayedGame)) {
            type = "3bet";
        } else if (GameAnalyzer.isPot4Bet(displayedGame)) {
            type = "4bet";
        } else if (GameAnalyzer.isPot5PlusBet(displayedGame)) {
            type = "5+ bet";
        }
        this.potTypeLabel.setText(potTypeLabel.getText() + type);

        this.playersPostFlopLabel.setText(playersPostFlopLabel.getText() + displayedGame.getPreFlop().getPlayersAfterBetting().size());

        strRep = new DecimalFormat("#0.00").format(displayedGame.getFinalPot()).replace(',', '.');
        this.totalPotLabel.setText(totalPotLabel.getText() + strRep);
    }

    private void displayShownCards(StreetDescription curStreet) {
        for (PlayerInGame p : curStreet.getPlayersAfterBetting()) {
            if (p.getId().equals("Hero")) {
                continue;
            }
            int curIndex = hashPlayerIndexMap.get(p.getId());
            PlayerInGame curPlayer = displayedGame.getPlayer(p.getId());
            Label leftLabel = (Label) gameDisplayAnchorPane.getScene().lookup("#player" + curIndex + "LeftCardLabel");
            leftLabel.setText(String.valueOf(rankCharMap.get(curPlayer.getHand().getCard1().getRank())));

            Label rightLabel = (Label) gameDisplayAnchorPane.getScene().lookup("#player" + curIndex + "RightCardLabel");
            rightLabel.setText(String.valueOf(rankCharMap.get(curPlayer.getHand().getCard2().getRank())));

            Rectangle leftRectangle = (Rectangle) gameDisplayAnchorPane.getScene().lookup("#player" + curIndex + "LeftCardRectangle");
            leftRectangle.setFill(suitColorMap.get(curPlayer.getHand().getCard1().getSuit()));

            Rectangle rightRectangle = (Rectangle) gameDisplayAnchorPane.getScene().lookup("#player" + curIndex + "RightCardRectangle");
            rightRectangle.setFill(suitColorMap.get(curPlayer.getHand().getCard2().getSuit()));

            leftLabel.setVisible(true);
            rightLabel.setVisible(true);

            leftRectangle.setVisible(true);
            rightRectangle.setVisible(true);

            ImageView leftCardShirt = (ImageView) gameDisplayAnchorPane.getScene().lookup("#player" + curIndex + "LeftCardShirt");
            ImageView rightCardShirt = (ImageView) gameDisplayAnchorPane.getScene().lookup("#player" + curIndex + "RightCardShirt");

            leftCardShirt.setOpacity(0);
            rightCardShirt.setOpacity(0);

        }
    }

    private void fillMaps() {
        suitColorMap.put(Card.Suit.SPADES, Color.rgb(53, 56, 59));
        suitColorMap.put(Card.Suit.HEARTS, Color.rgb(196, 30, 30));
        suitColorMap.put(Card.Suit.DIAMONDS, Color.rgb(30, 144, 255));
        suitColorMap.put(Card.Suit.CLUBS, Color.rgb(9, 183, 0));

        String allStrings = "23456789TJQKA";
        ArrayList<Card.Rank> ranks = new ArrayList<>(List.of(
                Card.Rank.TWO, Card.Rank.THREE, Card.Rank.FOUR,
                Card.Rank.FIVE, Card.Rank.SIX, Card.Rank.SEVEN,
                Card.Rank.EIGHT, Card.Rank.NINE, Card.Rank.TEN,
                Card.Rank.JACK, Card.Rank.QUEEN, Card.Rank.KING,
                Card.Rank.ACE
        ));
        for (int i = 0; i < allStrings.length(); ++i) {
            rankCharMap.put(ranks.get(i), allStrings.charAt(i));
        }

        actionStringMap.put(CHECK, "Check");
        actionStringMap.put(BET, "Bet");
        actionStringMap.put(FOLD, "Fold");
        actionStringMap.put(CALL, "Call");
        actionStringMap.put(RAISE, "Raise");
        actionStringMap.put(BLIND, "Blind");
        actionStringMap.put(ANTE, "Ante");
        actionStringMap.put(STRADDLE, "Straddle");
        actionStringMap.put(MISSED_BLIND, "Missed Blind");
    }

    private void resetAllStates() {
        curStreetStr = "preflop";
        curActionIndex = -1;
        prevLabel = null;

        potLabel.setText("POT: ");

        for (int i = 1; i <= 8; ++i) {
            Label actionLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "ActionLabel");
            Label leftCardLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardLabel");
            Label rightCardLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardLabel");
            Rectangle leftCardRectangle = (Rectangle) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardRectangle");
            Rectangle rightCardRectangle = (Rectangle) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardRectangle");
            ImageView leftCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardShirt");
            ImageView rightCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardShirt");
            actionLabel.setText("");

            leftCardLabel.setVisible(false);
            leftCardRectangle.setVisible(false);
            rightCardLabel.setVisible(false);
            rightCardRectangle.setVisible(false);
            leftCardShirtView.setVisible(false);
            rightCardShirtView.setVisible(false);
        }

        for (int i : hashPlayerIndexMap.values()) {
            Label actionLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "ActionLabel");
            Label leftCardLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardLabel");
            Label rightCardLabel = (Label) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardLabel");
            Rectangle leftCardRectangle = (Rectangle) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardRectangle");
            Rectangle rightCardRectangle = (Rectangle) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardRectangle");
            ImageView leftCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "LeftCardShirt");
            ImageView rightCardShirtView = (ImageView) this.gameDisplayAnchorPane.getScene().lookup("#player" + i + "RightCardShirt");

            actionLabel.setText("");
            leftCardLabel.setVisible(false);
            leftCardRectangle.setVisible(false);
            rightCardLabel.setVisible(false);
            rightCardRectangle.setVisible(false);
            leftCardShirtView.setOpacity(1);
            rightCardShirtView.setOpacity(1);
            leftCardShirtView.setVisible(true);
            rightCardShirtView.setVisible(true);

        }

        heroLeftCardRectangle.setOpacity(1);
        heroRightCardRectangle.setOpacity(1);
        heroActionLabel.setText("");

        flopCard1Label.setVisible(false);
        flopCard1Rectangle.setVisible(false);
        flopCard2Label.setVisible(false);
        flopCard2Rectangle.setVisible(false);
        flopCard3Label.setVisible(false);
        flopCard3Rectangle.setVisible(false);

        turnCardRectangle.setVisible(false);
        turnCardLabel.setVisible(false);

        riverCardRectangle.setVisible(false);
        riverCardLabel.setVisible(false);
    }


}
