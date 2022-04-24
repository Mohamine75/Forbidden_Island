import Enums.Artefact;
import Enums.Key;
import Enums.Level;
import Enums.Roles;

import java.awt.image.BufferedImage;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import Images.*;

interface Observer {


    void update();
}

abstract class Observable {

    private final ArrayList<Observer> observers;

    public Observable() {
        this.observers = new ArrayList<Observer>();
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }


    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }
}

public class Conway {

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            /** Voici le contenu qui nous intéresse. */
            CModele modele = new CModele();
            CVue vue = new CVue(modele);
        });
    }
}


class CModele extends Observable {
    public static final int HAUTEUR = 5, LARGEUR = 5;
    private final Cellule[][] cellules;
    protected boolean win = false;
    protected boolean loose = false;
    protected HashMap<Integer, Player> joueurs = new HashMap<>();
    protected int tour = 0;
    protected Banque b = new Banque();
    private Cellule heliport;
    protected int width = 1920;
    protected int height = 1080;

    public CModele() {
        creationJoueurs();
        cellules = new Cellule[LARGEUR + 2][HAUTEUR + 2];
        for (int i = 0; i < HAUTEUR + 2; i++) {
            for (int j = 0; j < LARGEUR + 2; j++) {
                cellules[i][j] = new Cellule(this, i, j);
            }
        }
        ArrayList<Cellule> temp = new ArrayList<>();
        Random r = new Random();
        int x = r.nextInt(LARGEUR);
        int y = r.nextInt(HAUTEUR);
        this.heliport = cellules[x][y];
        heliport.heliport = true;
        temp.add(heliport);
        init(Artefact.EAU, temp);
        init(Artefact.EAU, temp);
        init(Artefact.AIR, temp);
        init(Artefact.AIR, temp);
        init(Artefact.FEU, temp);
        init(Artefact.FEU, temp);
        init(Artefact.TERRE, temp);
        init(Artefact.TERRE, temp);
    }

    private void init(Artefact a, ArrayList<Cellule> temp) {
        Random r = new Random();
        int x = r.nextInt(LARGEUR);
        int y = r.nextInt(HAUTEUR);
        while (temp.contains(cellules[x][y])) {
            x = r.nextInt(LARGEUR);
            y = r.nextInt(HAUTEUR);
        }
        cellules[x][y].artefact = a;
        temp.add(cellules[x][y]);
    }

    private void creationJoueurs() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenue dans l'ile interdite, Veuillez entrer le nombre de joueurs, entre 2 et 4 compris");
        int nb;
        nb = sc.nextInt();
        while (nb < 2 || nb > 4) {
            System.out.println("Retape le nombre arrête de jouer au con frérot");
            nb = sc.nextInt();
        }
        ArrayList<String> noms = createNames(nb, sc);
        ajoutePlayer(nb);
        for (int i = 0; i < joueurs.size(); i++) {
            joueurs.get(i).setName(noms.get(i));
        }
        joueursSetRoles();
    }


    private ArrayList<String> createNames(int nb, Scanner sc) {
        ArrayList<String> noms = new ArrayList<>();
        for (int i = 1; i <= nb; i++) {
            System.out.println("Nom du joueur " + i);
            String n = sc.next();
            while (noms.contains(n)) {
                System.out.println("Nom déjà attribué, retapez un autre svp");
                n = sc.next();
            }
            noms.add(n);
        }
        return noms;
    }

    private void ajoutePlayer(int nb) {
        switch (nb) {
            case 2 -> {
                joueurs.put(0, new Player(0, 0));
                joueurs.put(1, new Player(0, 1));
            }
            case 3 -> {
                joueurs.put(0, new Player(0, 0));
                joueurs.put(1, new Player(0, 1));
                joueurs.put(2, new Player(1, 0));
            }
            case 4 -> {
                joueurs.put(0, new Player(0, 0));
                joueurs.put(1, new Player(0, 1));
                joueurs.put(2, new Player(1, 0));
                joueurs.put(3, new Player(1, 1));
            }
        }
    }

    private void joueursSetRoles() {
        Scanner sc = new Scanner(System.in);
        int nb;
        Roles r = Roles.BASE;
        ArrayList<Roles> roles = new ArrayList<>();
        System.out.println("Choissiez le rôle des joueurs en tapant le numéro associé:\n" +
                "1 - Pilote\n2 - Ingénieur\n3 - Explorateur\n4 - Navigateur\n" +
                "5 - Plongeur\n6 - Messager\nDeux joueurs ne pouvant avoir le " +
                "même role.");
        for (Player p : joueurs.values()) {
            System.out.println("Choisissez le rôle de " + p.name);
            nb = sc.nextInt();
            while (roles.contains(r.getR(nb)) || nb > 6 || nb < 1) {
                System.out.println("Role déjà attribué, veuillez re essayer ou invalide :");
                nb = sc.nextInt();
            }
            p.setRole(nb);
            roles.add(p.role);
            System.out.println("Le rôle de " + p.name + " est " + p.role.name());
        }
    }

    public void testLoose() throws InterruptedException {

        if (heliport.level == Level.submerge) {
            this.loose = true;
            System.out.println("Partie perdue !");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;


        }
        testDeaths();
    }

    private void testDeaths() throws InterruptedException {

        for (Player p : joueurs.values()) {
            if (cellules[p.posX][p.posY].level == Level.submerge && p.role != Roles.PLONGEUR) {
                System.out.println("Un joueur s'est noyé, fin du jeu :(");
                System.out.println("Joueur : " + p.name);
                this.loose = true;
                System.out.println("Partie perdue !");
                TimeUnit.SECONDS.sleep(10);
                System.exit(0);
                return;
            }
        }
    }

    public void avance() throws InterruptedException {
        testLoose();
        if (joueurs.get(tour).action > 0.5) {
            System.out.println("Il vous reste encore " + joueurs.get(tour).action + " action à faire ");
            return;
        }
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        joueurs.get(tour).addKeyHasard(16);
        Random random = new Random();
        ArrayList<Cellule> res = new ArrayList<>();
        while (res.size() < 3) {
            int x = random.nextInt(HAUTEUR + 1);
            int y = random.nextInt(LARGEUR + 1);
            Cellule c = getCellule(x, y);
            if (!getCellule(x, y).getLevel().equals(Level.submerge) && countEtats() >= 3) {
                getCellule(x, y).evolue();
                res.add(c);
            }
            if (countEtats() < 3) {
                res.add(c);
            }
        }
        notifyObservers();
        tourParTour();
    }

    private int countEtats() {
        int count = 0;
        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                if (!cellules[i][j].level.equals(Level.submerge)) {
                    count++;
                }
            }

        }
        return count;
    }

    public void tourParTour() {
        if (joueurs.get(tour).action <= 0.5) {
            joueurs.get(tour).action = 3;
            joueurs.get(tour).actionObjet = 1;
            tour++;
            if (tour == joueurs.size()) {
                tour = 0;
            }
        }
    }

    public void searchKey() throws InterruptedException {
        testLoose();
        if (joueurs.get(tour).action - 1 < 0) {
            return;
        }
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        Player p = joueurs.get(tour);
        Random r = new Random();
        switch (r.nextInt(3)) {
            case 0 -> {
                ArrayList<Cellule> res = new ArrayList<>();
                while (res.size() < 3) {
                    int x = r.nextInt(LARGEUR + 1);
                    int y = r.nextInt(HAUTEUR + 1);
                    Cellule c = getCellule(x, y);
                    if (!getCellule(x, y).getLevel().equals(Level.submerge) && countEtats() >= 3) {
                        getCellule(x, y).evolue();
                        res.add(c);
                    }
                    if (countEtats() < 3) {
                        res.add(c);
                    }
                }
            }
            case 1 -> p.addKeyHasard(5);
            case 2 -> System.out.println("Rien ne s'est passée...");
        }
        p.action -= 1;
    }

    public void helico() throws InterruptedException {
        ArrayList<Artefact> a = new ArrayList<>();
        testLoose();
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        if (joueurs.get(tour).action - 1 < 0) {
            return;
        }

        for (Player p : joueurs.values()) {
            if (heliport.x == p.posX && heliport.y == p.posY) {
                for (Artefact artefact : p.artefacts) {
                    if (!a.contains(artefact)) {
                        a.add(artefact);
                    }
                }
            } else {
                System.out.println("Un joueur n'est pas sur l'héliport, veuillez vous regrouper");
                return;
            }
        }
        if (a.size() == 4) {
            System.out.println("Bravo, partie gagnée");
            win = true;
            helico();
        }
        System.out.println("Les conditions ne sont pas réunies.");

    }


    public void move(String direction) throws InterruptedException {
        testLoose();
        if (joueurs.get(tour).action - 1 < 0) {
            return;
        }
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        Player p = joueurs.get(tour);
        int y = p.posY;
        int x = p.posX;
        switch (direction) {
            case "⬆" -> y = p.posY - 1;
            case "⬇" -> y = p.posY + 1;
            case "➡" -> x = p.posX + 1;
            case "⬅" -> x = p.posX - 1;
            case "⬈" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY - 1;
                    x = p.posX + 1;
                }
            }
            case "⬊" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY + 1;
                    x = p.posX + 1;
                }
            }
            case "⬋" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY + 1;
                    x = p.posX - 1;
                }
            }
            case "⬉" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY - 1;
                    x = p.posX - 1;
                }
            }

        }
        if ((x < 0 || x >= HAUTEUR) || (y >= HAUTEUR || y < 0)) {
            System.out.println("Out of Bounds");
            return;
        }
        if (getCellule(x, y).getLevel().equals(Level.submerge) && p.role != Roles.PLONGEUR) {
            System.out.println("impossible,vous n'êtes pas plongeur");
            return;
        }
        p.posX = x;
        p.posY = y;
        p.action -= 1;
        notifyObservers();
    }

    public Cellule getCellule(int x, int y) {
        return cellules[x][y];
    }

    protected void assecher(String direction) throws InterruptedException {
        if (joueurs.get(tour).action < 0.5 || loose) {
            return;

        }
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        Player p = joueurs.get(tour);
        int x = p.posX;
        int y = p.posY;
        switch (direction) {
            case "⬆" -> y = p.posY - 1;
            case "⬇" -> y = p.posY + 1;
            case "➡" -> x = p.posX + 1;
            case "⬅" -> x = p.posX - 1;
            case "⬛" -> {
            }
            case "⬈" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY - 1;
                    x = p.posX + 1;
                }
            }
            case "⬊" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY + 1;
                    x = p.posX + 1;
                }
            }
            case "⬋" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY + 1;
                    x = p.posX - 1;
                }
            }
            case "⬉" -> {
                if (p.role == Roles.EXPLORATEUR) {
                    y = p.posY - 1;
                    x = p.posX - 1;
                }
            }

        }
        if ((x < 0 || x >= HAUTEUR) || (y >= HAUTEUR || y < 0)) {
            System.out.println("La Case est en dehors de la grille");
            return;
        }
        if (cellules[x][y].level == Level.inonde) {
            cellules[x][y].level = Level.normal;
            if (p.role != Roles.INGENIEUR) {
                p.action -= 1;
            } else {
                p.action -= 0.5;
            }
        } else {

            System.out.println("La case demandée est submergée ou à l'état normale...");
        }
    }

    public void actionSpeciale() throws InterruptedException {
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        if (joueurs.get(tour).action - 1 < 0 || loose) {
            return;
        }
        switch (joueurs.get(tour).role) {
            case PILOTE -> pilote(joueurs.get(tour));
            case PLONGEUR -> System.out.println("Vous pouvez traverser les cases submergées, cela coùte une action");
            case NAVIGATEUR -> navigateur();
            case MESSAGER -> messager();
            case INGENIEUR -> System.out.println("Vous pouvez assecher 2 zones pour 1 action");
            case EXPLORATEUR -> System.out.println("Vous pouvez vous déplacer et assécher en diagonale");
        }
    }

    private void pilote(Player p) {
        int x;
        int y;
        Scanner sc = new Scanner(System.in);
        System.out.println("ACTION SPECIALE, veuillez entrez l'endroit où vous voulez aller, qui n'est bien sûr pas subermergé");
        System.out.println("x :");
        x = sc.nextInt();
        System.out.println("y :");
        y = sc.nextInt();
        while ((x < 0 || x >= LARGEUR) || (y >= HAUTEUR || y < 0) || cellules[x][y].level == Level.submerge) {
            System.out.println("Cellule submergée, impossible");
            System.out.println("x :");
            x = sc.nextInt();
            System.out.println("y :");
            y = sc.nextInt();
        }
        p.action -= 1;
        p.posX = x;
        p.posY = y;
    }

    private void navigateur() {
        int num;
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez le numéro du joueur que vous voulez déplacer");
        num = sc.nextInt() - 1;
        while (num < 0 || num > joueurs.size() || num == tour) {
            System.out.println("Retapez le numéro svp :)");
            num = sc.nextInt() - 1;
        }
        Player p = joueurs.get(num);
        int xy = 1000;
        System.out.println("Vous voulez le déplacer en x ou en y ?");
        switch (sc.next()) {
            case "x" -> {
                while (p.posX + xy >= LARGEUR || (xy < 0 && p.posX + xy < 0) || (xy > 2) || (xy < -2)) {
                    System.out.println("Entrez le nombre à addition (peut être négatif)");
                    xy = sc.nextInt();
                }
                p.posX = p.posX + xy;
            }
            case "y" -> {
                while (p.posY - xy >= HAUTEUR || (xy > 0 && p.posY - xy < 0) || (xy > 2) || (xy < -2)) {
                    System.out.println("Entrez le nombre à addition (peut être négatif)");
                    xy = sc.nextInt();
                }
                p.posY = p.posY - xy;
            }

        }
        joueurs.get(tour).action -= 1;
    }

    private void messager() {
        Player p = joueurs.get(tour);
        if (p.keys.size() == 0) return;
        Scanner sc = new Scanner(System.in);
        System.out.println("Quelle clé voulez-vous donner,entrez le type?");
        String key = sc.next();
        key = key.toLowerCase();
        while (!key.equals("eau") && !key.equals("terre") && !key.equals("feu") && !key.equals("air")) {
            System.out.println("Quelle clé voulez-vous donner,entrez le type?");
            key = sc.next();
            key = key.toLowerCase();
        }
        if (!p.keys.contains(Key.valueOf(key.toUpperCase(Locale.ROOT)))) {
            System.out.println("Vous n'avez pas ce type de clé");
            return;
        }
        p.keys.remove(Key.valueOf(key.toUpperCase(Locale.ROOT)));
        System.out.println("Entrez le numéro du joueur ciblé");
        int num = sc.nextInt() - 1;
        while (num < 0 || num + 1 > joueurs.size() || num == tour) {
            System.out.println("Retapez le numéro svp :)");
            num = sc.nextInt() - 1;
        }
        joueurs.get(num).addKey(Key.valueOf(key.toUpperCase(Locale.ROOT)));
        System.out.println("Le joueur " + joueurs.get(num).name + " A reçu la clé " + Key.valueOf(key.toUpperCase(Locale.ROOT)));
        p.action -= 1;
    }

    public void giveKey() {
        Player p = joueurs.get(tour);
        if (p.action - 1 < 0 || loose || p.keys.size() == 0) {
            return;
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Quelle clé voulez-vous donner,entrez le type?");
        String key = sc.next();
        key = key.toLowerCase();
        while (!key.equals("eau") && !key.equals("terre") && !key.equals("feu") && !key.equals("air")) {
            System.out.println("Quelle clé voulez-vous donner,entrez le type?");
            key = sc.next();
            key = key.toLowerCase();
        }
        Key k = Key.valueOf(key.toUpperCase(Locale.ROOT));
        if (!p.keys.contains(k)) {
            System.out.println("Vous n'avez pas ce type de clé");
            return;
        }
        for (Player x : joueurs.values()) {
            if (x.posX == p.posX && p.posY == x.posY && !x.name.equals(p.name)) {
                p.keys.remove(k);
                x.addKey(k);
                System.out.println("La clé de type " + k.name().toLowerCase() + " a été donné à " + x.name);
                p.action -= 1;
                return;
            }
        }
        System.out.println("Aucun joueur présent dans votre zone :(");
    }

    /**
     * Notez qu'à l'intérieur de la classe [CModele], la classe interne est
     * connue sous le nom abrégé [Cellule].
     * Son nom complet est [CModele.Cellule], et cette version complète est
     * la seule à pouvoir être utilisée depuis l'extérieur de [CModele].
     * Dans [CModele], les deux fonctionnent.
     */

    public boolean verifArtefact(Artefact a, Player p) {
        for (Key k : p.keys) {
            if (k.name().equals(a.name())) {
                return true;
            }
        }
        return false;
    }

    public void recupererArtefact() throws InterruptedException {
        if (joueurs.get(tour).action == 0 || loose) {
            return;
        }
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        Player p = joueurs.get(tour);

        if (null != getCellule(p.posX, p.posY).artefact) {
            if (verifArtefact(getCellule(p.posX, p.posY).artefact, p)) {
                p.keys.remove(Key.valueOf(getCellule(p.posX, p.posY).artefact.name()));
                p.addArtefact(getCellule(p.posX, p.posY).artefact);
                getCellule(p.posX, p.posY).artefact = null;
                System.out.println("Artefact  récupéré");
                p.action -= 1;

                return;
            }
        }
        System.out.println("aucun Artefact non récupéré ");
    }

    public void recupKey() throws InterruptedException {
        Player p = joueurs.get(tour);
        p.action -= 1;
        if (null != getCellule(p.posX, p.posY).key) {
            p.keys.add(getCellule(p.posX, p.posY).key);
            getCellule(p.posX, p.posY).key = null;
            System.out.println("Clé recupérée !!");

            return;

        }
        Random random = new Random();
        if (random.nextInt(4) == 0) {
            getCellule(p.posX, p.posY).evolue();
            System.out.println("Montée des eaux, !!");

        }
        System.out.println("Aucune clé ici");
        avance();
    }


    public void objet() throws InterruptedException {
        if (this.win) {
            System.out.println("Bravo, le jeu est gagné :)");
            TimeUnit.SECONDS.sleep(10);
            System.exit(0);
            return;
        }
        Player p = joueurs.get(tour);
        if ((p.action == 0 && p.actionObjet == 0) || loose) {
            return;
        }
        if (p.objets.size() == 0) {
            System.out.println("Vous n'avez aucun objet à utiliser");
            return;
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Quelle objet voulez-vous utiliser, faites stop sinon.");
        String objet = sc.next();
        objet = objet.toLowerCase();
        while (!p.objets.contains(objet)) {
            if (objet.equals("stop")) {
                return;
            }
            System.out.println("Quelle objet voulez-vous utiliser, faites stop sinon.");
            objet = sc.next();
            objet = objet.toLowerCase();
        }
        System.out.println("Objet, veuillez utiliser");
        System.out.println("x :");
        int x = sc.nextInt();
        System.out.println("y :");
        int y = sc.nextInt();
        while ((x < 0 || x >= LARGEUR) || (y >= HAUTEUR || y < 0) || cellules[x][y].level == Level.submerge) {
            System.out.println("impossible");
            System.out.println("x :");
            x = sc.nextInt();
            System.out.println("y :");
            y = sc.nextInt();
        }
        switch (objet) {
            case "helico" -> {
                System.out.println("Voulez-vous emporter les autres joueurs avec vous ?");
                objet = sc.next();
                objet = objet.toLowerCase();
                while (!objet.equals("oui") && !objet.equals("non")) {
                    System.out.println("oui ou non ?");
                    objet = sc.next();
                    objet = objet.toLowerCase();
                }
                if (objet.equals("oui")) {
                    for (Player p1 : joueurs.values()) {
                        if (p1.posY == p.posY && p1.posX == p.posX) {
                            p1.posY = y;
                            p1.posX = x;
                        }
                    }
                    p.posY = y;
                    p.posX = x;
                } else {
                    p.posY = y;
                    p.posX = x;
                }
                p.objets.remove(objet);
                if (p.actionObjet == 1) {
                    p.actionObjet -= 1;
                } else {
                    p.action -= 1;
                }
            }
                case "sable" -> {
                    if (cellules[x][y].level == Level.inonde) {
                        cellules[x][y].level = Level.normal;
                        p.objets.remove(objet);
                        if (p.actionObjet == 1) {
                            p.actionObjet -= 1;
                        } else {
                            p.action -= 1;
                        }
                    }
                }

            }
        }
    }
/**
 * Fin de la classe CModele.
 */

/**
 * Définition d'une classe pour les cellules.
 * Cette classe fait encore partie du modèle.
 */
class Cellule extends Graphismes.ZoneCliquable {
    /**
     * On stocke les coordonnées pour pouvoir les passer au modèle lors
     * de l'appel à [compteVoisines].
     */
    protected final int x, y;
    /**
     * On conserve un pointeur vers la classe principale du modèle.
     */
    private final CModele modele;
    protected Artefact artefact;
    protected Key key;
    protected boolean heliport;
    /**
     * L'état d'une cellule est donné par un booléen.
     */
    protected Level level;

    public Cellule(CModele modele, int x, int y) {
        super(40, 40);
        this.modele = modele;
        this.level = Level.normal;
        this.artefact = null;
        this.key = null;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Le passage à la génération suivante se fait en deux étapes :
     * - D'abord on calcule pour chaque cellule ce que sera sont état à la
     * génération suivante (méthode [evalue]). On stocke le résultat
     * dans un attribut supplémentaire [prochainEtat].
     * - Ensuite on met à jour l'ensemble des cellules (méthode [evolue]).
     * Objectif : éviter qu'une évolution immédiate d'une cellule pollue
     * la décision prise pour une cellule voisine.
     */

    protected void evolue() {
        switch (level) {
            case normal -> level = Level.inonde;
            case inonde, submerge -> level = Level.submerge;
        }
    }

    /**
     * Un test à l'usage des autres classes (sera utilisé par la vue).
     */
    public Level getLevel() {
        return level;
    }

    @Override
    public void clicGauche() {
    }

    @Override
    public void clicDroit() {

    }
}
/**
 * Fin de la classe Cellule, et du modèle en général.
 */


/**
 * La vue : l'interface avec l'utilisateur.
 * <p>
 * On définit une classe chapeau [CVue] qui crée la fenêtre principale de
 * l'application et contient les deux parties principales de notre vue :
 * - Une zone d'affichage où on voit l'ensemble des cellules.
 * - Une zone de commande avec un bouton pour passer à la génération suivante.
 */
class CVue {
    /**
     * JFrame est une classe fournie pas Swing. Elle représente la fenêtre
     * de l'application graphique.
     */
    private final JFrame frame;
    /**
     * VueGrille et VueCommandes sont deux classes définies plus loin, pour
     * nos deux parties de l'interface graphique.
     */
    private final VueGrille grille;
    private final VueCommandes commandes;

    /**
     * Construction d'une vue attachée à un modèle.
     */
    public CVue(CModele modele) {
        /** Définition de la fenêtre principale. */
        frame = new JFrame();
        frame.setTitle("Jeu de la vie de Conway");


        frame.setLayout(new GridLayout());

        /** Définition des deux vues et ajout à la fenêtre. */
        grille = new VueGrille(modele);
        frame.add(grille);
        commandes = new VueCommandes(modele);
        frame.add(commandes);
        System.out.println(frame.getSize());
        /**
         * Remarque : on peut passer à la méthode [add] des paramètres
         * supplémentaires indiquant où placer l'élément. Par exemple, si on
         * avait conservé la disposition par défaut [BorderLayout], on aurait
         * pu écrire le code suivant pour placer la grille à gauche et les
         * commandes à droite.
         *     frame.add(grille, BorderLayout.WEST);
         *     frame.add(commandes, BorderLayout.EAST);
         */

        /**
         * Fin de la plomberie :
         *  - Ajustement de la taille de la fenêtre en fonction du contenu.
         *  - Indiquer qu'on quitte l'application si la fenêtre est fermée.
         *  - Préciser que la fenêtre doit bien apparaître à l'écran.
         */

        frame.setSize(modele.width, modele.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}


/**
 * Une classe pour représenter la zone d'affichage des cellules.
 * <p>
 * JPanel est une classe d'éléments graphiques, pouvant comme JFrame contenir
 * d'autres éléments graphiques.
 * <p>
 * Cette vue va être un observateur du modèle et sera mise à jour à chaque
 * nouvelle génération des cellules.
 */
class VueGrille extends JPanel implements Observer {
    /**
     * Définition d'une taille (en pixels) pour l'affichage des cellules.
     */
    private final static int TAILLE = 100;
    /**
     * On maintient une référence vers le modèle.
     */
    private final CModele modele;

    /**
     * Constructeur.
     */
    public VueGrille(CModele modele) {
        this.modele = modele;
        /** On enregistre la vue [this] en tant qu'observateur de [modele]. */
        modele.addObserver(this);
        /**
         * Définition et application d'une taille fixe pour cette zone de
         * l'interface, calculée en fonction du nombre de cellules et de la
         * taille d'affichage.
         */
        Dimension dim = new Dimension(TAILLE * CModele.LARGEUR,
                TAILLE * CModele.HAUTEUR);
        this.setPreferredSize(dim);
    }

    /**
     * L'interface [Observer] demande de fournir une méthode [update], qui
     * sera appelée lorsque la vue sera notifiée d'un changement dans le
     * modèle. Ici on se content de réafficher toute la grille avec la méthode
     * prédéfinie [repaint].
     */
    public void update() {
        repaint();
    }

    /**
     * Les éléments graphiques comme [JPanel] possèdent une méthode
     * [paintComponent] qui définit l'action à accomplir pour afficher cet
     * élément. On la redéfinit ici pour lui confier l'affichage des cellules.
     * <p>
     * La classe [Graphics] regroupe les éléments de style sur le dessin,
     * comme la couleur actuelle.
     */
    public void paintComponent(Graphics g) {
        super.repaint();
        /** Pour chaque cellule... */
        for (int i = 0; i <= CModele.LARGEUR; i++) {
            for (int j = 0; j <= CModele.HAUTEUR; j++) {
                /**
                 * ... Appeler une fonction d'affichage auxiliaire.
                 * On lui fournit les informations de dessin [g] et les
                 * coordonnées du coin en haut à gauche.
                 */
                paint(g, modele.getCellule(i, j), (i) * TAILLE + 1, (j) * TAILLE + 1);
            }
        }

    }


    /**
     * Fonction auxiliaire de dessin d'une cellule.
     * Ici, la classe [Cellule] ne peut être désignée que par l'intermédiaire
     * de la classe [CModele] à laquelle elle est interne, d'où le type
     * [CModele.Cellule].
     * Ceci serait impossible si [Cellule] était déclarée privée dans [CModele].
     */
    private void paint(Graphics g, Cellule c, int x, int y) {
        switch (c.level) {
            case normal -> g.drawImage(modele.b.img("jgl_normale"), x, y, TAILLE, TAILLE, null);
            case inonde ->g.drawImage(modele.b.img("jgl_inonde"), x, y, TAILLE, TAILLE, null);
            case submerge -> g.drawImage(modele.b.img("submerge"), x, y, TAILLE, TAILLE, null);

        }
        int i = 0;
        if (!(c.artefact == null)) {
            switch (c.level) {
                case normal -> {
                    switch (c.artefact) {
                        case EAU -> g.drawImage(modele.b.img("eau_normale"), x, y, TAILLE, TAILLE, null);
                        case FEU -> g.drawImage(modele.b.img("feu_normale"), x, y, TAILLE, TAILLE, null);
                        case TERRE -> g.drawImage(modele.b.img("terre_normale"), x, y, TAILLE, TAILLE, null);
                        case AIR -> g.drawImage(modele.b.img("air_normale"), x, y, TAILLE, TAILLE, null);
                    }
                }
                case inonde -> {
                    switch (c.artefact) {
                        case EAU -> g.drawImage(modele.b.img("eau_inonde"), x, y, TAILLE, TAILLE, null);
                        case FEU -> g.drawImage(modele.b.img("feu_inonde"), x, y, TAILLE, TAILLE, null);
                        case TERRE -> g.drawImage(modele.b.img("terre_inonde"), x, y, TAILLE, TAILLE, null);
                        case AIR -> g.drawImage(modele.b.img("air_inonde"), x, y, TAILLE, TAILLE, null);
                    }
                }
            }
        }
        if (c.heliport) {
            if(c.level == Level.normal) {
                g.drawImage(modele.b.img("helico"), x, y, TAILLE, TAILLE, null);
            }
            else if(c.level == Level.inonde) {
                g.drawImage(modele.b.img("helico_inonde"), x, y, TAILLE, TAILLE, null);
                }
            else{
                g.drawImage(modele.b.img("submerge"), x, y, TAILLE, TAILLE, null);
                }
            }
        for (Player p : modele.joueurs.values()) {
            if (c.getX() == p.posX && c.getY() == p.posY) {
                switch (i) {
                    case 0 -> g.drawImage(modele.b.img("p1"), x, y, TAILLE/2, TAILLE/2, null);
                    case 1 ->g.drawImage(modele.b.img("p2"), x, y, TAILLE/2, TAILLE/2, null);
                    case 2 ->g.drawImage(modele.b.img("p3"), x, y, TAILLE/2, TAILLE/2, null);
                    case 3 -> g.drawImage(modele.b.img("p4"), x, y, TAILLE/2, TAILLE/2, null);
                }
            }
            i++;
        }
        Player p =  modele.joueurs.get(modele.tour);
        g.clearRect(TAILLE,TAILLE*6,200,110);
        g.setFont(g.getFont().deriveFont(Font.BOLD));
        g.drawString("Nom du Joueur :   " + p.name,TAILLE,TAILLE *6+ 10);
        g.drawString("Role :   " + p.role,TAILLE,TAILLE *6+ 30);

        g.drawString("Actions : " + p.action, TAILLE,(TAILLE * 6 )+ 50);
        g.drawString(" Artefacts : " + p.artefacts , TAILLE ,(TAILLE * 6 )+ 70);
        g.drawString(" Keys : " + p.keys , TAILLE ,(TAILLE * 6 )+ 90);
        g.drawString(" Objets : " + p.objets , TAILLE ,(TAILLE * 6 )+ 110);

    }
}


class VueCommandes extends JPanel {
    /**
     * Pour que le bouton puisse transmettre ses ordres, on garde une
     * référence au modèle.
     */
    private final CModele modele;
    private final String[] directions = {"⬉","⬆","⬈","⬅","⬛","➡","⬋","⬇","⬊"};

    /**
     * Constructeur.
     */
    public VueCommandes(CModele modele) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        this.modele = modele;

        Border empty = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border blackLine = BorderFactory.createLineBorder(Color.black);
        CompoundBorder line = new CompoundBorder(empty, blackLine);

        Controleur ctrl = new Controleur(modele);

        drawMoveButtons(line);
        drawDryUpButtons(line);
        drawActionButtons(line);
        drawEndOfTurn();

    }

    void drawMoveButtons(CompoundBorder line){
        JPanel moveButtons = new JPanel(new GridLayout(3, 3, 10, 5));
        Border moveBorder = BorderFactory.createTitledBorder(line, "MOVE");
        for (String direc : directions){
            JButton move = new JButton(direc);
            moveButtons.add(move);
            Droite d = new Droite(modele,direc);
            move.addActionListener(d);
        }
        moveButtons.setBorder(moveBorder);
        this.add(moveButtons);
    }

    void drawDryUpButtons(CompoundBorder line){
        JPanel dryupButtons = new JPanel(new GridLayout(3, 3, 10, 5));
        Border moveBorder = BorderFactory.createTitledBorder(line, "DRY UP");
        for (String direc : directions){
            JButton dry = new JButton(direc);
            dryupButtons.add(dry);
            Assecher assecher = new Assecher(modele,direc);
            dry.addActionListener(assecher);
        }
        dryupButtons.setBorder(moveBorder);
        this.add(dryupButtons);
    }

    void drawActionButtons(CompoundBorder line){
        JPanel actionButtons = new JPanel(new GridLayout(3, 2, 10, 5));
        Border actionBorder = BorderFactory.createTitledBorder(line, "ACTIONS");
        JButton key = new JButton("prendre clé");
        actionButtons.add(key);
        SearchKey search = new SearchKey(modele);
        key.addActionListener(search);
        JButton give = new JButton("Donner clé");
        actionButtons.add(give);
        GiveKey giveKey = new GiveKey(modele);
        give.addActionListener(giveKey);
        JButton recup = new JButton("récuperer artéfact");
        actionButtons.add(recup);
        RecupA recupA = new RecupA(modele);
        recup.addActionListener(recupA);
        JButton helico = new JButton("prendre hélicoptère");
        actionButtons.add(helico);
        Evasion h = new Evasion(modele);
        helico.addActionListener(h);
        JButton objet = new JButton("objet");
        actionButtons.add(objet);
        Objet o = new Objet(modele);
        objet.addActionListener(o);
        JButton ultime = new JButton("ultimate");
        actionButtons.add(ultime);
        ActionSpeciale spe = new ActionSpeciale(modele);
        ultime.addActionListener(spe);
        actionButtons.setBorder(actionBorder);
        this.add(actionButtons);
    }
    void drawEndOfTurn(){
        JPanel endOfTurnButton = new JPanel();
        JButton end = new JButton("Fin de tour");
        endOfTurnButton.add(end);
        Controleur c = new Controleur(modele);
        end.addActionListener(c);
        this.add(endOfTurnButton);
    }

}


class Controleur implements ActionListener {

    CModele modele;

    public Controleur(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.avance();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class Droite implements ActionListener {

    CModele modele;
    private String direc;

    public Droite(CModele modele, String direc) {
        this.modele = modele;
        this.direc = direc;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.move(direc);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}


class RecupA implements ActionListener {
    CModele modele;

    public RecupA(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.recupererArtefact();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class Assecher implements ActionListener {

    CModele modele;
    private String dir;

    public Assecher(CModele modele, String dir) {
        this.modele = modele;
        this.dir = dir;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.assecher(dir);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class Evasion implements ActionListener {
    CModele modele;

    public Evasion(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.helico();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class ActionSpeciale implements ActionListener {
    CModele modele;

    public ActionSpeciale(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.actionSpeciale();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class SearchKey implements ActionListener {
    CModele modele;

    public SearchKey(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.searchKey();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

class GiveKey implements ActionListener {
    CModele modele;

    public GiveKey(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.giveKey();
    }
}

class Objet implements ActionListener {
    CModele modele;

    public Objet(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            modele.objet();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}