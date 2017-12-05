package controleur;

import classes.*;
import classes.exceptions.*;
import controleur.erreurs.*;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import rmiService.MonService;
import tasks.GameTask;
import views.FenetrePrincipale;

import java.io.EOFException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * Created by Quentin on 09/01/2017.
 */
public class Controleur {

    private FenetrePrincipale maFenetrePrincipale;

    private MonService facade;

    private String login;

    private static String HOST = "127.0.0.1";

    private Thread menuThread;

    private String pieceSelectionneePhase1 = "";

    private boolean hasDisconnected = false;

    private Timeline timeline;

    public Controleur(){
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(HOST, 9345);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        String[] names = new String[0];
        try {
            names = registry.list();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        for(String name1 : names){
            System.out.println("~~~~" + name1 + "~~~~");
        }
        try {
            this.facade  = (MonService) registry.lookup(MonService.serviceName);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        this.maFenetrePrincipale = FenetrePrincipale.creerInstance(this);
        this.maFenetrePrincipale.setConnexionVue();
    }

    public void accepterInvitation(String login, String loginAdversary) throws PartieDejaCommencee, PartieNonTrouvee {
        try {
            this.facade.acceptInvitation(login, loginAdversary);
            timeline.stop();
            this.maFenetrePrincipale.setGameVue();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (GameAlreadyBegunException e) {
            throw new PartieDejaCommencee("La partie a déjà commencé.");
        } catch (GameNotFoundException e) {
            throw new PartieNonTrouvee("La partie est introuvable.");
        }
    }

    public Map getMyInvitations(String login){
        try {
            return this.facade.getMyInvitations(login);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void connexion(String login, String password) throws CoupleLoginPasswordInvalide, PlayerAlreadyConnected {
        try {
            facade.connection(login, password);
            this.login = login;
            this.maFenetrePrincipale.setMenuVue();
        } catch(UnknownPlayerException e)
        {
            throw new CoupleLoginPasswordInvalide("Couple d'identifiants inexistants !");
        }
        catch (PlayerAlreadyConnectedException e){
            throw new PlayerAlreadyConnected("Joueur déjà connecté!");
        }
        catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public void inscription(String login, String password, String passwordConfirmation) throws CouplePasswordInvalide, LoginDejaPris, LoginInvalide, MotDePasseInvalide {
        Image img = new Image("/images/sign-check-icon.png");
        try{
            facade.registration(login, password, passwordConfirmation);
            this.maFenetrePrincipale.setConnexionVue();
            Notifications notificationbuilder = Notifications.create()
                    .title("Succès")
                    .text("Inscription validée")
                    .graphic(new ImageView(img))
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.TOP_RIGHT);
            notificationbuilder.darkStyle();
            notificationbuilder.show();
        }catch(LoginAlreadyTakenException e){
            throw new LoginDejaPris("Le login est déjà existant.");
        }catch (RemoteException e){
            e.printStackTrace();
        } catch (PasswordConfirmationException e) {
            throw new CouplePasswordInvalide("Les mots de passe ne correspondent pas.");
        } catch (LoginTooShortException e) {
            throw new LoginInvalide("Votre login doit faire au moins 4 caractères.");
        } catch (PasswordTooShortException e) {
            throw new MotDePasseInvalide("Votre mot de passe doit faire au moins 4 caractères.");
        }
    }

    public void createGame(Boolean isPrivate){
        try {
            facade.createNewGame(this.login, isPrivate);
            timeline.stop();
            this.maFenetrePrincipale.setGameVue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void joinGameAsPlayer(String loginAdversary) throws PartieNonTrouvee, PartieTerminee {
        try {
            if(facade.getMyGame(loginAdversary).getState().equals("En attente")){
                try {
                    facade.joinGame(facade.getMyGame(loginAdversary), login);
                    this.maFenetrePrincipale.setGameVue();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (GameNotFoundException e) {
                    throw new PartieNonTrouvee("La partie est introuvable.");
                } catch (GameOverException e) {
                    throw new PartieTerminee("La partie est terminée.");
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void leaveGame(){
        try {
            facade.leaveGame(login);
            this.maFenetrePrincipale.setMenuVue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setTimer(Timeline timeline){
        this.timeline = timeline;
        this.timeline.play();
    }

    public void disconnection(){
        try {
            facade.disconnection(login);
            timeline.stop();
            this.maFenetrePrincipale.setConnexionVue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Map getStockPlayer(String login){
        try {
            return facade.getMyStock(login).getStock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void passerTour(String login){
        try {
            facade.incrementRound(login);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void declinerInvitation(String login, String loginPlayerRequest){
        try {
            facade.declineInvitation(login, loginPlayerRequest);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void userClosedWindow(){
        if (this.login != null) {
            try {
                facade.disconnection(login);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if(this.menuThread != null)
                this.getMenuThread().interrupt();
        }
        Platform.exit();
    }

    public Partie getMyGame(String login){
        try {
            return this.facade.getMyGame(login);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Joueur getJoueur(String login){
        try {
            return this.facade.getPlayersOnline().get(login);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRemainingPiecesOfType(String nomPiece, String login){
        try {
            return (this.facade.getMyStock(login).getStock().get(nomPiece).size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void placerPiece(String login, String nomPiece, int ligne, int colonne) throws PieceNonDisponible, MouvementPieceImpossible {
        try {
            this.facade.putPiece(login, TypePiece.valueOf(nomPiece), ligne, colonne);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (PieceAlreadyPlacedException e) {
            throw new PieceNonDisponible("La piece sélectionnée n'est plus disponible.");
        } catch (MoveImpossibleException e) {
            throw new MouvementPieceImpossible("Mouvement impossible");
        }
    }

    public boolean deplacerPiece(String login, int line, int column, int lineBis, int columnBis) throws MouvementPieceImpossible {
        try {
            return(this.facade.movePiece(login, line, column, lineBis, columnBis));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MoveImpossibleException e) {
            throw new MouvementPieceImpossible("Mouvement impossible");
        }
        return false;
    }

    public List<String> getListPlayersInGameWaiting(){
        List<String> listPlayersInGameWaiting = new ArrayList<String>();
        try {
            for (Map.Entry<String, Partie> entry : this.facade.getUniqueGames().entrySet()) {
                if(Objects.equals(entry.getValue().getState(), "En attente") && !entry.getValue().getIsPrivate())
                    listPlayersInGameWaiting.add(entry.getKey());
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return listPlayersInGameWaiting;
    }

    public List<String> getListConnectedUsersNotInGame(){
        List<String> listConnectedUsersNotInGame = new ArrayList<String>();
        try {
            for (String key : this.facade.getPlayersOnline().keySet()) {
                if(!this.facade.getPlayersInGame().containsKey(key))
                    listConnectedUsersNotInGame.add(key);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return listConnectedUsersNotInGame;
    }

    public void envoyerInvitation(Partie game, String login, String guestLogin) throws ImpossibleRejoindrePartie {
        try {
            this.facade.sendInvitation(game, login, guestLogin);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (PlayerAlreadyInGameException e) {
            throw new ImpossibleRejoindrePartie("La partie a déjà commencé ou n'existe plus.");
        }
    }

    public List<String> getListConnectedUsers(){
        List<String> listConnectedUsers = new ArrayList<String>();
        try {
            for (String key : this.facade.getPlayersOnline().keySet()) {
                listConnectedUsers.add(key);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        return listConnectedUsers;
    }

    public Plateau getPlateau(String login){
        try {
            return facade.getMyBoard(getMyGame(login));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void goToInscription(){this.maFenetrePrincipale.setInscripionVue();}

    public void goToConnexion(){this.maFenetrePrincipale.setConnexionVue();}

    public String getLogin() {return login;}

    public Thread getMenuThread() {return this.menuThread;}

    public void setMenuThread(Thread menuThread) {this.menuThread = menuThread;}

    public String getPieceSelectionneePhase1() {return pieceSelectionneePhase1;}

    public void setPieceSelectionneePhase1(String pieceSelectionneePhase1) {this.pieceSelectionneePhase1 = pieceSelectionneePhase1;}
}
