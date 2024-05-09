package appinterface;

import appinterface.controllers.HandsEVController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Game;
import models.GamesSet;
import parsers.gg.GGPokerokHoldem9MaxParser;

import java.io.IOException;
import java.util.ArrayList;

public class PSAApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PSAApplication.class.getResource("views/homeView.fxml"));

//        GGPokerokHoldem9MaxParser parser = new GGPokerokHoldem9MaxParser();
//        ArrayList<Game> parsedGames = null;
//        try {
//            parsedGames = parser.parseFile("sddrc/test/resources/ggPokerokFiles/gamesFiles/holdem9Max/gameSessionGames.txt");
//        } catch (Exception ignored) {
//            System.out.println("AA");
//        }

//        FXMLLoader fxmlLoader = new FXMLLoader(PSAApplication.class.getResource("views/handsEVView.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
//        HandsEVController controller = fxmlLoader.getController();
//        controller.setGamesAndUpdateTable(parsedGames);
//
//        GamesSet testGameSet = new GamesSet();
//        testGameSet.addGames(parsedGames);

//        controller.setGamesSet(testGameSet);

        // Working one - main one
//      FXMLLoader fxmlLoader = new FXMLLoader(PSAApplication.class.getResource("views/gamesListView.fxml"));

        stage.show();

//        Scene scene = new Scene(fxmlLoader.load(), 660, 440);
//        stage.setScene(scene);
//        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}