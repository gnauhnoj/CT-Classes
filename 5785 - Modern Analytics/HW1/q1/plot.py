from matplotlib.pylab import *

#part c
def plot_data(data,axeslabels):
  # fig = figure()
  # x = 1
  
  for a in xrange(1, 4):
    # ax = fig.add_subplot(3,1,x)
    # x+=1 
    xs = data[:, a]
    ys = data[:, 0] 
    scatter(xs, ys)
    xlabel(axeslabels[a])
    ylabel(axeslabels[0])
    if a == 2:
      xlim(0,25)
    else:
      xlim(0)
    ylim(0)
    savefig('plots/' + str(a) + '-mean.png', dpi=100)
    show()
    close()

  # fig.set_size_inches(15,12)
