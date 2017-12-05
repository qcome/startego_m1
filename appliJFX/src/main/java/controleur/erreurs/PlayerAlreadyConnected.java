package controleur.erreurs;

/**
 * Created by Quentin on 11/01/2017.
 */
public class PlayerAlreadyConnected extends Exception {
    public PlayerAlreadyConnected(String s) {
        super(s);
    }
}
