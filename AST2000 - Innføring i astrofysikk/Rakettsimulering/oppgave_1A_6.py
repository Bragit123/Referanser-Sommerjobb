### Ikke kodemal
import ast2000tools.utils as utils
from ast2000tools.solar_system import SolarSystem
import ast2000tools.constants as const
import numpy as np
import matplotlib.pyplot as plt

## Creating the Solar System
seed = utils.get_seed("15251")
system = SolarSystem(seed)

## Setting random seed
np.random.seed(int(seed))

## Constants
m_sun = const.m_sun
M = system.masses[0] * m_sun
G = const.G
R = system.radii[0] * 1e3
k = const.k_B
m_H2 = const.m_H2
m_rocket = 1e3
T = 1e4
L = 1e-6
mu = 0

## Display information about the Solar System
print("The Solar System:")
print(f"Bragus has a mass of {M:.2e} kg")
print(f"Bragus has a radius of {R:.2e} m")
print()

## Finding the escape velocity
v_esc = np.sqrt(2*G*M/R)
print(f"The escape velocity of Bragus is {v_esc:.2e} m/s")
print()

## Simulating random particle positions
N = int(1e5) # Number of particles
sigma = np.sqrt(k*T/m_H2)
r_par = np.random.uniform(0, L, (N, 3))
v_par = np.random.normal(mu, sigma, (N, 3))

## Plotting the particles positions for visualization
fig = plt.figure()
ax = fig.add_subplot(projection="3d")
ax.set_title(f"{N} particles with uniform distribution within the engine box.")
ax.set_xlabel("x [m]"); ax.set_ylabel("y [m]"); ax.set_zlabel("z [m]")
ax.scatter(r_par[:,0], r_par[:,1], r_par[:,2])
plt.show()

## Finding absolute velocity of each particle
v_abs = np.zeros(N)
for i in range(N):
    vi = v_par[i] # Extracts the v-vector with index i
    vi_abs = np.linalg.norm(vi) # Finds the absolute velocity of vi
    v_abs[i] = vi_abs

## Finding the kinetic energy of each particle
K = np.zeros(N)
for i in range(N):
    vi = v_abs[i]
    Ki = 1/2 * m_H2 * vi**2
    K[i] = Ki

## Function for finding the mean of an array
def find_mean(arr):
    total = 0
    length = len(arr)
    for i in range(length):
        total += arr[i]
    return total/N

## Finding the mean kinetic energy and absolute velocity
K_mean = find_mean(K)
K_mean_anal = 3/2*k*T # K_mean analytically
K_mean_relDiff = abs((K_mean-K_mean_anal)/K_mean_anal) # Relative difference between numerical and analytical K_mean

v_mean = find_mean(v_abs)
v_mean_anal = np.sqrt(8*k*T/(np.pi*m_H2)) # v_mean analytically
v_mean_relDiff = abs((v_mean-v_mean_anal)/v_mean_anal) # Relative difference between numerical and analytical v_mean

print("The mean kinetic energy of the particles:")
print(f" Numerically: {K_mean:.4e} J\n Analytically: {K_mean_anal:.4e} J\n Relative difference: {K_mean_relDiff:.2e}")
print()
print("The mean absolute velocity of the particles:")
print(f" Numerically: {v_mean:.4e} m/s\n Analytically: {v_mean_anal:.4e} m/s\n Relative difference: {v_mean_relDiff:.2e}")
print()

## Simulating the particles movement over time
t0 = 0
t1 = 1e-9 # The total timeperiod of the simulation
n = int(1e3) # Number of time-steps
delta_t = t1-t0 # The total timeperiod of the simulation.
dt = (delta_t)/n # Interval of a timestep. Notice how this differs from delta_t

n_p = 0 # (Counter) Number of particles hitting the wall at x=0
p = 0 # (Counter) Total momentum from the particles hitting the wall at x=0

# Eulers method:
for i in range(n+1):
    # Iterates over each timestep
    for par in range(N):
        # Iterates over each particle
        vi = v_par[par,:]
        ri = r_par[par,:]
        r_new = ri + dt*vi
        for coord in range(3):
            # Iterates over each coordinate in r_new
            # Here 0 = x; 1 = y; 2 = z
            if r_new[coord] <= 0:
                # The particle hit the wall in x/y/z = 0
                if coord == 0:
                    # If the particle hits the wall at x=0, we want to count the particle.
                    # We also want to count the momentum.
                    n_p += 1
                    p += abs(2*m_H2*vi[0]) # Momentum difference of the particles collision with the wall
                vi[coord] = -vi[coord] # Flips the speed-vector because of the collision
            if r_new[coord] >= L:
                # The particle hit the wall in x/y/z = L
                vi[coord] = -vi[coord] # Flips the speed-vector because of the collision
        r_par[par,:] = r_new # Inserts the new r-vector
        v_par[par,:] = vi # Inserts the (possibly) new v-vector.

## Here we will calculate the pressure both numerically and analytically.
P_num = p/((delta_t)*L**2) # Numerical pressure
P_anal = N/(L**3) * k*T # Analytical pressure (equation of state for an ideal gas).
relDiff = abs((P_num-P_anal)/P_anal)

print("The pressure in the box:")
print(f" Numerically: {P_num:.2e} Pa\n Analytically: {P_anal:.2e} Pa\n Relative difference: {relDiff:.2e}")
print()

## Simulating the hole in the rocket engine. We will now use the same loop for Eulers method as above, but now with som adjustments
n_p = 0 # Resets the number of particles. n_p is now the number of particles escaping through the hole.
p = 0 # Resets the total momentum. p is now the total momentum of the particles escaping through the hole.
# Eulers method:
for i in range(n+1):
    # Iterates over each timestep
    for par in range(N):
        # Iterates over each particle
        vi = v_par[par,:]
        ri = r_par[par,:]
        r_new = ri + dt*vi
        for coord in range(3):
            # Iterates over each coordinate in r_new
            if r_new[coord] <= 0:
                # The particle hit the wall in x/y/z = 0
                if coord == 0:
                    # Checks if the wall being hit was the wall at x=0
                    if r_new[1] < L/2 and r_new[2] < L/2:
                        # Checks that the particle hit the hole at y<L/2 and z<L/2
                        # If this is the case, we want to count the particle, and its momentum.
                        n_p += 1
                        p += 2*m_H2*vi[0] # Momentum difference of the particles collision with the wall
                        r_new[coord] = L # Simulates the adding of a new particle, when one particle escapes the engine
                        continue # If a particle escapes through the hole of the engine we want a new particle to appear (simulated in the previous line),
                                 # and we want the velocity to remain the same. Therefore we skip the rest of the loop, where we simulate the wall-bounce.
                vi[coord] = -vi[coord] # Flips the speed-vector because of the collision
            if r_new[coord] >= L:
                # The particle hit the wall in x/y/z = L
                vi[coord] = -vi[coord] # Flips the speed-vector because of the collision
        r_par[par,:] = r_new # Inserts the (possibly) new r-vector
        v_par[par,:] = vi # Inserts the (possibly) new v-vector.

p = -p
F = p/delta_t # The force from the particles escaping the engine.
a = F/m_rocket # The rockets acceleration, deduced from Newtons second law.
fuel_loss = n_p * m_H2
dT = 20*60 # 20 minutes = 1200 seconds. Total timeperiod until we want the rocket to reach escape-velocity.

v_rocket = a*dT

print("Results from simulating the rocket engine with one box:")
print(f" Force: {F:.2e} N\n Acceleration: {a:.2e} m/s^2\n fuel_loss: {fuel_loss:.2e} kg\n Rocket speed (after {delta_t:.0e} s): {v_rocket:.2e} m/s")
print()

## Calculating number of boxes needed:
N_box = v_esc * m_rocket * delta_t / (dT * p) # Number of boxes
total_fuel_loss = fuel_loss * N_box * (dT/delta_t) # Total fuel loss after 20 minutes.

print(f"Number of boxes needed for the rocket to reach escape velocity after 20 minutes: {N_box:.2e}")
print(f"Total fuel loss for the rocket after 20 minutes: {total_fuel_loss:.2e} kg")

## Plotting the rockets movement and velocity:
F_total = N_box * p / delta_t # Kraften på raketten.
a = F_total/m_rocket # Akselerasjonen til raketten.
n = 100 # n is now the number of timesteps for the complete rocketlaunch (20minutes)
time_points = np.linspace(t0, t0 + dT, n+1)
v_points = a*time_points # Array with the speed of the rocket (Found analytically)
dist = 1/2 * a*time_points**2  # Array with the height above the ground the rocket has reached (Found analytically)

plt.title("The rockets distance from Bragus over time.")
plt.xlabel("Time [s]"); plt.ylabel("Distance [m]")
plt.plot(time_points, dist, "g")
plt.show()

plt.title("The rockets absolute velocity over time, until it reaches absolute velocity.")
plt.xlabel("Time [s]"); plt.ylabel("Absolute velocity [m/s]")
plt.plot(time_points, v_points, "r")
plt.show()

"""
Terminal> python3 oppgave_1A_6.py
The Solar System:
Bragus has a mass of 1.22e+25 kg
Bragus has a radius of 8.35e+06 m

The escape velocity of Bragus is 1.40e+04 m/s

The mean kinetic energy of the particles:
 Numerically: 2.0693e-19 J
 Analytically: 2.0710e-19 J
 Relative difference: 7.97e-04

The mean absolute velocity of the particles:
 Numerically: 1.0242e+04 m/s
 Analytically: 1.0248e+04 m/s
 Relative difference: 5.83e-04

The pressure in the box:
 Numerically: 1.36e+04 Pa
 Analytically: 1.38e+04 Pa
 Relative difference: 1.35e-02

Results from simulating the rocket engine with one box:
 Force: 4.28e-09 N
 Acceleration: 4.28e-12 m/s^2
 fuel_loss: 2.56e-22 kg
 Rocket speed (after 1e-09 s): 5.14e-09 m/s

Number of boxes needed for the rocket to reach escape velocity after 20 minutes: 2.72e+12
Total fuel loss for the rocket after 20 minutes: 8.35e+02 kg
"""
