package views;
import classes.Joueur;
import classes.Partie;
import com.jfoenix.controls.JFXComboBox;
import controleur.Controleur;
import controleur.erreurs.PartieNonTrouvee;
import controleur.erreurs.PartieTerminee;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import tasks.MenuTask;

import java.io.IOException;
import java.net.URL;

import java.util.*;

/**
 * Created by Quentin on 09/01/2017.
 */
public class MenuVue{

    Controleur monControleur;
    private ObservableList playersOnlineObservableList = FXCollections.observableArrayList();
    private ObservableList playersInGameWaitingObservableList = FXCollections.observableArrayList();

    @FXML
    Pane menu;
    @FXML
    Label labelLogin;
    @FXML
    ListView listConnectedPlayers;
    @FXML
    JFXComboBox CBPlayersInGameWaiting;
    @FXML
    JFXComboBox CBGameToObservate;
    @FXML
    CheckBox partiePublique;
    @FXML
    CheckBox partiePrivee;
    @FXML
    VBox VBmyInvitations;



    public void initialize() {
        //debut timer pour timeout
         Timeline timeline = new Timeline(new KeyFrame(
                Duration.minutes(2),
                ae -> monControleur.disconnection()));
        monControleur.setTimer(timeline);

        this.labelLogin.setText(this.monControleur.getLogin());
        listConnectedPlayers.setItems(playersOnlineObservableList);
        CBPlayersInGameWaiting.setItems(playersInGameWaitingObservableList);
        // task qui met a jour les listes du menu
        MenuTask menuTask = new MenuTask(this.monControleur, this.playersOnlineObservableList, this.playersInGameWaitingObservableList, VBmyInvitations, CBPlayersInGameWaiting);
        Thread th = new Thread(menuTask);
        th.setDaemon(true);
        th.start();
        //enregistrement du thread dans le controleur principal
        monControleur.setMenuThread(th);

        addListenerCheckBoxes(partiePublique, partiePrivee);
    }

    public static MenuVue creerInstance(Controleur c) {
        URL location = MenuVue.class.getResource("/views/menu.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {

        }

        MenuVue vue = fxmlLoader.getController();
        vue.setMonControleur(c);


        vue.initialize();
        return vue;
    }

    public void runJoinGame(ActionEvent actionEvent){
        Image img = new javafx.scene.image.Image("/images/sign-delete-icon.png");
        if(CBPlayersInGameWaiting.getSelectionModel().getSelectedItem()==null){
            Notifications notificationbuilder = Notifications.create()
                    .title("Erreur")
                    .text("Aucune partie sélectionnée.")
                    .graphic(new ImageView(img))
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationbuilder.darkStyle();
            notificationbuilder.show();
        }
        else{
            try {
                this.monControleur.joinGameAsPlayer((String) CBPlayersInGameWaiting.getSelectionModel().getSelectedItem());
            } catch (PartieNonTrouvee partieNonTrouvee) {
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(partieNonTrouvee.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();
            } catch (PartieTerminee partieTerminee){
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(partieTerminee.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();
            }
        }
    }

    public void runCreateGame(ActionEvent actionEvent){
        if (!(this.partiePublique.isSelected() || this.partiePrivee.isSelected())){
            Image img = new javafx.scene.image.Image("/images/sign-delete-icon.png");
            Notifications notificationbuilder = Notifications.create()
                    .title("Erreur")
                    .text("Il faut choisir un type de partie.")
                    .graphic(new ImageView(img))
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationbuilder.darkStyle();
            notificationbuilder.show();
        }else{
            this.monControleur.createGame(this.partiePrivee.isSelected());
        }

    }

    public void runDisconnection(ActionEvent actionEvent){
        this.monControleur.disconnection();
    }

    public Node getNode(){return menu;}

    public void setMonControleur(Controleur monControleur){this.monControleur = monControleur;}

    public void addListenerCheckBoxes(CheckBox a, CheckBox b){
        a.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                b.setSelected(!newValue);
            }
        });
        b.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                a.setSelected(!newValue);
            }
        });
    }
}