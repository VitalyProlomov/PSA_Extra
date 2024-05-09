package appinterface.controllers;


import appinterface.PSAApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Game;
import models.GamesSet;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GamesListController {
    private final static String SERIALIZED_GAMES_PATH = "src/main/resources/serializedFiles/serializedGames.txt";

    @FXML
    private Button uploadButton;

    @FXML
    private TableView<Game> gamesTableView;

    @FXML
    private Button searchFiltersButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button examinePlayersButton;

    @FXML
    private ImageView helpImageView;

    private GamesSet gamesSet;


    @FXML
    void initialize() {
        uploadButton.setOnAction(actionEvent -> onUploadButtonClick());

        initializeTable();
        initializeSerializedSavedGames();
        updateTable(new HashSet<>(gamesSet.getGames().values()));
        searchFiltersButton.setOnMouseClicked(actionEvent -> onSearchFiltersButtonClick());

        profileButton.setOnMouseClicked(action -> onProfileButtonClicked());

        examinePlayersButton.setOnMouseClicked(action -> onExaminePlayersButtonClicked());

        helpImageView.setOnMouseClicked(action -> onHelpImageViewClicked());
    }

    public void setGamesSet(GamesSet gamesSet) {
        this.gamesSet = gamesSet;
        updateTable(new HashSet<>(gamesSet.getGames().values()));
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
    void onSearchFiltersButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/filterSearchView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            FilterSearchController controller = loader.getController();
            stage.setOnHiding(event -> {
                if (controller.getGamesAfterFilter() != null) {
                    updateTable(controller.getGamesAfterFilter());
                }
            });
            controller.setUnfilteredGames(new HashSet<>(gamesSet.getGames().values()));
            stage.show();
//        filterSearchController.searchFilteredGames()
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open filter search");
            alert.show();
        }

    }

    private void onHelpImageViewClicked() {
        Alert helpMessage = new Alert(Alert.AlertType.NONE);
        helpMessage.getDialogPane().getButtonTypes().add(ButtonType.OK);
        helpMessage.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        helpMessage.initStyle(StageStyle.UTILITY);
        helpMessage.setContentText("""
                Hello, User
                You are using PSA - Poker Statistics Analyzer.
                Here are the main functions in this app (first 4 are accessed by Buttons with the corresponding names):
                1) Upload Files - uploads new files w text representations of games (For Pokerok you can get them on PokerCraft.com
                2) Examine Players - see what players you have already assigned (identified player with just in-game hash by its actual username) and see their stats
                3) Profile - see general information about games you uploaded
                4) Search Filters - Search the games you need using special filters (ex. 3-bet multi-way pots)
                5) Every Game in this table is accessible - double-click the row to open the Game Replay
                Have Fun!""");
        helpMessage.show();
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

                updateTable(new HashSet<>(gamesSet.getGames().values()));

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
    private void initializeTable() {
        gamesTableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("date"));
        gamesTableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("gameId"));
        gamesTableView.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("BigBlindSize$"));
        gamesTableView.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("finalPot"));

        // Setting clinking the row to open gameDisplayView.
        gamesTableView.setRowFactory(tv -> {
            TableRow<Game> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    try {
                        Game rowData = row.getItem();

                        FXMLLoader loader;

                        Stage stage = new Stage();

                        if (rowData.getGameType().equals(Game.GameType.HOLDEM_RNC_6MAX)) {
                            loader = new FXMLLoader(PSAApplication.class.getResource("views/gameDisplay6MaxView.fxml"));
                            stage.setScene(new Scene(loader.load()));
                            GameDisplay6MaxController controller = loader.getController();
                            controller.setGame(rowData);
                        } else if (rowData.getGameType().equals(Game.GameType.HOLDEM_9MAX)) {
                            loader = new FXMLLoader(PSAApplication.class.getResource("views/gameDisplay9MaxView.fxml"));
                            stage.setScene(new Scene(loader.load()));
                            GameDisplay9MaxController controller = loader.getController();
                            controller.setGame(rowData);
                        }
                        else {
                            throw new Exception("Unknown game type, currently not supported");
                        }

                        stage.setResizable(false);
                        stage.show();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Could not properly load game page.");
                        if (ex.getMessage().equals("Unknown game type, currently not supported")) {
                            alert.setContentText(ex.getMessage());
                        }
                        alert.show();
                    }
                }
            });
            return row;
        });

    }

    private void initializeSerializedSavedGames() {
        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            if (new File(SERIALIZED_GAMES_PATH).length() != 0) {
//                gamesSet = objectMapper.readValue(new File(SERIALIZED_GAMES_PATH), GamesSet.class);
//            }
            if (this.gamesSet == null) {
                this.gamesSet = new GamesSet();
            }

            if (!gamesSet.getGames().isEmpty()) {
                gamesTableView.getItems().addAll(gamesSet.getGames().values());
            }
        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(true);
            alert.setContentText("Something went wrong while parsing the file with saved games." +
                    "Make sure not to change anything in them or to close the file if it is opened");
            alert.show();
        }
    }

    private void updateTable(Set<Game> gamesToShow) {
        gamesTableView.getItems().clear();
        if (gamesToShow == null) {
            return;
        }

        gamesTableView.getItems().addAll(gamesToShow);
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
