from matplotlib.pylab import *
import numpy as np

# extract into matrices
sample = np.loadtxt('iris.data', delimiter=',', usecols=([0,1,2,3]), dtype=None)
species = np.loadtxt('iris.data', delimiter=',', usecols=([4]), dtype='S20')

# create mappings for species to color 
# create dictionary for axes labels
colormap = {'Iris-setosa' : 'r', 'Iris-versicolor' : 'g' , 'Iris-virginica' : 'b'}
axeslabels = ['Sepal Length (cm)', 'Sepal Width (cm)', 'Petal Length (cm)', 'Petal Width (cm)']

# translate speciess into colors
colors = np.empty(species.size, dtype = 'S1')
for n,i in enumerate(species):
  colors[n] = colormap[i]

# create figure for combinations of plots
fig = figure()
x = 1

for a in xrange(0, 4):
  for b in xrange(0, 4):
    ax = fig.add_subplot(4,4,x)
    x+=1 
    comb = a * b
    if a == b:
      text(0.5, 0.5, axeslabels[a], horizontalalignment='center', verticalalignment='center')
      # figtext(0.5, 0.5, axeslabels[a], fontsize=12)
      ax.set_xticks([]) 
      ax.set_yticks([])
    if a != b:
      xs = sample[:, a]
      ys = sample[:, b] 
      scatter(xs, ys, c=colors)

fig.set_size_inches(15,12)
fig.savefig('writeup/HW0plot.png',dpi=100)
# show()