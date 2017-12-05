package classes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Akli on 21/11/2016.
 */
public class ReserveTest {
    @Test
    public void getIndexPiece() throws Exception {
        Reserve reserveTest = new Reserve("Blue");
        Piece pieceTest= new Piece(TypePiece.DRAPEAU,"Blue");
        reserveTest.addPiece(pieceTest,1);
        int indexTest = 0 ;
        assertEquals(indexTest,reserveTest.getIndexPiece(pieceTest.getType()));
    }

    @Test
    public void isEmpty() throws Exception {
        Reserve reserveTest = new Reserve("Blue");
        assertFalse("Stock is not empty !", reserveTest.isEmpty());

    }


    @Test
    public void getPieceInReserve() throws Exception {

    }

    @Test
    public void getStock() throws Exception {

    }

}