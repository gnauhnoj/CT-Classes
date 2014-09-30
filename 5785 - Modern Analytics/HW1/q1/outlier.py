import numpy as np

# MAD-based filter
def median_remove(matrix, col, num):
    list = matrix[:,col]
    d = np.abs(list - np.median(list))
    meddev = np.median(d)
    diff = d/meddev if meddev else 0.
    return matrix[diff<num]

# std-based filter
def mean_remove(matrix, col, num):
    list = matrix[:, col]
    return matrix[abs(list - np.mean(list)) < num * np.std(list)] 

# remove rows with a 0 in the specified column
def remove_zeros(matrix, col):
    list = matrix[:,col]
    return matrix[list > 0]

def post_outlier(data):
    print "outliers adjusted"
    print "number of distances","maximum distance","minimum distance"
    print len(data[:,3]),max(data[:,3]),min(data[:,3]) # distance is measured in miles

def outlier(data, col):
    data = remove_zeros(data, 0)
    # part b
    data = mean_remove(data, col, 1)
    # part e
    # data = median_remove(data, col, 6)
    post_outlier(data)
    return data