import random
import matplotlib.pylab as plt

x = range(0,30)
b = [random.random() for item in x]
print b
fig = plt.figure() 
plt.scatter(x, b)
plt.tick_params(\
    axis='both',          # changes apply to the x-axis
    which='both',      # both major and minor ticks are affected
    bottom='off',      # ticks along the bottom edge are off
    top='off',         # ticks along the top edge are off
    left = 'off',
    right = 'off',
    labelbottom='off',
    labelleft = 'off') # labels along the bottom edge are off
plt.xlabel('Number of Facebook Friends ("friends")')
plt.ylabel('Number of Granola Bars Purchased (bar)')
plt.title('Facebook Friends vs Granola Bar Purchases')
plt.xlim(-1)
plt.ylim(0)
plt.savefig('Random.png', dpi=100)
plt.show()
plt.close()