package Enums;

import javax.print.attribute.standard.MediaSize;

public enum Roles {
    PILOTE,INGENIEUR,EXPLORATEUR,NAVIGATEUR,PLONGEUR,MESSAGER,BASE;

   public Roles getR(int r){
        switch (r){
            case 1 -> {
                return PILOTE;
            }
            case 2 -> {
                return INGENIEUR;
            }
            case 3 -> {
                return EXPLORATEUR;
            }
            case 4 -> {
                return NAVIGATEUR;
            }
            case 5 -> {
                return PLONGEUR;
            }
            case 6 -> {
                return MESSAGER;
            }
            case 7 -> {
                return BASE;
            }
        }
        return null;
    }
}
