package views;

import controleur.Controleur;
import controleur.erreurs.CoupleLoginPasswordInvalide;
import controleur.erreurs.PlayerAlreadyConnected;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javafx.event.ActionEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Quentin on 09/01/2017.
 */
public class ConnexionVue {

    @FXML
    Pane connexion;

    @FXML
    TextField login;

    @FXML
    PasswordField password;

    private Controleur monControleur;

    public Node getNode() {
        return connexion;
    }

    public static ConnexionVue creerInstance(Controleur c) {
        URL location = ConnexionVue.class.getResource("/views/connexion.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConnexionVue vue = fxmlLoader.getController();
        vue.setMonControleur(c);
        return vue;
    }
    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }

    public void runConnexion(ActionEvent actionEvent) {
        javafx.scene.image.Image img = new javafx.scene.image.Image("/images/sign-delete-icon.png");
        if ((this.login.getText().length()>0) && (this.password.getText().length()>0)) {
            try {
                this.monControleur.connexion(this.login.getText(),this.password.getText());
            }catch(CoupleLoginPasswordInvalide coupleLoginPasswordInvalide) {
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(coupleLoginPasswordInvalide.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();
            }catch (PlayerAlreadyConnected playerAlreadyConnected){
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(playerAlreadyConnected.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();
            }
        }else{
            Notifications notificationbuilder = Notifications.create()
                    .title("Erreur")
                    .text("Les champs doivent être remplis !")
                    .graphic(new ImageView(img))
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationbuilder.darkStyle();
            notificationbuilder.show();
        }

    }
    public void runInscription(ActionEvent actionEvent){this.monControleur.goToInscription();}


}
