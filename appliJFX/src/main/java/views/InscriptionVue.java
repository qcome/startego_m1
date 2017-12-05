package views;

import controleur.Controleur;
import controleur.erreurs.CouplePasswordInvalide;
import controleur.erreurs.LoginDejaPris;
import controleur.erreurs.LoginInvalide;
import controleur.erreurs.MotDePasseInvalide;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx. control.Notifications;

import javafx.event.ActionEvent;

import java.awt.*;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Quentin on 09/01/2017.
 */
public class InscriptionVue {

    private Controleur monControleur;

    @FXML
    Pane inscription;

    @FXML
    TextField login;

    @FXML
    PasswordField password;

    @FXML
    PasswordField passwordConfirmation;

    public Node getNode(){
        return this.inscription;
    }

    public static InscriptionVue creerInstance(Controleur c) {
        URL location = ConnexionVue.class.getResource("/views/inscription.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = null;
        try {
            root = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InscriptionVue vue = fxmlLoader.getController();
        vue.setMonControleur(c);
        return vue;
    }

    public void setMonControleur(Controleur monControleur) {
        this.monControleur = monControleur;
    }


    public void runInscription(ActionEvent actionEvent) {
        javafx.scene.image.Image img = new javafx.scene.image.Image("/images/sign-delete-icon.png");
        if (this.login.getText().equals("") || this.password.getText().equals("") || this.passwordConfirmation.equals("")) {
            Notifications notificationbuilder = Notifications.create()
                    .title("Erreur")
                    .text("Erreur lors de la saisie")
                    .graphic(new ImageView(img))
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationbuilder.darkStyle();
            notificationbuilder.show();

        } else {
            try {
                this.monControleur.inscription(this.login.getText(), this.password.getText(), this.passwordConfirmation.getText());
            } catch (CouplePasswordInvalide couplePasswordInvalide) {
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(couplePasswordInvalide.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();

            } catch (LoginDejaPris loginDejaPris) {
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(loginDejaPris.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();
            } catch (LoginInvalide loginInvalide) {
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(loginInvalide.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();
            } catch (MotDePasseInvalide motDePasseInvalide) {
                Notifications notificationbuilder = Notifications.create()
                        .title("Erreur")
                        .text(motDePasseInvalide.getMessage())
                        .graphic(new ImageView(img))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.TOP_RIGHT);
                notificationbuilder.darkStyle();
                notificationbuilder.show();
            }
        }
    }
    public void cancelInscription(ActionEvent actionEvent){
        this.monControleur.goToConnexion();
    }
}
