abstract class Resept {
    protected static int idNum = 0; // Antall instanser som er lagd, brukes til å bestemme id.

    protected int id;
    protected Legemiddel legemiddel;
    protected Lege utskrivendeLege;
    protected Pasient pasient;
    protected int reit;

    public Resept(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        this.legemiddel = legemiddel;
        this.utskrivendeLege = utskrivendeLege;
        this.pasient = pasient;
        this.reit = reit;
        id = idNum++;
    }

    public int hentId() {
        return id;
    }

    public Legemiddel hentLegemiddel() {
        return legemiddel;
    }

    public Lege hentLege() {
        return utskrivendeLege;
    }

    public Pasient hentPasient() {
        return pasient;
    }

    public int hentReit() {
        return reit;
    }

    public boolean bruk() {
        if (reit >= 1) {
            reit--;
            return true;
        } else {
            return false;
        }
    }

    abstract public String farge();

    abstract public int prisAaBetale();

    public String toString() {
        String idString = Integer.toString(hentId());
        String legemiddelNavn = hentLegemiddel().hentNavn();
        String legeNavn = hentLege().hentNavn();
        String pasientNavn = pasient.hentNavn();
        String reitString = Integer.toString(hentReit());
        String prisAaBetaleString = Integer.toString(prisAaBetale());

        return "ID: " + idString + ", Legemiddel: " + legemiddelNavn + ", Lege: " + legeNavn + ", Pasient: " + pasientNavn + ", Reit: " + reitString + ", Farge: " + farge() + ", Pris å betale: " + prisAaBetaleString;
    }
}
