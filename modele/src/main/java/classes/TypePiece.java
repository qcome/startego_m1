package classes;

/**
 * Created by root on 11/18/16.
 */
public enum TypePiece
{

    BOMBE ("BOMBE"),
    MARECHAL ("MARECHAL"),
    GENERAL ("GENERAL"),
    COLONEL ("COLONEL"),
    COMMANDANT ("COMMANDANT"),
    CAPITAINE ("CAPITAINE"),
    LIEUTENANT ("LIEUTENANT"),
    SERGENT ("SERGENT"),
    DEMINEUR ("DEMINEUR"),
    ECLAIREUR ("ECLAIREUR"),
    ESPION ("ESPION"),
    DRAPEAU ("DRAPEAU");

    private String name;

    private int power;

    private boolean canMove;

    TypePiece(String name)
    {
        this.name = name;
        switch(name) {
            case "BOMBE":
                power = 11;
                canMove = false;
                break;
            case "MARECHAL":
                power = 10;
                canMove = true;
                break;
            case "GENERAL":
                power = 9;
                canMove = true;
                break;
            case "COLONEL":
                power = 8;
                canMove = true;
                break;
            case "COMMANDANT":
                power = 7;
                canMove = true;
                break;
            case "CAPITAINE":
                power = 6;
                canMove = true;
                break;
            case "LIEUTENANT":
                power = 5;
                canMove = true;
                break;
            case "SERGENT":
                power = 4;
                canMove = true;
                break;
            case "DEMINEUR":
                power = 3;
                canMove = true;
                break;
            case "ECLAIREUR":
                this.power = 2;
                canMove = true;
                break;
            case "ESPION":
                power = 1;
                canMove = true;
                break;
            case "DRAPEAU":
                power = 0;
                canMove = false;
                break;
        }
    }

    public String getName()
    {
        return name;
    }

    public int getPower()
    {
        return power;
    }

    public boolean getCanMove()
    {
        return canMove;
    }

}

