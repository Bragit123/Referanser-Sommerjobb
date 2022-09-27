
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

abstract class Rute extends JButton {
    protected int kol;  // Kolonne
    protected int rad;  // Rad
    protected Labyrint labyrint;

    // Array med naboene til ruten, i rekkefoelgen nord, syd, vest, oest
    protected Rute[] naboer = new Rute[4];

    public Rute(Labyrint lab, int x, int y) {
        labyrint = lab;
        kol = x;
        rad = y;
    }

    public void settNord (Rute rute) {
        naboer[0] = rute;
    }
    public void settSyd (Rute rute) {
        naboer[1] = rute;
    }
    public void settVest (Rute rute) {
        naboer[2] = rute;
    }
    public void settOest (Rute rute) {
        naboer[3] = rute;
    }

    public void finnUtvei() {
        ArrayList<Rute> vei = new ArrayList<Rute>(); // Holder orden paa veien
        gaa(vei);
    }

    public abstract void gaa(ArrayList<Rute> vei);

    public abstract char tilTegn();

    public String toString() {
        return "(" + kol + ", " + rad + ")";
    }

    // GUI:
    public void initGUI() {
        // Layout
        int[] dim = labyrint.hentDimensjoner();
        int antKol = dim[0];
        int antRad = dim[1];

        // Justerer stoerrelsen paa rutene ut fra stoerrelsen paa brettet, slik
        // at alle rutene synes i vinduet.
        if (antKol > 50 || antRad > 50) {
            setPreferredSize(new Dimension(10, 10));
        } else if (antKol > 30 || antRad > 30) {
            setPreferredSize(new Dimension(20, 20));
        } else if (antKol > 20 || antRad > 20) {
            setPreferredSize(new Dimension(30, 30));
        } else if (antKol > 10 || antRad > 10) {
            setPreferredSize(new Dimension(50, 50));
        } else {
            setPreferredSize(new Dimension(80, 80));
        }

        // Setter ramme rundt rutene.
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
}
