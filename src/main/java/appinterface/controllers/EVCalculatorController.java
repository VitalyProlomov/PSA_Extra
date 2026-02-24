package appinterface.controllers;

import pokerlibrary.analizer.CombinationAnalyzer;
import pokerlibrary.exceptions.IncorrectBoardException;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.exceptions.IncorrectHandException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import pokerlibrary.models.Board;
import pokerlibrary.models.Card;
import pokerlibrary.models.Hand;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EVCalculatorController {

    @FXML
    private AnchorPane anchorPane;


    @FXML
    private Button calculateEVButton;

    @FXML
    private TextField flopCardsTextField;

    @FXML
    private Label flopLabel;

    @FXML
    private CheckBox player1CheckBox;

    @FXML
    private ImageView player1ChooserImageView;

    @FXML
    private Label player1EVLabel;

    @FXML
    private TextField player1HandTextField;

    @FXML
    private CheckBox player2CheckBox;

    @FXML
    private ImageView player2ChooserImageView;

    @FXML
    private Label player2EVLabel;

    @FXML
    private TextField player2HandTextField;

    @FXML
    private CheckBox player3CheckBox;

    @FXML
    private ImageView player3ChooserImageView;

    @FXML
    private Label player3EVLabel;

    @FXML
    private TextField player3HandTextField;

    @FXML
    private CheckBox player4CheckBox;

    @FXML
    private ImageView player4ChooserImageView;

    @FXML
    private Label player4EVLabel;

    @FXML
    private TextField player4HandTextField;

    @FXML
    private CheckBox player5CheckBox;

    @FXML
    private ImageView player5ChooserImageView;

    @FXML
    private Label player5EVLabel;

    @FXML
    private TextField player5HandTextField;

    @FXML
    private TextField riverCardTextField;

    @FXML
    private Label riverLabel;

    @FXML
    private TextField turnCardTextField;

    @FXML
    private Label turnLabel;

    private static final int PLAYERS_AMOUNT = 5;

    private static final HashMap<Card.Suit, Color> suitColorMap = new HashMap<>();
    private static final HashMap<Card.Rank, Character> rankCharMap = new HashMap<>();

    private HashMap<Integer, Hand> hands = new HashMap<>();
    private ArrayList<Card> flopCards = new ArrayList<>();
    private Card turnCard = null;
    private Card riverCard = null;

    @FXML
    void initialize() {
        calculateEVButton.setOnMouseClicked(action -> calculateAndUpdateEv());

        turnCardTextField.setEditable(false);
        riverCardTextField.setEditable(false);

        fillMaps();

        for (int i = 1; i <= PLAYERS_AMOUNT; ++i) {
            TextField cardTextField = (TextField) anchorPane.lookup("#player" + i + "HandTextField");
            int finalI = i;
            cardTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                // order matters
                updateHandFromTextField(finalI);

                updateFlopTextField();
                updateTurnTextField();
                updateRiverTextField();
            });
        }

        TextField flopTextField = (TextField) anchorPane.lookup("#flopCardsTextField");
        flopTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // order matters
            updateFlopTextField();

            for (int i = 1; i <= PLAYERS_AMOUNT; ++i) {
                updateHandFromTextField(i);

            }
            updateTurnTextField();
            updateRiverTextField();
        });

        TextField turnTextField = (TextField) anchorPane.lookup("#turnCardTextField");
        turnTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // order matters
            updateTurnTextField();

            for (int i = 1; i <= PLAYERS_AMOUNT; ++i) {
                updateHandFromTextField(i);
            }
            updateFlopTextField();
            updateRiverTextField();
        });

        TextField riverTextField = (TextField) anchorPane.lookup("#riverCardTextField");
        riverTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // order matters
            updateRiverTextField();

            for (int i = 1; i <= PLAYERS_AMOUNT; ++i) {
                updateHandFromTextField(i);
            }
            updateFlopTextField();
            updateTurnTextField();
        });

    }

    public void updateHandFromTextField(int playerIndex) {
        TextField cardTextField = (TextField) anchorPane.lookup("#player" + playerIndex + "HandTextField");
        String cardReps = cardTextField.getText();

        try {
            if (cardReps.isEmpty()) {
                setToIncorrectHand(playerIndex);
                colorCorrect(playerIndex);
                updateHandsMap(playerIndex, null);
                return;
            }
            if (cardReps.length() != 4) {
                setToIncorrectHand(playerIndex);
                updateHandsMap(playerIndex, null);
                return;
            }
            String card1Rep = cardReps.substring(0, 2);
            String card2Rep = cardReps.substring(2, 4);
            Hand hand = new Hand(card1Rep, card2Rep);

            updateHandsMap(playerIndex, hand);

            if (!checkIfCompatibleWithHands(new ArrayList<>(hand.getCards()), playerIndex) ||
                    !checkIfCompatibleWithFlop(new ArrayList<>(hand.getCards())) ||
                    !checkIfCompatibleWithTurn(new ArrayList<>(hand.getCards())) ||
                    !checkIfCompatibleWithRiver(new ArrayList<>(hand.getCards()))) {
                setToIncorrectHand(playerIndex);
                return;
            }

            changeHandsCardsVisibility(playerIndex, hand, 1);
            colorCorrect(playerIndex);
            updateHandsMap(playerIndex, hand);
        } catch (IncorrectCardException | IncorrectHandException ex) {
            setToIncorrectHand(playerIndex);
            updateHandsMap(playerIndex, null);
        }
    }

    private void updateFlopTextField() {
        TextField cardTextField = (TextField) anchorPane.lookup("#flopCardsTextField");
        String cardReps = cardTextField.getText();

        try {
            if (cardReps.isEmpty()) {
                cardTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                changeFlopCardsVisibility(0);
                updateFlopCards(null);

                lockTurnTextField();
                lockRiverTextField();

                return;
            }


            if (cardReps.length() != 6) {
                cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                changeFlopCardsVisibility(0);
                updateFlopCards(null);
                return;
            }
            String card1Rep = cardReps.substring(0, 2);
            String card2Rep = cardReps.substring(2, 4);
            String card3Rep = cardReps.substring(4, 6);

            Card c1 = new Card(card1Rep);
            Card c2 = new Card(card2Rep);
            Card c3 = new Card(card3Rep);

            Board b = new Board(c1, c2, c3);
            updateFlopCards(b);


            if (!checkIfCompatibleWithHands(b.getCards(), -1) ||
                    !checkIfCompatibleWithTurn(b.getCards()) ||
                    !checkIfCompatibleWithRiver(b.getCards())) {
                cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                changeFlopCardsVisibility(0);
                return;
            }

            unlockTurnTextField();

            changeFlopCardsVisibility(1);
            cardTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
            setFlopCards(b);
            updateFlopCards(b);
        } catch (IncorrectCardException | IncorrectBoardException ex) {
            cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
            changeFlopCardsVisibility(0);
            updateFlopCards(null);
        }
    }

    public void updateTurnTextField() {
        TextField cardTextField = (TextField) anchorPane.lookup("#turnCardTextField");
        String cardReps = cardTextField.getText();

        try {
            if (cardReps.isEmpty()) {
                cardTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                changeTurnCardVisibility(0);
                updateTurnCard(null);

                lockRiverTextField();

                return;
            }
            if (cardReps.length() != 2) {
                cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                changeTurnCardVisibility(0);
                updateTurnCard(null);
                return;
            }

            String card1Rep = cardReps.substring(0, 2);
            Card c1 = new Card(card1Rep);

            ArrayList<Card> ar = new ArrayList<>();
            ar.add(turnCard);
            if (!checkIfCompatibleWithHands(ar, -1) ||
                    !checkIfCompatibleWithFlop(ar) ||
                    !checkIfCompatibleWithRiver(ar)) {
                cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                changeTurnCardVisibility(0);
                return;
            }

            unlockRiverTextField();

            changeTurnCardVisibility(1);
            cardTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
            setTurnCard(c1);
            updateTurnCard(c1);
        } catch (IncorrectCardException e) {
            cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
            changeTurnCardVisibility(0);
            updateTurnCard(null);
        }
    }

    private void updateRiverTextField() {
        TextField cardTextField = (TextField) anchorPane.lookup("#riverCardTextField");
        String cardReps = cardTextField.getText();

        try {
            if (cardReps.isEmpty()) {
                cardTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                changeRiverCardVisibility(0);
                updateRiverCard(null);
                return;
            }
            if (cardReps.length() != 2) {
                cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                changeRiverCardVisibility(0);
                updateRiverCard(null);
                return;
            }

            String card1Rep = cardReps.substring(0, 2);
            Card c1 = new Card(card1Rep);

            ArrayList<Card> ar = new ArrayList<>();
            ar.add(riverCard);
            if (!checkIfCompatibleWithHands(ar, -1) ||
                    !checkIfCompatibleWithFlop(ar) ||
                    !checkIfCompatibleWithTurn(ar)) {
                cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                changeRiverCardVisibility(0);
                return;
            }
            changeRiverCardVisibility(1);
            cardTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
            setRiverCard(c1);
            updateRiverCard(c1);
        } catch (IncorrectCardException e) {
            cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
            changeRiverCardVisibility(0);
            updateRiverCard(null);
        }
    }

    private void lockTurnTextField() {
        TextField cardTextField = (TextField) anchorPane.lookup("#turnCardTextField");
        cardTextField.setText("");
        cardTextField.setEditable(false);
        turnCard = null;
    }

    private void lockRiverTextField() {
        TextField cardTextField = (TextField) anchorPane.lookup("#riverCardTextField");
        cardTextField.setText("");
        cardTextField.setEditable(false);
        riverCard = null;
    }

    private void unlockTurnTextField() {
        TextField cardTextField = (TextField) anchorPane.lookup("#turnCardTextField");
        cardTextField.setEditable(true);
    }

    private void unlockRiverTextField() {
        TextField cardTextField = (TextField) anchorPane.lookup("#riverCardTextField");
        cardTextField.setEditable(true);
    }

    private boolean checkIfCompatibleWithHands(ArrayList<Card> cards, int excludedIndex) {
        for (int i = 1; i <= PLAYERS_AMOUNT; ++i) {
            Hand hToCheck = hands.get(i);
            if (hToCheck != null && i != excludedIndex) {
                ArrayList<Card> cardsToCheck = new ArrayList<>(cards);
                cardsToCheck.add(hToCheck.getCard1());
                cardsToCheck.add(hToCheck.getCard2());
                if (!CombinationAnalyzer.isBoardValid(cardsToCheck)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkIfCompatibleWithFlop(ArrayList<Card> cards) {
        if (flopCards.isEmpty()) {
            return true;
        }
        ArrayList<Card> cardsToCheck = new ArrayList<>(flopCards);
        cardsToCheck.addAll(cards);
        return CombinationAnalyzer.isBoardValid(cardsToCheck);
    }

    private boolean checkIfCompatibleWithTurn(ArrayList<Card> cards) {
        if (turnCard == null) {
            return true;
        }
        return !cards.contains(turnCard);
    }

    private boolean checkIfCompatibleWithRiver(ArrayList<Card> cards) {
        if (riverCard == null) {
            return true;
        }
        return !cards.contains(riverCard);
    }

    private void updateHandsMap(int playerIndex, Hand hand) {
        hands.put(playerIndex, hand);
    }

    private void updateFlopCards(Board b) {
        if (b == null) {
            flopCards = new ArrayList<>();
            return;
        }
        flopCards = new ArrayList<>(b.getCards());
    }

    private void updateTurnCard(Card card) {
        this.turnCard = card;
    }

    private void updateRiverCard(Card card) {
        this.riverCard = card;
    }

    private void setToIncorrectHand(int playerIndex) {
        colorIncorrect(playerIndex);
        changeHandsCardsVisibility(playerIndex, null, 0);

        for (int i = 1; i <= PLAYERS_AMOUNT; ++i) {
            Label evLabel = (Label) anchorPane.lookup("#player" + i + "EVLabel");
            evLabel.setText("");
        }
    }

    private void setToCorrectHand(int playerIndex, Hand h, Double evPercentages) {
        colorCorrect(playerIndex);
        changeHandsCardsVisibility(playerIndex, h, 1);

        String strPercentages = new DecimalFormat("#0.00").format(evPercentages);
        Label evLabel = (Label) anchorPane.lookup("#player" + playerIndex + "EVLabel");
        evLabel.setText(strPercentages);
    }

    private void colorIncorrect(int playerIndex) {
        TextField cardTextField = (TextField) anchorPane.lookup("#player" + playerIndex + "HandTextField");

        if (!cardTextField.getText().isEmpty()) {
            cardTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
        }
    }

    private void colorCorrect(int playerIndex) {
        TextField cardTextField = (TextField) anchorPane.lookup("#player" + playerIndex + "HandTextField");
        cardTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
    }

    private void changeHandsCardsVisibility(int playerIndex, Hand hand, double visibility) {
        Rectangle rightRectangle = (Rectangle) anchorPane.lookup("#player" + playerIndex + "RightCardRectangle");
        Rectangle leftRectangle = (Rectangle) anchorPane.lookup("#player" + playerIndex + "LeftCardRectangle");
        Label rightLabel = (Label) anchorPane.lookup("#player" + playerIndex + "RightCardLabel");
        Label leftLabel = (Label) anchorPane.lookup("#player" + playerIndex + "LeftCardLabel");

        if (Math.abs(visibility - 1) < 0.0005) {
            leftRectangle.setFill(suitColorMap.get(hand.getCard1().getSuit()));
            leftLabel.setText(String.valueOf(rankCharMap.get(hand.getCard1().getRank())));
            rightRectangle.setFill(suitColorMap.get(hand.getCard2().getSuit()));
            rightLabel.setText(String.valueOf(rankCharMap.get(hand.getCard2().getRank())));
        }

        leftLabel.setOpacity(visibility);
        rightLabel.setOpacity(visibility);
        leftRectangle.setOpacity(visibility);
        rightRectangle.setOpacity(visibility);
    }

    private void changeFlopCardsVisibility(double visibility) {
        Rectangle card1Rectangle = (Rectangle) anchorPane.lookup("#flopCard1Rectangle");
        Rectangle card2Rectangle = (Rectangle) anchorPane.lookup("#flopCard2Rectangle");
        Rectangle card3Rectangle = (Rectangle) anchorPane.lookup("#flopCard3Rectangle");

        Label card1Label = (Label) anchorPane.lookup("#flopCard1Label");
        Label card2Label = (Label) anchorPane.lookup("#flopCard2Label");
        Label card3Label = (Label) anchorPane.lookup("#flopCard3Label");

        card1Label.setOpacity(visibility);
        card2Label.setOpacity(visibility);
        card3Label.setOpacity(visibility);

        card1Rectangle.setOpacity(visibility);
        card2Rectangle.setOpacity(visibility);
        card3Rectangle.setOpacity(visibility);
    }

    private void changeTurnCardVisibility(double visibility) {
        Rectangle cardRectangle = (Rectangle) anchorPane.lookup("#turnCardRectangle");
        Label cardLabel = (Label) anchorPane.lookup("#turnCardLabel");

        cardLabel.setOpacity(visibility);
        cardRectangle.setOpacity(visibility);
    }

    private void changeRiverCardVisibility(double visibility) {
        Rectangle cardRectangle = (Rectangle) anchorPane.lookup("#riverCardRectangle");
        Label cardLabel = (Label) anchorPane.lookup("#riverCardLabel");

        cardLabel.setOpacity(visibility);
        cardRectangle.setOpacity(visibility);
    }


    private void setFlopCards(Board b) {
        for (int i = 1; i <= 3; ++i) {
            Card c = b.getCards().get(i - 1);
            Rectangle cardR = (Rectangle) anchorPane.lookup("#flopCard" + i + "Rectangle");
            Label cardL = (Label) anchorPane.lookup("#flopCard" + i + "Label");
            cardR.setFill(suitColorMap.get(c.getSuit()));
            cardL.setText(String.valueOf(rankCharMap.get(c.getRank())));
        }
    }

    private void setTurnCard(Card card) {
        Rectangle cardR = (Rectangle) anchorPane.lookup("#turnCardRectangle");
        Label cardL = (Label) anchorPane.lookup("#turnCardLabel");
        cardR.setFill(suitColorMap.get(card.getSuit()));
        cardL.setText(String.valueOf(rankCharMap.get(card.getRank())));
    }

    private void setRiverCard(Card card) {
        Rectangle cardR = (Rectangle) anchorPane.lookup("#riverCardRectangle");
        Label cardL = (Label) anchorPane.lookup("#riverCardLabel");
        cardR.setFill(suitColorMap.get(card.getSuit()));
        cardL.setText(String.valueOf(rankCharMap.get(card.getRank())));
    }


    public void calculateAndUpdateEv() {
        HashMap<Integer, Hand> allHands = new HashMap<>();
        for (int i = 1; i <= PLAYERS_AMOUNT; ++i) {
            TextField cardTextField = (TextField) anchorPane.lookup("#player" + i + "HandTextField");

            try {
                Hand h = new Hand(cardTextField.getText().substring(0, 2), cardTextField.getText().substring(2, 4));
                allHands.put(i, h);
            } catch (Exception ignored) {
            }
        }
        if (allHands.size() >= 2) {
            HashMap<Hand, Double> evMap = null;
            if (flopCards == null || flopCards.isEmpty()) {
                evMap = CombinationAnalyzer.countEVPreFlopMonteCarlo(new ArrayList<>(allHands.values()));
            } else {
                ArrayList<Card> cards = new ArrayList<>(flopCards);
                if (turnCard != null) {
                    cards.add(turnCard);
                } if (riverCard != null) {
                    cards.add(riverCard);
                }
                try {
                    evMap = CombinationAnalyzer.countEVPostFlop(new Board(cards), new ArrayList<>(allHands.values()));
                } catch (IncorrectBoardException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Board is invalid");
                    alert.show();
                }
            }
            if (evMap != null) {
                for (Integer i : allHands.keySet()) {
                    setToCorrectHand(i, allHands.get(i), evMap.get(allHands.get(i)) * 100);
                }
            }
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


    }
}
