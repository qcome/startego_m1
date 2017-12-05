package classes;

import java.io.Serializable;

/**
 * Created by root on 11/11/16.
 */
public class Case implements Serializable
{

    private int line;

    private int column;

    private Piece piece;

    private boolean isLake;

    private boolean isFree;

    public Case(int line, int column)
    {
        this.line = line;
        this.column = column;
        piece = null;
        if ((column == 2 || column == 3 || column == 6 || column == 7) && (line == 4 || line == 5))
        {
            isLake = true;
            isFree = false;
        }
        else
        {
            isLake = false;
            isFree = true;
        }
    }

    /*
     * Méthode qui indique si la case est occupée par une pièce d'une certaine couleur
     */
    public boolean isTaken(String color)
    {
        if (piece == null)
            return false;
        else
            return (piece.getColor().equals(color));
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isLake()
    {
        return isLake;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean isFree)
    {
        this.isFree = isFree;
    }
}
