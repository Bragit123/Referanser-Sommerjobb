## Ikke kodemal

import ast2000tools.utils as utils
from ast2000tools.solar_system import SolarSystem
import ast2000tools.constants as const
import numpy as np
import matplotlib.pyplot as plt

## Henter mitt seed
seed = utils.get_seed("15251")
print(f"Seed: {seed}") # Mitt seed er 95933, så jeg bruker mappe "seed33"

## Henter konstanter
m_sun = const.m_sun # Solmassen
c = const.c # Lyshastigheten
G = const.G # Gravitasjonskonstant
day = const.day # Lengden til en dag i sekunder

## Lager Start-klassen
class Star:
    def __init__(self, filename):
        ## Henter ut info fra filnavn
        self.lambd_0 = 656.28e-9 # lambda_0 i m
        self.index = int(filename[4]) # Stjerneindeksen
        self.M = float(filename[6:10]) * m_sun # Massen til stjerna i kg

        ## Henter ut data fra filen
        file_data = self.read_star_file(filename) # Leser data fra stjernefilen
        self.t = file_data[0]
        self.lambd = file_data[1]
        self.F = file_data[2]

        ## Finner radial hastighet
        self.v_r = self.find_v_r() # Finner v_r
        self.v_pec = self.find_v_pec() # Finner v_pec
        self.v = self.v_r - self.v_pec # Finner hastigheten til stjerna relativt til massesenteret

    ## Henter ut data fra stjernefilen
    def read_star_file(self, filename):
        ## Initierer lister for tid, bølgelengde og fluks
        t = [] # Tid
        lambd = [] # Bølgelengde (lambd, siden lambda er opptatt som funksjon i python)
        F = [] # Fluks

        ## Leser gjennom stjernefilen
        with open(filename, "r") as infile:
            for line in infile:
                data = line.split() # Deler linjene opp i de tre kolonnene
                t.append(float(data[0])) # Legger tidskolonnen i t-listen
                lambd.append(float(data[1])) # Legger bølgelengdekolonnen i lambd-listen
                F.append(float(data[2])) # Legger flukskolonnen i F-listen

        # Gjør om alle listene til arrays:
        t = np.array(t)
        lambd = 1e-9 * np.array(lambd) # Gjør også om alle lambd-verdiene fra nm til m
        F = np.array(F)
        return (t, lambd, F) # Returnerer et tuppel med t-, lambd- og F-arrayene

    ## Finner radial hastighet til stjernen
    def find_v_r(self):
        dlambd = self.lambd - self.lambd_0 # Forskjellen mellom lambda-verdiene og lambda_0
        v_r = dlambd/self.lambd_0 * c # Finner radial hastighet ved hvert tidspunkt fra dopplerformelen
        return v_r

    ## Finner egenhastigheten til stjerna
    def find_v_pec(self):
        v_r = self.v_r
        n = len(v_r) # Antall elementer i v_r
        total = 0 # Teller, starter på 0
        for i in range(n):
            total += v_r[i] # Legger til hver verdi av den radielle hastigheten til totalen
        v_pec = total/n # Finner gjennomsnittlig hastighet. Dette er v_pec
        return v_pec

    ## Finner nedre grense for planetmassen i kg
    def find_m_p_min(self, v_r_max, P):
        ## v_r_max = maksimal radiell hastighet; P = perioden til planetbanen
        M = self.M # Massen
        P = P*day # Konverterer perioden fra dager til sekunder
        num = M**(2/3) * v_r_max * P**(1/3) # Numerator: telleren i formelen for m_p
        den = (2 * np.pi * G)**(1/3) # Denominator: nevneren i formelen for m_p
        return num/den

    ## Finner modell for v_r fra gitt verdi for t0, v_r_max og P
    def find_v_r_model(self, t0, v_r_max, P):
        # t0 = tidspunkt for bølgetopp til v_r (hvor v_r er maksimal); v_r_max = bølgetopp (maksimal v_r); P = perioden fra en bølgetopp til neste
        t_data = self.t # Tidspunktene for målingene i data-filen [dager]
        return v_r_max * np.cos(2*np.pi * (t_data - t0)/P) # Modell for v_r fra t0, v_r og P

    ## Finner estimat for t0, v_r og P ved minste kvadraters metode
    def least_squares(self, t0_list, v_r_list, P_list):
        v_r_data = self.v # v_r til planeten fra data-filen [m/s]
        # Legg merke til at vi her ikke konverterer målingene fra dager til sekunder (som jo er SI-enheten). Dette er siden
        # vi kun ser på forholdet mellom tidspunkt og periode, så hvilken enhet vi bruker her er ikke så nøye, så lenge begge
        # bruker den samme. Vi ser fra formelen for v_r_model at vi får (t-t0 [dager]) / P [dager] = x [enhetsløst]. Dermed
        # blir dette satt inn i cosinus, og vi får en enhetsløs verdi ganget med v_r [m/s]. I Delta-funksjonen får vi da
        # v_r_data [m/s] - v_r_model [m/s], som gir at resultatet vårt blir oppgitt i m/s.
        Delta_best = None # Variabel som holder på den minste (beste) Delta-verdien vi har funnet
        v_r = None; v_r_best = None; P_best = None # Variabler som lagrer t0-, v_r og P-verdiene som hører til den beste Delta-verdien
        for t0 in t0_list:
            for v_r in v_r_list:
                for P in P_list:
                    # Går gjennom alle kombinasjoner av t0-, v_r- og P-verdiene
                    v_r_model = self.find_v_r_model(t0, v_r, P) # Finner en modell for v_r
                    diff = v_r_data - v_r_model # Differansen mellom v_r_data og modellen vi fant for v_r
                    Delta = sum(diff**2) # Finner Delta fra summen av kvadratet til differansen over alle t-verdier
                    if Delta_best == None or Delta_best > Delta:
                        # Sjekker om Delta-verdien vi fant er mindre (og dermed bedre) enn den minste (beste) vi har funnet fram til nå
                        Delta_best = Delta # Oppdaterer Delta_best
                        t0_best = t0 # Oppdaterer t0_best
                        v_r_best = v_r # Oppdaterer v_r_best
                        P_best = P # Oppdaterer P_best
        return (t0_best, v_r_best, P_best) # Returnerer t0, v_r og P-verdien som sammen ga den laveste (og dermed beste) verdien for Delta

    ## Printer info om stjerna
    def print_info(self):
        print(f"Stjerne #{self.index}:  M = {self.M}")
        print(f"t = {self.t}")
        print(f"lambd = {self.lambd}")
        print(f"F = {self.F}")

## Lager et objekt av Star-klassen for hver stjerne
star_num = 5 # Antall stjerner
stars = [None]*star_num # Liste for å holde alle stjerneobjektene
stars[0] = Star("star0_1.50.txt")
stars[1] = Star("star1_1.46.txt")
stars[2] = Star("star2_1.27.txt")
stars[3] = Star("star3_0.85.txt")
stars[4] = Star("star4_3.99.txt")

## Printer egenhastigheten til hver stjerne:
print("Egenhastigheten til hver stjerne:")
for i in range(star_num):
    print(f"  {i+1}: {stars[i].v_pec:.2e} m/s")

## Plotter hastighet og fluks over tid
for star in stars:
    t = star.t; v = star.v; F = star.F # Henter t, v og F fra stjernen
    fig, axs = plt.subplots(2, sharex=True) # Lager plotfigur for to plot
    fig.suptitle(f"Plot av hastighet og relativ lysfluks for stjerne {star.index + 1}", fontsize=16)
    axs[0].set(ylabel="$v$ [m/s]")
    axs[1].set(xlabel="$t$", ylabel="$F$")
    axs[0].plot(t, v, label="$v(t)$") # Plotter hastighet øverst i figuren
    axs[1].plot(t, F, label="$F(t)$") # Plotter fluksen nederst i figuren
    for ax in axs:
        ax.xaxis.label.set_size(16)
        ax.yaxis.label.set_size(16)
        ax.legend(fontsize=16)
    plt.show()

## Finner nedre verdi for planetmassen m_p_min ved øyemål.
v_r_max = np.array([12, 42, 6, 22, None]) # Array med maksimal radiell hastighet for planetene (None hvis ingen planet)
P = np.array([5000, 6000, 4500, 3500, None]) # Array med perioden til planetbanene (None hvis ingen planet)
m_p_min = np.zeros(star_num) # Array for å holde på planetmasseverdiene
for i in range(star_num):
    # Går gjennom alle stjernene og finner planetmassen (hvis den har en planet)
    if v_r_max[i] == None:
        m_p_min[i] = None # Setter planetmassen til None hvis stjernen ikke har noen planet i bane
    else:
        m_p_min[i] = stars[i].find_m_p_min(v_r_max[i], P[i]) # Finner planetmassen til stjernen og legger inn i arrayen

# printer resultatet for massen til hver planet
print(f"Nedre verdi for massen til hver planet (ved øyemål):")
for i in range(star_num):
    if np.isnan(m_p_min[i]):
        print(f"  {i+1}: Ingen planet i bane")
    else:
        print(f"  {i+1}: {m_p_min[i]:.4e} kg")

## Finner planetradiusen r_p ved øyemål
# Kan ikke finne planetradiusen ved øyemål!

## Finner mer eksakte verdier for P og v_r ved å bruke minste kvadraters metode
star = stars[1] # Velger å fokusere på denne stjernen siden den har en tydelig planetbane rundt seg.

# Finner min- og maksverdier for t0, v_r og P
t0_min = 1500; t0_max = 2500 # Min- og maksverdi for t-verdien til første bølgetopp [dager]
t1_min = 7500; t1_max = 8500 # Min- og maksverdi for t-verdien til andre bølgetopp [dager]
v_r_min = 35; v_r_max = 50 # Min- og maksverdi for v_r i bølgetopp [m/s]
P_min = t1_min-t0_max; P_max = t1_max-t0_min # Min- og maksverdi for perioden P fra første til andre bølgetopp [dager]

# Lager arrays med forskjellige verdier for t0, v_r og P som ligger mellom min- og maksverdiene deres
N = 20 # Antall verdier i arrayene til t0, v_r og P
t0_list = np.linspace(t0_min, t0_max, N) # t0-verdiene [dager]
v_r_list = np.linspace(v_r_min, v_r_max, N) # v_r-verdiene [m/s]
P_list = np.linspace(P_min, P_max, N) # P-verdiene [dager]

t0, v_r, P = star.least_squares(t0_list, v_r_list, P_list)
v_r_model = star.find_v_r_model(t0, v_r, P)

## Plotter v_r fra data-filen sammen med modellen vår
plt.title(f"Sammenligning av $v_r$ fra datafilen og fra modellen vår for Stjerne {star.index + 1}", fontsize=16)
plt.xlabel("$t$ [dager]", fontsize=16); plt.ylabel("$v$ [m/s]", fontsize=16)
plt.plot(star.t, star.v, "b", label="$v_r$ fra data")
plt.plot(star.t, v_r_model, "r", label="Vår modell for $v_r$")
plt.legend(fontsize=16)
plt.show()

## Finner nedre verdi for massen til planeten med v_r- og P-verdiene fra modellen vår
m_p_min = star.find_m_p_min(v_r, P) # Finner massen ved hjelp av funksjonen vi brukte tidligere
print(f"Nedre verdi for massen til planeten i bane rundt stjerne {star.index + 1} fra modellen vår:")
print(f"  m_p_min = {m_p_min:.4e} kg")

"""
Kjøreeksempel:
Terminal> python3 oppgave_1C_4.py
Seed: 95933
Egenhastigheten til hver stjerne:
  1: -7.27e+04 m/s
  2: 1.36e+05 m/s
  3: 8.50e+04 m/s
  4: 3.30e+04 m/s
  5: -2.50e+05 m/s
Nedre verdi for massen til hver planet (ved øyemål):
  1: 2.5112e+27 kg
  2: 9.1733e+27 kg
  3: 1.0850e+27 kg
  4: 2.7993e+27 kg
  5: Ingen planet i bane
Nedre verdi for massen til planeten i bane rundt stjerne 2 fra modellen vår:
  m_p_min = 9.5131e+27 kg
"""
