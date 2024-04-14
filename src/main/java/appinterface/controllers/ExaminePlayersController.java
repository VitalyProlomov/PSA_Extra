package appinterface.controllers;

import appinterface.PSAApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import models.GamesSet;
import models.UserProfile;
import models.UserProfileSet;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ExaminePlayersController {

    private final static String SERIALIZED_USER_PROFILES_PATH = "src/main/resources/serializedFiles/serializedUserProfiles.txt";
    @FXML
    private CustomTextField searchIdCustomTextField;

    @FXML
    private Button assignNewPlayerButton;


    @FXML
    private TableView<UserProfile> userProfilesTableView;


    private GamesSet gamesSet;

    public void setGameSet(GamesSet set) {
        this.gamesSet = set;
    }

    private UserProfileSet userProfileSet;

    private void onAssignNewPlayerMouseClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/assignNewPlayerView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);
            AssignNewPlayerController controller = loader.getController();
            HashSet<UserProfile> newUserProfiles;
            stage.getScene().getWindow().setOnHiding(event -> assignNewUsers(controller));
            controller.setGamesSet(gamesSet);
            stage.show();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open filter search");
            alert.show();
        }
    }

    private void assignNewUsers(AssignNewPlayerController controller) {
        for (UserProfile user : controller.getNewlyAssignedUsers()) {
            userProfileSet.addUser(user);
            System.out.println(user.getUserName() + ": " + user.getAllGamesIds());
        }

        updateTable();
        serializeUserProfileSet();

    }

    private void serializeUserProfileSet() {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File(SERIALIZED_USER_PROFILES_PATH);
        try {
            objectMapper.writeValue(file, this.userProfileSet);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Could not properly save User Profiles \n" +
                    "Changes will not be seen during next program opening");
            alert.show();
        }
    }

    private void updateTable() {
        userProfilesTableView.getItems().clear();
        userProfilesTableView.getItems().addAll(userProfileSet.getIdUserMap().values());
    }

    private void showSearchResults(String search) {
        if (search.equals("")) {
            updateTable();
            return;
        }
        ArrayList<UserProfile> profiles = new ArrayList<>();
        for (UserProfile userP : userProfileSet.getIdUserMap().values()) {
            if (userP.getUserName().contains(search)) {
                profiles.add(userP);
            }
        }
        userProfilesTableView.getItems().clear();
        userProfilesTableView.getItems().addAll(profiles);
    }

    private void initializeTable() {
        userProfilesTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("userName"));
        userProfilesTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("gamesAmount"));

        // Setting clinking the row to open gameDisplayView.
        userProfilesTableView.setRowFactory(tv -> {
            TableRow<UserProfile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    try {
                        UserProfile rowData = row.getItem();

                        FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/userProfileDisplayView.fxml"));
                        Stage stage = new Stage();
                        stage.setScene(new Scene(loader.load()));

                        UserProfileDisplayController controller = loader.getController();
                        controller.setGamesAndProfile(gamesSet.getGames(), rowData);

                        stage.setResizable(false);
                        stage.show();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Could not properly load User profile window.");
                        alert.show();
                        ex.printStackTrace();
                    }
                }
            });
            return row;
        });

    }

    private void initializeSerializedSavedUsers() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            if (new File(SERIALIZED_USER_PROFILES_PATH).length() != 0) {
                userProfileSet = objectMapper.readValue(new File(SERIALIZED_USER_PROFILES_PATH), UserProfileSet.class);
            }
            if (this.userProfileSet == null) {
                this.userProfileSet = new UserProfileSet();
            }

            if (userProfileSet.getIdUserMap().size() != 0) {
                userProfilesTableView.getItems().addAll(userProfileSet.getIdUserMap().values());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Something went wring while parsing the file with saved games." +
                    "Make sure not to change anything in them or to close the file if it is opened");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.show();
        }
    }

    private void initializeEventHandling() {
        searchIdCustomTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER)) {
                    showSearchResults(searchIdCustomTextField.getText());
                }
            }
        });

        assignNewPlayerButton.setOnMouseClicked(action -> onAssignNewPlayerMouseClicked());

    }

    @FXML
    void initialize() {
        initializeSerializedSavedUsers();

        initializeTable();

        initializeEventHandling();
    }

}
