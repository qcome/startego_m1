package classes;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Akli on 21/11/2016.
 */
public class CaseTest {
    @Test
    public void isTaken() throws Exception {

        Case caset = new Case(1,4);
        Piece piecet = caset.getPiece();
        String colort = "Orange";
        assertFalse("Case is not already taken by this color",caset.isTaken(colort));

    }

}