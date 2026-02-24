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
import pokerlibrary.models.Game;
import pokerlibrary.models.GamesSet;

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
    private Button enhancedButton;

    @FXML
    private Button evCalculatorButton;

    @FXML
    private Button examinePlayersButton;

    @FXML
    private Button handsEVButton;

    @FXML
    private Button helpButton;

    @FXML
    private Button uploadButton;

    @FXML
    private Button deleteGamesButton;

    //    private final static String SERIALIZED_GAMES_PATH = "resources/serializedFiles/serializedGames.txt";
    private final static String SERIALIZED_GAMES_PATH = "src/main/resources/serializedFiles/serializedGames.txt";
    private GamesSet gamesSet;


    public void initialize() {
        initializeSerializedSavedGames();
        downloadedGamesButton.setOnMouseClicked(actionEvent -> onDownloadedGamesButtonClicked());
        enhancedButton.setOnMouseClicked(action -> onEnhancedStatsButtonClicked());
        evCalculatorButton.setOnMouseClicked(actionEvent -> onEVCalculatorButtonClicked());
        examinePlayersButton.setOnMouseClicked(action -> onExaminePlayersButtonClicked());
        handsEVButton.setOnMouseClicked(action -> onHandsEVButtonClicked());
        helpButton.setOnMouseClicked(action -> onHelpButtonClicked());
        uploadButton.setOnAction(actionEvent -> onUploadButtonClick());
        deleteGamesButton.setOnMouseClicked(action -> onDeleteGamesButtonClicked());

    }

    @FXML
    void onDownloadedGamesButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/gamesListView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

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

    private void onEnhancedStatsButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/enhancedStatsView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            EnhancedStatsController controller = loader.getController();
            controller.setGamesSet(gamesSet);

            stage.show();
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open Enhanced Stats. Try reopening the app.");
            alert.show();
        }
    }

    private void onDeleteGamesButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/deleteGamesView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            DeleteGamesController controller = loader.getController();
            try {
                controller.setGamesSet(gamesSet);
            } catch (Exception ex) {
                controller.setGamesSet(null);
            }
            stage.show();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open Remove games window. Try reopening the app.");
            alert.show();
        }
    }

    private void onHelpButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("""
                 Hello, user. Poker Statistics Analyzer is an app
                that will help you improve your strategy by
                analyzing your games.
                There are following functions here:
                1) Downloaded games - here you can see all
                of the games you uploaded and replay them by
                double clicking on row with chosen game.
                2) Upload games - upload new games (format
                must be from the files downloaded on website PokerCraft.com
                Website can only be accessed through link in poker room GGPoker.
                3) Examine Players - assign player nicknames to their
                hashes. Players can then be seen assigned to the games.
                4) EV Calculator - here you can calculate mathematical
                expected value with chosen hands on chosen board.
                5) Hands EV - here you can see all the expected value
                with every possible hand.
                6) Enhanced Stats - here you can analyze your actions
                and some statistics in games that you can filter by
                different parameters
                7) Delete games - delete unwanted games (can be searched by filter)
                Some basic notations:
                For cards A - is Ace, K - King, Q- Queen, J - Jack, T - ten,
                all number cards are written as their rank.
                As for the suits: s - spades, h - hearts, c - clubs, d - diamonds
                So, to write a nine of clubs, write 9c
                In app the suits of cards are represented with colors:
                Hearts - red, Spades - black, Clubs - green, Diamonds - blue
                to search for King Jack off suit - write KJo, suited - KJs
                Have fun using the app!
                """);
        alert.show();
    }

    private void initializeSerializedSavedGames() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

//            HomeController.class.getResourceAsStream(SERIALIZED_GAMES_PATH);
//            File file = new File(SERIALIZED_GAMES_PATH);
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
            alert.setContentText("File with saved games was not able to upload, please close it, if it is opened");
            alert.show();
        }
    }

}
