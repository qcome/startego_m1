package tasks;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import controleur.Controleur;
import controleur.erreurs.PartieDejaCommencee;
import controleur.erreurs.PartieNonTrouvee;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;


import java.util.*;

/**
 * Created by Quentin on 11/01/2017.
 */
public class MenuTask extends Task {
    private Controleur monControleur;
    private JFXComboBox CBPlayersInGameWaiting;
    private ObservableList<String> playersOnlineObservableList;
    private ObservableList<String> playersInGameWaitingObservableList;
    private ObservableList invitationsObservableList;
    private List<String> listeTemporaire = new ArrayList<>();
    private List<String> listeSaveInvitations = new ArrayList<>();
    private VBox VBmyInvitations;
    private Map<String, HBox> mapInvitations = new Hashtable();
    private String myLogin;

    public MenuTask(Controleur monControleur, ObservableList playersOnlineObservableList, ObservableList playersInGameWaitingObservableList, VBox VBmyInvitations, JFXComboBox CBPlayersInGameWaiting){
        this.monControleur = monControleur;
        this.CBPlayersInGameWaiting = CBPlayersInGameWaiting;
        this.playersOnlineObservableList = playersOnlineObservableList;
        this.playersInGameWaitingObservableList = playersInGameWaitingObservableList;
        this.VBmyInvitations = VBmyInvitations;
        this.invitationsObservableList = VBmyInvitations.getChildren();
        this.myLogin = monControleur.getLogin();
        //this.VBmyInvitations.alignmentProperty().setValue(Pos.TOP_CENTER);
    }
    private void addButtonCss(JFXButton myButton, boolean accepter){
        myButton.setPrefHeight(20.0);
        myButton.setPrefWidth(80.0);
        if(accepter) {
            myButton.setStyle("-fx-background-color: #2196F3;");
        }
        else
            myButton.setStyle("-fx-background-color: #e45353;");
        myButton.setTextFill(Color.WHITE);
    }
    @Override
    protected Void call() throws Exception {
        while (true) {
            //----------------------------maj liste joueurs en ligne---------------------------
            listeTemporaire.clear();
            for (String login : monControleur.getListConnectedUsers()) {
                if (!Objects.equals(myLogin, login)) {
                    listeTemporaire.add(login);
                    if (!playersOnlineObservableList.contains(login)){
                        Platform.runLater(() ->playersOnlineObservableList.add(login));
                    }
                }
            }
            for (String login : playersOnlineObservableList){
                if(!listeTemporaire.contains(login)) {
                    Platform.runLater(() ->playersOnlineObservableList.remove(login));
                }
            }

            //------------------maj liste joueurs en attente d'adversaire-----------------------
            listeTemporaire.clear();
            boolean affichePremierePartieDefault = false;
            for (String login : monControleur.getListPlayersInGameWaiting()) {
                if (!affichePremierePartieDefault) {
                    Platform.runLater(() -> CBPlayersInGameWaiting.getSelectionModel().select(login));
                    affichePremierePartieDefault = true;
                }
                listeTemporaire.add(login);
                if(!playersInGameWaitingObservableList.contains(login)) {
                    Platform.runLater(() -> playersInGameWaitingObservableList.add(login));
                }
            }
            for (String login : playersInGameWaitingObservableList){
                if(!listeTemporaire.contains(login)){
                    Platform.runLater(() ->playersInGameWaitingObservableList.remove(login));
                }
            }



            //-------------------------update invitations-------------------------------------
            listeTemporaire.clear();
            Platform.runLater(() ->VBmyInvitations.getChildren().clear());
            for (Object login : monControleur.getMyInvitations(myLogin).keySet()) {
                    StackPane ligneInvitation = new StackPane();
                    Label label = new Label((String) login);
                    //Platform.runLater(()->label.setLayoutY(20));
                    JFXButton buttonAccept = new JFXButton("Accepter");
                    JFXButton buttonDecline = new JFXButton("Décliner");
                    //Platform.runLater(() ->buttonAccept.setPadding(new Insets(0, 5, 0, 5)));
                    buttonAccept.setId((String) login);
                    buttonDecline.setId((String) login);
                    addButtonCss(buttonAccept, true);
                    addButtonCss(buttonDecline, false);
                    Platform.runLater(() -> {
                        ligneInvitation.setPrefHeight(30);
                        ligneInvitation.getChildren().addAll(label, buttonAccept, buttonDecline);
                        ligneInvitation.setAlignment(label, Pos.CENTER_LEFT);
                        ligneInvitation.setAlignment(buttonDecline, Pos.CENTER_RIGHT);
                        VBmyInvitations.getChildren().add(ligneInvitation);
                    });

                    buttonDecline.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String loginPlayerRequest = ((Control)event.getSource()).getId();
                            monControleur.declinerInvitation(myLogin, loginPlayerRequest);
                            Platform.runLater(() -> mapInvitations.get(login).getChildren().clear());
                        }
                    });
                    buttonAccept.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            Image img = new javafx.scene.image.Image("/images/sign-delete-icon.png");
                            String loginAdversary = ((Control)event.getSource()).getId();
                            try {
                                monControleur.accepterInvitation(myLogin, loginAdversary);
                            } catch (PartieDejaCommencee partieDejaCommencee) {
                                Notifications notificationbuilder = Notifications.create()
                                        .title("Erreur")
                                        .text(partieDejaCommencee.getMessage())
                                        .graphic(new ImageView(img))
                                        .hideAfter(Duration.seconds(10))
                                        .position(Pos.TOP_RIGHT);
                                notificationbuilder.darkStyle();
                                notificationbuilder.show();
                            } catch (PartieNonTrouvee partieNonTrouvee) {
                                Notifications notificationbuilder = Notifications.create()
                                        .title("Erreur")
                                        .text(partieNonTrouvee.getMessage())
                                        .graphic(new ImageView(img))
                                        .hideAfter(Duration.seconds(10))
                                        .position(Pos.TOP_RIGHT);
                                notificationbuilder.darkStyle();
                                notificationbuilder.show();
                            }
                        }
                    });




            }



            try{
                Thread.sleep(5000);
            }catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;

    }
}
