package appinterface.controllers;

import exceptions.IncorrectCardException;
import exceptions.IncorrectHandException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import models.GamesSet;
import models.Hand;
import models.PositionType;
import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;

import static models.PositionType.*;

public class EnhancedStatsController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private CheckComboBox<String> heroActionCheckComboBox;

    @FXML
    private CheckComboBox<PositionType> heroPositionCheckComboBox;

    @FXML
    private CheckComboBox<String> heroRoleCheckComboBox;

    @FXML
    private Label infoLabel1;

    @FXML
    private Label infoLabel2;

    @FXML
    private Label infoLabel3;

    @FXML
    private Label infoLabel4;

    @FXML
    private Label infoLabel5;

    @FXML
    private Label infoLabel6;

    @FXML
    private Label infoLabel7;

    @FXML
    private PieChart pieChart1;

    @FXML
    private PieChart pieChart2;

    @FXML
    private ComboBox<String> playersPostFLopComboBox;

    @FXML
    private Button setSettingsButton;

    @FXML
    private ComboBox<String> streetComboBox;

    @FXML
    private Label streetLabel;

    @FXML

    private TextField handsTextField;

    private final String ranksString = "23456789TJQKA";

    private final int LABELS_AMOUNT = 7;

    private GamesSet gamesSet;

    private ArrayList<Hand> handsChosen = new ArrayList<>();


    @FXML
    void initialize() {
        heroRoleCheckComboBox.getItems().addAll(FXCollections.observableArrayList("limper", "srpC", "srpR", "3bC", "3bR", "4bC", "4bR", "5bC", "5bR", "folded preflop"));
        heroPositionCheckComboBox.getItems().addAll(FXCollections.observableArrayList(UTG, UTG_1, UTG_2, LJ, HJ, CO, BTN, SB, BB));
        heroActionCheckComboBox.getItems().addAll("limp", "RFI", "call RFI", "3BC", "3BR", "4bet");
        streetComboBox.getItems().addAll(FXCollections.observableArrayList("Preflop", "FLop", "Turn", "River"));
        playersPostFLopComboBox.getItems().addAll(FXCollections.observableArrayList("heads up", "multi way", "folded"));

        setSettingsButton.setOnMouseClicked(actionEvent -> onSetSettingsButtonClicked());

        handsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateHandFromTextField();
        });

        setToDefault();
    }

    public void setGamesSet(GamesSet gamesSet) {
        this.gamesSet = new GamesSet(gamesSet.getGames());
    }

    private void setToDefault() {
        for (int i = 1; i <= LABELS_AMOUNT; ++i) {
            Label l = (Label) this.anchorPane.lookup("#infoLabel" + i);
            l.setOpacity(0);
        }
        streetLabel.setOpacity(0);
    }


    private void updateHandFromTextField() {
        String cardReps = handsTextField.getText();
        if (cardReps.length() < 2 || cardReps.length() > 4) {
            handsTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
            handsChosen.clear();
            return;
        }

        try {
            if (cardReps.isEmpty()) {
                handsTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                return;
            }
            if (cardReps.length() == 4) {
                String card1Rep = cardReps.substring(0, 2);
                String card2Rep = cardReps.substring(2, 4);
                Hand hand = new Hand(card1Rep, card2Rep);
                handsChosen.add(hand);
                handsTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                return;
            }

            if (cardReps.length() >= 2) {
                String suits = "hcsd";
                if (ranksString.contains(cardReps.substring(0, 1)) && ranksString.contains(cardReps.substring(1, 2))) {
                    String rank1Str = cardReps.substring(0, 1);
                    String rank2Str = cardReps.substring(1, 2);
                    if (cardReps.length() == 2) {
                        for (int i = 0; i < suits.length(); ++i) {
                            for (int j = 0; j < suits.length(); ++j) {
                                if (i != j || !rank2Str.equals(rank1Str)) {
                                    handsChosen.add(new Hand(rank1Str + suits.charAt(i), rank2Str + suits.charAt(j)));
                                }
                            }
                        }
                        handsTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                        return;

                    }
                    if (cardReps.length() == 3) {
                        if (cardReps.charAt(2) == 's') {
                            if (rank1Str.equals(rank2Str)) {
                                handsTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                                handsChosen.clear();
                            } else {
                                for (int i = 0; i < suits.length(); ++i) {
                                    handsChosen.add(new Hand(rank1Str + suits.charAt(i), rank2Str + suits.charAt(i)));
                                }
                                handsTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                            }
                        } else if (cardReps.charAt(2) == 'o') {
                            for (int i = 0; i < suits.length(); ++i) {
                                for (int j = 0; j < suits.length(); ++j) {
                                    if (i != j) {
                                        handsChosen.add(new Hand(rank1Str + suits.charAt(i), rank2Str + suits.charAt(j)));
                                    }
                                }
                            }
                            handsTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                        } else {
                            handsTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                            handsChosen.clear();
                        }
                    }
                } else {
                    handsTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
                    handsChosen.clear();
                }
            }
        } catch (IncorrectCardException | IncorrectHandException ex) {
            handsTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
            handsChosen.clear();
        }
    }


    private void onSetSettingsButtonClicked() {
        String street = streetComboBox.getSelectionModel().getSelectedItem();
        if (street == null) {
            street = "Preflop";
        }
        if (street.equals("Preflop")) {
            initializePreflop();
        } else if (street.equals("Flop")) {
            initializeFlop();
        } else if (street.equals("Turn")) {
            initializeTurn();
        } else if (street.equals("River")) {
            initializeRiver();
        }
    }

    private void initializePreflop() {
        streetLabel.setText("Preflop");
        infoLabel1.setText("All Called: ");
        infoLabel2.setText("Raised: ");
        infoLabel3.setText("All folded: ");
        infoLabel4.setText("Hero won game: ");
        infoLabel5.setText("Hero lost game: ");

        streetLabel.setOpacity(1);
        for (int i = 1; i <= 5; ++i) {
            Label l = (Label) anchorPane.lookup("#infoLabel" + i);
            l.setOpacity(1);
        }

    }

    private void initializeFlop() {

    }

    private void initializeTurn() {

    }

    private void initializeRiver() {

    }
}
