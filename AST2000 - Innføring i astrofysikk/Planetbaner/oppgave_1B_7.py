## Ikke kodemal
## Dette er hovedprogrammet for oppgaven.
import ast2000tools.utils as utils
from ast2000tools.solar_system import SolarSystem
import ast2000tools.constants as const
import numpy as np
import matplotlib.pyplot as plt

## Henter solsystemet
seed = utils.get_seed("15251")
system = SolarSystem(seed)
system.print_info()

ecc = system.eccentricities
print(f"Eccentricities: {ecc}")

## Henter konstanter og data fra solsystemet
r0 = system.initial_positions # Startposisjon til planetene i AU
v0 = system.initial_velocities # Starthastighet til planetene i AU/yr
m = system.masses # Massen til planetene i m_sun (solmasser)
num_planets = system.number_of_planets
M = system.star_mass # Stjernemassen i m_sun
G = const.G_sol # Gravitasjonskonstant i solsystem enheter (AU^3/yr^2/m_sun)

## Funksjon for å regne ut kraften på en planet
def F(r, m):
    r_norm = np.linalg.norm(r) # Lengden til r
    r_unit = r/r_norm # Enhetsvektor i retning r
    return -G * M*m/r_norm**2 * r_unit # Newtons gravitasjonslov kombinert med Newtons andre lov

## Forberedelser til Euler-Cromer
t0 = 0
t1 = 2
dt = 1e-4 # Tidsforskjellen mellom stegene.
N = int(t1/dt) # Antall tidssteg

time_steps = np.linspace(t0, t1, N+1) # Tidspunktene i yr (år)

# Genererer arrayer for posisjon, hastighet og akselerasjon til planetene over tid
r = np.zeros((2, num_planets, N+1))
v = np.zeros((2, num_planets, N+1))
a = np.zeros((2, num_planets, N+1))

r[:,:,0] = r0 # Setter inn startposisjon
v[:,:,0] = v0 # Setter inn starthastighet
for i in range(num_planets):
    # Setter inn startakselerasjon for hver planet
    F0 = F(r[:,i,0], m[i]) # Kraften på planeten ved t=0
    a[:,i,0] = F0 / m[i] # Startakselerasjon fra kraften

## Euler-Cromer
for k in range(N):
    v[:,:,k+1] = v[:,:,k] + a[:,:,k] * dt # Finner hastighet fra akselerasjon
    r[:,:,k+1] = r[:,:,k] + v[:,:,k+1] * dt # Finner posisjon fra hastighet
    for i in range(num_planets):
        Fi = F(r[:,i,k+1], m[i]) # Finner kraften på hver planet
        a[:,i,k+1] = Fi / m[i] # Finner akselerasjon fra kraften (Newtons andre lov)

## Genererer XML for visualisering i SSView
system.generate_orbit_video(time_steps, r)

## Klasse for plotting av forskjellige baner
class OrbitPlot():
    def __init__(self, t, r):
        self.t = t # Array med verdiene for t
        self.r = r # Array med posisjonsvektor for hver planet til hver tid t

    def pos_plot(self, indices):
        # Plot av posisjonsvektoren til planetbanene. Med x-komponenten langs x-aksen, og y-komponenten langs y-aksen
        plt.title("Planetbaner for solsystemet Bragul", fontsize=20)
        plt.xticks(fontsize=18); plt.yticks(fontsize=18)
        plt.xlabel("$x$ [AU]", fontsize=18)
        plt.ylabel("$y$ [AU]", fontsize=18)
        plt.plot(0,0,"ko",label="Solen") # Markering av solen
        for i in indices:
            # Plotter for planetene med indeks i indices-arrayen.
            plt.plot(self.r[0,i,:], self.r[1,i,:], label=f"Planet {i+1}") # Plot av planetene
        plt.legend(fontsize=18)
        plt.axis("equal")
        plt.show()

    def time_plot(self, indices):
        # Plot av x-komponenten til posisjonsvektorene som funksjon over tid.
        plt.title(f"$x(t)$ for planetene", fontsize=20)
        plt.xticks(fontsize=18); plt.yticks(fontsize=18)
        plt.xlabel("$t$ [yr]", fontsize=18)
        plt.ylabel("$x$ [AU]", fontsize=18)
        for i in indices:
            # Plotter for planetene med indeks i indices-arrayen
            plt.plot(self.t, self.r[0,i,:], label=f"Planet {i+1}")
        plt.legend(fontsize=18)
        plt.axis("equal")
        plt.show()

## Sorterer banenes indeks i arrays for finere plot av baner.
# Funksjon for å finne den største verdien til en array
def find_max(x_list):
    x_max = 0 # Teller, starter på 0, siden alle banene må ha x-verdi over 0 på et tidspunkt
    for x in x_list:
        if x > x_max:
            x_max = x # Hvis en verdi er større enn x_max, settes denne til x_max
    return x_max # Returnerer den største verdien fra arrayen
# Genererer tomme lister til sortering av indre, midtre og ytre planetbaner
all_orbits = np.array(list(range(num_planets)))
inner_orbits = []
middle_orbits = []
outer_orbits = []
# Går gjennom hver planet og legger indeksen til planeten i riktig bane-liste
for i in all_orbits:
    x_max = find_max(r[0,i,:])
    if x_max < 0.1:
        inner_orbits.append(i)
    elif x_max < 0.25:
        middle_orbits.append(i)
    else:
        outer_orbits.append(i)
# Gjør om listene til arrayer
inner_orbits = np.array(inner_orbits)
middle_orbits = np.array(middle_orbits)
outer_orbits = np.array(outer_orbits)

## Plot av planetbanene
orbit_plot = OrbitPlot(time_steps, r) # Lager et objekt av OrbitPlot-klassen.

orbit_plot.pos_plot(all_orbits)
orbit_plot.pos_plot(inner_orbits)
orbit_plot.pos_plot(middle_orbits)
orbit_plot.pos_plot(outer_orbits)

## Plot av x(t) for hver planet over tid
orbit_plot.time_plot(inner_orbits)
orbit_plot.time_plot(middle_orbits)
orbit_plot.time_plot(outer_orbits)

"""
Terminal> python3 oppgave_1B_7.py
Information about the solar system with seed 95933:

Number of planets: 8
Star surface temperature: 3022.78 K
Star radius: 295138 km
Star mass: 0.261664 solar masses

Individual planet information. Masses in units of m_sun, radii in km,
atmospheric densities in kg/m^3, rotational periods in Earth days.
Planet |      Mass      |     Radius     |   Atm. dens.   |  Rot. period   |
     0 | 6.14121617e-06 |   8349.4190589 |    1.471593162 |     0.81624476 |
     1 | 2.40023808e-06 |   7228.7441837 |    5.247194831 |     2.01071586 |
     2 | 2.79573045e-08 |   1624.5442740 |    1.312927886 |    25.36048230 |
     3 | 6.09177383e-09 |    936.4060227 |    1.166261407 |    35.18272322 |
     4 | 0.000336202809 |  59826.1161866 |   19.798290285 |     0.47475286 |
     5 |  5.2925268e-08 |   1783.2997303 |    1.278947612 |    11.36315107 |
     6 |  9.8431816e-07 |   4705.0963960 |    1.128938279 |    38.07211536 |
     7 | 4.14338804e-08 |   1574.4149768 |    1.454895540 |     5.37212235 |

Individual planet initial positions (in AU) and velocities (in AU/yr).
Planet |       x        |       y        |       vx       |       vy       |
     0 |   0.0865528241 |   0.0000000000 |   0.0000000000 |  10.8828328918 |
     1 |  -0.1162939172 |  -0.0009863941 |  -0.0651235308 |  -9.6288128576 |
     2 |  -0.2102093447 |   0.8990940074 |  -3.3026412252 |  -0.9063698617 |
     3 |   0.0807120546 |   0.6600936011 |  -4.0357216700 |   0.4004035875 |
     4 |   0.0396410904 |  -0.3775317749 |   5.2843191202 |   0.2605151990 |
     5 |   0.0321975184 |   0.2306154899 |  -6.5483120879 |   0.8851133724 |
     6 |  -0.0017792740 |  -0.1473202771 |   8.6067302606 |   0.3772608724 |
     7 |   0.0336155362 |   0.0377113984 | -10.6769733027 |   9.9391885448 |
Eccentricities: [0.00765737 0.04641123 0.06158724 0.06305911 0.06228203 0.01635867
 0.08083281 0.04599011]
Generating orbit video with 4965 frames.
Note that planet/moon rotations and moon velocities are adjusted for smooth animation.
XML file orbit_video.xml was saved in XMLs/.
It can be viewed in SSView.
"""
