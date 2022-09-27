import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class HvitRute extends Rute {
    public HvitRute(Labyrint lab, int x, int y) {
        super(lab, x, y);
    }

    public char tilTegn() {
        return '.';
    }

    public void gaa(ArrayList<Rute> vei) {
        Rute pRute; // Forrige Rute (p = previous)

        if (vei.size() > 0){
            // Finner forrige rute, hvis ikke dette er startruten
            pRute = vei.get(vei.size() - 1);
        } else {
            pRute = null;
        }

        // Lager en nyVei-liste, og legger til den forrige vei-listen pluss denne ruten.
        ArrayList<Rute> nyVei = new ArrayList<Rute>();
        nyVei.addAll(vei);
        nyVei.add(this);

        // Gaar videre til nabo:
        for (Rute nabo : naboer) {
            if (nabo != null) {
                // Gaar bare videre til nabo hvis nabo eksisterer.
                if (nabo.equals(pRute) == false) {
                    // Sjekker at nabo ikke er den forrige ruten.
                    nabo.gaa(nyVei);
                }
            }
        }
    }

    // GUI:
    @Override
    public void initGUI() {
        super.initGUI();
        setBackground(Color.WHITE); // Farger ruten hvit.

        // Eventbehandling
        class RuteKlikk implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                behandleRuteKlikk();
            }
        }
        addActionListener(new RuteKlikk());
    }

    public void behandleRuteKlikk() {
        labyrint.nullstill(); // Fjerner farget utvei fra GUI foer ny utvei genereres.
        labyrint.finnUtveiFra(this); // Finner utvei fra denne ruten.
        labyrint.visUtvei(0); // Viser utveien i GUIet.
    }
}
