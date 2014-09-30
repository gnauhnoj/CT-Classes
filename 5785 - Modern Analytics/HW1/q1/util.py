import time
import math
import numpy as np

# append a column of ones to the matrix
def append_ones(matrix):
  return np.column_stack((np.ones(len(matrix)), matrix))

# distance between point and line defined by line1, line2 in space
def point_line_dist_square(point, line1, line2):
  point = np.mat(point)
  line1 = np.mat(line1)
  line2 = np.mat(line2)

  l1_p = line1 - point
  l2_l1 = line2 - line1
  n_l1_p = np.linalg.norm(l1_p)
  n_l2_l1 = np.linalg.norm(l2_l1)

  dot = np.dot(l1_p, l2_l1.T)
  num = (math.pow(n_l1_p,2) * math.pow(n_l2_l1, 2)) - math.pow(dot, 2)
  den = math.pow(n_l2_l1, 2)
  return  num / den

# part g
def day_night(time):
  # day is classified as 0-12 hrs
  if (time >= 0. and time < 12.):
    return 1.
  else:
    return 0.

# part h
def normalize(matrix, col):
  # normalization with respect to the sample mean and deviation
  list = matrix[:, col]
  matrix[:, col] = list - np.mean(list) / np.std(list) 
  return matrix

# part d
def linReg(x, y):
  x = append_ones(x)
  x = np.mat(x)
  y = np.mat(y).T
  xTx = x.T * x
  if np.linalg.det(xTx) == 0.0:
    return
  w = xTx.I * (x.T*y)
  return w

# part g
def change_time(datestring):
  # change datetime to time of day in hours
  date = time.strptime(datestring, "%Y-%m-%d %H:%M:%S")
  total = float(date[3]) + float(date[4]) / 60 + float(date[5]) / 60 / 60
  # change time to day/night classification
  total = day_night(total)
  return total

# part d
def OLS(ytest, yhat):
  return math.pow(np.linalg.norm(ytest - yhat.T),2)

# part d
def TLS(xtest, ytest, xtest_ones, w):
  # build a vector representing the minimum of each independent coefficient
  y1 = [1]
  y2 = [1]
  if hasattr(xtest[0], "__len__"):
    max = len(xtest[0])
  else: 
    max = 1

  for i in range(1, max+1):
    column = xtest_ones[:,i]
    minInd = np.argmin(column)
    y1.append(column[minInd])
    maxInd = np.argmax(column)
    y2.append(column[maxInd])

  #calculate minimum and maximum points on the regression line
  dot1 = np.asarray(y1) * w
  dot2 = np.asarray(y2) * w
  y1.pop(0)
  y2.pop(0)
  y1.append(dot1.item(0))
  y2.append(dot2.item(0))
  vector1 = y1
  vector2 = y2

  # calculate/sum TLS
  TLS = 0
  for a, b in zip(xtest, ytest):
    # this is not robust
    if hasattr(a, "__len__"):
      a = a.tolist()
      a.append(b)
      point = a
    else:
      point = [a, b]
    TLS += point_line_dist_square(point, vector1, vector2)
  return TLS

# part b
def get_distance(lat1, long1, lat2, long2):
    # Convert latitude and longitude to
    # spherical coordinates in radians.
    degrees_to_radians = math.pi/180.0

    # phi = 90 - latitude
    phi1 = (90.0 - lat1)*degrees_to_radians
    phi2 = (90.0 - lat2)*degrees_to_radians

    # theta = longitude
    theta1 = long1*degrees_to_radians
    theta2 = long2*degrees_to_radians

    # Compute spherical distance from spherical coordinates.

    # For two locations in spherical coordinates
    # (1, theta, phi) and (1, theta, phi)
    # cosine( arc length ) =
    #    sin phi sin phi' cos(theta-theta') + cos phi cos phi'
    # distance = rho * arc length

    cos = (math.sin(phi1)*math.sin(phi2)*math.cos(theta1 - theta2) +
           math.cos(phi1)*math.cos(phi2))
    arc = math.acos( cos )

    # Remember to multiply arc by the radius of the earth
    # in your favorite set of units to get length.
    # MODIFIED TO return distance in miles
    return arc*3960.0