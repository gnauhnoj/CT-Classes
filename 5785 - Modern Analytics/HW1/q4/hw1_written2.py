################################################################################
#   File: hw1_written2.py
#   HW 1, CS5785
#   Neil Lakin
#
#   Purpose: Draw some plots for homework 1, written exercise 2.
#
################################################################################

import numpy as np
from matplotlib import pyplot as plt
from sklearn import linear_model as lm

def main():

    fig = plt.figure()
    ax = fig.add_subplot(1,1,1)
    
    # Get set of points between 0 and 100
    x = np.arange(0,50,.5)
    
    # Make an 'error' vector of random values between -3 and 3
    r = np.random.uniform(-3,3,x.shape[0])
    
    # Make y values by calculating y = 2x
    y = 2*x + r

    # replace some point with an outlier
    y[5]=100
    
    #plot them
    ax.scatter(x,y, color = 'black', s = 4)
    
    # draw least squares regression
    lsr = lm.LinearRegression()
    lsr.fit(x.reshape(x.shape[0],1), y)
    ax.plot(x, lsr.predict(x.reshape(x.shape[0],1)), color = 'blue', 
        linewidth = 3)
    
    fig.suptitle('Nearly-linear data with one outlier')
    fig.show()
    
    figb = plt.figure()
    axb = figb.add_subplot(1,2,1)
    axb2 = figb.add_subplot(1,2,2)
    
    xb = np.array([1, 1, 2, 3, 3])
    yb = np.array([2, 0, 1, 2, 0])

    axb.scatter(xb, yb, color = 'black', s = 5)
    axb2.scatter(xb, yb, color = 'black', s = 5)
    axb2.plot([1,3], [0,2], color = 'red', linewidth = 1)
    axb2.plot([1,3], [2,0], color = 'red', linewidth = 1)
    lsrb = lm.LinearRegression()
    lsrb.fit(xb.reshape(5,1), yb)
    axb2.plot(xb, lsrb.predict(xb.reshape(5,1)), color = 'blue',
        linewidth = 1)
    
    figb.suptitle('Single L2 solution, multiple L1 solutions')
    figb.show()
    
if __name__ == '__main__':
    main()