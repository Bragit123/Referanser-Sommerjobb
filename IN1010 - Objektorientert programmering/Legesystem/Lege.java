class Lege implements Comparable<Lege> {
    private String navn;
    private Lenkeliste<Resept> utskrevedeResepter = new Lenkeliste<Resept>();

    public Lege (String navn) {
        this.navn = navn;
    }

    public String hentNavn() {
        return navn;
    }

    public String toString() {
        return "Navn: " + hentNavn();
    }

    public Lenkeliste<Resept> hentResepter() {
        return utskrevedeResepter;
    }

    @Override
    public int compareTo(Lege andre) {
        String andreNavn = andre.hentNavn();

        if (navn.compareTo(andreNavn) < 0) {
            return -1;
        }
        if (navn.compareTo(andreNavn) > 0) {
            return 1;
        }
        return 0;
    }

    public Hvit skrivHvitResept(Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        boolean erSpesialist = (this instanceof Spesialist);
        boolean erNarkotisk = (legemiddel instanceof Narkotisk);
        if (erSpesialist == false && erNarkotisk == true) {
            // Sender feilmelding hvis legen ikke er spesialist, og legemiddelet er narkotisk.
            throw new UlovligUtskrift(this, legemiddel);
        }
        Hvit resept = new Hvit(legemiddel, this, pasient, reit);
        pasient.leggTil(resept);
        utskrevedeResepter.leggTil(resept);
        return resept;
    }

    public MilitaerResept skrivMilitaerResept(Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        boolean erSpesialist = (this instanceof Spesialist);
        boolean erNarkotisk = (legemiddel instanceof Narkotisk);
        if (erSpesialist == false && erNarkotisk == true) {
            // Sender feilmelding hvis legen ikke er spesialist, og legemiddelet er narkotisk.
            throw new UlovligUtskrift(this, legemiddel);
        }
        MilitaerResept resept = new MilitaerResept(legemiddel, this, pasient, reit);
        pasient.leggTil(resept);
        utskrevedeResepter.leggTil(resept);
        return resept;
    }

    public PResept skrivPResept(Legemiddel legemiddel, Pasient pasient) throws UlovligUtskrift {
        boolean erSpesialist = (this instanceof Spesialist);
        boolean erNarkotisk = (legemiddel instanceof Narkotisk);
        if (erSpesialist == false && erNarkotisk == true) {
            // Sender feilmelding hvis legen ikke er spesialist, og legemiddelet er narkotisk.
            throw new UlovligUtskrift(this, legemiddel);
        }
        PResept resept = new PResept(legemiddel, this, pasient);
        pasient.leggTil(resept);
        utskrevedeResepter.leggTil(resept);
        return resept;
    }

    public Blaa skrivBlaaResept(Legemiddel legemiddel, Pasient pasient, int reit) throws UlovligUtskrift {
        boolean erSpesialist = (this instanceof Spesialist);
        boolean erNarkotisk = (legemiddel instanceof Narkotisk);
        if (erSpesialist == false && erNarkotisk == true) {
            // Sender feilmelding hvis legen ikke er spesialist, og legemiddelet er narkotisk.
            throw new UlovligUtskrift(this, legemiddel);
        }
        Blaa resept = new Blaa(legemiddel, this, pasient, reit);
        pasient.leggTil(resept);
        utskrevedeResepter.leggTil(resept);
        return resept;
    }
}
