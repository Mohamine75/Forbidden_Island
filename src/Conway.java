import Enums.Artefact;
import Enums.Key;
import Enums.Level;
import Enums.Roles;

import java.lang.reflect.Array;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;


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
    public static final int HAUTEUR = 6, LARGEUR = 6;
    private Cellule heliport;
    private final Cellule[][] cellules;
    protected boolean win = false;
    protected boolean loose = false;
    protected HashMap<Integer, Player> joueurs = new HashMap<>();
    protected int tour = 0;

    public CModele() {
        creationJoueurs();
        cellules = new Cellule[LARGEUR + 2][HAUTEUR + 2];
        for (int i = 0; i < HAUTEUR+ 2; i++) {
            for (int j = 0; j < LARGEUR+ 2; j++) {
                cellules[i][j] = new Cellule(this, i, j);
            }
        }
        ArrayList<Cellule> temp = new ArrayList<>();
        Random r = new Random();
        int x = r.nextInt(cellules.length);
        int y =r.nextInt(cellules.length);
        this.heliport = cellules[x][y];
        heliport.heliport = true;
        temp.add(heliport);
        init(Artefact.EAU,temp);
        init(Artefact.EAU,temp);
        init(Artefact.AIR,temp);
        init(Artefact.AIR,temp);
        init(Artefact.FUEGO,temp);
        init(Artefact.FUEGO,temp);
        init(Artefact.TERRE,temp);
        init(Artefact.TERRE,temp);
    }

    private void init(Artefact a,ArrayList<Cellule> temp){
        Random r = new Random();
        int x = r.nextInt(LARGEUR);
       int  y =r.nextInt(HAUTEUR);
        while (temp.contains(cellules[x][y])){
            x = r.nextInt(LARGEUR);
            y =r.nextInt(HAUTEUR);
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
            System.out.println("Nom du joueur" + i);
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
        System.out.println("Choissiez le rôle des joueurs en tapant le numéro associé, " +
                "1 pour Pilote,2 pour Ingénieur,3 pour Explorateur,4 pour Navigateur" +
                "5 pour Plongeur et 6 pour Messager,deux joueurs ne pouvant avoir le " +
                "même role!");
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

    public void testLoose() {

        if (heliport.level == Level.submerge) {
            this.loose = true;
            System.out.println("L'héliport est noyé, vous êtes coincés sur l'île, partie perdu :(");
        }
        testDeaths();
    }

    private void testDeaths() {

        for (Player p : joueurs.values()) {
            if (cellules[p.posY][p.posY].level == Level.submerge && p.role!=Roles.PLONGEUR) {
                System.out.println("Un joueur s'est noyé, fin du jeu :(");
                System.out.println("Joueur : " +p.name);
                this.loose = true;
            }
        }
    }

    public void avance() {
        testLoose();
        if (joueurs.get(tour).action != 0 || loose) {
            System.out.println("Il vous reste encore " + joueurs.get(tour).action + " action à faire ");
            return;
        }
        joueurs.get(tour).addKeyHasard();
        Random random = new Random();
        ArrayList<Cellule> res = new ArrayList<>();
        while (res.size() < 3) {
            int x = random.nextInt(LARGEUR);
            int y = random.nextInt(LARGEUR);
            Cellule c = getCellule(x, y);
            if (!getCellule(x, y).getLevel().equals(Level.submerge) && countEtats()>=3) {
                getCellule(x, y).evolue();
                System.out.println(x + "," + y);
                res.add(c);
            }
            if(countEtats()<3){
                res.add(c);
            }
        }
        notifyObservers();
        tourParTour();
    }

    private int countEtats(){
       int  count = 0;
        for (int i = 0; i < HAUTEUR; i++) {
            for (int j = 0; j < LARGEUR; j++) {
                if(!cellules[i][j].level.equals(Level.submerge)){
                    count++;
                }
            }

        }
        return count;
    }
    public void tourParTour() {
        if (joueurs.get(tour).action == 0) {
            joueurs.get(tour).action = 3;
            tour++;
            if (tour == joueurs.size()) {
                tour = 0;
            }
        }
    }

    public void helico() {
        int arte = 0;
        testLoose();
        if (loose) {
            return;
        }
        for (Player p :
                joueurs.values()) {
            if (heliport.x == p.posX && heliport.y == p.posY) {
                    arte += p.artefacts.size();
                }else{
                System.out.println("Un joueur n'est pas sur l'héliport, veuillez vous regrouper");
                return;
                }
            }
        if (arte == 4){
            System.out.println("Bravo, partie gagnée");
            win = true;
            }
            System.out.println("Les conditions ne sont pas réunies.");

        }


    public void move(String direction) {
        testLoose();
        if (joueurs.get(tour).action == 0 || this.loose) {
            return;
        }
        Player p = joueurs.get(tour);
        int y = p.posY;
        int x = p.posX;
        switch (direction) {
            case "haut" -> y = p.posY + 1;
            case "bas" -> y = p.posY - 1;
            case "droite" -> x = p.posX + 1;
            case "gauche" -> x = p.posX - 1;
        }
        if ((x < 0 || x >= HAUTEUR) || (y >= HAUTEUR || y < 0)) {
            System.out.println("Out of Bounds");
            return;
        }
        if (getCellule(x, y).getLevel().equals(Level.submerge) && p.role!=Roles.PLONGEUR) {
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

    protected void assecher(String direction) {
        if (joueurs.get(tour).action == 0 || loose) {
            return;
        }

        Player p = joueurs.get(tour);
        int x = p.posX;
        int y = p.posY;

        switch (direction){
            case "haut" -> y = p.posY - 1;
            case "bas" -> y = p.posY + 1;
            case "droite" -> x = p.posX + 1;
            case "gauche" -> x = p.posX - 1;
        }
        if ((x < 0 || x >= HAUTEUR) || (y >= HAUTEUR || y < 0)) {
            System.out.println("La Case est en dehors de la grille");
            return;
        }
        if (cellules[x][y].level == Level.inonde) {
            cellules[x][y].level = Level.normal;
            p.action -= 1;
                }
        System.out.println("La case demandée est submergée ou à l'état normale...");
    }

    public void actionSpeciale(){
        if (joueurs.get(tour).action == 0 || loose) {
            return;
        }
        switch(joueurs.get(tour).role){
            case PILOTE ->
                pilote(joueurs.get(tour));
            case PLONGEUR ->
                    System.out.println("Vous pouvez traverser les cases submergées, cela coùte une action");
            case NAVIGATEUR -> navigateur();
        }
    }

    private void pilote(Player p){
        int x;
        int y;
        Scanner sc  = new Scanner(System.in);
        System.out.println("ACTION SPECIALE, veuillez entrez l'endroit où vous voulez aller, qui n'est bien sûr pas subermergé");
        System.out.println("x :");
        x= sc.nextInt();
        System.out.println("y :");
        y = sc.nextInt();
        while(( x < 0 || x>= LARGEUR)|| (y >=HAUTEUR || y < 0) || cellules[x][y].level == Level.submerge){
            System.out.println("Cellule submergée, impossible");
            System.out.println("x :");
            x= sc.nextInt();
            System.out.println("y :");
            y = sc.nextInt();
        }
        p.action-=1;
        p.posX = x;
        p.posY = y;
    }

    private void navigateur() {
        int num;
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez le numéro du joueur que vous voulez déplacer");
        num = sc.nextInt()-1;
        while (num < 0 || num > joueurs.size() || num ==tour) {
            System.out.println("Retapez le numéro svp :)");
            num = sc.nextInt()-1;
        }
        Player p = joueurs.get(num);
        int xy = 1000;
        System.out.println("Vous voulez le déplacer en x ou en y ?");
        switch (sc.next()) {
            case "x" -> {
                while (p.posX + xy >= LARGEUR || (p.posX - xy < 0) || (xy > 2) || (xy < -2)) {
                    System.out.println("Entrez le nombre à addition (peut être négatif)");
                    xy = sc.nextInt();
                }
                p.posX = p.posX + xy;
            }
            case "y" -> {
                while (p.posY + xy >= HAUTEUR || (p.posY - xy < 0) || (xy > 2) || (xy < -2)) {
                    System.out.println("Entrez le nombre à addition (peut être négatif)");
                    xy = sc.nextInt();
                }
                p.posY = p.posY - xy;
            }

        }
        joueurs.get(tour).action -= 1;
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

    public void recupererArtefact() {
        if (joueurs.get(tour).action == 0 || loose) {
            return;
        }
        Player p = joueurs.get(tour);

        if (null != getCellule(p.posX, p.posY).artefact) {
            if (verifArtefact(getCellule(p.posX, p.posY).artefact, p)) {
                p.addArtefact(getCellule(p.posX, p.posY).artefact);
                getCellule(p.posX, p.posY).artefact = null;
                System.out.println("Artefact  récupéré");
                p.action -= 1;

                return;
            }
        }
        System.out.println("aucun Artefact non récupéré ");
    }

    public void recupKey() {
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
        frame.pack();
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
            case normal -> g.setColor(Color.WHITE);
            case inonde -> g.setColor(Color.CYAN);
            case submerge -> g.setColor(Color.BLUE);

        }
        g.fillRect(x, y, TAILLE, TAILLE);
        int i = 0;
        for (Player p : modele.joueurs.values()) {
            if (c.getX() == p.posX && c.getY() == p.posY) {
                switch (i) {
                    case 0 -> g.setColor(Color.GREEN);
                    case 1 -> g.setColor(Color.magenta);
                    case 2 -> g.setColor(Color.red);
                    case 3 -> g.setColor(Color.black);
                }
                g.fillOval(x, y, TAILLE / 2, TAILLE / 2);

                //System.out.println(p.posX+","+p.posY);
            }
            i++;
        }
        if (!(c.artefact == null)) {
            switch(c.artefact){
                case EAU -> g.setColor(Color.GRAY);
                case FUEGO -> g.setColor(Color.red);
                case TERRE -> g.setColor(Color.BLACK);
                case AIR -> g.setColor(Color.pink);
            }
            //  g.drawOval(x, y, TAILLE*1, TAILLE*1);
            g.fillOval(x, y, TAILLE*5 / 2, TAILLE*5 / 2);
        }
        if (c.heliport) {
            g.setColor(Color.pink);
            g.fillOval(x, y, TAILLE / 2, TAILLE / 2);
        }
    }
}


/**
 * Une classe pour représenter la zone contenant le bouton.
 * <p>
 * Cette zone n'aura pas à être mise à jour et ne sera donc pas un observateur.
 * En revanche, comme la zone précédente, celle-ci est un panneau [JPanel].
 */
class VueCommandes extends JPanel {
    /**
     * Pour que le bouton puisse transmettre ses ordres, on garde une
     * référence au modèle.
     */
    private final CModele modele;

    /**
     * Constructeur.
     */
    public VueCommandes(CModele modele) {
        this.modele = modele;

        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        JButton boutonAvance = new JButton("Fin de tour");

        JPanel border = new JPanel(new GridLayout(1, 1, 10, 10));
        JPanel panel2 = new JPanel(new GridLayout(1, 1, 20, 20));
        JPanel panel3 = new JPanel(new GridLayout(2, 2, 20, 20));
        JPanel panel4 = new JPanel(new GridLayout(1, 3, 20, 20));

        /**
         * Le bouton, lorsqu'il est cliqué par l'utilisateur, produit un
         * événement, de classe [ActionEvent].
         *
         * On a ici une variante du schéma observateur/observé : un objet
         * implémentant une interface [ActionListener] va s'inscrire pour
         * "écouter" les événements produits par le bouton, et recevoir
         * automatiquements des notifications.
         * D'autres variantes d'auditeurs pour des événements particuliers :
         * [MouseListener], [KeyboardListener], [WindowListener].
         *
         * Cet observateur va enrichir notre schéma Modèle-Vue d'une couche
         * intermédiaire Contrôleur, dont l'objectif est de récupérer les
         * événements produits par la vue et de les traduire en instructions
         * pour le modèle.
         * Cette strate intermédiaire est potentiellement riche, et peut
         * notamment traduire les mêmes événements de différentes façons en
         * fonction d'un état de l'application.
         * Ici nous avons un seul bouton réalisant une seule action, notre
         * contrôleur sera donc particulièrement simple. Cela nécessite
         * néanmoins la création d'une classe dédiée.
         */
        Controleur ctrl = new Controleur(modele);
        /** Enregistrement du contrôleur comme auditeur du bouton. */
        boutonAvance.addActionListener(ctrl);



        JButton droite = new JButton("droite");
        panel.add(droite);
        Droite d = new Droite(modele);
        droite.addActionListener(d);

        GridBagLayout grid = new GridBagLayout() ;
        grid.addLayoutComponent("droite",droite);

        JButton gauche = new JButton("gauche");
        panel.add(gauche);
        Gauche g = new Gauche(modele);
        gauche.addActionListener(g);

        JButton haut = new JButton("bas");
        panel.add(haut);
        Haut h = new Haut(modele);
        haut.addActionListener(h);

        JButton bas = new JButton("haut");
        panel.add(bas);
        Bas b = new Bas(modele);
        bas.addActionListener(b);
        border.add(boutonAvance);



        this.add(panel);
        this.add(border);
        JButton recupA = new JButton("Recup Artefact");
        panel4.add(recupA);
        RecupA r = new RecupA(modele);
        recupA.addActionListener(r);
        String a[] ={"haut","bas","droite","gauche"};
        for (String s: a){
            JButton assecher = new JButton(s);
            panel3.add(assecher);
            Assecher assecher1 = new Assecher(modele,s);
            assecher.addActionListener(assecher1);
        }


        JButton evasion = new JButton("Prendre l'hélico");
        panel4.add(evasion);
        Evasion e  =  new Evasion(modele);
        evasion.addActionListener(e);

        JButton action = new JButton("Speciale");
        panel4.add(action);
        ActionSpeciale speciale = new ActionSpeciale(modele);
        action.addActionListener(speciale);
        this.add(panel4);

        JPanel joueurs =  new JPanel(new GridLayout(1, 4, 20, 20));
        for (Player p: modele.joueurs.values()){
            JLabel label = new JLabel(p.name + " : " +p.action + ".\n");
            joueurs.add(label);
        }
        //this.add(joueurs);
        this.add(panel3);
    }

}

class Controleur implements ActionListener {

    CModele modele;

    public Controleur(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.avance();
    }
}

class Droite implements ActionListener {

    CModele modele;

    public Droite(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.move("droite");
    }
}

class Bas implements ActionListener {

    CModele modele;

    public Bas(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.move("bas");
    }
}

class Gauche implements ActionListener {

    CModele modele;

    public Gauche(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.move("gauche");
    }
}

class Haut implements ActionListener {

    CModele modele;

    public Haut(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.move("haut");
    }
}

class RecupA implements ActionListener {
    CModele modele;

    public RecupA(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.recupererArtefact();
    }
}

class Assecher implements ActionListener {

    CModele modele;
    private String dir;
    public Assecher(CModele modele,String dir) {
        this.modele = modele;
        this.dir = dir;
    }

    public void actionPerformed(ActionEvent e) {
        modele.assecher(dir);
    }
}

class Evasion implements  ActionListener{
    CModele modele;

    public Evasion(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.helico();
    }
}
class ActionSpeciale implements  ActionListener{
    CModele modele;

    public ActionSpeciale(CModele modele) {
        this.modele = modele;
    }

    public void actionPerformed(ActionEvent e) {
        modele.actionSpeciale();
    }
}