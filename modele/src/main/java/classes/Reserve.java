package classes;

import classes.exceptions.PieceAlreadyPlacedException;

import java.io.Serializable;
import java.util.*;

/**
 * Created by root on 11/19/16.
 */
public class Reserve implements Serializable
{
    private Map<String, List<Piece>> stock = new HashMap<>();

    public Reserve(String color)
    {
        stock.put("BOMBE", new ArrayList<>());
        stock.put("MARECHAL", new ArrayList<>());
        stock.put("GENERAL", new ArrayList<>());
        stock.put("COLONEL", new ArrayList<>());
        stock.put("COMMANDANT", new ArrayList<>());
        stock.put("CAPITAINE", new ArrayList<>());
        stock.put("LIEUTENANT", new ArrayList<>());
        stock.put("SERGENT", new ArrayList<>());
        stock.put("DEMINEUR", new ArrayList<>());
        stock.put("ECLAIREUR", new ArrayList<>());
        stock.put("ESPION", new ArrayList<>());
        stock.put("DRAPEAU", new ArrayList<>());

        fillStock(TypePiece.BOMBE, color, 6);
        fillStock(TypePiece.MARECHAL, color, 1);
        fillStock(TypePiece.GENERAL, color, 1);
        fillStock(TypePiece.COLONEL, color, 2);
        fillStock(TypePiece.COMMANDANT, color, 3);
        fillStock(TypePiece.CAPITAINE, color, 4);
        fillStock(TypePiece.LIEUTENANT, color, 4);
        fillStock(TypePiece.SERGENT, color, 4);
        fillStock(TypePiece.DEMINEUR, color, 5);
        fillStock(TypePiece.ECLAIREUR, color, 8);
        fillStock(TypePiece.ESPION, color, 1);
        fillStock(TypePiece.DRAPEAU, color, 1);
    }

    /*
     * Méthode permettant de créer et d'ajouter un certain nombre de pièce d'un type choisi dans la réserve lors de sa création
     */
    public void fillStock(TypePiece type, String color, int occurrencesNumber)
    {
        Iterator it = stock.keySet().iterator();
        while(it.hasNext())
        {
            String key = (String)it.next();
            if(key.equals(type.toString()))
            {
                List list = stock.get(key);
                for(int i = 0; i < occurrencesNumber; i++)
                {
                    list.add(new Piece(type, color));
                }
                stock.put(key, list);
                return;
            }
        }
    }

    /*
     * Méthode permettant de supprimer une pièce de la réserve en fonction de son type
     */
    public void removePiece(TypePiece type)
    {
        List list = stock.get(type.toString());
        list.remove(list.size() - 1);

        stock.put(type.toString(), list);
    }

    /*
     * Méthode permettant d'ajouter une pièce venant d'être détruite à la réserve
     */
    public void addPiece(Piece piece)
    {
        List list = stock.get(piece.toString());
        list.add(piece);
        stock.put(piece.toString(), list);
    }

    /*
     * Méthode permettant de savoir si la réserve est vide ou non
     */
    public boolean isEmpty()
    {
        Set<String> keys = stock.keySet();
        for(String key : keys)
        {
            if(!stock.get(key).isEmpty())
                return false;
        }
        return true;
    }

    public String toString()
    {
        String result = "Pièces disponibles pour placement: ";
        Set<String> keys = stock.keySet();
        for(String key : keys)
        {
            result += key + ": " + stock.get(key).size() + " ";
        }
        return result;
    }

    public Piece getPiece(TypePiece type) throws PieceAlreadyPlacedException
    {
        List list = stock.get(type.getName());
        if(list.isEmpty())
            throw new PieceAlreadyPlacedException();
        else
            return (Piece)list.get(list.size() - 1);
    }

    public Map<String, List<Piece>> getStock() {
        return stock;
    }
}
