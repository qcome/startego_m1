package application;

import controleur.Controleur;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Quentin on 08/01/2017.
 */
public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        Controleur monControleur = new Controleur();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
