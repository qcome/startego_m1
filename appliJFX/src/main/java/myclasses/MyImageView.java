package myclasses;

import javafx.scene.image.ImageView;

/**
 * Created by Quentin on 13/01/2017.
 */
public class MyImageView extends ImageView {
    private String nomPiece;
    private String colorImage;

    public MyImageView(String nomPiece, String colorImage){
        super();
        this.nomPiece = nomPiece;
        this.colorImage = colorImage;
    }

    public String getNomPiece(){
        return nomPiece;
    }

    public String getColorImage(){
        return colorImage;
    }

}
