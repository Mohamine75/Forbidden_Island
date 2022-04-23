import Enums.Artefact;
import Enums.Key;
import Enums.Level;
import Enums.Roles;

import java.util.*;

public class Player  {
    protected int posX;
    protected  int posY;
    protected HashSet<Artefact> artefacts;
    protected ArrayList<Key> keys;
    protected int action = 3;
    protected String name;
    protected Roles role;
    public Player(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.artefacts = new HashSet<>();
        this.keys = new ArrayList<>();
        this.name = null;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getPosX() {
        return posX;
    }

    public void addArtefact(Artefact a) {
        this.artefacts.add(a);
    }

    public void addKeyHasard(int limit){
        Random r = new Random();
        int rand = r.nextInt(limit);
        switch (rand) {
            case 0 -> {
                addKey(Key.EAU);
                System.out.println("Une clé a été ajoute à " + name + " de type "+ Key.EAU.name().toLowerCase());
            }
            case 1 -> {
                addKey(Key.AIR);
                System.out.println("Une clé a été ajoute à " + name + " de type "+ Key.AIR.name().toLowerCase());
            }
            case 2 -> {
                addKey(Key.FEU);
                System.out.println("Une clé a été ajoute à " + name+ " de type "+ Key.FEU.name().toLowerCase());
            }
            case 3 -> {
                addKey(Key.TERRE);
                System.out.println("Une clé a été ajoute à " + name+ " de type "+ Key.TERRE.name().toLowerCase());
            }
        }
    }


    public void addKey(Key k) {
        this.keys.add(k);
    }




   /* public void assecher(int posX, int posY) {
        modele.testLoose();
        if(modele.loose){
            return;
        }
        if ((posX - 1 == this.posX | this.posX == posX + 1 | this.posX == posX) && (posY - 1 == this.posY | this.posY == posY + 1 | this.posY == posY)) {
            if (modele.getCellule(posX,posY).getLevel().equals(Level.inonde)) {
                modele.getCellule(posX,posY).level = Level.normal;
                action-= 1;
            }
        }
    }*/



    // TOSTRINGS
    public void posToString(){
        System.out.println("  la position du joueur est " +this.posX +"," +this.posY);
    }
    public void artefactsToString(){
        if (artefacts.size() == 0){
            System.out.println("'Aucun artéfact en votre possession.");
        }
        else {
            System.out.print("Les artefacts possédés sont :");
            for (Artefact a : artefacts) {
                System.out.println(a.name());
            }
        }
    }

    public void setRole(int role) {
        switch (role){
            case 1-> this.role = Roles.PILOTE;
            case 2-> this.role = Roles.INGENIEUR;
            case 3-> this.role = Roles.EXPLORATEUR;
            case 4-> this.role = Roles.NAVIGATEUR;
            case 5-> this.role = Roles.PLONGEUR;
            case 6-> this.role = Roles.MESSAGER;
        }
    }
}

