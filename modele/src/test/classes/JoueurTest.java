package classes;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Akli on 21/11/2016.
 */
public class JoueurTest {
    @Test
    public void addInvitation() throws Exception {

    }

    @Test
    public void declineInvitation() throws Exception {

    }

    @Test
    public void deleteAllInvitations() throws Exception {

    }

    @Test
    public void getLogin() throws Exception {
        Joueur playerTest = new Joueur("playerTest");
        assertEquals("playerTest",playerTest.getLogin());
    }



    @Test
    public void getColor() throws Exception {
        Joueur playerTest = new Joueur("playerTest");
        playerTest.setColor("Blue");
        String colort = "Blue";
        assertEquals("Blue",playerTest.getColor());

    }

    @Test
    public void setColor() throws Exception {
        Joueur playerTest = new Joueur("playerTest");
        playerTest.setColor("Blue");
        String colort = "Blue";
        assertEquals("Blue",playerTest.getColor());

    }

    @Test
    public void getInvitations() throws Exception {

    }

}