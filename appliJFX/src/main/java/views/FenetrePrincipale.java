package views;

import controleur.Controleur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Quentin on 09/01/2017.
 */
public class FenetrePrincipale {

    private Controleur monControleur;


    @FXML
    BorderPane maFenetre;
    ConnexionVue connexionVue;
    InscriptionVue inscriptionVue;

    private Stage monTheatre;

    //---------------change view-----------------

    public void setConnexionVue() {
        try {
            createContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(this.monControleur.getMenuThread() != null)
            this.monControleur.getMenuThread().interrupt();

        this.maFenetre.setCenter(this.connexionVue.getNode());
        this.monTheatre.show();
    }

    public void setInscripionVue() {
        this.maFenetre.setCenter(this.inscriptionVue.getNode());
        this.monTheatre.show();
    }

    public void setMenuVue(){
        this.monTheatre.setHeight(800);
        this.monTheatre.setWidth(1000);
        this.maFenetre.setCenter(MenuVue.creerInstance(this.monControleur).getNode());
        this.monTheatre.show();
    }

    public void setGameVue(){
        if(this.monControleur.getMenuThread() != null)
            this.monControleur.getMenuThread().interrupt();
        this.monTheatre.setHeight(1000);
        this.monTheatre.setWidth(1000);
        this.maFenetre.setCenter(GameVue.creerInstance(this.monControleur).getNode());
        this.monTheatre.show();
    }

    //---------------end change view-----------------

    public static FenetrePrincipale creerInstance(Controleur c) {
        URL location = FenetrePrincipale.class.getResource("/views/fenetrePrincipale.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FenetrePrincipale vue = fxmlLoader.getController();
        vue.setMonControleur(c);

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Stratego");
        primaryStage.setScene(new Scene(root, 1000, 800));

        vue.setMonTheatre(primaryStage);
        vue.handlerUserClosedWindow();
        return vue;
    }

    public void createContent() throws Exception{


        Image background = new Image(String.valueOf(FenetrePrincipale.class.getResource("/images/background1.jpg")));
        ImageView imageView = new ImageView(background);

        maFenetre.getChildren().add(imageView);
    }

    public void handlerUserClosedWindow(){
        monTheatre.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                monControleur.userClosedWindow();
            }
        });
    }

    public void setMonTheatre(Stage monTheatre) {
        this.monTheatre = monTheatre;
    }

    public void setMonControleur(Controleur monControleur) {

        this.monControleur = monControleur;
        this.connexionVue = ConnexionVue.creerInstance(monControleur);
        this.inscriptionVue = InscriptionVue.creerInstance(monControleur);
    }

}
