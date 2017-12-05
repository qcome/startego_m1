package views;

import controleur.Controleur;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import tasks.GameTask;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Created by Quentin on 11/01/2017.
 */
public class GameVue {
    Controleur monControleur;
    @FXML
    BorderPane BPgame;
    @FXML
    Label lJoueur;
    @FXML
    Label lAdversaire;
    @FXML
    GridPane GPplateau;
    @FXML
    HBox HBpiecesRestantes;
    @FXML
    StackPane PaneTop;

    public void initialize(){
        String login = monControleur.getLogin();
        String colorPlayer = monControleur.getJoueur(login).getColor();

        /* labels adversaire & joueur */
        lJoueur.setText(lJoueur.getText() + monControleur.getLogin());
        if(monControleur.getMyGame(login).getOtherPlayer(login) != null)
            lAdversaire.setText(lAdversaire.getText() + monControleur.getMyGame(login).getOtherPlayer(login).getLogin());
        else
            lAdversaire.setText(lAdversaire.getText() + "En attente...");
        Label lStatutGame = new Label(monControleur.getMyGame(login).getState());
        lStatutGame.setFont(new Font(36));
        PaneTop.getChildren().add(lStatutGame);
        PaneTop.setAlignment(lStatutGame, Pos.TOP_CENTER);

        /* grille */
        GPplateau.setBackground(getBackground("/images/board.jpg"));


        /* Task GameTask */
        GameTask gameTask = new GameTask(monControleur, lAdversaire,BPgame, PaneTop,GPplateau, HBpiecesRestantes, monControleur.getMyGame(login).getIsPrivate());
        Thread th = new Thread(gameTask);
        th.setDaemon(true);
        th.start();
    }

    public Background getBackground (String path){
        Image image = new Image(String.valueOf(GameVue.class.getResource(path)));
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        return background;
    }

    public static GameVue creerInstance(Controleur c) {
        URL location = GameVue.class.getResource("/views/game.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {

        }

        GameVue vue = fxmlLoader.getController();

        vue.setMonControleur(c);

        vue.initialize();
        return vue;
    }

    public void runLeftGame(){
        monControleur.leaveGame();
    }

    public Node getNode(){return BPgame;}

    public void setMonControleur(Controleur monControleur){this.monControleur = monControleur;}
}
