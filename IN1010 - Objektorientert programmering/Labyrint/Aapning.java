import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Aapning extends HvitRute {
    public Aapning(Labyrint lab, int x, int y) {
        super(lab, x, y);
    }

    public void gaa(ArrayList<Rute> vei) {
        // Lager en utvei-liste med hele veien fram til naa, pluss denne ruten.
        ArrayList<Rute> utvei = new ArrayList<Rute>();
        utvei.addAll(vei);
        utvei.add(this);

        // Legger til utveien i labyrintens utveier-liste.
        labyrint.leggTilUtvei(utvei);
    }

    // GUI:
    @Override
    public void initGUI() {
        super.initGUI(); // Samme GUI som HvitRute.
    }
}
