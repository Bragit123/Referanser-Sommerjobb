import java.lang.Math;  // Trengs for å bruke Math.round til å runde av prisen fra double til int.

class Blaa extends Resept {
    public Blaa(Legemiddel legemiddel, Lege utskrivendeLege, Pasient pasient, int reit) {
        super(legemiddel, utskrivendeLege, pasient, reit);
    }

    @Override
    public String farge() {
        return "Blaa";
    }

    @Override
    public int prisAaBetale() {
        int gammelPris = legemiddel.hentPris();
        int nyPris = (int) Math.round(0.25 * gammelPris);
        return nyPris;
    }
}
