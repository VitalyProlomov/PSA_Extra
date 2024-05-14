package appinterface.controllers;

import appinterface.PSAApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Game;
import models.GamesSet;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class DeleteGamesController {
    @FXML
    private Button searchFilterButton;

    @FXML
    private Button deleteGamesButton;

    @FXML
    private TableView<Game> gamesTableView;

    private final static String SERIALIZED_GAMES_PATH = "src/main/resources/serializedFiles/serializedGames.txt";

    private GamesSet gamesSet;

    private ArrayList<Game> selectedGames = new ArrayList<>();

    @FXML
    void initialize() {
        initializeTable();
        if (gamesSet != null) {
            updateTable(gamesSet.getGames().values());
        }
        searchFilterButton.setOnMouseClicked(actionEvent -> onSearchFilterButtonClick());
        deleteGamesButton.setOnMouseClicked(actionEvent -> onDeleteGamesButtonClicked());
    }

    public void setGamesSet(GamesSet gamesSet) {
        this.gamesSet = gamesSet;
        updateTable(gamesSet.getGames().values());
        selectedGames = new ArrayList<>(gamesSet.getGames().values());
    }

    @FXML
    private void initializeTable() {
        TableColumn<Game, String> idColumn = new TableColumn<>();
        TableColumn<Game, Double> potColumn = new TableColumn<>();
        TableColumn<Game, Date> dateColumn = new TableColumn<>();
        TableColumn<Game, Double> bbSizeColumn = new TableColumn<>();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        potColumn.setCellValueFactory(new PropertyValueFactory<Game, Double>("finalPot"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        bbSizeColumn.setCellValueFactory(new PropertyValueFactory<>("BigBlindSize$"));


        dateColumn.setText("Date");
        idColumn.setText("ID");
        bbSizeColumn.setText("Blinds");
        potColumn.setText("Pot");


        dateColumn.setMinWidth(10);
        idColumn.setMinWidth(10);
        bbSizeColumn.setMinWidth(10);
        potColumn.setMinWidth(10);

        dateColumn.setPrefWidth(200);
        idColumn.setPrefWidth(160);
        bbSizeColumn.setPrefWidth(160);
        potColumn.setPrefWidth(85);

        potColumn.setCellFactory(new Callback<>() {
            public TableCell call(TableColumn p) {
                TableCell cell = new TableCell<Game, Double>() {
                    @Override
                    public void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : getString());
                        setGraphic(null);
                    }

                    private String getString() {
                        String ret = "";
                        if (getItem() != null) {
                            ret = new DecimalFormat("#0.00").format(getItem());
                        } else {
                            ret = "0.00";
                        }
                        return ret;
                    }
                };

                return cell;
            }
        });


        // Setting clinking the row to open gameDisplayView.
//        gamesTableView.setRowFactory(tv -> {
//            TableRow<Game> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && (!row.isEmpty())) {
//                    try {
//                        Game rowData = row.getItem();
//
//                        FXMLLoader loader;
//
//                        Stage stage = new Stage();
//
//                        if (rowData.getGameType().equals(Game.GameType.HOLDEM_RNC_6MAX)) {
//                            loader = new FXMLLoader(PSAApplication.class.getResource("views/gameDisplay6MaxView.fxml"));
//                            stage.setScene(new Scene(loader.load()));
//                            GameDisplay6MaxController controller = loader.getController();
//                            controller.setGame(rowData);
//                        } else if (rowData.getGameType().equals(Game.GameType.HOLDEM_9MAX)) {
//                            loader = new FXMLLoader(PSAApplication.class.getResource("views/gameDisplay9MaxView.fxml"));
//                            stage.setScene(new Scene(loader.load()));
//                            GameDisplay9MaxController controller = loader.getController();
//                            controller.setGame(rowData);
//                        } else {
//                            throw new Exception("Unknown game type, currently not supported");
//                        }
//
//                        stage.setResizable(false);
//                        stage.show();
//                    } catch (Exception ex) {
//                        Alert alert = new Alert(Alert.AlertType.ERROR);
//                        alert.setContentText("Could not properly load game page.");
//                        if (ex.getMessage().equals("Unknown game type, currently not supported")) {
//                            alert.setContentText(ex.getMessage());
//                        }
//                        alert.show();
//                    }
//                }
//            });
//            return row;
//        });
        gamesTableView.getColumns().addAll(dateColumn, idColumn, bbSizeColumn, potColumn);
    }


    @FXML
    void onSearchFilterButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(PSAApplication.class.getResource("views/filterSearchView.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setResizable(false);

            FilterSearchController controller = loader.getController();
            stage.setOnHiding(event -> {
                        updateTable(controller.getGamesAfterFilter());
                        if (controller.getGamesAfterFilter() == null) {
                            selectedGames = new ArrayList<>();
                        } else {
                            selectedGames = new ArrayList<>(controller.getGamesAfterFilter());
                        }
                    }
            );
            controller.setUnfilteredGames(new HashSet<>(gamesSet.getGames().values()));

            stage.show();
        } catch (
                IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Could not open filter search");
            alert.show();
        }
    }

    @FXML
    private void onDeleteGamesButtonClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are ypu sure you want to delete " +
                selectedGames.size() + " games? ", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            Alert extraConfiramtion = new Alert(Alert.AlertType.WARNING,
                    "You will not be able to restore these games, continue?",
                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            extraConfiramtion.showAndWait();
            if (extraConfiramtion.getResult() == ButtonType.YES) {
                for (Game g : selectedGames) {
                    gamesSet.getGames().remove(g.getGameId());
                }
                serializeGames();
                updateTable(null);
                selectedGames.clear();
            }
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

    private void updateTable(Collection<Game> gamesToShow) {
        gamesTableView.getItems().clear();
        if (gamesToShow == null) {
            return;
        }

        gamesTableView.getItems().addAll(gamesToShow);
    }
}
