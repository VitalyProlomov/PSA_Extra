package appinterface.controllers;

import pokerlibrary.analizer.GameAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import pokerlibrary.models.Game;
import pokerlibrary.models.UserProfileSet;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map;

public class ProfileController {
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
    }
}
