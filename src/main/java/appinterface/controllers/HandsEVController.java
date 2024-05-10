package appinterface.controllers;

import analizer.CombinationAnalyzer;
import appinterface.PSAApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HandsEVController {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button setHandsButton;

    @FXML
    private Label shownHandCardsLabel;

    @FXML
    private Rectangle shownHandRectangle;

    @FXML
    private Label shownHandTotalHandsLabel;

    @FXML
    private Label shownHandWinlossLabel;

    @FXML
    private Label totalHandsLabel;

    @FXML
    private Label totalWinlossLabel;

    private static int RANKS_AMOUNT = 13;

    private static Paint negativePaint = Color.rgb(180, 70, 70);

    private static Paint positivePaint = Color.rgb(66, 231, 88);

    private static Paint neutralPaint = Color.rgb(121, 125, 129);

    private GamesSet gamesSet;

    @FXML
    void initialize() throws IOException {
        int curX = 0;
        int curY = 0;
        for (int i = 0; i < RANKS_AMOUNT; ++i) {
            curY = 0;
            for (int j = 0; j < RANKS_AMOUNT; ++j) {
                Label handLabel = new Label();
                handLabel.setId("handLabel" + i + "_" + j);
                handLabel.setLayoutX(curX);
                handLabel.setLayoutY(curY + 5);
                handLabel.setPrefWidth(40);
                handLabel.mouseTransparentProperty().set(true);
                String ranks = "23456789TJQKA";
                if (i < j) {
                    handLabel.setText(ranks.charAt(RANKS_AMOUNT - 1 - i) +
                            Character.toString(ranks.charAt(RANKS_AMOUNT - 1 - j)) + "o");
                } else if (i > j) {
                    handLabel.setText(ranks.charAt(RANKS_AMOUNT - 1 - j) +
                            Character.toString(ranks.charAt(RANKS_AMOUNT - 1 - i)) + "s");
                } else {
                    handLabel.setText(ranks.charAt(RANKS_AMOUNT - 1 - i) +
                            Character.toString(ranks.charAt(RANKS_AMOUNT - 1 - j)));
                }

                handLabel.setAlignment(Pos.CENTER);
                handLabel.setFont(Font.font(10));


                Label evLabel = new Label();
                evLabel.setId("evLabel" + i + "_" + j);
                evLabel.setLayoutX(curX);
                evLabel.setLayoutY(curY + 25);
                evLabel.setPrefWidth(40);
                evLabel.setAlignment(Pos.CENTER);
                evLabel.setFont(Font.font(10));
                evLabel.setMouseTransparent(true);


                Rectangle r = new Rectangle();
                r.setId("rectangle" + i + "_" + j);
                r.setX(curX);
                r.setY(curY);
                r.setHeight(40);
                r.setWidth(40);

                r.setFill(positivePaint);
                r.setStrokeWidth(1);
                r.setStroke(Color.BLACK);
                if (i == j) {
//                    r.setStroke(Color.WHITE);
                    r.setStrokeWidth(2.5);
                }
                r.getProperties().put("handsPlayed", 0);
                r.setOnMouseClicked(event -> {
                    int amount = (Integer) r.getProperties().get("handsPlayed");
                    shownHandTotalHandsLabel.setText("Total hands: " + amount);
                    shownHandWinlossLabel.setText("Winloss: " + evLabel.getText());
                    shownHandRectangle.setFill(r.getFill());
                    shownHandCardsLabel.setText(handLabel.getText());
                });

                initializeSetGamesButton();


                anchorPane.getChildren().add(r);
                anchorPane.getChildren().add(handLabel);
                anchorPane.getChildren().add(evLabel);
                curY += 45;
            }
            curX += 45;
        }
    }

    public void setGamesSet(GamesSet gamesSet) {
        this.gamesSet = new GamesSet(gamesSet.getGames());
        setGamesAndUpdateTable(new ArrayList<>(gamesSet.getGames().values()));
    }

    private void initializeSetGamesButton() {
        setHandsButton.setOnMouseClicked(actionEvent -> onSearchFiltersButtonClick());
    }

    @FXML
    void onSearchFiltersButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/filterSearchView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            FilterSearchController controller = loader.getController();
            stage.setOnHiding(event -> {
                if (controller.getGamesAfterFilter() != null) {
                    setGamesAndUpdateTable(new ArrayList<>(controller.getGamesAfterFilter()));
                }
            });
            if (gamesSet != null) {
                controller.setUnfilteredGames(new HashSet<>(gamesSet.getGames().values()));
                stage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("There are no games downloaded, ");
                alert.show();
            }
//        filterSearchController.searchFilteredGames()
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open filter search");
            alert.show();
        }

    }


    public void setGamesAndUpdateTable(ArrayList<Game> games) {
        HashMap<Hand, Double> values = countHandsEV(games);
        ArrayList<ArrayList<Double>> evChart = new ArrayList<>();
        ArrayList<ArrayList<Integer>> handsPlayedAmount = new ArrayList<>();
        for (int i = 0; i < RANKS_AMOUNT; ++i) {
            evChart.add(new ArrayList<>());
            handsPlayedAmount.add(new ArrayList<>());
            for (int j = 0; j < RANKS_AMOUNT; ++j) {
                evChart.get(i).add(0.0);
                handsPlayedAmount.get(i).add(0);
                Rectangle r = (Rectangle) this.anchorPane.getScene().lookup(
                        "#rectangle" + i + "_" + j);
                r.getProperties().put("handsPlayed", 0);
            }
        }

        double totalEV = 0.0;
        int totalHandsAmount = 0;
        for (Hand h : values.keySet()) {
            int horizontalShift = 0;
            int verticalShift = 0;
            if (h.getCard1().getSuit().equals(h.getCard2().getSuit())) {
                verticalShift += Card.Rank.ACE.value - h.getCard1().getRank().value;
                horizontalShift += Card.Rank.ACE.value - h.getCard2().getRank().value;
            } else {
                horizontalShift += Card.Rank.ACE.value - h.getCard1().getRank().value;
                verticalShift += Card.Rank.ACE.value - h.getCard2().getRank().value;
            }
            evChart.get(horizontalShift).set(verticalShift,
                    evChart.get(horizontalShift).get(verticalShift) + values.get(h));
            totalEV += values.get(h);
            ++totalHandsAmount;
            Rectangle r = (Rectangle) this.anchorPane.getScene().lookup(
                    "#rectangle" + horizontalShift + "_" + verticalShift);
            r.getProperties().put("handsPlayed", (Integer) r.getProperties().get("handsPlayed") + 1);
        }
        for (int horizontalShift = 0; horizontalShift < RANKS_AMOUNT; ++horizontalShift) {
            for (int verticalShift = 0; verticalShift < RANKS_AMOUNT; ++verticalShift) {
                Double value = evChart.get(horizontalShift).get(verticalShift);
                Label l = (Label) this.anchorPane.getScene().lookup(
                        "#evLabel" + horizontalShift + "_" + verticalShift);

                String formattedDouble = new DecimalFormat("#0.00").format(value);

                formattedDouble += "$";
                l.setText(formattedDouble);

                Rectangle r = (Rectangle) this.anchorPane.getScene().lookup(
                        "#rectangle" + horizontalShift + "_" + verticalShift);
                if (Math.abs(value - 0) < 0.000005) {
                    r.setFill(neutralPaint);
                } else if (value > 0) {
                    r.setFill(positivePaint);
                } else if (value < 0) {
                    r.setFill(negativePaint);
                } else {
                    throw new RuntimeException("value was not detected..");
                }
            }
        }

        totalHandsLabel.setText("Total hands: " + totalHandsAmount);
        String formattedDouble = new DecimalFormat("#0.00").format(totalEV);
        totalWinlossLabel.setText("Total EV: " + formattedDouble + "$");
    }

    public HashMap<Hand, Double> countHandsEV(List<Game> games) {
        HashMap<Hand, Double> allHandsEv = new HashMap<>();
        for (Game g : games) {
            Hand h = g.getPlayer("Hero").getHand();


            PlayerInGame hero = g.getPlayer("Hero");
            if (g.getPreFlop().isAllIn() && g.getPreFlop().getPlayersAfterBetting().contains(hero) ||
                    g.getFlop() != null && g.getFlop().isAllIn() && g.getFlop().getPlayersAfterBetting().contains(hero) ||
                    g.getTurn() != null && g.getTurn().isAllIn() && g.getTurn().getPlayersAfterBetting().contains(hero) ||
                    g.getRiver() != null && g.getRiver().isAllIn() && g.getRiver().getPlayersAfterBetting().contains(hero)) {
                try {
                    allHandsEv.merge(g.getPlayer("Hero").getHand(), CombinationAnalyzer.countMoneyEv(g).get("Hero"), Double::sum);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                allHandsEv.merge(g.getPlayer("Hero").getHand(), g.getHeroWinloss(), Double::sum);
//                System.out.println(g.getPlayer("Hero").getHand() + ": " + g.getHeroWinloss());
            }
        }

        return allHandsEv;
    }
}

