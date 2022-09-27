class SortertLenkeliste<T extends Comparable<T>> extends Lenkeliste<T> {
    @Override
    public void leggTil(T x) {
        // Legger x til listen slik at listen forblir sortert.
        Node peker = start;
        Node forrigePeker = null;
        Node nyNode = new Node(x);

        if (stoerrelse() == 0) {
            // Legger til paa vanlig vis dersom listen er tom
            super.leggTil(x);
        } else if (stoerrelse() == 1) {
            // Hvis listen kun har ett element legges x til foran eller bak dette
            // avhengig av verdien. Her trenger vi altsaa ikke forrigePeker.
            if (peker.data.compareTo(x) > 0) {
                // Hvis elementet i listen er stoerre enn x legges x til foer det
                // gamle elementet.
                nyNode.neste = peker;
                start = nyNode;
            } else {
                // Hvis elementet i listen er mindre eller lik x legges x til
                // etter det gamle elementet.
                start.neste = nyNode;
            }
        } else {
            // Hvis listen inneholder flere enn ett element maa vi bruke forrigePeker.
            for (int i = 0; i < stoerrelse(); i++) {
                // Gaar gjennom alle elementene i listen
                if (peker.data.compareTo(x) > 0) {
                    // Dersom elementet er stoerre enn x, legges x til mellom
                    // dette elementet og den forrige

                    if (i == 0) {
                        // Legger nyNode i starten hvis det foerste elementet er stoerre enn denne
                        nyNode.neste = peker;
                        start = nyNode;
                        return;
                    }
                    forrigePeker.neste = nyNode;
                    nyNode.neste = peker;
                    return;
                }
                // Oppdaterer forrigePeker og peker.
                forrigePeker = peker;
                peker = peker.neste;
            }
            // Hvis vi naar helt hit i koden betyr det at x er stoerre enn alle
            // elementene i listen. Vi legger dermed x til paa vanlig vis, ettersom
            // dette legger x til slutten av listen.
            super.leggTil(x);
        }
    }

    @Override
    public T fjern() throws UgyldigListeIndeks {
        // Fjerner det stoerste (siste) elementet i listen.
        Node peker = start;
        Node forrigePeker = null;

        if (peker == null) {
            // Sender feilmelding hvis listen er tom
            throw new UgyldigListeIndeks(-1);
        }

        if (peker.neste == null) {
            // Toemmer listen hvis det kun er ett element
            start = null;
            return peker.data;
        }

        while (peker.neste != null) {
            // Finner de to siste elementene i listen.
            forrigePeker = peker;
            peker = peker.neste;
        }
        forrigePeker.neste = null;
        return peker.data;
    }

    @Override
    public void sett(int pos, T x) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void leggTil(int pos, T x) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
