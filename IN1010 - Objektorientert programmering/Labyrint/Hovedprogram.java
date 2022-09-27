import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;

class Hovedprogram {
    public static void main (String[] args) {
        try {
            File fil = velgFil(); // Henter fil med JFileChooser.
            Labyrint labyrint = new Labyrint(fil); // Oppretter labyrint.
            labyrint.initGUI(); // Initierer GUI.
        }
        catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static File velgFil() throws FileNotFoundException {
        // Metode for aa velge fil med JFileChooser.
        File fil = null;

        JFileChooser velger = new JFileChooser();
        int resultat = velger.showOpenDialog(null);
        if (resultat == velger.APPROVE_OPTION) {
            fil = velger.getSelectedFile();
        } else {
            System.exit(0);
        }

        return fil;
    }
}
