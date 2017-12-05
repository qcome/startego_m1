package classes;

import java.io.Serializable;

/**
 * Created by root on 11/11/16.
 */
public class Piece implements Serializable
{

    private TypePiece type;

    private String color;

    private Case box;

    public Piece(TypePiece type, String color)
    {
        this.type = type;
        this.color = color;
        box = null;
    }

    public String toString()
    {
        return type.getName();
    }

    public TypePiece getType()
    {
        return type;
    }

    public String getColor()
    {
        return color;
    }

    public Case getBox() {
        return box;
    }

    public void setBox(Case box) {
        this.box = box;
    }
}
