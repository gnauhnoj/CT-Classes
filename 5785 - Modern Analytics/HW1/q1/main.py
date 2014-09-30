import numpy as np
import retrieve 
import outlier
import util

if __name__ == '__main__':
  ## initial data retrieval (parts a - f)
  # parts a-f: retrieve data
  data = retrieve.retrieve_data('example_data.csv')
  # part c: filter outliers
  data = outlier.outlier(data, 3)

  ## split training and test, remove outliers if new data
  # part d
  training = outlier.outlier(data[3::4], 3)
  test = outlier.outlier(np.delete(data, np.s_[3::4], axis=0), 3)
  # part f
  # training = outlier.outlier(data, 3)
  # test = outlier.outlier(retrieve.retrieve_data('trip_data_2.csv'), 3)
  # part i
  # training = outlier.outlier(retrieve.retrieve_data('trip_data_1.csv'), 3)
  # test = outlier.outlier(retrieve.retrieve_data('trip_data_2.csv'), 3)

  ## normalize the  matrix
  # part h - don't normalize train 2 (pickup time) bc of the binary modification
  for count in range(0, 8):
    if count != 2:
      training = util.normalize(training, count)
      test = util.normalize(test, count)

  ## plot the graphs
  # part c
  # axis dictionary, plot
  # import plot
  # axeslabels = ['Trip Time (sec)', 'Trip Distance (mi)', 'Pickup Time (hours)', 'Distance between Pickup & Dropoff (mi)']
  # plot.plot_data(data, axeslabels)

  ## separating independent variables 
  # part d & f
  xtrain = training[:,1]
  xtest = test[:,1]
  # part i
  # xtrain = np.delete(training, 0, axis=1)
  # xtest = np.delete(test, 0, axis=1)

  ## separating dependent variable
  ytrain = training[:,0]
  ytest = test[:,0]

  ## calculate linear regression
  w = util.linReg(xtrain, ytrain)

  ## calculate CC, OLS, TLS
  xtest_ones = util.append_ones(xtest)
  yHat = xtest_ones * w
  cc = np.corrcoef(yHat.T, ytest)
  print "least sqs fit: ", w
  print "cc: ", cc[0][1]
  print "OLS: ", util.OLS(ytest, yHat)
  print "TLS: ", util.TLS(xtest, ytest, xtest_ones, w)
