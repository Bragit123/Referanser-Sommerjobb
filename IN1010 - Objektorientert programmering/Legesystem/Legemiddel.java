abstract class Legemiddel { // abstract gjør at det ikke kan opprettes en instans av klassen.
    protected static int idNum = 0; // Antall instanser som er lagd, brukes til å bestemme id.

    protected int id;
    protected String navn;
    protected int pris;
    protected double virkestoff;

    public Legemiddel(String navn, int pris, double virkestoff) {
        this.navn = navn;
        this.pris = pris;
        this.virkestoff = virkestoff;
        id = idNum++;
    }

    public int hentId() {
        return id;
    }

    public String hentNavn() {
        return navn;
    }

    public int hentPris() {
        return pris;
    }

    public double hentVirkestoff() {
        return virkestoff;
    }

    public void settNyPris(int nyPris) {
        pris = nyPris;
    }

    public String toString(String type) {
        return "Type: " + type + ", navn: " + hentNavn() + ", pris: " + Integer.toString(hentPris()) + ", virkestoff: " + Double.toString(hentVirkestoff());
    }
}
