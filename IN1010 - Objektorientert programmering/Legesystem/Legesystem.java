import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;   // For aa kunne bruke Math.round();

class Legesystem {
    private static Lenkeliste<Lenkeliste> lesFil(String filnavn) {
        // Metode som leser en fil med informasjon om pasienter, legemidler, leger
        // og resepter, og returnerer disse som en nested lenkeliste. Altsaa en liste
        // hvor element 0 er en liste med pasienter, element 1 er en liste med legemidler,
        // element 2 er en liste med leger, og element 3 er en liste med resepter.

        // Deklarerer lenkelistene for pasienter, legemidler, leger og resepter:
        Lenkeliste<Pasient> pasienter = new Lenkeliste<Pasient>();
        Lenkeliste<Legemiddel> legemidler = new Lenkeliste<Legemiddel>();
        Lenkeliste<Lege> leger = new Lenkeliste<Lege>();
        Lenkeliste<Resept> resepter = new Lenkeliste<Resept>();

        // Proever aa lage en Scanner for filen, dersom den eksisterer.
        Scanner fil = null;
        try {
            fil = new Scanner(new File(filnavn));
        } catch (Exception e) {
            System.out.println("Fant ikke filen ved navn " + filnavn);
            System.exit(1);
        }

        fil.nextLine(); // Hopper over foerste linje (# Pasienter ...)

        // De neste linjene legger til pasienter,fram til vi moeter paa en linje med "#".
        while (fil.hasNextLine()) {
            String linje = fil.nextLine();
            if (linje.contains("#")) {
                break;  // Bryter ut av while-loekken, og gaar videre til aa legge til legemidler.
            }

            // Deler opp linjen
            String[] ord = linje.split(",");
            if (ord.length != 2) {
                // Ignorerer linjen hvis antall ord ikke er 2
                continue;
            }
            // Lager ny pasient med informasjonen fra linjen og legger til i pasienter.
            String navn = ord[0];
            String fnr = ord[1];
            Pasient pasient = new Pasient(navn, fnr);
            pasienter.leggTil(pasient);
        }

        // De neste linjene legger til legemidler,fram til vi moeter paa en linje med "#".
        while (fil.hasNextLine()) {
            String linje = fil.nextLine();
            if (linje.contains("#")) {
                break;  // Bryter ut av while-loekken, og gaar videre til aa legge til leger.
            }

            // Deler opp linjen
            String[] ord = linje.split(",");
            if (!(ord.length == 4 || ord.length == 5)) {
                // Ignorerer linjen hvis antallet ord ikke er 4 eller 5.
                continue;
            }
            String navn = ord[0];
            String type = ord[1];
            int pris;
            try {
                pris = Integer.parseInt(ord[2]);
            } catch (Exception e0) {
                try {
                    pris = (int) Math.round(Double.parseDouble(ord[2]));
                } catch (Exception e1) {
                    // Ignorer linjen pga feil
                    continue;
                }
            }
            double virkestoff = Double.parseDouble(ord[3]);
            boolean harStyrke = ord.length == 5;    // Hvis linjen har 5 ord inkluderer dette styrke

            if (!type.equals("vanlig") && !harStyrke) {
                // Ignorerer hvis legemiddelet ikke er vanlig, men heller ikke har oppgitt styrke.
                continue;
            }
            int styrke = 0;
            if (harStyrke) {
                // Legger til styrke dersom dette er oppgitt.
                try {
                    styrke = Integer.parseInt(ord[4]);
                } catch (Exception e0) {
                    try {
                        styrke = (int) Math.round(Double.parseDouble(ord[4]));
                    } catch (Exception e1) {
                        // Ignorer linjen pga feil
                        continue;
                    }
                }
            }

            // Legger til legemiddelet ut fra hvilken type det er:
            Legemiddel legemiddel = null;

            if (type.equals("vanlig")) {
                // Legger til vanlig legemiddel
                legemiddel = new Vanlig(navn, pris, virkestoff);
            } else if (type.equals("vanedannende")) {
                // Legger til vanedannende legemiddel
                legemiddel = new Vanedannende(navn, pris, virkestoff, styrke);
            } else if (type.equals("narkotisk")) {
                // Legger til narkotisk legemiddel
                legemiddel = new Narkotisk(navn, pris, virkestoff, styrke);
            } else {
                // Hvis typen verken er vanlig, vanedannende eller narkotisk ignorerer vi linjen
                continue;
            }
            legemidler.leggTil(legemiddel);
        }

        // De neste linjene legger til leger,fram til vi moeter paa en linje med "#".
        while (fil.hasNextLine()) {
            String linje = fil.nextLine();
            if (linje.contains("#")) {
                break;  // Bryter ut av while-loekken, og gaar videre til aa legge til resepter.
            }

            // Deler opp linjen
            String[] ord = linje.split(",");
            if (ord.length != 2) {
                // Ignorerer linjen hvis antallet ord ikke er 2
                continue;
            }
            String navn = ord[0];
            String kontrollId = ord[1];

            Lege lege = null;
            if (kontrollId.equals("0")) {
                // Hvis kontrollId er 0 legger vi til en vanlig lege
                lege = new Lege(navn);
            } else {
                // Hvis kontrollId ikke er 0 legger vi til en spesialist
                lege = new Spesialist(navn, kontrollId);
            }
            leger.leggTil(lege);
        }

        // De neste linjene legger til resepter, fram til vi har gaat gjennom hele filen.
        while (fil.hasNextLine()) {
            String linje = fil.nextLine();

            // Deler opp linjen
            String[] ord = linje.split(",");
            if (!(ord.length == 4 || ord.length == 5)) {
                // Ignorerer hvis antall ord ikke er 4 eller 5
                continue;
            }
            int legemiddelId = Integer.parseInt(ord[0]);
            String legeNavn = ord[1];
            int pasientId = Integer.parseInt(ord[2]);
            String type = ord[3];
            boolean harReit = ord.length == 5;

            if (!type.equals("p") && !harReit) {
                // Ignorerer hvis type ikke er "p" og reit ikke er oppgitt
                continue;
            }

            int reit = 0;
            if (harReit) {
                reit = Integer.parseInt(ord[4]);
            }

            int legemiddelIndeks = -1;
            int legeIndeks = -1;
            int pasientIndeks = -1;
            // Finner legemiddel i legemidler med id legemiddelId
            for (int i = 0; i < legemidler.stoerrelse(); i++) {
                if (legemidler.hent(i).hentId() == legemiddelId) {
                    legemiddelIndeks = i;
                    break;
                }
            }
            // Finner lege i leger med navn legeNavn
            for (int i = 0; i < leger.stoerrelse(); i++) {
                if (leger.hent(i).hentNavn().equals(legeNavn)) {
                    legeIndeks = i;
                    break;
                }
            }
            // Finner pasient i pasienter med id pasientId
            for (int i = 0; i < pasienter.stoerrelse(); i++) {
                if (pasienter.hent(i).hentId() == pasientId) {
                    pasientIndeks = i;
                    break;
                }
            }
            if (legemiddelIndeks == -1 || legeIndeks == -1 || pasientIndeks == -1) {
                // Ignoerer linjen siden legemiddelet, legen eller pasienten som er oppgitt
                // ikke eksisterer i systemet.
                continue;
            }
            Resept resept;

            try {
                if (type.equals("hvit")) {
                    resept = leger.hent(legeIndeks).skrivHvitResept(legemidler.hent(legemiddelIndeks), pasienter.hent(pasientIndeks), reit);
                } else if (type.equals("blaa")) {
                    resept = leger.hent(legeIndeks).skrivBlaaResept(legemidler.hent(legemiddelIndeks), pasienter.hent(pasientIndeks), reit);
                } else if (type.equals("militaer")) {
                    resept = leger.hent(legeIndeks).skrivMilitaerResept(legemidler.hent(legemiddelIndeks), pasienter.hent(pasientIndeks), reit);
                } else if (type.equals("p")) {
                    resept = leger.hent(legeIndeks).skrivPResept(legemidler.hent(legemiddelIndeks), pasienter.hent(pasientIndeks));
                } else {
                    // Ignorerer linjen hvis type ikke er verken hvit, blaa, militaer eller p
                    continue;
                }
            } catch (UlovligUtskrift u) {
                // Ignorerer linjen siden legen ikke kan skrive ut legemiddelet
                continue;
            }
            resepter.leggTil(resept);
        }
        fil.close();    // Lukker scanneren


        // Oppretter den nestede lenkelisten som skal returneres
        Lenkeliste<Lenkeliste> data = new Lenkeliste<Lenkeliste>();
        data.leggTil(pasienter); data.leggTil(legemidler);
        data.leggTil(leger); data.leggTil(resepter);
        return data;
    }

    // Siden jeg ikke har presisert innholdet i hver Lenkeliste i Lenkelisten info
    // maa jeg inkludere en @SuppressWarnings("unchecked")
    @SuppressWarnings("unchecked")
    public static void main (String[] args) throws UlovligUtskrift {
        Lenkeliste<Lenkeliste> info;
        info = lesFil("tekstfil.txt");
        Lenkeliste<Pasient> pasienter = info.hent(0);
        Lenkeliste<Legemiddel> legemidler = info.hent(1);
        Lenkeliste<Lege> leger = info.hent(2);
        Lenkeliste<Resept> resepter = info.hent(3);

        // Hovedsiden med alternativer til hva brukeren kan gjoere:
        Scanner input = new Scanner(System.in);
        boolean hasQuit = false;
        while (!hasQuit) {
            // Skriver beskjeden som blir gitt i terminalen:
            System.out.println("Hva vil du gjoere?");
            System.out.println("0: Se oversikt over pasienter, leger, legemidler og resepter");
            System.out.println("1: Legge til elementer i systemet");
            System.out.println("2: Bruke resept");
            System.out.println("3: Vis statistikk");
            System.out.println("q: Avslutte programmet");
            System.out.print("> ");

            // Henter input fra brukeren, og lagrer i variabelen cmd
            String cmd = input.nextLine();
            System.out.println();

            // Bestemmer hva som skjer videre ut fra inputen til brukeren

            // Skriver ut oversikt over pasienter, legemidler, leger og resepter
            if (cmd.equals("0")) {
                // Skriver ut oversikt over pasienter:
                System.out.println("# Pasienter (ID,navn,fnr):");
                for (Pasient pasient : pasienter) {
                    int id = pasient.hentId();
                    String navn = pasient.hentNavn();
                    String fnr = pasient.hentFnr();

                    System.out.println(id + "," + navn + "," + fnr);
                }
                System.out.println("---");
                // Skriver ut oversikt over legemidler:
                System.out.println("# Legemidler (ID,navn,type,pris,virkestoff,[styrke]):");
                for (Legemiddel legemiddel : legemidler) {
                    int id = legemiddel.hentId();
                    String navn = legemiddel.hentNavn();
                    int pris = legemiddel.hentPris();
                    double virkestoff = legemiddel.hentVirkestoff();
                    if (legemiddel instanceof Vanlig) {
                        System.out.println(id + "," + navn + ",vanlig," + pris + "," + virkestoff);
                    } else {
                        int styrke;
                        if (legemiddel instanceof Narkotisk) {
                            Narkotisk narkotisk = (Narkotisk) legemiddel;
                            styrke = narkotisk.hentNarkotiskStyrke();
                            System.out.println(id + "," + navn + ",narkotisk," + pris + "," + virkestoff + "," + styrke);
                        } else if (legemiddel instanceof Vanedannende) {
                            Vanedannende vanedannende = (Vanedannende) legemiddel;
                            styrke = vanedannende.hentVanedannendeStyrke();
                            System.out.println(id + "," + navn + ",vanedannende," + pris + "," + virkestoff + "," + styrke);
                        }
                    }
                }
                System.out.println("---");
                // Skriver ut oversikt over leger:
                System.out.println("# Leger (navn,[kontrollID]):");
                SortertLenkeliste<Lege> sorterteLeger = new SortertLenkeliste<Lege>();
                for (Lege lege : leger) {
                    // Gaar gjennom alle legene i leger, og legger dem til i
                    // den sorterte listen sorterteLeger.
                    sorterteLeger.leggTil(lege);
                }
                for (Lege lege : sorterteLeger) {
                    // Naar vi naa gaar gjennom legene i sorterteLeger kommer de alfabetisk.
                    String navn = lege.hentNavn();
                    if (lege instanceof Spesialist) {
                        Spesialist spesialist = (Spesialist) lege;
                        String kontrollId = spesialist.hentKontrollId();
                        System.out.println(navn + "," + kontrollId);
                    } else {
                        System.out.println(navn);
                    }
                }
                System.out.println("---");
                // Skriver ut oversikt over resepter:
                System.out.println("# Resepter (id,type,legemiddelID,lege,pasientID,reit):");
                for (Resept resept : resepter) {
                    int id = resept.hentId();
                    int legemiddelId = resept.hentLegemiddel().hentId();
                    String lege = resept.hentLege().hentNavn();
                    int pasientId = resept.hentPasient().hentId();
                    int reit = resept.hentReit();

                    if (resept instanceof MilitaerResept) {
                        System.out.println(id + ",militaer," + legemiddelId + "," + lege + "," + pasientId + "," + reit);
                    } else if (resept instanceof PResept) {
                        System.out.println(id + ",p," + legemiddelId + "," + lege + "," + pasientId + "," + reit);
                    } else if (resept instanceof Hvit) {
                        System.out.println(id + ",hvit," + legemiddelId + "," + lege + "," + pasientId + "," + reit);
                    } else if (resept instanceof Blaa) {
                        System.out.println(id + ",blaa," + legemiddelId + "," + lege + "," + pasientId + "," + reit);
                    }
                }
                System.out.println();
            }

            // Legge til element i systemet:
            else if (cmd.equals("1")) {
                System.out.println("Hva oensker du aa legge til i systemet?");
                System.out.println("0: Pasient");
                System.out.println("1: Legemiddel");
                System.out.println("2: Lege");
                System.out.println("3: Resept");
                System.out.print("> ");

                cmd = input.nextLine();
                System.out.println();
                System.out.println("Vennligst oppgi informasjon i foelgende format:");

                // Legger til pasient i systemet
                if (cmd.equals("0")) {

                    System.out.println("navn,fnr"); // Format for pasient
                    System.out.print("> ");
                    cmd = input.nextLine();
                    String[] pasientInfo = cmd.split(",");
                    if (pasientInfo.length != 2) {
                        // Sjekker at bruker gir riktig mengde informasjon (2)
                        System.out.println("Ugyldig input! Husk formatet (navn,fnr)");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    String navn = pasientInfo[0];
                    String fnr = pasientInfo[1];
                    Pasient nyPasient = new Pasient(navn, fnr);
                    pasienter.leggTil(nyPasient);
                    System.out.println("Pasient ble lagt til i systemet!");
                    System.out.println();
                }

                // Legger til legemiddel i systemet
                else if (cmd.equals("1")) {

                    System.out.println("navn,type,pris,virkestoff,[styrke]"); // Format for legemiddel
                    System.out.println("(Mulige typer: vanlig, vanedannende, narkotisk)");
                    System.out.print("> ");
                    cmd = input.nextLine();
                    String[] legemiddelInfo = cmd.split(",");
                    if (!(legemiddelInfo.length == 4 || legemiddelInfo.length == 5)) {
                        // Sjekker at bruker gir riktig mengde informasjon (4 eller 5)
                        System.out.println("Ugyldig input! Husk formatet (navn,type,pris,virkestoff,[styrke])");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    boolean harStyrke = legemiddelInfo.length == 5;
                    String navn = legemiddelInfo[0];
                    String type = legemiddelInfo[1];
                    int pris = 0;
                    double virkestoff = 0;
                    try {
                        pris = Integer.parseInt(legemiddelInfo[2]);
                    } catch (Exception e) {
                        System.out.println("Pris maa vaere et heltall!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    try {
                        virkestoff = Double.parseDouble(legemiddelInfo[3]);
                    } catch (Exception e) {
                        System.out.println("Virkestoff maa vaere et tall!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    if (!type.equals("vanlig") && !harStyrke) {
                        // Sjekker at styrke er oppgitt dersom type != vanlig
                        System.out.println("Maa oppgi styrke hvis type != vanlig!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    int styrke = 0;
                    if (harStyrke) {
                        try {
                            styrke = Integer.parseInt(legemiddelInfo[4]);
                        } catch (Exception e) {
                            System.out.println("Styrke maa vaere et heltall!");
                            continue; // Hopper tilbake til hovedmenyen
                        }
                    }
                    Legemiddel legemiddel = null;
                    if (type.equals("vanlig")) {
                        Vanlig vanlig = new Vanlig(navn, pris, virkestoff);
                        legemiddel = vanlig;
                    } else if (type.equals("vanedannende")) {
                        Vanedannende vanedannende = new Vanedannende(navn, pris, virkestoff, styrke);
                        legemiddel = vanedannende;
                    } else if (type.equals("narkotisk")) {
                        Narkotisk narkotisk = new Narkotisk(navn, pris, virkestoff, styrke);
                        legemiddel = narkotisk;
                    } else {
                        System.out.println("type maa vaere vanlig, vanedannende eller narkotisk!");
                        continue;
                    }
                    legemidler.leggTil(legemiddel);
                    System.out.println("Legemiddel ble lagt til i systemet!");
                    System.out.println();
                }

                // Legger til lege i systemet
                else if (cmd.equals("2")) {
                    System.out.println("navn,[kontrollID]");
                    System.out.println("(Ha kun med kontrollID dersom legen er en spesialist!)");
                    System.out.print("> ");
                    cmd = input.nextLine();
                    String[] legeInfo = cmd.split(",");
                    if (!(legeInfo.length == 1 || legeInfo.length == 2)) {
                        // Sjekker at brukeren har gitt riktig mengde info (1 eller 2)
                        System.out.println("Ugyldig input! Husk formatet (navn,[kontrollID])");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    String navn = legeInfo[0];
                    String kontrollId;
                    Lege nyLege = null;
                    if (legeInfo.length == 1) {
                        nyLege = new Lege(navn);
                    } else {
                        kontrollId = legeInfo[1];
                        nyLege = new Spesialist(navn, kontrollId);
                    }
                    leger.leggTil(nyLege);
                    System.out.println("Lege ble lagt til i systemet!");
                    System.out.println();
                }

                // Legger til resept i systemet
                else if (cmd.equals("3")) {
                    System.out.println("legemiddelID,legeNavn,pasientID,type,[reit]");
                    System.out.println("(Mulige typer: hvit, blaa, militaer, p)");
                    System.out.print("> ");
                    cmd = input.nextLine();
                    String[] reseptInfo = cmd.split(",");
                    if (!(reseptInfo.length == 4 || reseptInfo.length == 5)) {
                        // Sjekker at brukeren har gitt riktig mengde info (4 eller 5)
                        System.out.println("Ugyldig input! Husk formatet (legemiddelID,legeNavn,pasientID,type,[reit])");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    boolean harReit = reseptInfo.length == 5;
                    int legemiddelId = 0;
                    try {
                        legemiddelId = Integer.parseInt(reseptInfo[0]);
                    } catch (Exception e) {
                        System.out.println("legemiddelID maa vaere et heltall!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    String legeNavn = reseptInfo[1];
                    int pasientId = 0;
                    try {
                        pasientId = Integer.parseInt(reseptInfo[2]);
                    } catch (Exception e) {
                        System.out.println("pasientID maa vaere et heltall!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    String type = reseptInfo[3];
                    int reit = 0;
                    if (harReit) {
                        try {
                            reit = Integer.parseInt(reseptInfo[4]);
                        } catch (Exception e) {
                            System.out.println("reit maa vaere et heltall!");
                            continue; // Hopper tilbake til hovedmenyen
                        }
                    }
                    if (!type.equals("p") && !harReit) {
                        System.out.println("Maa inkludere reit naar type != p");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    // Finne legemiddel, lege og pasient fra id/navn
                    Legemiddel legemiddelFraId = null; boolean fantLegemiddel = false;
                    Lege legeFraNavn = null; boolean fantLege = false;
                    Pasient pasientFraId = null; boolean fantPasient = false;
                    for (Legemiddel legemiddel : legemidler) {
                        if (legemiddel.hentId() == legemiddelId) {
                            legemiddelFraId = legemiddel;
                            fantLegemiddel = true;
                        }
                    }
                    for (Lege lege : leger) {
                        if (lege.hentNavn().equals(legeNavn)) {
                            legeFraNavn = lege;
                            fantLege = true;
                        }
                    }
                    for (Pasient pasient : pasienter) {
                        if (pasient.hentId() == pasientId) {
                            pasientFraId = pasient;
                            fantPasient = true;
                        }
                    }
                    // Sjekker at oppgitt legemiddel, lege og pasient eksisterer
                    if (!fantLegemiddel) {
                        System.out.println("Fant ikke oppgitt legemiddel!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    if (!fantLege) {
                        System.out.println("Fant ikke oppgitt lege!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    if (!fantPasient) {
                        System.out.println("Fant ikke oppgitt pasient!");
                        continue; // Hopper tilbake til hovedmenyen
                    }
                    // Legger til resept
                    Resept resept = null;
                    if (type.equals("hvit")) {
                        Hvit hvit = legeFraNavn.skrivHvitResept(legemiddelFraId, pasientFraId, reit);
                        resept = hvit;
                    } else if (type.equals("blaa")) {
                        Blaa blaa = legeFraNavn.skrivBlaaResept(legemiddelFraId, pasientFraId, reit);
                        resept = blaa;
                    } else if (type.equals("militaer")) {
                        MilitaerResept militaer = legeFraNavn.skrivMilitaerResept(legemiddelFraId, pasientFraId, reit);
                        resept = militaer;
                    } else if (type.equals("p")) {
                        PResept pResept = legeFraNavn.skrivPResept(legemiddelFraId, pasientFraId);
                        resept = pResept;
                    }
                    resepter.leggTil(resept);
                    System.out.println("Resept ble lagt til i systemet!");
                    System.out.println();
                }

                else {
                    System.out.println("Ugyldig kommando!");
                    continue;
                }
            }

            // Bruke resept
            else if (cmd.equals("2")) {
                System.out.println("Hvilken pasient vil du se resepter for?");
                int cmdNr = 0;
                for (Pasient pasient : pasienter) {
                    System.out.println(cmdNr++ + ": " + pasient.hentNavn() + " (fnr " + pasient.hentFnr() + ")");
                }
                System.out.print("> ");
                int cmdTall;
                try {
                    cmdTall = input.nextInt();
                } catch (Exception e) {
                    System.out.println("Input maa vaere et heltall!");
                    continue; // Hopper tilbake til hovedmenyen
                }
                System.out.println();

                // Paa grunn av maaten vi har brukt cmdNr skal denne vaere en verdi hoeyere
                // enn det hoeyeste tallet brukeren har lov aa oppgi
                if (cmdTall < 0 || cmdTall >= cmdNr) {
                    System.out.println("Input maa vaere et tall mellom 0 og " + (cmdNr - 1));
                    continue; // Hopper tilbake til hovedmenyen
                }
                Pasient pasient = pasienter.hent(cmdTall); // Tallet oppgitt i terminalen er den samme som posisjonen i pasienter-listen.
                Stabel<Resept> pasientResepter = pasient.hentResepter();
                System.out.println("Valgt pasient: " + pasient.hentNavn() + " (fnr " + pasient.hentFnr() + ")");
                System.out.println("Hvilken resept vil du bruke?");
                cmdNr = 0;
                for (Resept resept : pasientResepter) {
                    System.out.println(cmdNr++ + ": " + resept.hentLegemiddel().hentNavn() + " (" + resept.hentReit() + " reit)");
                }
                System.out.print("> ");
                try {
                    cmdTall = input.nextInt();
                } catch (Exception e) {
                    System.out.println("Input maa vaere et heltall!");
                }
                if (cmdTall < 0 || cmdTall >= cmdNr) {
                    System.out.println("Input maa vaere et tall mellom 0 og " + (cmdNr - 1));
                    continue; // Hopper tilbake til hovedmenyen
                }
                Resept bruktResept = pasientResepter.hent(cmdTall);
                boolean kanBrukes = bruktResept.bruk();
                System.out.println();
                if (kanBrukes == false) {
                    System.out.println("Kunne ikke bruke resept paa " + bruktResept.hentLegemiddel().hentNavn() + " (ingen gjenvaerende reit).");
                    System.out.println();
                    continue; // Hopper tilbake til hovedmenyen
                }
                System.out.println("Brukte resept paa " + bruktResept.hentLegemiddel().hentNavn() + ". Antall gjenvaerende reit: " + bruktResept.hentReit());
                System.out.println();
            }

            // Vise statistikk
            else if (cmd.equals("3")) {
                System.out.println("Hvilken statistikk oensker du aa se?");
                System.out.println("0: Antall utskrevne resepter paa vanedannende legemidler");
                System.out.println("1: Antall utskrevne resepter paa narkotiske legemidler");
                System.out.println("2: Sjekk mulig misbruk av narkotika");
                System.out.print("> ");
                cmd = input.nextLine();
                System.out.println();

                // Viser antall vanedannende
                if (cmd.equals("0")) {
                    int total = 0;
                    for (Resept resept : resepter) {
                        Legemiddel reseptLegemiddel = resept.hentLegemiddel();
                        if(reseptLegemiddel instanceof Vanedannende) {
                            total++;
                        }
                    }
                    System.out.println("Antall utskrevne resepter paa vanedannende legemidler: " + total);
                    System.out.println();
                }

                // Viser antall narkotiske
                else if (cmd.equals("1")) {
                    int total = 0;
                    for (Resept resept : resepter) {
                        Legemiddel reseptLegemiddel = resept.hentLegemiddel();
                        if (reseptLegemiddel instanceof Narkotisk) {
                            total++;
                        }
                    }
                    System.out.println("Antall utskrevne resepter paa narkotiske legemidler: " + total);
                    System.out.println();
                }

                // Sjekker misbruk
                else if (cmd.equals("2")) {
                    // Gaar foerst gjennom leger
                    System.out.println("# Leger som har skrevet ut narkotiske legemidler:");
                    for (Lege lege : leger) {
                        int antallNarkotiske = 0;
                        Lenkeliste<Resept> legeResepter = lege.hentResepter();
                        for (Resept resept : legeResepter) {
                            if (resept.hentLegemiddel() instanceof Narkotisk) {
                                antallNarkotiske++;
                            }
                        }
                        if (antallNarkotiske > 0) {
                            // Skriver kun ut info hvis legen faktisk har skrevet ut noen narkotiske legemidler
                            System.out.println(lege.hentNavn() + " har skrevet ut " + antallNarkotiske + " resepter paa narkotiske legemidler.");
                        }
                    }
                    System.out.println("---");
                    // Gaar dermed gjennom pasienter
                    System.out.println("# Pasienter som har resept paa narkotiske legemidler:");
                    for (Pasient pasient : pasienter) {
                        int antallNarkotiske = 0;
                        Stabel<Resept> pasientResepter = pasient.hentResepter();
                        for (Resept resept : pasientResepter) {
                            if (resept.hentLegemiddel() instanceof Narkotisk) {
                                antallNarkotiske++;
                            }
                        }
                        if (antallNarkotiske > 0) {
                            // Skriver kun ut info hvis pasienten faktisk har faatt utskrevet narkotiske legemidler
                            System.out.println(pasient.hentNavn() + " har " + antallNarkotiske + " resepter paa narkotiske legemidler.");
                        }
                    }
                    System.out.println();
                }
            }

            else if (cmd.equals("q")) {
                hasQuit = true;
                System.out.println("Avslutter programmet...");
            } else {
                System.out.println("Ugyldig kommando!");
                System.out.println();
                continue;
            }
        }
    }
}
