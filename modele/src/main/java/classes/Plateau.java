package classes;


import java.io.Serializable;

/**
 * Created by root on 11/11/16.
 */
public class Plateau implements Serializable
{

    private static final int LENGTH = 10;

    // Le tableau de référence est celui du joueur 2, c'est à dire celui qui est affiché face au joueur 2
    private Case[][] tab;

    /*
     * Méthode qui initialise le plateau avec un tableau de cases vides
     */
    public Plateau()
    {
        tab = new Case[LENGTH][LENGTH];
        for(int i = 0; i < LENGTH; i++)
        {
            for(int j = 0; j < LENGTH; j++)
            {
                tab[i][j] = new Case(i, j);
            }
        }
    }

    public String toString()
    {
        String result = "";
        for(int i = 0; i < LENGTH ; i++)
        {
            for(int j = 0; j < LENGTH ; j++)
            {
                if (tab[i][j].isLake())
                    result = result + "|" + "XXXXXXXXXX";
                else if (tab[i][j].isFree())
                    result = result + "|" + "          ";
                else
                {
                    // A modifier, trouver un moyen de récupérer la couleur du joueur
                    if (!tab[i][j].getPiece().getColor().equals("steelblue"))
                        result = result + "|" + "PIECEADVER";
                    else
                    {
                        if (tab[i][j].getPiece().toString().length() < 6)
                            result = result + "|" + tab[i][j].getPiece().toString() + "     ";
                        else if (tab[i][j].getPiece().toString().length() < 7)
                            result = result + "|" + tab[i][j].getPiece().toString() + "    ";
                        else if (tab[i][j].getPiece().toString().length() < 8)
                            result = result + "|" + tab[i][j].getPiece().toString() + "   ";
                        else if (tab[i][j].getPiece().toString().length() < 9)
                            result = result + "|" + tab[i][j].getPiece().toString() + "  ";
                        else if (tab[i][j].getPiece().toString().length() < 10)
                            result = result + "|" + tab[i][j].getPiece().toString() + " ";
                        else
                            result = result + "|" + tab[i][j].getPiece().toString();
                    }

                }
            }
            result += "|\n";
        }
        return result;
    }

    /*
     * Méthode permettant de placer une pièce d'un joueur dans une case lors de la phase d'initialisation
     */
    public boolean putPiece(Case box, Piece piece)
    {
        if (piece.getColor().equals("steelblue"))
        {
            if (box.isFree() && box.getLine() < 4)
            {
                setBox(box.getLine(), box.getColumn(), piece);
                return true;
            } else
                return false;
        }
        else if (box.isFree() && box.getLine() > 5)
        {
            setBox(box.getLine(), box.getColumn(), piece);
            return true;
        } else
            return false;
    }

    /*
     * Méthode permettant de déplacer une pièce d'un joueur d'une case à une autre
     */
    public boolean movePiece(Case box, Piece piece, Joueur player, Partie game)
    {
        // Récupération du joueur victime de l'attaque
        Joueur victim = game.getOtherPlayer(player.getLogin());

        // Si la case où l'on veut effectuer le déplacement est occupée par le joueur adverse une attaque se lance
        if (!box.isFree() && !box.isLake() && box.isTaken(victim.getColor()))
        {
            boolean isOver = attackPiece(box, piece, player, game);
            // Vrai si la pièce attaquée est le drapeau du joueur adverse, ce qui termine la partie et donne la victoire au joueur qui a lancé l'attaque
            if (isOver)
                return true;
            // Sinon
            else
            {
                // Vérification que le joueur qui a subit l'attaque peut encore effectuer des mouvements
                // Si ce n'est pas le cas la partie est terminée et la victoire revient au joueur ayant effectué l'attaque
                isOver = victory(game.getPlayer2());
                return isOver;
            }

        }
        // Sinon on met dans cette case la pièce désirée
        else
        {
            int line = piece.getBox().getLine();
            int column = piece.getBox().getColumn();

            setBox(box.getLine(), box.getColumn(), piece);

            // La case de départ de la pièce déplacée est vidée
            tab[line][column] = new Case(line, column);

            return false;
        }
    }

    /*
     * Méthode pour déterminer si le mouvement qu'un joueur souhaite effectué est possible ou non
     */
    public boolean moveIsPossible(Joueur player, Piece piece, int line, int column, Partie game)
    {
        // Cas où le joueur essaye de déplacer une pièce qui ne le peut pas, c'est à dire une pièce de type DRAPEAU ou BOMBE, ou cas où il essaye de déplacer une pièce appartenant au joueur adverse
        if (!piece.getType().getCanMove() || !piece.getColor().equals(player.getColor()))
            return false;
            // Sinon
        else
        {
            // Vérification qu'il n'y ait pas de débordement
            if (line >= 0 && line < LENGTH && column >= 0 && column < LENGTH)
            {
                // Cas particulier pour le type de pièce ECLAIREUR
                // Pièce pouvant se déplacer d'autant de cases que le joueur souhaite horizontalement ou verticalement sur le plateau
                if (piece.toString().equals("ECLAIREUR"))
                {
                    // Double-clic pièce
                    if (line == piece.getBox().getLine() && column == piece.getBox().getColumn())
                        return false;
                    // Déplacement horizontal
                    else if (line == piece.getBox().getLine())
                    {
                        // Cas déplacement vers la droite
                        if (column > piece.getBox().getColumn())
                        {
                            // Boucle de la case à droite de la piece selectionnée jusqu'à la case cible
                            for(int j = piece.getBox().getColumn() + 1; j <= column; j++)
                            {
                                // Cas où un lac fait obstacle
                                if (tab[line][j].isLake())
                                    return false;

                                // Cas où une pièce fait obstacle
                                else if (!tab[line][j].isFree())
                                    // Si on est arrivé à destination et que la pièce appartient à l'autre joueur, le déplacement est possible et une attaque va être lancée
                                    // Dans le cas contraire, impossible d'aller plus loin
                                    return j == column && tab[line][j].isTaken(game.getOtherPlayer(player.getLogin()).getColor());
                            }
                            return true;
                        // Cas déplacement vers la gauche
                        } else
                        {
                            for(int j = piece.getBox().getColumn() - 1; j >= column; j--)
                            {
                                if (tab[line][j].isLake())
                                    return false;

                                else if (!tab[line][j].isFree())
                                    return j == column && tab[line][j].isTaken(game.getOtherPlayer(player.getLogin()).getColor());
                            }
                            return true;
                        }
                        // Déplacement vertical
                    } else if (column == piece.getBox().getColumn())
                    {
                        if (line > piece.getBox().getLine())
                        {
                            for(int i = piece.getBox().getLine() + 1; i <= line; i++) {
                                if (tab[i][column].isLake())
                                    return false;

                                else if (!tab[i][column].isFree()) {
                                    return i == line && tab[i][column].isTaken(game.getOtherPlayer(player.getLogin()).getColor());
                                }
                            }
                            return true;
                        } else
                        {
                            for(int i = piece.getBox().getLine() - 1; i >= line; i--)
                            {
                                if (tab[i][column].isLake())
                                    return false;

                                else if (!tab[i][column].isFree())
                                    return i == line && tab[i][column].isTaken(game.getOtherPlayer(player.getLogin()).getColor());
                            }
                            return true;
                        }
                    } else
                        return false;
                }
                // Cas pour les pièces classiques
                else
                {
                    // Les pièces classiques ne peuvent se déplacer que d'une case horizontalement ou verticalement, pas en diagonale
                    if (((line == piece.getBox().getLine() + 1 || line == piece.getBox().getLine() - 1)
                            && column == piece.getBox().getColumn())
                            || ((column == piece.getBox().getColumn() + 1 || column == piece.getBox().getColumn() - 1))
                            && line == piece.getBox().getLine())
                    {
                        Case targetBox = tab[line][column];
                        // Si la case où l'on veut effectuer le déplacement est libre on peut alors s'y déplacer
                        // Si elle ne l'est pas elle est soit occupée par un lac soit par une autre pièce
                        // Si elle est ni occupée par un lac ni par une autre pièce appartenant au joueur le déplacement est alors possible
                        return targetBox.isFree()
                                || (!targetBox.isFree() && !targetBox.isLake() && !targetBox.isTaken(player.getColor()));

                    } else
                        return false;
                }
            } else
                return false;
        }
    }


    /*
     * Méthode permettant de lancer une attaque
     */
    public boolean attackPiece(Case box, Piece piece, Joueur player, Partie game)
    {
        Piece enemyPiece = box.getPiece();
        // Drapeau capturé, partie terminée
        if (enemyPiece.toString().equals("DRAPEAU"))
            return true;
        else
        {
            // Si un espion attaque un maréchal il gagne
            // Si un démineur attaque une bombe il gagne également
            if ((piece.toString().equals("ESPION") && enemyPiece.toString().equals("MARECHAL"))
                    || (piece.toString().equals("DEMINEUR") && enemyPiece.toString().equals("BOMBE")))
            {
                replacePiece(enemyPiece, piece);
                game.getReservePlayer(game.getOtherPlayer(player.getLogin()).getLogin()).addPiece(enemyPiece);
            }
            else
            {
                // Si la pièce du joueur lançant l'attaque est plus forte que la pièce du joueur adverse alors elle gagne et prend sa place
                if (piece.getType().getPower() > enemyPiece.getType().getPower())
                {
                    replacePiece(enemyPiece, piece);
                    // Ajout de la pièce adverse venant d'être détruite à sa réserve
                    game.getReservePlayer(game.getOtherPlayer(player.getLogin()).getLogin()).addPiece(enemyPiece);
                }
                // Sinon elle est détruite
                else if (piece.getType().getPower() < enemyPiece.getType().getPower())
                {
                    tab[piece.getBox().getLine()][piece.getBox().getColumn()] = new Case(piece.getBox().getLine(), piece.getBox().getColumn());
                    // Ajout de la pièce du joueur ayant lancé et perdu l'attaque à sa réserve
                    game.getReservePlayer(player.getLogin()).addPiece(piece);
                }
                // Si les deux pièces sont de puissances égales elles sont toutes les deux détruites
                else
                {
                    tab[piece.getBox().getLine()][piece.getBox().getColumn()] = new Case(piece.getBox().getLine(), piece.getBox().getColumn());
                    tab[box.getLine()][box.getColumn()] = new Case(box.getLine(),box.getColumn());
                    // Ajout des pièces détruites à la réserve du joueur correspondant
                    game.getReservePlayer(player.getLogin()).addPiece(piece);
                    game.getReservePlayer(game.getOtherPlayer(player.getLogin()).getLogin()).addPiece(enemyPiece);
                }
            }
            return false;
        }
    }

    /*
     * Méthode appelée après une attaque subit par un joueur afin de savoir si il peut encore effectuer des mouvements
     * Appelée seulement si l'attaque n'a pas été portée sur une case contenant le drapeau adverse
     */
    public boolean victory(Joueur player)
    {
        for(int i = 0; i < LENGTH ; i++)
        {
            for(int j = 0; j < LENGTH ; j++)
            {
                if (!tab[i][j].isFree() && !tab[i][j].isLake())
                {
                    // Si le joueur possède encore au moins une pièce pouvant se déplacer alors la partie n'est pas terminée
                    if (tab[i][j].getPiece().getColor().equals(player.getColor())
                            && tab[i][j].getPiece().getType().getCanMove())
                        return false;
                }
            }
        }
        return true;
    }

    /*
     * Méthode permettant de remplacer la pièce perdante par la pièce gagnante après une attaque
     */
    public void replacePiece(Piece losingPiece, Piece winningPiece)
    {
        int oldLineWinningPiece = winningPiece.getBox().getLine();
        int oldColumnWinningPiece = winningPiece.getBox().getColumn();

        // Les coordonnées de la pièce perdante deviennent celles de la pièce gagnante
        // La case où se trouvait la pièce perdante va donc dorénavent contenir la pièce gagnante
        int newLineWinningPiece = losingPiece.getBox().getLine();
        int newColumnWinningPiece = losingPiece.getBox().getColumn();
        setBox(newLineWinningPiece, newColumnWinningPiece, winningPiece);

        // La case qui contenait la pièce gagnante avant l'attaque est ensuite libérée
        tab[oldLineWinningPiece][oldColumnWinningPiece] = new Case(oldLineWinningPiece, oldColumnWinningPiece);
    }

    public Case[][] getTab()
    {
        return tab;
    }

    public static int getLENGTH()
    {
        return LENGTH;
    }

    public Case getBox(int line, int column)
    {
        return tab[line][column];
    }

    public void setBox(int line, int column, Piece piece)
    {
        piece.setBox(tab[line][column]);
        tab[line][column].setPiece(piece);
        tab[line][column].setFree(false);
    }
}
