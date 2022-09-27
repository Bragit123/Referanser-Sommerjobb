
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Labyrint {
    private int antKol; // Antall kolonner
    private int antRad; // Antall rader
    private Rute[][] ruter; // Rutenettet [kol][rad]
    private ArrayList<ArrayList<Rute>> utveier = new ArrayList<ArrayList<Rute>>();

    private int utveiIndeks = 0; // Indeks til hvilken utvei som vises i GUIet.
    private JLabel status = new JLabel("Klikk paa en rute for aa se utveier!"); // Plassert her for aa kunne naas i begge GUI-metodene

    public Labyrint(File fil) throws FileNotFoundException {
        Scanner leser = new Scanner(fil);
        String linje = leser.nextLine(); // Foerste linje forteller dimensjonene til labyrinten.
        String[] dim = linje.split(" ");
        antRad = Integer.parseInt(dim[0]);
        antKol = Integer.parseInt(dim[1]);

        ruter = new Rute[antKol][antRad]; // Setter rutenettet ut fra antall kolonner og rader.

        int rad = 0; // Den raden som er i fokus

        while (leser.hasNextLine()) {
            // Leser gjennom alle linjene i filen.

            linje = leser.nextLine(); // En linje tilsvarer en rad i labyrinten.
            for (int kol=0; kol < antKol; kol++) {
                // Gaar gjennom alle kolonnene i raden.

                char tegn = linje.charAt(kol);
                if (tegn == '.') {
                    // Ruten er hvit.
                    if (rad == 0 || rad == (antRad-1) || kol == 0 || kol == (antKol-1)) {
                        // En hvit rute er en aapning hvis den er i kanten av labyrinten
                        ruter[kol][rad] = new Aapning(this, kol, rad);
                    } else {
                        ruter[kol][rad] = new HvitRute(this, kol, rad);
                    }
                } else if (tegn == '#') {
                    // Ruten er svart.
                    ruter[kol][rad] = new SortRute(this, kol, rad);
                } else {
                    // Hvis vi kommer hit er den innleste filen ugyldig.
                    System.out.println("ERROR: Symboler i labyrinten maa vaere . eller #, ikke " + tegn);
                    System.exit(1);
                }
            }
            rad++;   // Skifter fokus til neste rad
        }

        // Definerer naboene til hver rute.
        Rute rute;
        Rute nabo;
        for (int k=0; k < antKol; k++) {
            for (int r=0; r < antRad; r++) {
                // Gaar gjennom hver rute (k=kolonne, r=rad)

                rute = ruter[k][r];
                // Nord
                if (r != 0) {
                    // Den oeverste linjen har ingen nord-nabo
                    nabo = ruter[k][r-1];
                    rute.settNord(nabo);
                }
                // Syd
                if (r != (antRad - 1)) {
                    // Den nederste linjen har ingen syd-nabo
                    nabo = ruter[k][r+1];
                    rute.settSyd(nabo);
                }
                // Vest
                if (k != 0) {
                    // Linjen helt til venstre har ingen vest-nabo
                    nabo = ruter[k-1][r];
                    rute.settVest(nabo);
                }
                // Oest
                if (k != (antKol-1)) {
                    // Linjen helt til hoeyre har ingen oest-nabo
                    nabo = ruter[k+1][r];
                    rute.settOest(nabo);
                }
            }
        }
    }

    public ArrayList<ArrayList<Rute>> finnUtveiFra(Rute start) {
        utveier = new ArrayList<ArrayList<Rute>>(); // Nullstiller utveier.
        start.finnUtvei();
        return utveier; // Utveiene blir lagret direkte fra Aapning til instansvariabelen utveier.
    }

    public void leggTilUtvei (ArrayList<Rute> utvei) {
        utveier.add(utvei);
    }

    public int[] hentDimensjoner() {
        // Returnerer dimensjonene til labyrinten (antKol og antRad).
        int[] dimensjoner = {antKol, antRad};
        return dimensjoner;
    }

    // GUI:
    public void initGUI() {
        // Oppretter GUI-vinduet
        JFrame vindu = new JFrame("Labyrint");
        vindu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // HovedPanel med kontroll paa underliggende panel
        JPanel hovedPanel = new JPanel();

        // LabyrintPanel som har kontroll paa selve labyrinten
        JPanel labyrintPanel = new JPanel();
        labyrintPanel.setLayout(new GridLayout(antRad, antKol));

        Rute rute;
        for (int rad=0; rad < antRad; rad++) {
            for (int kol=0; kol < antKol; kol++) {
                // Henter hver rute fra ruter-listen, initierer GUIet, og legger til i panelet.
                rute = ruter[kol][rad];
                rute.initGUI();
                rute.setOpaque(true); // Denne linjen er noedvendig for at bakgrunnsfarge skal synes.
                labyrintPanel.add(rute);
            }
        }

        // KontrollPanel som har kontroll paa knapper og tekst
        JPanel kontrollPanel = new JPanel();
        kontrollPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JButton avsluttKnapp = new JButton("Avslutt");
        class AvsluttKlikk implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Avslutter programmet.
                System.exit(0);
            }
        }
        avsluttKnapp.addActionListener(new AvsluttKlikk());

        JButton nesteKnapp = new JButton("Neste");
        class NesteKlikk implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Viser neste utvei i labyrinten.
                behandleNesteKlikk();
            }
        }
        nesteKnapp.addActionListener(new NesteKlikk());

        // Legger elementene til kontrollPanel.
        kontrollPanel.add(status);
        kontrollPanel.add(avsluttKnapp);
        kontrollPanel.add(nesteKnapp);

        // Setter sammen GUI-komponentene:
        hovedPanel.add(labyrintPanel);
        hovedPanel.add(kontrollPanel);

        vindu.add(hovedPanel);
        vindu.pack();
        vindu.setVisible(true);
    }

    public void visUtvei(int num) {
        if (utveier.size() <= 0) {
            // Dersom ruten som ble klikket paa ikke har noen loesning.
            status.setText("Ingen loesninger!");
            return;
        }

        // Oppdaterer status-teksten.
        status.setText("Viser loesning " + (num+1) + " av " + utveier.size());

        if (utveiIndeks >= utveier.size()) {
            // Dette skal ikke skje!
            System.out.println("utveiIndeks kan ikke vaere stoerre enn antallet utveier!");
            System.exit(1);
        }

        ArrayList<Rute> utvei = utveier.get(num); // Henter utveien

        boolean foerste = true;
        for (Rute rute : utvei) {
            if (foerste) {
                // Hvis dette er den foerste ruten skal den ha en moerkere groennfarge
                // for aa vise hvilken rute brukeren har klikket paa.
                rute.setBackground(new Color(0, 150, 0)); // Moerk groenn.
                foerste = false; // Soerger for at alle de neste rutene havner i else-blokken.
            } else {
                // Alle andre enn foerste rute skal vaere "vanlig" groenn.
                rute.setBackground(Color.GREEN);
            }
            rute.setOpaque(true); // Noedvendig for at bakgrunnsfarge skal funke.
        }
    }

    public void settNesteUtveiIndeks() {
        // Setter neste utveiIndeks til en mer enn den allerede er, elle ned til 0 hvis den er paa max.
        if (utveiIndeks < utveier.size()-1) {
            utveiIndeks++;
        } else {
            utveiIndeks = 0;
        }
    }

    public void behandleNesteKlikk() {
        nullstill(); // Nullstiller fargen til alle rutene.
        settNesteUtveiIndeks(); // Oeker utveiIndeks med en.
        visUtvei(utveiIndeks); // Viser neste utvei.
    }

    public void nullstill() {
        for (Rute[] ruter_ : ruter) {
            for (Rute rute : ruter_) {
                rute.initGUI(); // Nullstiller fargen til alle rutene
                rute.setOpaque(true); // Noedvendig for aa faa bakgrunnsfarge
            }
        }
    }
}
