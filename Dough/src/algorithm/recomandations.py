import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix
from sklearn.neighbors import NearestNeighbors
import matplotlib.pyplot as plt
import seaborn as sns

sellers = pd.read_csv("", sep = ';', skiprows=[1]) # sellers csv address
ratings = pd.read_csv("", sep = ';', skiprows=[1]) # ratings csv address