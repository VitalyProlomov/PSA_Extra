package appinterface.controllers;
import appinterface.PSAApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Game;
import models.GamesSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button downloadedGamesButton;

    @FXML
    private Button enhancedButtons;

    @FXML
    private Button evCalculatorButton;

    @FXML
    private Button examinePlayersButton;

    @FXML
    private Button handsEVButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button uploadButton;

    private final static String SERIALIZED_GAMES_PATH = "src/main/resources/serializedFiles/serializedGames.txt";
    private GamesSet gamesSet;

    public void initialize() {
        initializeSerializedSavedGames();
        downloadedGamesButton.setOnMouseClicked(actionEvent -> onDownloadedGamesButtonClicked());
//        enhancedButtons;
        evCalculatorButton.setOnMouseClicked(actionEvent -> onEVCalculatorButtonClicked());
        examinePlayersButton.setOnMouseClicked(action -> onExaminePlayersButtonClicked());
        handsEVButton.setOnMouseClicked(action -> onHandsEVButtonClicked());;
        profileButton.setOnMouseClicked(action -> onProfileButtonClicked());
        uploadButton.setOnAction(actionEvent -> onUploadButtonClick());

    }

    @FXML
    void onDownloadedGamesButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/gamesListView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            GamesListController controller = loader.getController();
            controller.setGamesSet(gamesSet);

            stage.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open downloaded games list, try reopening the app.");
            alert.show();
        }
    }

    @FXML
    private void onExaminePlayersButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/examinePlayersView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            ExaminePlayersController controller = loader.getController();
            controller.setGameSet(gamesSet);
            stage.show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not properly load Examine Players page.");
            alert.show();
        }
    }

    @FXML
    void onProfileButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/profileView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            ProfileController controller = loader.getController();
            controller.setInfo(gamesSet.getGames());
            stage.show();
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open Profile. Try reopening the app.");
            alert.show();
        }
    }

    @FXML
    void onHandsEVButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/handsEVView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            HandsEVController controller = loader.getController();

            controller.setGamesSet(gamesSet);

//            FXMLLoader fxmlLoader = new FXMLLoader(PSAApplication.class.getResource("views/handsEVView.fxml"));
//            stage.setScene(new Scene(fxmlLoader.load()));
//            HandsEVController controller = fxmlLoader.getController();
//            GamesSet testGameSet = new GamesSet();
//            testGameSet.addGames(gamesSet.getGames().values());
//            controller.setGamesSet(testGameSet);
            stage.show();
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open Hands EV. Try reopening the app.");
            alert.show();
        }
    }

    @FXML
    void onUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TextFiles", "*.txt"));
//            fileChooser.setSelectedExtensionFilter();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(uploadButton.getParent().getScene().getWindow());

        if (selectedFiles != null) {
            try {
                UploadController uploadController = new UploadController();
                ArrayList<Game> addedGames = uploadController.uploadFiles(selectedFiles);

                gamesSet.addGames(addedGames);

                serializeGames();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Something went wrong while uploading files..");
                System.out.println(ex.getMessage());
                // System.out.println(Arrays.toString(ex.getCause().getStackTrace()));
                alert.show();
            }
        }
    }

    public void onEVCalculatorButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/evCalculatorView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            stage.show();
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open EV Calculator. Try reopening the app.");
            alert.show();
        }
    }

    private void initializeSerializedSavedGames() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            if (new File(SERIALIZED_GAMES_PATH).length() != 0) {
                gamesSet = objectMapper.readValue(new File(SERIALIZED_GAMES_PATH), GamesSet.class);
            }
            if (this.gamesSet == null) {
                this.gamesSet = new GamesSet();
            }

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(true);
            alert.setContentText("Something went wrong while parsing the file with saved games." +
                    "Make sure not to change anything in them or to close the file if it is opened");
            alert.show();
        }
    }

    private void serializeGames() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            File file = new File(SERIALIZED_GAMES_PATH);

            objectMapper.writeValue(file, gamesSet);
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("File with saved games was not able to upload, please close it if it is opened");
            alert.show();
        }
    }

}
