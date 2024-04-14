package appinterface.controllers;

import analizer.GameAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import models.Game;
import models.UserProfileSet;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProfileController {
    private static final int SIZE = 13;

    @FXML
    private BorderPane borderPaneProfile;

    @FXML
    private Label fiveBetPotLabel;

    @FXML
    private Label fourBetPotLabel;

    @FXML
    private Label gamesAmountLabel;

    @FXML
    private Label playersAssignedAmountLabel;

    @FXML
    private Label potsPostFlopLabel;

    @FXML
    private Label profileId;

    @FXML
    private Label threebetPotLabel;

    private double heroWinLoss;

    private int gamesAmount;

    private HashSet<Game> games;

    @FXML
    void initialize() {

//        VBox box = new VBox();
//        box.setSpacing(1);
//        for (int i = 0; i < SIZE; ++i) {
//            HBox row = new HBox();
//            row.setId("row_" + i);
//            row.setSpacing(1);
//            box.getChildren().add(row);
//        }
//
//        borderPaneProfile.setRight(box);
//
//        for (int i = 0; i < SIZE; ++i) {
//            for (int y = 0; y < SIZE; ++y) {
//                Rectangle r = new Rectangle(40, 40);
//                r.setFill(Color.WHITE);
//                ((HBox) box.getChildren().get(i)).getChildren().add(r);
//            }
//        }
    }
    public void setInfo(Map<String, Game> games) {
        this.gamesAmount = games.size();

        int threeBetGamesAmount = 0;
        int potsPostFLopAmount = 0;
        int fourBetPotsAmount = 0;
        int fiveBetPotsAmount = 0;
        for (Game g : games.values()) {
            if (GameAnalyzer.isPot3Bet(g)) {
                ++threeBetGamesAmount;
            }
            if (GameAnalyzer.isPot4Bet(g)) {
                ++fourBetPotsAmount;
            }
            if (GameAnalyzer.isPot5PlusBet(g)) {
                ++fiveBetPotsAmount;
            }
            if (GameAnalyzer.isHeroPostFlop(g)) {
                ++potsPostFLopAmount;
            }
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserProfileSet userProfileSet = objectMapper.readValue(
                    new File("src/main/resources/serializedFiles/serializedUserProfiles.txt"),
                    UserProfileSet.class);
            playersAssignedAmountLabel.setText(playersAssignedAmountLabel.getText() + userProfileSet.getIdUserMap().size());
        } catch (IOException ex) {
            playersAssignedAmountLabel.setText(playersAssignedAmountLabel.getText() + "0");

        }

        gamesAmountLabel.setText(gamesAmountLabel.getText() + gamesAmount);
        threebetPotLabel.setText(threebetPotLabel.getText() + threeBetGamesAmount);
        fourBetPotLabel.setText(fourBetPotLabel.getText() + fourBetPotsAmount);
        fiveBetPotLabel.setText(fiveBetPotLabel.getText() + fiveBetPotsAmount);
        potsPostFlopLabel.setText(potsPostFlopLabel.getText() + potsPostFLopAmount);


        String balanceStr = new DecimalFormat("#0.00").format(heroWinLoss);
        balanceStr = balanceStr.replace(',', '.');

    }
}
