package appinterface.controllers;

import pokerlibrary.analizer.GameAnalyzer;
import appinterface.PSAApplication;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.exceptions.IncorrectHandException;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;
import pokerlibrary.models.*;
import static pokerlibrary.models.PositionType.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

public class EnhancedStatsController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Game> gamesTableView;

//    @FXML
//    private CheckComboBox<String> heroActionCheckComboBox;

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
    private Label infoLabel8;

    @FXML
    private PieChart pieChart1;

    @FXML
    private PieChart pieChart2;

    @FXML
    private ComboBox<String> playersPostFlopComboBox;

    @FXML
    private Button setSettingsButton;

    @FXML
    private ComboBox<String> streetComboBox;

    @FXML
    private Label streetLabel;

    @FXML

    private TextField handsTextField;

    private final String ranksString = "23456789TJQKA";

    private final int LABELS_AMOUNT = 8;

    private GamesSet gamesSet;

    private ArrayList<Hand> handsChosen = new ArrayList<>();


    @FXML
    void initialize() {
        initializeTable();
        heroRoleCheckComboBox.getItems().addAll(FXCollections.observableArrayList("limper", "srpC", "srpR", "3bC", "3bR", "4bC", "4bR", "5+Bet Pot"));
        heroPositionCheckComboBox.getItems().addAll(FXCollections.observableArrayList(UTG, UTG_1, UTG_2, LJ, HJ, CO, BTN, SB, BB));
//        heroActionCheckComboBox.getItems().addAll("limp", "RFI", "call RFI", "3BC", "3BR", "4bet");
        streetComboBox.getItems().addAll(FXCollections.observableArrayList("Preflop", "Flop"));
        playersPostFlopComboBox.getItems().addAll(FXCollections.observableArrayList("heads up", "multi way", "folded", "all"));

        setSettingsButton.setOnMouseClicked(actionEvent -> onSetSettingsButtonClicked());

        handsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateHandFromTextField();
        });

        setToDefault();
    }

    @FXML
    private void initializeTable() {
        TableColumn<Game, String> idColumn = new TableColumn<Game, String>();
        TableColumn<Game, Double> potColumn = new TableColumn<Game, Double>();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        potColumn.setCellValueFactory(new PropertyValueFactory<Game, Double>("finalPot"));
        idColumn.setPrefWidth(190);
        idColumn.setMinWidth(10);
        potColumn.setPrefWidth(190);
        potColumn.setMinWidth(10);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        idColumn.setText("ID");
        potColumn.setText("Pot");

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

        gamesTableView.getColumns().addAll(idColumn, potColumn);


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
                        } else {
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

    public void setGamesSet(GamesSet gamesSet) {
        this.gamesSet = new GamesSet(gamesSet.getGames());
//        updateTable(gamesSet.getGames().values());
    }

    private void setToDefault() {
        for (int i = 1; i <= LABELS_AMOUNT; ++i) {
            Label l = (Label) this.anchorPane.lookup("#infoLabel" + i);
            l.setOpacity(0);
        }
        streetLabel.setOpacity(0);
    }

    private void updateTable(Collection<Game> games) {
        gamesTableView.getItems().clear();
        if (games != null && !games.isEmpty()) {
            gamesTableView.getItems().addAll(games);
        }
    }

    private void updateHandFromTextField() {
        String cardReps = handsTextField.getText();
        if (cardReps.isEmpty()) {
            handsChosen.clear();
            handsTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
            return;
        }
        if (cardReps.length() < 2 || cardReps.length() > 4) {
            handsTextField.setStyle("-fx-background-color: red; -fx-border-color: black");
            handsChosen.clear();
            return;
        }

        try {

            if (cardReps.length() == 4) {
                String card1Rep = cardReps.substring(0, 2);
                String card2Rep = cardReps.substring(2, 4);
                Hand hand = new Hand(card1Rep, card2Rep);
                handsChosen.clear();
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
                        handsChosen.clear();
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
                                handsChosen.clear();
                                for (int i = 0; i < suits.length(); ++i) {
                                    handsChosen.add(new Hand(rank1Str + suits.charAt(i), rank2Str + suits.charAt(i)));
                                }
                                handsTextField.setStyle("-fx-background-color: white; -fx-border-color: black");
                            }
                        } else if (cardReps.charAt(2) == 'o') {
                            handsChosen.clear();
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
        boolean isHeroLimperChosen = heroRoleCheckComboBox.getCheckModel().isChecked(0);
        boolean isHeroSRPCallerChosen = heroRoleCheckComboBox.getCheckModel().isChecked(1);
        boolean isHeroSRPRaiserChosen = heroRoleCheckComboBox.getCheckModel().isChecked(2);
        boolean isHero3betCallerChosen = heroRoleCheckComboBox.getCheckModel().isChecked(3);
        boolean isHero3betRaiserChosen = heroRoleCheckComboBox.getCheckModel().isChecked(4);
        boolean isHero4betCallerChosen = heroRoleCheckComboBox.getCheckModel().isChecked(5);
        boolean isHero4betRaiserChosen = heroRoleCheckComboBox.getCheckModel().isChecked(6);
        boolean isHero5PlusBetPotChosen = heroRoleCheckComboBox.getCheckModel().isChecked(7);

        boolean isOneRoleSelected = true;
        if (!isHeroLimperChosen && !isHeroSRPCallerChosen && !isHeroSRPRaiserChosen &&
                !isHero3betCallerChosen && !isHero3betRaiserChosen && !isHero4betCallerChosen &&
                !isHero4betRaiserChosen && !isHero5PlusBetPotChosen) {
            isHeroLimperChosen = true;
            isHeroSRPCallerChosen = true;
            isHeroSRPRaiserChosen = true;
            isHero3betCallerChosen = true;
            isHero3betRaiserChosen = true;
            isHero4betCallerChosen = true;
            isHero4betRaiserChosen = true;
            isHero5PlusBetPotChosen = true;

            isOneRoleSelected = false;
        }

//        // "limp", "RFI", "call RFI", "3BC", "3BR", "4bet"
//        boolean hasHeroLimpedChosen = heroActionCheckComboBox.getCheckModel().isChecked(0);
//        boolean hasHeroRFIChosen = heroActionCheckComboBox.getCheckModel().isChecked(1);
//        boolean hasHeroCallRFIChosen = heroActionCheckComboBox.getCheckModel().isChecked(2);
//        boolean hasHeroCalled3BetChosen = heroActionCheckComboBox.getCheckModel().isChecked(3);
//        boolean hasHero3BetChosen = heroActionCheckComboBox.getCheckModel().isChecked(4);
//        boolean hasHero4BetChosen = heroActionCheckComboBox.getCheckModel().isChecked(5);

        ArrayList<Game> games = new ArrayList<>(gamesSet.getGames().values());
        for (Game g : gamesSet.getGames().values()) {
            if (!handsChosen.isEmpty()) {
                if (!handsChosen.contains(g.getPlayer("Hero").getHand())) {
                    games.remove(g);
                    continue;
                }
            }

            if (GameAnalyzer.HasPlayerInitiallyFolded(g, "Hero")) {
                games.remove(g);
                continue;
            }

            if (!heroPositionCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                if (!heroPositionCheckComboBox.getCheckModel().getCheckedItems().contains(g.getPlayer("Hero").getPosition())) {
                    games.remove(g);
                    continue;
                }
            }
//            "heads up", "multi way", "folded"

            String pfPlayers = playersPostFlopComboBox.getSelectionModel().getSelectedItem();
            if (pfPlayers != null && !pfPlayers.equals("all")) {
                if (pfPlayers.equals("heads up") && g.getPreFlop().getPlayersAfterBetting().size() != 2 ||
                        pfPlayers.equals("multi way") && !GameAnalyzer.isPotMultiWay(g) ||
                        pfPlayers.equals("folded") && g.getPreFlop().getPlayersAfterBetting().size() != 1) {
                    games.remove(g);
                    continue;
                }
            }
            if (isOneRoleSelected) {
                if (GameAnalyzer.isPlayerLimper(g, "Hero") && !isHeroLimperChosen ||
                        GameAnalyzer.isPlayerSRPC(g, "Hero") && !isHeroSRPCallerChosen ||
                        GameAnalyzer.isPlayerSRPR(g, "Hero") && !isHeroSRPRaiserChosen ||
                        GameAnalyzer.isPlayer3BetCaller(g, "Hero") && !isHero3betCallerChosen ||
                        GameAnalyzer.isPlayer3BetRaiser(g, "Hero") && !isHero3betRaiserChosen ||
                        GameAnalyzer.isPlayer4BetCaller(g, "Hero") && !isHero4betCallerChosen ||
                        GameAnalyzer.isPlayer4BetRaiser(g, "Hero") && !isHero4betRaiserChosen ||
                        GameAnalyzer.isPot5PlusBet(g) && !isHero5PlusBetPotChosen ||
                        !g.getPreFlop().getPlayersAfterBetting().contains(new PlayerInGame("Hero"))) {
                    games.remove(g);
                    continue;
                }
            }

//            if (!heroActionCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) {
//                // "limp", "RFI", "call RFI", "3BC", "3BR", "4bet"
//                if (hasHeroLimpedChosen && !GameAnalyzer.didPlayerLimp(g, "Hero") ||
//                        hasHeroRFIChosen && !GameAnalyzer.didPlayerRFI(g, "Hero") ||
//                        hasHeroCallRFIChosen && !GameAnalyzer.didPlayerCallRFI(g, "Hero") ||
//                        hasHeroCalled3BetChosen && !GameAnalyzer.didPlayerCall3Bet(g, "Hero") ||
//                        hasHero3BetChosen && !GameAnalyzer.didPlayer3Bet(g, "Hero") ||
//                        hasHero4BetChosen && !GameAnalyzer.didPlayer4Bet(g, "Hero")) {
//                    games.remove(g);
//                    continue;
//                }
//            }
            if (GameAnalyzer.HasPlayerInitiallyFolded(g, "Hero")) {
                games.remove(g);
            }
        }

        updateTable(games);

        String street = streetComboBox.getSelectionModel().getSelectedItem();
        if (street == null) {
            street = "Preflop";
        }
        if (street.equals("Preflop")) {
            initializePreflop(games);
        } else if (street.equals("Flop")) {
            initializeFlop(games);
        }
//        } else if (street.equals("Turn")) {
//            initializeTurn(games);
//        }
//        } else if (street.equals("River")) {
//            initializeRiver(games);
//        }

    }

    private void initializePreflop(ArrayList<Game> games) {
        double unraisedPots = 0.0;
        double singleRasiedPots = 0.0;
        double pots3Bet = 0;
        double pots4Bet = 0;
        double pot5PlusBet = 0;

        double gamesWonAmount = 0;
        double gamesLostAmount = 0;

        for (Game g : games) {
            if (GameAnalyzer.isPotUnRaised(g)) {
                ++unraisedPots;
            } else if (GameAnalyzer.isPotSingleRaised(g)) {
                ++singleRasiedPots;
            } else if (GameAnalyzer.isPot3Bet(g)) {
                ++pots3Bet;
            } else if (GameAnalyzer.isPot4Bet(g)) {
                ++pots4Bet;
            } else if (GameAnalyzer.isPot5PlusBet(g)) {
                ++pot5PlusBet;
            }

            if (g.getHeroWinloss() > 0) {
                ++gamesWonAmount;
            } else {
                ++gamesLostAmount;
            }
        }

//        for (Game g : games) {
//            ArrayList<Action> actions = g.getPreFlop().getAllActions();
//
//            if (g.getPlayer("Hero") == null) {
//                continue;
//            }
//            int i = 0;
//            while (i < actions.size() &&
//                    (!actions.get(i).getPlayerId().equals("Hero") || actions.get(i).getActionType().equals(ANTE) ||
//                    actions.get(i).getActionType().equals(BLIND) || actions.get(i).getActionType().equals(MISSED_BLIND))) {
//                ++i;
//            }
//            if (i >= actions.size()) {
//                continue;
//            }
//            if (actions.get(i).getActionType().equals(FOLD)) {
//                continue;
//            }
//
//            ++i;
//            boolean allCalled = true;
//            boolean allFolded = true;
//            boolean hasRaised = false;
//            while (i < actions.size()) {
//                if (actions.get(i).getActionType() == CALL) {
//                    allFolded = false;
//                }
//                if (actions.get(i).getActionType() == RAISE) {
//                    allFolded = false;
//                    allCalled = false;
//                    hasRaised = true;
//                }
//                ++i;
//            }
//            if (allCalled) {
//                ++allCalledPercentage;
//            }
//            if (allFolded) {
//                ++allFoldedPercentage;
//            }
//            if (hasRaised) {
//                ++someoneRaisedPercentage;
//            }
//
//            if (g.getHeroWinloss() > 0) {
//                ++gamesWonAmount;
//            } else {
//                ++gamesLostAmount;
//            }
//        }
        double sum = unraisedPots + singleRasiedPots + pots3Bet + pots4Bet + pot5PlusBet;
        if (sum != 0) {
            unraisedPots /= sum;
            singleRasiedPots /= sum;
            pots3Bet /= sum;
            pots4Bet /= sum;
            pot5PlusBet /= sum;
        }

        String unraisedPercentageStr = new DecimalFormat("#0").format(unraisedPots * 100);
        String singleRaisedPercentageStr = new DecimalFormat("#0").format(singleRasiedPots * 100);
        String pots3BetPercentage = new DecimalFormat("#0").format(pots3Bet * 100);
        String pots4BetPercentage = new DecimalFormat("#0").format(pots4Bet * 100);
        String pots5PlusBetPercentage = new DecimalFormat("#0").format(pot5PlusBet * 100);

        streetLabel.setText("Preflop");
        infoLabel1.setText("Games amount: " + games.size());
        infoLabel2.setText("unraised: " + unraisedPercentageStr + "%");
        infoLabel3.setText("Single rasied: " + singleRaisedPercentageStr + "%");
        infoLabel4.setText("3 Bet: " + pots3BetPercentage + "%");
        infoLabel5.setText("4 Bet: " + pots4BetPercentage + "%");
        infoLabel6.setText("5 Bet: " + pots5PlusBetPercentage + "%");
        infoLabel7.setText("Hero won game: " + gamesWonAmount);
        infoLabel8.setText("Hero lost game: " + gamesLostAmount);

        setToDefault();
        streetLabel.setOpacity(1);
        for (int i = 1; i <= 8; ++i) {
            Label l = (Label) anchorPane.lookup("#infoLabel" + i);
            l.setOpacity(1);
        }


        pieChart1.setOpacity(1);
        pieChart1.getData().clear();
        ObservableList<PieChart.Data> pieChart1Data =
                FXCollections.observableArrayList(
                        new PieChart.Data("unraised", Integer.parseInt(unraisedPercentageStr)),
                        new PieChart.Data("Raised", Integer.parseInt(singleRaisedPercentageStr)),
                        new PieChart.Data("4 Bet", Integer.parseInt(pots3BetPercentage)),
                        new PieChart.Data("3 Bet", Integer.parseInt(pots4BetPercentage)),
                        new PieChart.Data("5+ Bet", Integer.parseInt(pots5PlusBetPercentage)));

        pieChart1Data.forEach(data ->
                data.nameProperty().bind(Bindings.concat(data.getName(), ": ", data.pieValueProperty(), "%")));

        pieChart1.setLabelsVisible(false);
        pieChart1.getData().addAll(pieChart1Data);

        pieChart2.setOpacity(1);
        pieChart2.getData().clear();

        if (!games.isEmpty()) {
            gamesWonAmount /= games.size();
            gamesWonAmount *= 100;
            gamesLostAmount /=  games.size();
            gamesLostAmount *= 100;
        }

        ObservableList<PieChart.Data> pieChart2Data =
                FXCollections.observableArrayList(
                        new PieChart.Data("games won", gamesWonAmount),
                        new PieChart.Data("games lost", gamesLostAmount));


        pieChart2Data.forEach(data ->
                data.nameProperty().bind(Bindings.concat(data.getName(), ": ",
                        new DecimalFormat("#0").format(data.pieValueProperty().doubleValue()), "%")));

        pieChart2.setLabelsVisible(false);
        pieChart2.getData().addAll(pieChart2Data);

    }

    private void initializeFlop(Collection<Game> games) {
        int pfrCBetAmount = 0;
        int pfrCheckedAmount = 0;
        int callerCalledAmount = 0;
        int callerCRAmount = 0;
        int callerFoldedAmount = 0;
//        double heroCBetAmount = 0;

        double gamesWonAmount = 0.0;
        double gamesLostAmount = 0.0;
        for (Game g : games) {
            if (GameAnalyzer.didCBetFlop(g, "Hero")) {
                ++pfrCBetAmount;
            } else if (GameAnalyzer.didPFRCheckFlop(g, "Hero")) {
                ++pfrCheckedAmount;
            }

            if (GameAnalyzer.didCallCBetFlop(g, "Hero")) {
                ++callerCalledAmount;
            } else if (GameAnalyzer.isPlayerPFR(g, "Hero") &&
                    GameAnalyzer.didCheckRaiseFlop(g, "Hero")) {
                ++callerCRAmount;
            } else if (GameAnalyzer.didCallerFoldFlop(g, "Hero")) {
                ++callerFoldedAmount;
            }
            if (g.getHeroWinloss() > 0) {
                ++gamesWonAmount;
            } else {
                ++gamesLostAmount;
            }
        }

        setToDefault();
        for (int i = 1; i <= 8; ++i) {
            Label l = (Label) this.anchorPane.lookup("#infoLabel" + i);
            l.setOpacity(1);
        }
        streetLabel.setOpacity(1);

        streetLabel.setText("Flop");
        infoLabel1.setText("Games amount: " + games.size());
        infoLabel2.setText("Hero (PFR) c-bet: " + pfrCBetAmount);
        infoLabel3.setText("Hero (PFR) checked: " + pfrCheckedAmount);
        infoLabel4.setText("Hero (Caller) folded: " + callerFoldedAmount);
        infoLabel5.setText("Hero (Caller) called: " + callerCalledAmount);
        infoLabel6.setText("Hero (Caller) C-R: " + callerCRAmount);
        infoLabel7.setText("Hero won game: " + gamesWonAmount);
        infoLabel8.setText("Hero lost game: " + gamesLostAmount);

        pieChart1.setOpacity(1);
        pieChart1.getData().clear();
        ObservableList<PieChart.Data> pieChart1Data =
                FXCollections.observableArrayList(
                        new PieChart.Data("PFR c-bet", pfrCBetAmount),
                        new PieChart.Data("PFR checked", pfrCheckedAmount),
                        new PieChart.Data("Caller folded", callerFoldedAmount),
                        new PieChart.Data("Caller called", callerCalledAmount),
                        new PieChart.Data("Called C-R", callerCRAmount));

        pieChart1Data.forEach(data ->
                data.nameProperty().bind(Bindings.concat(data.getName(), ": ", (int) Math.floor(data.pieValueProperty().getValue()))));

        pieChart1.getData().addAll(pieChart1Data);

        pieChart2.setOpacity(1);
        pieChart2.getData().clear();


        if (!games.isEmpty()) {
            gamesWonAmount /= games.size();
            gamesWonAmount *= 100;
            gamesLostAmount /= games.size();
            gamesLostAmount *= 100;
        }

        ObservableList<PieChart.Data> pieChart2Data =
                FXCollections.observableArrayList(
                        new PieChart.Data("games won", gamesWonAmount),
                        new PieChart.Data("games lost", gamesLostAmount));

        pieChart2Data.forEach(data ->
                data.nameProperty().bind(Bindings.concat(data.getName(), ": ",
                        new DecimalFormat("#0").format(data.pieValueProperty().doubleValue()), "%")));

        pieChart2.getData().addAll(pieChart2Data);
        pieChart2.setLabelsVisible(false);
    }

    private void initializeTurn(Collection<Game> games)  {
        int pfrCBetAmount = 0;
        int pfrCheckedAmount = 0;
        int callerCalledAmount = 0;
        int callerCRAmount = 0;
        int callerFoldedAmount = 0;
//        double heroCBetAmount = 0;

        double gamesWonAmount = 0.0;
        double gamesLostAmount = 0.0;
        for (Game g : games) {
            if (GameAnalyzer.didCBetTurn(g, "Hero")) {
                ++pfrCBetAmount;
            } else if (GameAnalyzer.didPFRCheckTurn(g, "Hero")) {
                ++pfrCheckedAmount;
            }

            if (GameAnalyzer.didCallCBetTurn(g, "Hero")) {
                ++callerCalledAmount;
            } else if (GameAnalyzer.isPlayerPFR(g, "Hero") &&
                    GameAnalyzer.didCheckRaiseTurn(g, "Hero")) {
                ++callerCRAmount;
            } else if (GameAnalyzer.didCallerFoldTurn(g, "Hero")) {
                ++callerFoldedAmount;
            }
            if (g.getHeroWinloss() > 0) {
                ++gamesWonAmount;
            } else {
                ++gamesLostAmount;
            }
        }

        setToDefault();
        for (int i = 1; i <= 8; ++i) {
            Label l = (Label) this.anchorPane.lookup("#infoLabel" + i);
            l.setOpacity(1);
        }
        streetLabel.setOpacity(1);

        streetLabel.setText("Flop");
        infoLabel1.setText("Games amount: " + games.size());
        infoLabel2.setText("Hero (PFR) c-bet: " + pfrCBetAmount);
        infoLabel3.setText("Hero (PFR) checked: " + pfrCheckedAmount);
        infoLabel4.setText("Hero (Caller) folded: " + callerFoldedAmount);
        infoLabel5.setText("Hero (Caller) called: " + callerCalledAmount);
        infoLabel6.setText("Hero (Caller) C-R: " + callerCRAmount);
        infoLabel7.setText("Hero won game: " + gamesWonAmount);
        infoLabel8.setText("Hero lost game: " + gamesLostAmount);

        pieChart1.setOpacity(1);
        pieChart1.getData().clear();
        ObservableList<PieChart.Data> pieChart1Data =
                FXCollections.observableArrayList(
                        new PieChart.Data("PFR c-bet", pfrCBetAmount),
                        new PieChart.Data("PFR checked", pfrCheckedAmount),
                        new PieChart.Data("Caller folded", callerFoldedAmount),
                        new PieChart.Data("Caller called", callerCalledAmount),
                        new PieChart.Data("Called C-R", callerCRAmount));

        pieChart1Data.forEach(data ->
                data.nameProperty().bind(Bindings.concat(data.getName(), ": ", (int) Math.floor(data.pieValueProperty().getValue()))));

        pieChart1.getData().addAll(pieChart1Data);

        pieChart2.setOpacity(1);
        pieChart2.getData().clear();


        if (!games.isEmpty()) {
            gamesWonAmount /= games.size();
            gamesWonAmount *= 100;
            gamesLostAmount /= games.size();
            gamesLostAmount *= 100;
        }

        ObservableList<PieChart.Data> pieChart2Data =
                FXCollections.observableArrayList(
                        new PieChart.Data("games won", gamesWonAmount),
                        new PieChart.Data("games lost", gamesLostAmount));

        pieChart2Data.forEach(data ->
                data.nameProperty().bind(Bindings.concat(data.getName(), ": ",
                        new DecimalFormat("#0").format(data.pieValueProperty().doubleValue()), "%")));

        pieChart2.getData().addAll(pieChart2Data);
        pieChart2.setLabelsVisible(false);
    }

    private void initializeRiver() {

    }
}
