
class Pasient {
    private static int idNum;

    private int id;
    private String navn;
    private String fnr; // Foedselsnummer

    private Stabel<Resept> resepter = new Stabel<Resept>();

    public Pasient(String navn, String fnr) {
        this.navn = navn;
        this.fnr = fnr;

        id = idNum++;
    }

    public String hentNavn() {
        return navn;
    }

    public int hentId() {
        return id;
    }

    public String hentFnr() {
        return fnr;
    }

    public void leggTil(Resept resept) {
        resepter.leggPaa(resept);
    }

    public Stabel<Resept> hentResepter() {
        return resepter;
    }
}
