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
            case "terre_normale" ->{
                return terre_normale;
            }
            case "air_normale" ->{
                return air_normale;
            }
            case "feu_inonde" ->{
                return feu_inonde;
            }
            case "jgl_inonde" ->{
                return jgl_inonde;
            }
            case "submerge" ->{
                return submerge;
            }
            case "p1" ->{
                return p1;
            }
            case "p2" ->{
                return p2;
            }
            case "p3" ->{
                return p3;
            }
            case "p4" ->{
                return p4;
            }
            case "eau_inonde"->{
                return eau_inonde;
            }
            case "terre_inonde"->{
                return terre_inonde;
            }
            case "air_inonde" ->{
                return air_inonde;
            }
            case "helico" ->{
                return helico;
            }
            case "helico_inonde" ->{
                return helico_inonde;
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

    public static Image terre_normale;
    static {
        try {
            terre_normale = ImageIO.read(new File("src/Images/terre_normale.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image air_normale;
    static {
        try {
            air_normale = ImageIO.read(new File("src/Images/air_normale.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image feu_inonde;
    static {
        try {
            feu_inonde = ImageIO.read(new File("src/Images/feu_inonde.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image jgl_inonde;
    static {
        try {
            jgl_inonde = ImageIO.read(new File("src/Images/jgl_inonde.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image submerge;
    static {
        try {
            submerge = ImageIO.read(new File("src/Images/submerge.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image p1;
    static {
        try {
            p1 = ImageIO.read(new File("src/Images/p1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image p2;
    static {
        try {
            p2 = ImageIO.read(new File("src/Images/p2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image p3;
    static {
        try {
            p3 = ImageIO.read(new File("src/Images/p3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image p4;
    static {
        try {
            p4 = ImageIO.read(new File("src/Images/p4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image eau_inonde;
    static {
        try {
            eau_inonde = ImageIO.read(new File("src/Images/eau_inonde.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image terre_inonde;
    static {
        try {
            terre_inonde = ImageIO.read(new File("src/Images/terre_inonde.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image air_inonde;
    static {
        try {
            air_inonde = ImageIO.read(new File("src/Images/air_inonde.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image helico;
    static {
        try {
            helico = ImageIO.read(new File("src/Images/helico_normal.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Image helico_inonde;
    static {
        try {
            helico_inonde = ImageIO.read(new File("src/Images/helico_inonde.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
