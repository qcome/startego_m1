package actions;

import classes.*;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;
import classes.exceptions.PlayerAlreadyInGameException;

import classes.exceptions.MoveImpossibleException;
import classes.exceptions.PieceAlreadyPlacedException;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 11/18/16.
 */
public class Partie extends ActionSupport implements ApplicationAware, SessionAware
{

    private String ALREADY_IN_GAME = "alreadyInGame";

    private String login;

    private String colorPlayer;

    private boolean isObserver;

    private String loginAdversary;

    private String type;

    private String line;

    private String column;

    private String lineBis;

    private String columnBis;

    private boolean isPrivate;

    private Case[][] board;

    private Map<String, List<Piece>> stock;

    private Map<String, List<Piece>> stockAdversary;

    private String gameState;

    private boolean readyForNextPhase;

    private boolean canMovePiece;

    private boolean endGame;

    private boolean victory;

    private String loginPlayer1;

    private String loginPlayer2;

    private String colorPlayer1;

    private String colorPlayer2;

    private Map <String, Joueur> playersOnline;

    private Map<String, Object> session;

    private IGestionStratego facade;

    private String[] selectPlayer;

    public void setApplication(Map<String, Object> map)
    {
        facade = (IGestionStratego) map.get("facade");
        if(facade == null)
        {
            facade = GestionStratego.getInstance();
            map.put("facade", facade);
        }

    }

    @Override
    public String execute() throws Exception
    {
        return SUCCESS;
    }

    public String sendInvitation()
    {
        for(int i = 0 ; i < selectPlayer.length ; i++)
        {
            try {
                facade.sendInvitation(facade.getMyGame(login), login, selectPlayer[i]);
            } catch (PlayerAlreadyInGameException e)
            {
            }
        }
        return SUCCESS;
    }

    public String putPiece()
    {
        try{
            TypePiece typePiece = TypePiece.valueOf(type);
            readyForNextPhase = facade.putPiece(login, typePiece, Integer.parseInt(line), Integer.parseInt(column));
            board = facade.getMyGame(login).getTab(login);
            stock = facade.getMyStock(login).getStock();
            colorPlayer = facade.getPlayersOnline().get(login).getColor();
        } catch (PieceAlreadyPlacedException e)
        {
        } catch (MoveImpossibleException e)
        {
        }
        return SUCCESS;
    }

    public String movePiece()
    {
        try {
            isObserver = facade.isObserver(login, facade.getMyGame(login));
            endGame = facade.movePiece(login, Integer.parseInt(line), Integer.parseInt(column), Integer.parseInt(lineBis), Integer.parseInt(columnBis));
            colorPlayer = facade.getPlayersOnline().get(login).getColor();
            gameState = facade.getMyGame(login).getState();
            loginAdversary = facade.getMyGame(login).getOtherPlayer(login).getLogin();
            board = facade.getMyGame(login).getTab(login);
            stock = facade.getMyStock(login).getStock();
            stockAdversary = facade.getMyStock(loginAdversary).getStock();
        } catch (MoveImpossibleException e)
        {
        }
        return SUCCESS;
    }

    public String refreshGame()
    {
        isObserver = facade.isObserver(login, facade.getMyGame(login));
        isPrivate = facade.getMyGame(login).getIsPrivate();
        System.out.println(isPrivate);
        gameState = facade.getMyGame(login).getState();
        if(!isObserver)
        {
            colorPlayer = facade.getPlayersOnline().get(login).getColor();
            if(gameState.equals("En attente"))
            {
                loginAdversary = "";

                if(isPrivate)
                    playersOnline = facade.getPlayersOnline();

                return SUCCESS;
            }
            else
            {
                Joueur adversary = facade.getMyGame(login).getOtherPlayer(login);
                if(adversary == null)
                    loginAdversary = "";
                else
                    loginAdversary = adversary.getLogin();
            }
            if(!gameState.equals("Terminée"))
            {
                stock = facade.getMyStock(login).getStock();
                stockAdversary = facade.getMyStock(loginAdversary).getStock();
            }
        }
        else
        {
            Joueur player1 = facade.getMyGame(login).getPlayer1();
            if(player1 == null)
                loginPlayer1 = "";
            else
                loginPlayer1 = player1.getLogin();

            Joueur player2 = facade.getMyGame(login).getPlayer2();

            if(player2 == null)
                loginPlayer2 = "";
            else
                loginPlayer2 = player2.getLogin();
            if(!gameState.equals("Terminée"))
            {
                stock = facade.getMyStock(login).getStock();
                stockAdversary = facade.getMyStock(loginPlayer1).getStock();
            }
            else
            {
                if(!loginPlayer1.equals("") && !loginPlayer2.equals(""))
                {
                    canMovePiece = facade.getMyGame(login).canMovePiece(loginPlayer1);
                    if(canMovePiece)
                        victory = true;
                    else
                        victory = false;
                }
            }
        }
        board = facade.getMyGame(login).getTab(login);

        if(gameState.equals("Terminée"))
            return ERROR;

        canMovePiece = facade.getMyGame(login).canMovePiece(login);

        return SUCCESS;
    }

    public String skipTurn(){
        facade.getMyGame(login).incrementRound();
        return SUCCESS;
    }

    public String leaveGame()
    {
        session.put("login", login);
        facade.leaveGame(login);
        session.put("uniqueGames", facade.getUniqueGames());
        session.put("invitations", facade.getMyInvitations(login));
        session.put("playersOnline", facade.getPlayersOnline());

        return SUCCESS;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getColorPlayer() {return colorPlayer;}

    public void setColorPlayer(String colorPlayer) {this.colorPlayer = colorPlayer;}

    public boolean getIsObserver() {
        return isObserver;
    }

    public void setIsObserver(boolean isObserver) {
        this.isObserver = isObserver;
    }

    public String getLoginAdversary() {
        return loginAdversary;
    }

    public void setLoginAdversary(String loginAdversary) {
        this.loginAdversary = loginAdversary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getLineBis() {
        return lineBis;
    }

    public void setLineBis(String lineBis) {
        this.lineBis = lineBis;
    }

    public String getColumnBis() {
        return columnBis;
    }

    public void setColumnBis(String columnBis) {
        this.columnBis = columnBis;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setisPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Case[][] getBoard() {
        return board;
    }

    public void setBoard(Case[][] board) {
        this.board = board;
    }

    public Map<String, List<Piece>> getStock() {
        return stock;
    }

    public void setStock(Map<String, List<Piece>> stock) {
        this.stock = stock;
    }

    public Map<String, List<Piece>> getStockAdversary() {
        return stockAdversary;
    }

    public void setStockAdversary(Map<String, List<Piece>> stockAdversary) {
        this.stockAdversary = stockAdversary;
    }

    public Map<String, Joueur> getPlayersOnline() {
        return playersOnline;
    }

    public boolean isReadyForNextPhase() {
        return readyForNextPhase;
    }

    public void setReadyForNextPhase(boolean readyForNextPhase) {
        this.readyForNextPhase = readyForNextPhase;
    }

    public boolean isEndGame() {
        return endGame;
    }

    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    public boolean isVictory() {
        return victory;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }

    public String getLoginPlayer1() {
        return loginPlayer1;
    }

    public void setLoginPlayer1(String loginPlayer1) {
        this.loginPlayer1 = loginPlayer1;
    }

    public String getLoginPlayer2() {
        return loginPlayer2;
    }

    public void setLoginPlayer2(String loginPlayer2) {
        this.loginPlayer2 = loginPlayer2;
    }

    public String getColorPlayer1() {
        return colorPlayer1;
    }

    public void setColorPlayer1(String colorPlayer1) {
        this.colorPlayer1 = colorPlayer1;
    }

    public String getColorPlayer2() {
        return colorPlayer2;
    }

    public void setColorPlayer2(String colorPlayer2) {
        this.colorPlayer2 = colorPlayer2;
    }

    public void setPlayersOnline(Map<String, Joueur> playersOnline) {
        this.playersOnline = playersOnline;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getGameState() {return gameState;}

    public void setGameState(String gameStatus) {this.gameState = gameStatus;}

    public boolean isCanMovePiece() {
        return canMovePiece;
    }

    public void setCanMovePiece(boolean canMovePiece) {
        this.canMovePiece = canMovePiece;
    }

    public String[] getSelectPlayer() {return selectPlayer;}

    public void setSelectPlayer(String[] selectPlayer) {this.selectPlayer = selectPlayer;}

}
