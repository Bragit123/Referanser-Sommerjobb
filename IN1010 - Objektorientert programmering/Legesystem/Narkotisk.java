class Narkotisk extends Legemiddel {
    protected int styrke;
    public Narkotisk(String navn, int pris, double virkestoff, int styrke) {
        super(navn, pris, virkestoff);
        this.styrke = styrke;
    }
    public int hentNarkotiskStyrke() {
        return styrke;
    }

    public String toString() {
        return super.toString("Narkotisk") + ", styrke: " + Integer.toString(hentNarkotiskStyrke());
    }
}
