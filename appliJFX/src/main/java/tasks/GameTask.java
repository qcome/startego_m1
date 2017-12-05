package tasks;

import classes.Case;
import classes.Partie;
import controleur.Controleur;
import controleur.erreurs.ImpossibleRejoindrePartie;
import controleur.erreurs.MouvementPieceImpossible;
import controleur.erreurs.PieceNonDisponible;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import myclasses.MyImageView;
import org.controlsfx.control.Notifications;
import views.GameVue;
import java.util.*;

/**
 * Created by Quentin on 11/01/2017.
 */
public class GameTask extends Task<Void> {
    private Controleur monControleur;
    private Partie myGame;
    private String login;
    private String loginAdversary;
    private Label lAdversaire;
    private StackPane PaneTop;
    private VBox VBleft;
    private VBox VBright;
    private GridPane GPplateau;
    private String colorPlayer;
    private String colorAdversary;
    private HBox HBpiecesRestantes;
    private int linePieceSelectionneePourDeplacement = -1;
    private int columnPieceSelectionneePourDeplacement = -1;
    private Case[][] myBoard;
    private Label infoTurn = new Label();
    private AnchorPane anchorPane;
    private int cptTimer;
    private Thread timer;

    private boolean hasStartedPhase2 = false;
    public boolean isMyTurn;
    public boolean timerIsAlive = false;
    private boolean stopTurn = false;
    private boolean waitingForPlayer;
    private boolean phase1 = false;
    private boolean phase2;
    private boolean victoryByDeplacement = false;
    private boolean gameFinished = false;
    private boolean isPrivate;

    private List<String> listeTemporaire = new ArrayList<>();
    private Map <String, Integer> piecesPerduesAdversary = new Hashtable<>();
    private Map <String, Integer> piecesPerduesPlayer = new Hashtable<>();
    private Map <String, Label> labelPiecesPerduesPlayer = new Hashtable<>();
    private Map <String, Label> labelpiecesPerduesAdversary = new Hashtable<>();
    private Map <String, Integer> nbMaxPieceType = new Hashtable<>();

    public GameTask(Controleur monControleur, Label lAdversaire, BorderPane maFenetre, StackPane PaneTop, GridPane GPplateau, HBox HBpiecesRestantes, boolean isPrivate){
        this.monControleur = monControleur;
        this.isPrivate = isPrivate;
        this.VBleft = (VBox) maFenetre.getLeft();
        this.VBright = (VBox) maFenetre.getRight();
        this.PaneTop = PaneTop;
        this.GPplateau = GPplateau;
        this.login = monControleur.getLogin();
        this.myGame = monControleur.getMyGame(this.login);
        this.lAdversaire = lAdversaire;
        this.colorPlayer =  monControleur.getJoueur(login).getColor();
        this.HBpiecesRestantes = HBpiecesRestantes;
        this.anchorPane = (AnchorPane) HBpiecesRestantes.getParent();
        if(Objects.equals(this.myGame.getState(), "En attente")){
            this.loginAdversary = "";
            this.waitingForPlayer = true;
        }else{
            this.loginAdversary = this.myGame.getOtherPlayer(login).getLogin();
            this.waitingForPlayer = false;
        }

    }

    @Override
    protected Void call() throws Exception {
        //-----------------------------En attente partie privée--------------------------------
        if (isPrivate && waitingForPlayer){
            ObservableList<String> listConnectedPlayersInMenu = FXCollections.observableArrayList();
            Button BinvitePlayer = new Button("Inviter");
            ListView LVinvitePlayers = new ListView();
            LVinvitePlayers.setMaxHeight(150);
            Platform.runLater(() ->{
                VBright.getChildren().add(new Label("Inviter un joueur:"));
                VBright.setAlignment(Pos.TOP_CENTER);
                LVinvitePlayers.setItems(listConnectedPlayersInMenu);
                VBright.getChildren().add(LVinvitePlayers);
                VBright.getChildren().add(BinvitePlayer);
            });
            //quand une invitation se fait
            BinvitePlayer.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    try {
                        monControleur.envoyerInvitation(monControleur.getMyGame(login), login, (String) LVinvitePlayers.getSelectionModel().getSelectedItem());
                        javafx.scene.image.Image img = new javafx.scene.image.Image("/images/sign-check-icon.png");
                        Notifications notificationbuilder = Notifications.create()
                                .title("Succès")
                                .text("Invitation envoyée.")
                                .graphic(new ImageView(img))
                                .hideAfter(Duration.seconds(10))
                                .position(Pos.TOP_RIGHT);
                        notificationbuilder.darkStyle();
                        notificationbuilder.show();
                    } catch (ImpossibleRejoindrePartie impossibleRejoindrePartie) {
                        javafx.scene.image.Image img = new javafx.scene.image.Image("/images/sign-delete-icon.png");
                        Notifications notificationbuilder = Notifications.create()
                                .title("Erreur")
                                .text(impossibleRejoindrePartie.getMessage())
                                .graphic(new ImageView(img))
                                .hideAfter(Duration.seconds(10))
                                .position(Pos.TOP_RIGHT);
                        notificationbuilder.darkStyle();
                        notificationbuilder.show();
                    }
                }
            });
            while(!phase1){
                listeTemporaire.clear();
                for(String loginFound : monControleur.getListConnectedUsers()){
                    if (!Objects.equals(monControleur.getLogin(), loginFound)) {
                        if(monControleur.getListConnectedUsersNotInGame().contains(loginFound)) {
                            listeTemporaire.add(loginFound);
                            if(!listConnectedPlayersInMenu.contains(loginFound)){
                                Platform.runLater(() -> listConnectedPlayersInMenu.add(loginFound));
                            }
                        }
                    }
                }
                for (String loginFound : listConnectedPlayersInMenu){
                    if(!listeTemporaire.contains(loginFound)){
                        Platform.runLater(() -> listConnectedPlayersInMenu.remove(loginFound));
                    }
                }
                Thread.sleep(5000);
                phase1 = (Objects.equals(monControleur.getMyGame(login).getState(), "Phase 1"));
                //suppression de la liste d'invitations
                if(phase1){
                    Platform.runLater(() ->VBright.getChildren().clear());
                }else if(Objects.equals(monControleur.getMyGame(login).getState(), "Terminée"))
                    gameFinished = true;

            }

        }
        //-----------------------------En attente partie publique--------------------------------

        while (waitingForPlayer && !gameFinished) {
            this.myGame = monControleur.getMyGame(login);
            if(this.myGame.getState().equals("Phase 1")){
                this.loginAdversary = this.myGame.getOtherPlayer(login).getLogin();
                Platform.runLater(() ->{
                    lAdversaire.setText("Adversaire: " + this.loginAdversary);
                    Node node = PaneTop.getChildren().get(0);
                    Label lStatutPartie =(Label) node;
                    lStatutPartie.setText(this.myGame.getState());
                });
                this.colorAdversary = monControleur.getJoueur(loginAdversary).getColor();
                waitingForPlayer = false;
            }else if(this.myGame.getState().equals("Terminée")){
                gameFinished = true;
            }else{
                    Thread.sleep(5000);

            }
        }
        if(!gameFinished){
            this.colorAdversary = this.myGame.getOtherPlayer(login).getColor();
            //ajoute des Pane à chaque case du GridPane (plateau)
            Platform.runLater(this::initializeGridCells);

            //ajout selection pieces à placer
            Platform.runLater(() ->afficherPiecesAPlacer());

            initializeMapPiecesPerdues(piecesPerduesAdversary);
            initializeMapPiecesPerdues(piecesPerduesPlayer);

            Map reserve = monControleur.getStockPlayer(login);
            Set<String> nomPieces = reserve.keySet();
            for(String nomPiece : nomPieces){
                nbMaxPieceType.put(nomPiece, monControleur.getRemainingPiecesOfType(nomPiece, login));
            }
        }

        /*
         * --------------------------------Phase 1-----------------------------------
         */
        phase1 = true;

        boolean adversaryReadyForSecondPhase = false;
        while(phase1 && !gameFinished){
            this.myBoard = monControleur.getPlateau(login).getTab();
            this.myGame = monControleur.getMyGame(login);
            /*
               tant que l'adversaire n'a pas terminé de placer ces pieces
               recherche des nouvelle pieces placées par l'adversaire
               update la case du GridPane concernée
            */

            if (!adversaryReadyForSecondPhase) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (this.myBoard[j][i].isTaken(colorAdversary)) {
                            Node node;
                            if (Objects.equals(this.myGame.getPlayer1().getLogin(), login))
                                node = getNodeFromGridPane(this.GPplateau, i, 9 - j);
                            else
                                node = getNodeFromGridPane(this.GPplateau, i, j);
                            if (node != null) {
                                Pane pane = (Pane) node;
                                if (pane.getChildren().isEmpty())
                                    Platform.runLater(() -> pane.getChildren().add(createImageViewPiece(colorAdversary, "hidden")));
                            }
                        }
                    }
                }
            }
            adversaryReadyForSecondPhase = this.myGame.readyForNextPhase(loginAdversary);

            if(this.myGame.getState().equals("Phase 2")) {
                Platform.runLater(() -> {
                    Node node = PaneTop.getChildren().get(0);
                    Label lStatutPartie = (Label) node;
                    lStatutPartie.setText(this.myGame.getState());
                });
                phase1 = false;
                phase2 = true;
            }else if (this.myGame.getState().equals("Terminée")){
                gameFinished = true;
            }else {
                Thread.sleep(5000);
            }

        }

        if(!gameFinished) {

            //suppression de la HBox contenant les pièces à placer
            Platform.runLater(() -> this.HBpiecesRestantes.getChildren().clear());
            if(!hasStartedPhase2){
                Platform.runLater(() ->{
                    if(!this.anchorPane.getChildren().isEmpty())
                        anchorPane.getChildren().clear();
                    infoTurn.setFont(new Font(30.0));
                    anchorPane.getChildren().add(infoTurn);

                });
                setInfoTurnLabel(monControleur.getMyGame(login).canMovePiece(login));
            }

            //ajout label VBox left & right du BPgame
            Platform.runLater(() ->{
                this.VBleft.getChildren().add(new Label("Pièce(s) capturée(s): "));
                this.VBright.getChildren().add(new Label("Pièce(s) perdue(s): "));
                VBleft.setAlignment(Pos.TOP_CENTER);
                VBright.setAlignment(Pos.TOP_CENTER);
            });

        }


        /*
         * ----------------------------------Phase 2------------------------------------
         */


        while(phase2 && !gameFinished){
            this.myBoard = monControleur.getPlateau(login).getTab();
            this.myGame = monControleur.getMyGame(login);
            this.isMyTurn = monControleur.getMyGame(login).canMovePiece(login);

            if(this.myGame.getState().equals("Phase 2")){
                //si le joueur adverse vient de jouer ou a passé son tour
                if(monControleur.getMyGame(login).canMovePiece(login) && !timerIsAlive) {
                    setInfoTurnLabel(isMyTurn);
                    //maj pieces detruites

                    //1ere etape : verification perte piece joueur

                    Map reserve = monControleur.getStockPlayer(login);
                    Set<String> nomPieces = reserve.keySet();
                    for(String nomPiece : nomPieces){
                        if(monControleur.getRemainingPiecesOfType(nomPiece, login) != piecesPerduesPlayer.get(nomPiece)){
                            majViewPiecesPerdues(nomPiece, colorPlayer);
                        }
                    }
                    //2eme étape : verification perte piece adversaire
                    reserve = monControleur.getStockPlayer(loginAdversary);
                    nomPieces = reserve.keySet();
                    for(String nomPiece : nomPieces){
                        if(monControleur.getRemainingPiecesOfType(nomPiece, loginAdversary) != piecesPerduesAdversary.get(nomPiece)){
                            majViewPiecesPerdues(nomPiece, colorAdversary);
                        }
                    }

                    //maj plateau
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            //si la case est occupée par l'adversaire
                            if (this.myBoard[j][i].isTaken(colorAdversary)) {
                                Node node;
                                if (Objects.equals(this.myGame.getPlayer1().getLogin(), login))
                                    node = getNodeFromGridPane(this.GPplateau, i, 9 - j);
                                else
                                    node = getNodeFromGridPane(this.GPplateau, i, j);
                                if (node != null) {
                                    Pane pane = (Pane) node;
                                    //si lors de l'action effectuée par l'adversaire
                                    //la case était vide a la base
                                    //alors maj
                                    if(pane.getChildren().isEmpty())
                                        Platform.runLater(() -> pane.getChildren().add(createImageViewPiece(colorAdversary, "hidden")));
                                    //si a la base il y avait une piece a nous
                                    else {
                                        MyImageView myImageView = (MyImageView) pane.getChildren().get(0);
                                        if(myImageView.getColorImage().equals(colorPlayer)){
                                            Platform.runLater(() -> {
                                                pane.getChildren().clear();
                                                pane.getChildren().add(createImageViewPiece(colorAdversary, "hidden"));
                                            });
                                        }
                                    }
                                }
                            }//sinon si la case n'a pas de piece ou n'est pas le lac
                            else if (!this.myBoard[j][i].isTaken(colorPlayer) && !this.myBoard[j][i].isLake()){
                                Node node;
                                if(Objects.equals(this.myGame.getPlayer1().getLogin(), login))
                                    node = getNodeFromGridPane(this.GPplateau, i, 9 - j);
                                else
                                    node = getNodeFromGridPane(this.GPplateau, i, j);
                                if(node != null){
                                    Pane pane = (Pane) node;
                                    //si cette case est désormais vide suite à un mouvement de l'adversaire
                                    if(!pane.getChildren().isEmpty())
                                        Platform.runLater(() ->pane.getChildren().clear());
                                }
                            }
                        }
                    }
                }
                Thread.sleep(5000);

            }else {
                gameFinished = true;
            }
        }
         /*
         * -------------------------Fin partie-----------------------------
         */


        if(gameFinished && !victoryByDeplacement){
            this.myBoard = monControleur.getMyGame(login).getTab(login);
            showAllTheBoard();
            if(monControleur.getMyGame(loginAdversary) == null)
                Platform.runLater(()-> setLabelsPartie("Victoire!", "Votre adversaire est partit! Dommage!"));
            else
                Platform.runLater(()-> setLabelsPartie("Défaite!", "Vous avez perdu la partie! Dommage!"));
        }
        return null;
    }

    private void setLabelsPartie(String titre, String info){
            setTitre(titre);
            if (!this.anchorPane.getChildren().isEmpty())
                anchorPane.getChildren().clear();
            Label adversaryLeft = new Label(info);
            adversaryLeft.setPadding(new Insets(10, 0, 0, 0));
            adversaryLeft.setFont(new Font(30.0));
            anchorPane.setLeftAnchor(adversaryLeft, 220.0);
            anchorPane.getChildren().add(adversaryLeft);
    }
    private void initializeGridCells(){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Pane pane = new Pane();
                int finalI = i;
                int finalJ = j;
                pane.setId(String.valueOf(i)+String.valueOf(j));
                // quand il y a sélection d'une pièce à placer
                pane.setOnMouseClicked((MouseEvent event) -> {
                    this.myGame = monControleur.getMyGame(login);

                    // -----------------------------------Phase-1---------------------------------------------
                    if(Objects.equals(monControleur.getMyGame(login).getState(), "Phase 1")){
                        //si une piece a été selectionnée
                        if(!Objects.equals(monControleur.getPieceSelectionneePhase1(), "")){
                            try {
                                monControleur.placerPiece(login, monControleur.getPieceSelectionneePhase1(), finalJ, finalI);
                                pane.getChildren().add(createImageViewPiece(monControleur.getJoueur(login).getColor(), monControleur.getPieceSelectionneePhase1()));
                                //maj du titre + suppression si passage phase 2
                                if(Objects.equals(monControleur.getMyGame(login).getState(), "Phase 2")){
                                    Node node = PaneTop.getChildren().get(0);
                                    Label lStatutPartie =(Label) node;
                                    lStatutPartie.setText(monControleur.getMyGame(login).getState());
                                    this.HBpiecesRestantes.getChildren().clear();
                                    isMyTurn = monControleur.getMyGame(login).canMovePiece(login);
                                    //ajout info turn
                                    Platform.runLater(() ->{
                                        if(!this.anchorPane.getChildren().isEmpty())
                                            anchorPane.getChildren().clear();
                                        infoTurn.setFont(new Font(30.0));
                                        anchorPane.getChildren().add(infoTurn);
                                    });
                                        hasStartedPhase2 = true;
                                        setInfoTurnLabel(isMyTurn);
                                }
                            } catch (PieceNonDisponible | MouvementPieceImpossible e) {
                            }
                        }
                    }

                    // -----------------------------------Phase-2---------------------------------------------
                    if(Objects.equals(monControleur.getMyGame(login).getState(), "Phase 2")){
                        if(isMyTurn){
                            if (this.linePieceSelectionneePourDeplacement == -1 || this.columnPieceSelectionneePourDeplacement == -1) {
                                linePieceSelectionneePourDeplacement = Character.getNumericValue(pane.getId().charAt(1));
                                columnPieceSelectionneePourDeplacement = Character.getNumericValue(pane.getId().charAt(0));
                            }
                            else{
                                try {

                                    //si la case ciblée ne contient pas de piece
                                    if(this.myGame.getTab(login)[finalJ][finalI].isFree()){
                                        monControleur.deplacerPiece(login, linePieceSelectionneePourDeplacement, columnPieceSelectionneePourDeplacement, finalJ, finalI);

                                        MyImageView imageCaseDepart = createImageViewPiece(colorPlayer, monControleur.getMyGame(login).getTab(login)[finalJ][finalI].getPiece().toString());
                                        pane.getChildren().add(imageCaseDepart);

                                        //supression du node
                                        Pane nodeToDelete = (Pane) getNodeFromGridPane(this.GPplateau, columnPieceSelectionneePourDeplacement, linePieceSelectionneePourDeplacement);
                                        if (nodeToDelete != null) {
                                            nodeToDelete.getChildren().clear();
                                        }
                                        //arret timer
                                        stopTurn = true;
                                    }//s'il y a attaque, ISSOU YATANGAKI!
                                    else{
                                        this.myBoard = monControleur.getMyGame(login).getTab(login);
                                        String nomPieceCaseDepart = myBoard[linePieceSelectionneePourDeplacement][columnPieceSelectionneePourDeplacement].getPiece().toString();
                                        String nomPieceCaseVisee = myBoard[finalJ][finalI].getPiece().toString();
                                        //si la partie est terminée grave a un deplacement du joueur
                                        if(monControleur.deplacerPiece(login, linePieceSelectionneePourDeplacement, columnPieceSelectionneePourDeplacement, finalJ, finalI)){
                                            this.myBoard = monControleur.getMyGame(login).getTab(login);
                                            this.myGame = monControleur.getMyGame(login);
                                            //arret du refresh pour la phase 2
                                            gameFinished = true;
                                            victoryByDeplacement = true;
                                            setLabelsPartie("Victoire!", "Vous avez remporté la partie, bien joué!");
                                            showAllTheBoard();
                                        }
                                        //si la partie continue
                                        else{
                                            this.myBoard = monControleur.getMyGame(login).getTab(login);
                                            //si les deux pieces ont disparues
                                            if(this.myBoard[linePieceSelectionneePourDeplacement][columnPieceSelectionneePourDeplacement].isFree() && this.myBoard[finalJ][finalI].isFree()){
                                                //supression case depart
                                                Pane nodeToDelete = (Pane) getNodeFromGridPane(this.GPplateau, columnPieceSelectionneePourDeplacement, linePieceSelectionneePourDeplacement);
                                                if (nodeToDelete != null) {
                                                    nodeToDelete.getChildren().clear();
                                                }
                                                //suppression case cible
                                                pane.getChildren().clear();
                                                //maj pieces perdues
                                                majViewPiecesPerdues(nomPieceCaseVisee, colorAdversary);
                                                majViewPiecesPerdues(nomPieceCaseDepart, colorPlayer);
                                            }else if(this.myBoard[linePieceSelectionneePourDeplacement][columnPieceSelectionneePourDeplacement].isFree()){
                                                //si piece de la case cible est devenue une piece du joueur (success)
                                                if (Objects.equals(this.myBoard[finalJ][finalI].getPiece().getColor(), colorPlayer)) {
                                                    //supression case depart
                                                    Pane nodeToDelete = (Pane) getNodeFromGridPane(this.GPplateau, columnPieceSelectionneePourDeplacement, linePieceSelectionneePourDeplacement);
                                                    if (nodeToDelete != null) {
                                                        nodeToDelete.getChildren().clear();
                                                    }
                                                    //maj case visée avec la piece du joueur
                                                    MyImageView imageCaseDepart = createImageViewPiece(colorPlayer, nomPieceCaseDepart);
                                                    pane.getChildren().clear();
                                                    pane.getChildren().add(imageCaseDepart);
                                                    //maj pieces perdues
                                                    majViewPiecesPerdues(nomPieceCaseVisee, colorAdversary);
                                                }else{
                                                    //si piece de la case cible est toujours une piece du joueur adverse (fail)
                                                    if (Objects.equals(this.myBoard[finalJ][finalI].getPiece().getColor(), colorAdversary)) {
                                                        //supression case depart
                                                        Pane nodeToDelete = (Pane) getNodeFromGridPane(this.GPplateau, columnPieceSelectionneePourDeplacement, linePieceSelectionneePourDeplacement);
                                                        if (nodeToDelete != null) {
                                                            nodeToDelete.getChildren().clear();
                                                        }
                                                        //maj pieces perdues
                                                        majViewPiecesPerdues(nomPieceCaseDepart, colorPlayer);
                                                    }
                                                }
                                            }
                                            //arret timer
                                            stopTurn = true;
                                        }
                                    }
                                    linePieceSelectionneePourDeplacement = -1;
                                    columnPieceSelectionneePourDeplacement = -1;
                                } catch (MouvementPieceImpossible mouvementPieceImpossible) {
                                    linePieceSelectionneePourDeplacement = -1;
                                    columnPieceSelectionneePourDeplacement = -1;
                                    //mouvementPieceImpossible.printStackTrace();
                                }
                            }

                        }
                    }

                });
                GPplateau.add(pane, i, j);
            }
        }
    }

    private void showAllTheBoard(){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (this.myBoard[j][i].isTaken(colorAdversary)) {
                    Node node;

                        node = getNodeFromGridPane(this.GPplateau, i, j);
                    if (node != null) {
                        Pane pane = (Pane) node;
                        if(!pane.getChildren().isEmpty())
                            Platform.runLater(() ->pane.getChildren().clear());
                        int finalJ = j;
                        int finalI = i;
                        Platform.runLater(() -> pane.getChildren().add(createImageViewPiece(colorAdversary, myBoard[finalJ][finalI].getPiece().getType().getName())));
                    }
                }
            }
        }
    }

    private void initializeMapPiecesPerdues(Map myMap){
        Map reserve = monControleur.getStockPlayer(login);
        Set<String> keys = reserve.keySet();
        for(String key : keys){
            myMap.put(key, 0);
        }
    }

    private Label getLabelPiecesPerdues(String nomPiece, String color){
        if (Objects.equals(color, colorPlayer)){
            return labelPiecesPerduesPlayer.get(nomPiece);
        }else{
            return labelpiecesPerduesAdversary.get(nomPiece);
        }
    }

    private void majViewPiecesPerdues(String nomPiece, String colorPiece){
        Label nbPieces = new Label();
        if(Objects.equals(colorPiece, colorPlayer)){
            //maj de mes pieces perdues
            nbPieces.setLayoutX(50);
            Integer value = piecesPerduesPlayer.get(nomPiece);
            value++;
            piecesPerduesPlayer.put(nomPiece, value);
            if(value == 1) {
                labelPiecesPerduesPlayer.put(nomPiece, nbPieces);
                Pane piece  = new Pane();
                piece.setPrefHeight(58);
                piece.setBackground(getBackground("/piece/"+ colorPlayer +"/"+ nomPiece +".png"));
                nbPieces.setText(value + "/" + Integer.toString(nbMaxPieceType.get(nomPiece)));
                piece.getChildren().add(nbPieces);
                piece.setId(nomPiece);
                piece.setMaxWidth(45);
                Platform.runLater(() -> this.VBright.getChildren().add(piece));
                VBright.setMargin(piece, new Insets(0, 0, 5, 0));
            }else{
                Label label = getLabelPiecesPerdues(nomPiece, colorPlayer);
                Integer finalValue = value;
                Platform.runLater(() -> label.setText(finalValue + "/" + Integer.toString(nbMaxPieceType.get(nomPiece))));
            }
        }else {
            //maj pieces perdues adversaire
            nbPieces.setLayoutX(50);
            Integer value = piecesPerduesAdversary.get(nomPiece);
            value++;
            piecesPerduesAdversary.put(nomPiece, value);
            if(value == 1) {
                labelpiecesPerduesAdversary.put(nomPiece, nbPieces);
                Pane piece  = new Pane();
                piece.setPrefHeight(58);
                piece.setMaxWidth(45);
                nbPieces.setText(value + "/" + Integer.toString(nbMaxPieceType.get(nomPiece)));
                piece.getChildren().add(nbPieces);
                piece.setPadding( new Insets(0, 0, 5, 0));
                piece.setBackground(getBackground("/piece/"+ colorAdversary +"/"+ nomPiece +".png"));
                piece.setId(nomPiece);
                Platform.runLater(() ->this.VBleft.getChildren().add(piece));
                VBleft.setMargin(piece, new Insets(0, 0, 5, 0));
            }else if(value > 1){
                Label label = getLabelPiecesPerdues(nomPiece, colorAdversary);
                Integer finalValue = value;
                Platform.runLater(() -> label.setText(finalValue + "/" + Integer.toString(nbMaxPieceType.get(nomPiece))));
            }
        }
    }

    private MyImageView createImageViewPiece(String color, String nomPiece){
        Image image = new Image("/piece/"+ color +"/"+ nomPiece +".png");
        MyImageView imageView = new MyImageView(nomPiece, color);
        imageView.setImage(image);
        imageView.setFitHeight(58);
        imageView.setFitWidth(45);
        imageView.setX(8);
        imageView.setY(1);
        return imageView;
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if(GridPane.getColumnIndex(node)!=null && GridPane.getRowIndex(node) != null)
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return node;
                }
        }
        return null;
    }

    public void setTitre(String msg){
        Platform.runLater(() -> {
            Node node = PaneTop.getChildren().get(0);
            Label lStatutPartie =(Label) node;
            lStatutPartie.setText(msg);
        });
    }

    public Background getBackground (String path){
        Image image = new Image(String.valueOf(GameVue.class.getResource(path)));
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);
        return background;
    }

    private void afficherPiecesAPlacer(){
        Label lPiecesRestantes = new Label("Pieces restantes: ");
        lPiecesRestantes.setAlignment(Pos.CENTER_LEFT);
        HBpiecesRestantes.getChildren().add(lPiecesRestantes);

        Map reserve = monControleur.getStockPlayer(login);
        Set<String> keys = reserve.keySet();
        for(String key : keys)
        {
            //result += key + ": " + stock.get(key).size() + " ";
            Pane piece  = new Pane();
            piece.setPrefWidth(40);
            piece.setBackground(getBackground("/piece/"+ colorPlayer +"/"+ key +".png"));
            piece.setId(key);
            piece.setOnMouseClicked((MouseEvent event)->{
                        monControleur.setPieceSelectionneePhase1(key);
                    }
            );

            Tooltip tp = new Tooltip();

            piece.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    tp.setText(key + "(" + monControleur.getRemainingPiecesOfType(key, login) + ")");
                    tp.show(piece, mouseEvent.getScreenX()+50, mouseEvent.getScreenY());
                }
            });
            piece.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    tp.hide();
                }
            });
            HBpiecesRestantes.getChildren().add(piece);
            HBpiecesRestantes.setMargin(piece, new Insets(0, 0, 0, 10));
        }
    }

    private void setInfoTurnLabel(boolean isMyTurn){
        if(isMyTurn){
            infoTurn.setPadding(new Insets(10, 0, 0, 0));
            anchorPane.setLeftAnchor(infoTurn, 200.0);
            cptTimer = 15;
            this.timer = new Thread(){
                public void run(){
                    timerIsAlive = true;
                    while(cptTimer>=0 && !stopTurn && !gameFinished){
                        Platform.runLater(() ->  infoTurn.setText("A vous de jouer : " + Integer.toString(cptTimer) + " seconde(s) restante(s)."));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        cptTimer--;
                    }
                    if(!gameFinished){
                        if(!stopTurn)
                            monControleur.passerTour(login);
                        anchorPane.setLeftAnchor(infoTurn, 300.0);
                        Platform.runLater(() ->infoTurn.setText("Au tour de votre adversaire."));
                        stopTurn = false;
                        timerIsAlive = false;
                    }
                }
            };
            timer.start();
        }else{
            anchorPane.setLeftAnchor(infoTurn, 300.0);
            Platform.runLater(() ->infoTurn.setText("Au tour de votre adversaire."));
        }
    }
}
