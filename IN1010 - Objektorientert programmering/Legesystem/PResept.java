class PResept extends Hvit {
    public PResept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient) {
        super(legemiddel, utskrivendeLege, pasient, 3);
    }

    @Override
    public int prisAaBetale() {
        int gammelPris = legemiddel.hentPris();
        int nyPris = gammelPris - 108;
        if (nyPris > 0) {
            return nyPris;
        } else {
            return 0;
        }
    }
}
