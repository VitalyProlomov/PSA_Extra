package appinterface.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import models.*;

import java.util.HashSet;

import static models.PositionType.*;

public class AssignNewPlayerController {
    @FXML
    private Button assignButton;

    @FXML
    private TextField gameIdTextField;

    @FXML
    private TextField hashTextField;

    @FXML
    private ChoiceBox<PositionType> positionChoiceBox;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label warningLabel;

    private GamesSet gamesSet;

    private HashSet<UserProfile> newlyAssignedUsers = new HashSet<>();

    public HashSet<UserProfile> getNewlyAssignedUsers() {
        return newlyAssignedUsers;
    }

    public void setGamesSet(GamesSet gamesSet) {
        this.gamesSet = gamesSet;
    }


    private void onAssignButtonClicked() {
        String userName = usernameTextField.getText();
        String gameId = gameIdTextField.getText();
        PositionType position = positionChoiceBox.getValue();
        String hash = hashTextField.getText();

        if (userName.isEmpty() || gameId.isEmpty() || position == null || hash.isEmpty()) {
            warningLabel.setVisible(true);
        } else {
            FilterSearchController filter = new FilterSearchController();

            String table = "";
            if (gamesSet.getGames().get(gameId) == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setContentText("Game with such Game Id was not found");
                alert.show();
            } else {
                Game g = gamesSet.getGames().get(gameId);
                PlayerInGame curPlayer = g.getPlayer(hash);
                if (curPlayer == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.setContentText("There is no player with such hashcode in given Game");
                    alert.show();
                    return;
                }
                if (!curPlayer.getPosition().equals(position)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.setContentText("Position of chosen player is incorrect, double check please.");
                    alert.show();

                    return;
                } else {
                    // Check for existing of UserName first
                    UserProfile userProfile = new UserProfile(userName);
                    curPlayer.setRef(userProfile);
                    userProfile.findGames(curPlayer, g, new HashSet<>(gamesSet.getGames().values()));
                    newlyAssignedUsers.add(userProfile);

                    warningLabel.setVisible(false);
                    warningLabel.getScene().getWindow().hide();
                    return;
                }

            }

            warningLabel.setVisible(false);
        }
    }

    @FXML
    void initialize() {
        positionChoiceBox.getItems().addAll(SB, BB, LJ, HJ, CO, BTN);
        assignButton.setOnMouseClicked(action -> onAssignButtonClicked());
    }

}
