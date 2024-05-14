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

        Scene scene = new Scene(fxmlLoader.load(), 660, 440);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}