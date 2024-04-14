package appinterface.controllers;

import analizer.GameAnalyzer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import models.Game;
import org.controlsfx.control.CheckComboBox;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FilterSearchController {
    @FXML
    private CheckComboBox<Double> bbSizeCheckComboBox;

    @FXML
    private Button clearButton;

    @FXML
    private CheckBox unraisedCheckBox;
    @FXML
    private CheckBox fiveBetCheckBox;

    @FXML
    private CheckBox fourBetCheckBox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private CheckComboBox<String> gameTypeCheckComboBox;

    @FXML
    private CheckComboBox<String> playersPostFlopCheckComboBox;

    @FXML
    private CheckComboBox<String> roomCheckComboBox;

    @FXML
    private CheckComboBox<String> heroRoleComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private CheckBox sprCheckBox;

    @FXML
    private CheckBox threeBetCheckBox;

    @FXML
    private DatePicker toDatePicker;

    private Set<Game> unfilteredGames;
    private Set<Game> gamesAfterFilter;

    public void setUnfilteredGames(Set<Game> games) {
        this.unfilteredGames = games;
    }

    public Set<Game> getGamesAfterFilter() {
        return gamesAfterFilter;
    }

    private void setToDefault() {
        bbSizeCheckComboBox.getCheckModel().clearChecks();
        playersPostFlopCheckComboBox.getCheckModel().clearChecks();
        roomCheckComboBox.getCheckModel().clearChecks();
        gameTypeCheckComboBox.getCheckModel().clearChecks();
        sprCheckBox.setSelected(true);
        threeBetCheckBox.setSelected(true);
        fourBetCheckBox.setSelected(true);
        fourBetCheckBox.setSelected(true);
        fiveBetCheckBox.setSelected(true);

        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
    }

    @FXML
    public void initialize() {
        bbSizeCheckComboBox.getItems().addAll(FXCollections.observableArrayList(0.02, 0.05, 0.10, 0.25));
        playersPostFlopCheckComboBox.getItems().addAll(FXCollections.observableArrayList("Heads-up", "Multi-way", "Folded"));
        roomCheckComboBox.getItems().addAll(FXCollections.observableArrayList("GGPokerok"));
        gameTypeCheckComboBox.getItems().addAll(FXCollections.observableArrayList("Rush`n`Cash", "8 max 3 Blinds Holdem"));
        heroRoleComboBox.getItems().addAll(FXCollections.observableArrayList("limper", "srpC", "srpR", "3bC", "3bR", "4bC", "4bR", "5bC", "5bR"));

        searchButton.setOnMouseClicked(action -> {
            gamesAfterFilter = searchFilteredGames(unfilteredGames);
            this.searchButton.getScene().getWindow().hide();
        });
        clearButton.setOnMouseClicked(action -> {
            setToDefault();
        });
    }

    public Set<Game> searchFilteredGames(Set<Game> unfilteredGames) {
        if (unfilteredGames == null) {
            return new HashSet<>();
        }
        if (unfilteredGames.size() == 0) {
            return unfilteredGames;
        }

        HashSet<Double> chosenBBSizes =
                new HashSet<>(bbSizeCheckComboBox.getCheckModel().getCheckedItems().stream().toList());

        Set<Game> filteredGames = new HashSet<>(unfilteredGames);

        LocalDate localDate = fromDatePicker.getValue();
        Date filteredFromDate;
        if (localDate == null) {
            filteredFromDate = null;
        } else {
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            filteredFromDate = Date.from(instant);
        }

        localDate = toDatePicker.getValue();
        Date filteredToDate;
        if (localDate == null) {
            filteredToDate = null;
        } else {
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            filteredToDate = Date.from(instant);
        }

        boolean headsUpFilter = playersPostFlopCheckComboBox.getCheckModel().isChecked(0);
        boolean multiWayFilter = playersPostFlopCheckComboBox.getCheckModel().isChecked(1);
        boolean foldedFilter = playersPostFlopCheckComboBox.getCheckModel().isChecked(2);
        if (!headsUpFilter && !multiWayFilter && !foldedFilter) {
            headsUpFilter = true;
            multiWayFilter = true;
            foldedFilter = true;
        }

        boolean isHeroLimperChosen = heroRoleComboBox.getCheckModel().isChecked(0);
        boolean isHeroSRPCallerChosen = heroRoleComboBox.getCheckModel().isChecked(1);
        boolean isHeroSRPRaiserChosen = heroRoleComboBox.getCheckModel().isChecked(2);
        boolean isHero3betCallerChosen = heroRoleComboBox.getCheckModel().isChecked(3);
        boolean isHero3betRaiserChosen = heroRoleComboBox.getCheckModel().isChecked(4);
        boolean isHero4betCallerChosen = heroRoleComboBox.getCheckModel().isChecked(5);
        boolean isHero4betRaiserChosen = heroRoleComboBox.getCheckModel().isChecked(6);
        boolean isHero5betCallerChosen = heroRoleComboBox.getCheckModel().isChecked(7);
        boolean isHero5betRaiserChosen = heroRoleComboBox.getCheckModel().isChecked(8);

        HashSet<String> chosenGamesTypes =
                new HashSet<>(gameTypeCheckComboBox.getCheckModel().getCheckedItems().stream().toList());
        boolean rncChosen = chosenGamesTypes.contains("Rush`n`Cash");
        boolean nl8MaxChosen3Blinds = chosenGamesTypes.contains("8 max 3 Blinds Holdem");
        if (!rncChosen && !nl8MaxChosen3Blinds) {
            rncChosen = true;
            nl8MaxChosen3Blinds = true;
        }

        boolean isUnraisedChosen = unraisedCheckBox.isSelected();
        boolean isSPRChosen = sprCheckBox.isSelected();
        boolean is3BetChosen = threeBetCheckBox.isSelected();
        boolean is4BetChosen = fourBetCheckBox.isSelected();
        boolean is5BetChosen = fiveBetCheckBox.isSelected();


        for (Game g : unfilteredGames) {
            if (chosenBBSizes.size() != 0 && !chosenBBSizes.contains(g.getBigBlindSize$())) {
                filteredGames.remove(g);
                continue;
            }

            if (filteredFromDate != null && filteredFromDate.after(g.getDate())) {
                filteredGames.remove(g);
                continue;
            }

            if (filteredToDate != null && filteredToDate.before(g.getDate())) {
                filteredGames.remove(g);
                continue;
            }

            if (g.getPreFlop().getPlayersAfterBetting().size() > 2 && !multiWayFilter ||
                    g.getPreFlop().getPlayersAfterBetting().size() == 2 && !headsUpFilter ||
                    g.getPreFlop().getPlayersAfterBetting().size() == 1 && !foldedFilter) {
                filteredGames.remove(g);
                continue;
            }

            if (!rncChosen && g.getGameId().startsWith("RC")) {
                filteredGames.remove(g);
                continue;
            }
            if (!nl8MaxChosen3Blinds &&
                    g.getGameId().startsWith("HD")) {
                filteredGames.remove(g);
                continue;
            }

            if (GameAnalyzer.isUnRaised(g) && !isUnraisedChosen ||
                    GameAnalyzer.isPotSingleRaised(g) && !isSPRChosen ||
                    GameAnalyzer.isPot3Bet(g) && !is3BetChosen ||
                    GameAnalyzer.isPot4Bet(g) && !is4BetChosen ||
                    GameAnalyzer.isPot5PlusBet(g) && !is5BetChosen) {
                filteredGames.remove(g);
            }

//            if (TODO(g) && !filter ||
//                    GameAnalyzer.isHeroSRPCaller(g) && !isHeroSRPCallerChosen
//            TODO(g) && !filter) {
//                filteredGames.remove(g);
//            }
        }
        return filteredGames;
    }
}
