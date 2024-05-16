package appinterface.controllers;

import analizer.GameAnalyzer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Game;
import models.PlayerInGame;
import models.UserProfile;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class UserProfileDisplayController {

    @FXML
    private Label flopCBetCallLabel;

    @FXML
    private Label flopCBetLabel;

    @FXML
    private Label flopCheckRaiseLabel;

    @FXML
    private Label flopRaiseLabel;

    @FXML
    private VBox flopVBox;

    @FXML
    private Label preflop3BetLabel;

    @FXML
    private Label preflop4BetLabel;

    @FXML
    private Label preflop5BetLabel;

    @FXML
    private Label preflopPFRLabel;

    @FXML
    private VBox preflopVBox;

    @FXML
    private Label riverCBetLabel;

    @FXML
    private Label riverCallLabel;

    @FXML
    private Label riverLeadLabel;

    @FXML
    private VBox riverVBox;

    @FXML
    private Label riverWShowdownLabel;

    @FXML
    private Label turnCBetCallLabel;

    @FXML
    private Label turnCBetLabel;

    @FXML
    private Label turnCheckRaiseLabel;

    @FXML
    private Label turnRaiseLabel;

    @FXML
    private VBox turnVBox;

    private HashMap<String, Game> games;

    private UserProfile userProfile;

    public void setGamesAndProfile(Map<String, Game> games, UserProfile userProfile) {
        this.userProfile = userProfile;
        this.games = new HashMap<>(games);
        initializeLabels();
    }

    private void initializeLabels() {
        double totalGames = 0;
        double flopGames = 0;
        double turnGames = 0;
        double riverGames = 0;


        double preflopPFRGames = 0;
        double preflop3BetGames = 0;
        double preflop4BetGames = 0;
        double preflop5BetGames = 0;

        double flopCBetGames = 0;
        double flopCheckRaiseGames = 0;
        double flopCBetCallGames = 0;
        double flopRaiseGames = 0;

        double turnCBetGames = 0;
        double turnCheckRaiseGames = 0;
        double turnCBetCallGames = 0;
        double turnRaiseGames = 0;

        double riverCBetGames = 0;
        double riverLeadGames = 0;
        double riverWShowdownGames = 0;
        double riverCallGames = 0;

        for (String id : games.keySet()) {
            if (userProfile.getAllGamesIds().contains(id)) {
                ++totalGames;
                String hash = userProfile.getHashInGame(id);
                Game g = games.get(id);
                if (g.getPlayer(hash) == null) {
                    continue;
                }
                if (GameAnalyzer.isPlayerPFR(g, hash)) {
                    ++preflopPFRGames;
                }
                if (GameAnalyzer.isPlayer3BetRaiser(g, hash)) {
                    ++preflop3BetGames;
                }
                if (GameAnalyzer.isPlayer4BetRaiser(g, hash)) {
                    ++preflop4BetGames;
                }
                if (GameAnalyzer.isPlayer5BetRaiser(g, hash)) {
                    ++preflop5BetGames;
                }

                if (g.getFlop() == null) {
                    continue;
                }
                if (g.getPreFlop().getPlayersAfterBetting().contains(new PlayerInGame(hash))) {
                    ++flopGames;
                }
                if (GameAnalyzer.didCBetFlop(g, hash)) {
                    ++flopCBetGames;
                }
                if (GameAnalyzer.didCheckRaiseFlop(g, hash)) {
                    ++flopCheckRaiseGames;
                }
                if (GameAnalyzer.didCallCBetFlop(g, hash)) {
                    ++flopCBetCallGames;
                }
                if (GameAnalyzer.didRaiseFlop(g, hash)) {
                    ++flopRaiseGames;
                }

                if (g.getTurn() == null) {
                    continue;
                }
                if (g.getFlop().getPlayersAfterBetting().contains(new PlayerInGame(hash))) {
                    ++turnGames;
                }
                if (GameAnalyzer.didCBetTurn(g, hash)) {
                    ++turnCBetGames;
                }
                if (GameAnalyzer.didCheckRaiseTurn(g, hash)) {
                    ++turnCheckRaiseGames;
                }
                if (GameAnalyzer.didCallCBetTurn(g, hash)) {
                    ++turnCBetCallGames;
                }
                if (GameAnalyzer.didRaiseTurn(g, hash)) {
                    ++turnRaiseGames;
                }


                if (g.getRiver() == null) {
                    continue;
                }
                if (g.getTurn().getPlayersAfterBetting().contains(new PlayerInGame(hash))) {
                    ++riverGames;
                }
                if (GameAnalyzer.didCBetRiver(g, hash)) {
                    ++riverCBetGames;
                }
                if (GameAnalyzer.didLeadRiver(g, hash)) {
                    ++riverLeadGames;
                }
                if (GameAnalyzer.didWinAtShowdownRiver(g, hash)) {
                    ++riverWShowdownGames;
                }
                if (GameAnalyzer.didCallRiver(g, hash)) {
                    ++riverCallGames;
                }
            }
        }

        DecimalFormat dcf = new DecimalFormat("#0.00");
        // .format(String)           balanceStr = balanceStr.replace(',', '.');
        if (totalGames != 0) {
            preflopPFRLabel.setText(preflopPFRLabel.getText() + dcf.format((preflopPFRGames / totalGames * 100)) + "%");
            preflop3BetLabel.setText(preflop3BetLabel.getText() + dcf.format((preflop3BetGames / totalGames) * 100) + "%");
            preflop4BetLabel.setText(preflop4BetLabel.getText() + dcf.format((preflop4BetGames / totalGames) * 100) + "%");
            preflop5BetLabel.setText(preflop5BetLabel.getText() + dcf.format((preflop5BetGames / totalGames) * 100) + "%");
        } else {
            preflopPFRLabel.setText("no games");
            preflop3BetLabel.setText("no games");
            preflop4BetLabel.setText("no games");
            preflop5BetLabel.setText("no games");
        }

        if (flopGames != 0) {
            flopCBetLabel.setText(flopCBetLabel.getText() + dcf.format(flopCBetGames / flopGames * 100) + "%");
            flopCheckRaiseLabel.setText(flopCheckRaiseLabel.getText() + dcf.format(flopCheckRaiseGames / flopGames * 100) + "%");
            flopCBetCallLabel.setText(flopCBetCallLabel.getText() + dcf.format(flopCBetCallGames / flopGames * 100) + "%");
            flopRaiseLabel.setText(flopRaiseLabel.getText() + dcf.format(flopRaiseGames / flopGames * 100) + "%");
        } else {
            flopCBetLabel.setText("no games");
            flopCheckRaiseLabel.setText("no games");
            flopCBetCallLabel.setText("no games");
            flopRaiseLabel.setText("no games");
        }

        if (turnGames != 0) {
            turnCBetLabel.setText(turnCBetLabel.getText() + dcf.format(turnCBetGames / turnGames * 100) + "%");
            turnCheckRaiseLabel.setText(turnCheckRaiseLabel.getText() + dcf.format(turnCheckRaiseGames / turnGames * 100) + "%");
            turnCBetCallLabel.setText(turnCBetCallLabel.getText() + dcf.format(turnCBetCallGames / turnGames * 100) + "%");
            turnRaiseLabel.setText(turnRaiseLabel.getText() + dcf.format(turnRaiseGames / turnGames * 100) + "%");
        } else {
            turnCBetLabel.setText("no games");
            turnCheckRaiseLabel.setText("no games");
            turnCBetCallLabel.setText("no games");
            turnRaiseLabel.setText("no games");
        }

        if (riverGames != 0) {
            riverCBetLabel.setText(riverCBetLabel.getText() + dcf.format(riverCBetGames / riverGames * 100) + "%");
            riverCallLabel.setText(riverCallLabel.getText() + dcf.format(riverCallGames / riverGames * 100) + "%");
            riverLeadLabel.setText(riverLeadLabel.getText() + dcf.format(riverLeadGames / riverGames * 100) + "%");
            riverWShowdownLabel.setText(riverWShowdownLabel.getText() + dcf.format(riverWShowdownGames / riverGames * 100) + "%");
        } else {
            riverCBetLabel.setText("no games");
            riverCallLabel.setText("no games");
            riverLeadLabel.setText("no games");
            riverWShowdownLabel.setText("no games");
        }
    }

//    private void setPercentText(Label label) {
//        turnCBetLabel.setText(turnCBetLabel.getText() + dcf.format(turnCBetGames / turnGames * 100) + "%");
//    }


    @FXML
    void initialize() {

    }

}
