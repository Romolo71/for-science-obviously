package it.rtonini.forscienceobviously;

import it.rtonini.forscienceobviously.game.GamePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        GamePane gamePane = new GamePane();
        Scene scene = new Scene(gamePane, 800, 600);
        stage.setTitle("Tower Defense");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}