package classes;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Akli on 21/11/2016.
 */
public class PartieTest {
    @Test
    public void endInitPhase() throws Exception {
        Joueur player1 = new Joueur("playerTest");
        Joueur player2 = new Joueur("playerTest1");
        Partie gameTest = new Partie(player1,false);
        gameTest.addSecondPlayer(player2);
        Reserve reserveTest0 = gameTest.getReservePlayer("playerTest");
        Reserve reserveTest1 = gameTest.getReservePlayer("playerTest1");
        assertFalse(reserveTest0.isEmpty());
        assertFalse(reserveTest1.isEmpty());
    }

    @Test
    public void getPlayer1() throws Exception {
        Joueur player1 = new Joueur("playerTest");
        Partie gameTest = new Partie(player1,false);
        assertEquals(player1,gameTest.getPlayer1());

    }

    @Test
    public void getPlayer2() throws Exception {

        Joueur player1 = new Joueur("playerTest");
        Joueur player2 = new Joueur("PlayerTest1");
        Partie gameTest = new Partie(player1,false);
        gameTest.addSecondPlayer(player2);
        assertEquals(player2,gameTest.getPlayer2());
    }

    @Test
    public void getOtherPlayer() throws Exception {
        Joueur player1 = new Joueur("playerTest");
        Joueur player2 = new Joueur("PlayerTest1");
        Partie gameTest = new Partie(player1,false);
        gameTest.addSecondPlayer(player2);
        assertEquals(gameTest.getPlayer1(),gameTest.getOtherPlayer(player2.getLogin()));
        assertEquals(gameTest.getPlayer2(),gameTest.getOtherPlayer(player1.getLogin()));

    }

    @Test
    public void getReservePlayer() throws Exception {



    }

    @Test
    public void getObservers() throws Exception {

    }

    @Test
    public void isPrivate() throws Exception {

    }

    @Test
    public void getState() throws Exception {

    }

    @Test
    public void getBoard() throws Exception {

    }

    @Test
    public void getTurn() throws Exception {

    }

    @Test
    public void getWinner() throws Exception {

    }

}