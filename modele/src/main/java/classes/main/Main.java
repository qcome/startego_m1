package classes.main;

import classes.GestionStratego;
import classes.TypePiece;

import classes.exceptions.*;

/**
 * Created by root on 11/19/16.
 */
public class Main
{
    public static void main(String[] args)
    {
        System.out.println("-----------------");
        System.out.println("Programme de test");
        System.out.println("-----------------");
        System.out.println("\n");

        GestionStratego facade = new GestionStratego();

        try {
            facade.registration("mike", "mike", "mike");
        } catch(LoginTooShortException e)
        {
        } catch(PasswordConfirmationException e)
        {
        } catch(PasswordTooShortException e)
        {
        } catch(LoginAlreadyTakenException e)
        {
        }

        try {
            facade.registration("akli", "akli", "akli");
        } catch(LoginTooShortException e)
        {
        } catch(PasswordConfirmationException e)
        {
        } catch(PasswordTooShortException e)
        {
        } catch(LoginAlreadyTakenException e)
        {
        }

        try {
            facade.connection("mike", "mike");
        } catch(UnknownPlayerException e)
        {

        } catch(PlayerAlreadyConnectedException e)
        {
        }

        try {
            facade.connection("akli", "akli");
        } catch(UnknownPlayerException e)
        {

        } catch(PlayerAlreadyConnectedException e)
        {
        }

        facade.createNewGame("mike", false);

        try {
            facade.joinGame(facade.getMyGame("mike"), "akli");
        } catch(GameNotFoundException e)
        {
        } catch(GameOverException e)
        {
        }

        try {
            TypePiece typePiece = TypePiece.valueOf("COLONEL");
            facade.putPiece("mike", typePiece , 6, 0);
        } catch(PieceAlreadyPlacedException e)
        {
        } catch(MoveImpossibleException e)
        {
        }

        try {
            TypePiece typePiece = TypePiece.valueOf("COLONEL");
            facade.putPiece("mike", typePiece , 7, 4);
        } catch(PieceAlreadyPlacedException e)
        {
        } catch(MoveImpossibleException e)
        {
        }


        try {
            TypePiece typePiece = TypePiece.valueOf("ECLAIREUR");
            facade.putPiece("mike", typePiece , 6, 5);
        } catch(PieceAlreadyPlacedException e)
        {
        } catch(MoveImpossibleException e)
        {
        }

        try {
            TypePiece typePiece = TypePiece.valueOf("MARECHAL");
            facade.putPiece("akli", typePiece , 6, 0);
        } catch(PieceAlreadyPlacedException e)
        {
        } catch(MoveImpossibleException e)
        {
        }

        try {
            TypePiece typePiece = TypePiece.valueOf("DRAPEAU");
            facade.putPiece("akli", typePiece , 6, 5);
        } catch(PieceAlreadyPlacedException e)
        {
        } catch(MoveImpossibleException e)
        {
        }

        try {
            TypePiece typePiece = TypePiece.valueOf("COLONEL");
            facade.putPiece("mike", typePiece , 6, 9);
        } catch(PieceAlreadyPlacedException e)
        {
        } catch(MoveImpossibleException e)
        {
        }

        System.out.println(facade.getMyBoard(facade.getMyGame("mike")).toString());

        facade.getMyGame("mike").incrementRound();

        boolean endGame = false;
        try {
            facade.movePiece("mike", 6, 5, 3, 5);
        } catch(MoveImpossibleException e)
        {
            System.out.println("Erreur mouvement!");
        }

        System.out.println(facade.getMyBoard(facade.getMyGame("mike")).toString());

        System.out.println(endGame);

    }
}
