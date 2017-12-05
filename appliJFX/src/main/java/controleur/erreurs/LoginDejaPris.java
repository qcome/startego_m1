package controleur.erreurs;

/**
 * Created by Quentin on 09/01/2017.
 */
public class LoginDejaPris extends Exception {
    public LoginDejaPris(String s) {
        super(s);
    }
}
