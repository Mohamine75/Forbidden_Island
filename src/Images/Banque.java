package Images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Banque {
    private String name;
    public Banque(){
        this.name = "ok";
    }
    public Image img(String name){
        switch (name){
            case "feu_normale" -> {
                return feu;
            }
            case "jgl_normale" -> {
                return jgl_normale;
            }
            case "eau_normale" ->{
                return eau_normale;
            }
        }
        return null;
    }
    public static Image jgl_normale;
    static {
        try {
            jgl_normale = ImageIO.read(new File("src/Images/jgl_normale.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image feu;
    static {
        try {
            feu = ImageIO.read(new File("src/Images/feu_normale.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image eau_normale;
    static {
        try {
            eau_normale = ImageIO.read(new File("src/Images/eau_normale.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
