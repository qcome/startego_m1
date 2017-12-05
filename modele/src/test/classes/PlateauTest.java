package classes;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Akli on 21/11/2016.
 */
public class PlateauTest {
    @Test
    public void moveIsPossible() throws Exception {

        Plateau plat = new Plateau();
        Case boxt = new Case(1,4);
        int linet = 2;
        int columnt= 4;
        Piece piecet = new Piece(TypePiece.ESPION, "Bleu");
        Joueur jtest = new Joueur("playerTest");
        Partie gamet= new Partie(jtest, false);
        assertTrue("Piece has been succesfully moved",plat.moveIsPossible(jtest,piecet,linet,columnt,gamet));
    }

    @Test
    public void attackPiece() throws Exception {

    }

    @Test
    public void victory() throws Exception {

    }

    @Test
    public void replacePiece() throws Exception {

    }

    @Test
    public void getTab() throws Exception {
        Plateau plat = new Plateau();
        Case caset = new Case(2,4);
        assertEquals(caset,plat.getTab());


    }

    @Test
    public void getBox() throws Exception {
        Plateau plat = new Plateau();
        Case caset = new Case(2,4);;

    }

    @Test
    public void setBox() throws Exception {

    }

    @Test
    public void putPiece() throws Exception {
        Plateau plat = new Plateau();
        Case boxt = new Case(2,3);
        Piece piecet = new Piece (TypePiece.MARECHAL, "Bleu");
        assertTrue("Case bien placé", plat.putPiece(boxt,piecet));

    }

    @Test
    public void movePiece() throws Exception { // erreur test
        Plateau plat = new Plateau();
        Case boxt = new Case(1,4);
        Piece piecet = new Piece(TypePiece.ESPION, "Bleu");
        Joueur jtest = new Joueur("playerTest");
        Partie gamet= new Partie(jtest, false);
        assertFalse("Piece has been succesfully moved",plat.movePiece(boxt,piecet,jtest,gamet));
    }

}