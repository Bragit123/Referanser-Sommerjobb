import java.util.Iterator;
import java.lang.Iterable;

class Lenkeliste<T> implements Liste<T> {
    class Node {
        Node neste = null;
        T data;
        Node (T x) {
            data = x;
        }
    }

    class LenkelisteIterator implements Iterator<T> {
        private int pos = 0;
        public boolean hasNext() {
            return pos < stoerrelse();
        }
        public T next() {
            return hent(pos++);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LenkelisteIterator();
    }

    protected Node start = null;  // Foerste Node

    @Override
    public void leggTil(T x) {
        // Legger x til slutten av listen
        Node peker = start;

        if (peker == null) {
            // Setter foerste element til x hvis listen fra foer er tom.
            start = new Node(x);
        } else {
            while (peker.neste != null) {
                // Finner siste Node i listen (noden hvor neste==null)
                peker = peker.neste;
            }
            peker.neste = new Node(x);
        }
    }

    @Override
    public T fjern() throws UgyldigListeIndeks {
        // Fjerner foerste element fra listen
        Node first = start;

        if (first == null) {
            // Feilmelding hvis listen er tom
            throw new UgyldigListeIndeks(-1);
        }

        Node newFirst = start.neste;
        start = newFirst;
        return first.data;
    }

    @Override
    public void sett(int pos, T x) throws UgyldigListeIndeks {
        // Endrer element i posisjon pos til x

        if (pos < 0 || pos >= stoerrelse()) {
            // Feilmelding hvis indeks ikke finnes
            throw new UgyldigListeIndeks(pos);
        }

        Node peker = start;
        for (int i = 0; i < pos; i++) {
            // Finner noden i posisjon pos
            peker = peker.neste;
        }
        peker.data = x;
    }

    @Override
    public void leggTil(int pos, T x) throws UgyldigListeIndeks {
        // Legger til nytt element x i posisjon pos

        if (pos<0 || pos>stoerrelse()) {
            // Feilmelding hvis indeks ikke finnes
            System.out.println(stoerrelse());
            throw new UgyldigListeIndeks(pos);
        }

        Node peker = start;
        Node forrigePeker = null;
        Node nyNode = new Node(x);

        if (pos == 0) {
            // Setter nytt element som start hvis pos==0
            start = nyNode;
            nyNode.neste = peker;
        } else {
            for (int i = 0; i < pos; i++) {
                // Finner node i posisjon pos
                forrigePeker = peker;
                peker = peker.neste;
            }
            forrigePeker.neste = nyNode;
            nyNode.neste = peker;
        }
    }

    @Override
    public T fjern(int pos) throws UgyldigListeIndeks {
        // Fjerner element i posisjon pos
        Node peker = start;
        Node forrigePeker = null;

        if (peker == null) {
            // Feilmelding hvis listen er tom
            throw new UgyldigListeIndeks(-1);
        }
        if (pos < 0 || pos >= stoerrelse()) {
            // Feilmelding hvis indeks ikke finnes
            throw new UgyldigListeIndeks(pos);
        }

        if (pos == 0) {
            // Fjerner foerste element hvis pos==0
            start = start.neste;
            return peker.data;
        }

        for (int i = 0; i < pos; i++) {
            // Finner node i posisjon pos
            forrigePeker = peker;
            peker = peker.neste;
        }
        forrigePeker.neste = peker.neste;
        return peker.data;
    }

    @Override
    public int stoerrelse() {
        // Returnerer antall elementer i listen
        int teller = 0;
        Node peker = start;
        while (peker != null) {
            // Peker paa neste node fram til pekeren er null
            teller++;   // Legger til 1 til teller for hvert element
            peker = peker.neste;
        }
        return teller;
    }

    @Override
    public T hent(int pos) throws UgyldigListeIndeks {
        // Returnerer element i posisjon pos

        if (pos < 0 || pos >= stoerrelse()) {
            // Feilmelding hvis indeks ikke finnes
            throw new UgyldigListeIndeks(pos);
        }

        Node peker = start;
        for (int i = 0; i < pos; i++) {
            peker = peker.neste;
        }
        return peker.data;
    }

    public String toString() {
        // Lager en streng med informasjon om elementene i listen.
        // Formateringen her er: "[ element, element, ..., element ]"
        // Altså med klammeparenteser paa sidene, slik som arrayer.
        String tekst = "[";
        Node peker = start;
        while (peker != null) {
            if (tekst.equals("[")) {
                // Legger ikke til komma i starten dersom dette er foerste element.
                tekst += peker.data;
            } else {
                tekst += ", " + peker.data;
            }
            peker = peker.neste;
        }
        tekst += "]";
        return tekst;
    }
}
