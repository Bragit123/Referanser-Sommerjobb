import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class SortRute extends Rute {
    public SortRute(Labyrint lab, int x, int y) {
        super(lab, x, y);
    }

    public char tilTegn() {
        return '#';
    }

    public void gaa(ArrayList<Rute> vei) {
        return;
    }

    // GUI:
    @Override
    public void initGUI() {
        super.initGUI();
        setBackground(Color.DARK_GRAY); // Sorte ruter blir farget moerk graa.
    }
}
