import math
from typing import Tuple

R = 6371e3

def distance(coords1: tuple, coords2: tuple):  # coords(latitudine, longitudine)
    delta_lat = ((coords2[0] - coords1[0]) * math.pi) / 180  # latitudine
    delta_lon = ((coords2[1] - coords1[1]) * math.pi) / 180  # longitudine

    a = math.sin(delta_lat/2) * math.sin(delta_lat/2) + \
    math.cos(coords1[0]) * math.cos(coords2[0]) * \
    math.sin(delta_lon/2) * math.sin(delta_lon/2)

    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))

    return R*c


t = (46.04379, 13.22522)
j = (36.96376, 15.20358)

print(distance(t, j))