################################################################################
#   File: titanic.py
#   HW 1, CS5785
#
#   Purpose: Predict survival in Titanic disaster through logistic regression.
#
################################################################################

from matplotlib import *
from sklearn import preprocessing
from sklearn.linear_model import LogisticRegression as logreg
from sklearn.cross_validation import train_test_split
import numpy as np
import ma_disputils as disp
import csv

VARS = ['passengerid','survived','pclass','name','sex','age','sibsp','parch',
    'ticket','fare','cabin','embarked']
features = ['pclass','sex','age','sibsp','parch','fare']

def load(in_file):
    """
    Takes csv file name as string, returns tuple of list of header row and data
    as list of lists (header, data).
    """
    f = csv.reader(open(in_file, 'rb'))  
    header = f.next()
    return header, np.array([row for row in f])


def split_targets(raw, tcol):
    """
    Given input array of data with 'Survived' column, return tuple of
    data without survived column and survived column as a separate vector.
    """
    return np.hstack((raw[:,:tcol], raw[:,tcol + 1:])), raw[:,tcol]

def preprocess(data, keys, features, booleanfam = False, 
        bin_all = False, bin_age = False, bin_pclass = False):
    """
    Get array of data (with 'survived' column removed), list of keys, and list
    of desired features; return array of only those features as floats.  Recipe
    for each feature may be different.  There's a lot of redundant code in here,
    but I didn't want to pass more copies of arrays and it didn't actually save
    much code when I tried writing a 'get column' function, since the processing
    varies by feature.
    """
    faremax = 40.
        
    N = data.shape[0]

    #Start with Nx1 matrix of passenger ids
    result = np.array(data[:,0].reshape(N,1)).astype(np.float)
        
    if 'pclass' in features:
        if (bin_all or bin_pclass):
            result = np.append(result,
                (data[:,keys.index('pclass')] != '3').astype(np.float).reshape(N,1),
                1)
        else:
            result = np.append(result,
                data[:,keys.index('pclass')].astype(np.float).reshape(N,1),
                1)
    
    if 'sex' in features:
        result = np.append(result,
            (data[:,keys.index('sex')] == 'male').astype(np.float).reshape(N,1),
            1)
            
    if 'age' in features:
        ages = data[:, keys.index('age')]
        if (bin_all or bin_age):
            ages[ages == ''] = 0.
            ages = ages.astype(np.float)
            result = np.append(result,
                (ages < 15).reshape(N,1),
                1)
        else:
            ages[ages == ''] = np.nan
            result = np.append(result,
                ages.astype(np.float).reshape(N,1),
                1)
    
    if booleanfam:
        family = ((data[:,keys.index('sibsp')].astype(np.int) +
                    data[:,keys.index('parch')].astype(np.int)) != 0)
        result = np.append(result,
                    family.astype(np.float).reshape(N,1),
                    1)
    else:
        if 'sibsp' in features:
            if bin_all:
                result = np.append(result,
                    (data[:,keys.index('sibsp')] != '0').astype(np.float).reshape(N,1),
                    1)
            else:
                result = np.append(result,
                    data[:,keys.index('sibsp')].astype(np.float).reshape(N,1),
                    1)
    
        if 'parch' in features:
            if bin_all:
                result = np.append(result,
                    (data[:,keys.index('parch')] != '0').astype(np.float).reshape(N,1),
                    1)
            else:
                result = np.append(result,
                    data[:,keys.index('parch')].astype(np.float).reshape(N,1),
                    1)
            
    if 'fare' in features:
        fares = data[:, keys.index('fare')]
        fares[fares == ''] = np.nan
        fares = fares.astype(np.float)
        fares[fares > faremax] = faremax
        result = np.append(result,
            fares.astype(np.float).reshape(N,1),
            1)

    if 'cabin' in features:
        result = np.append(result,
            (data[:,keys.index('cabin')] != '').astype(np.float).reshape(N,1),
            1)
            
    imp = preprocessing.Imputer(missing_values = 'NaN', strategy = 'median', axis = 0)
    imp.fit(result)
    print result
    print 'N samples after preprocessing: ' + str(result.shape[0])
    return imp.transform(result)

def save_result(passengers, predictions, filename):
    """
    Write result to CSV file.
    """
    # Get num passengers
    N = passengers.shape[0]
    if predictions.shape[0] != N:
        raise ValueError('Mismatch in vector length')
        
    f = csv.writer(open(filename, 'wb'))
    f.writerow(["PassengerId", "Survived"])
    
    for i in range(0,N):
        f.writerow([passengers[i], str(predictions[i])])
    
def main():
    # set features here
    features = ['pclass', 'sex', 'age', 'parch', 'sibsp']
    # set True to remove entries with no age data from training set, false
    # otherwise.
    remove_ageless = False
    
    h, train = load('train.csv')

    if remove_ageless:
        train = train[train[:,h.index('Age')]!='']
        
    raw_x, raw_t = split_targets(train, h.index('Survived'))
    
    h, test = load('test.csv')
    # get array of keys; won't include 'survived'
    keys = [key.lower() for key in h]

    X = preprocess(raw_x, keys, features, bin_age = True, bin_pclass = True)
    T = raw_t.reshape(raw_t.shape[0], 1).astype(np.float)
    
    lr = logreg()
    lr.fit(X[:,1:],T)
    print 'Coef: ' + str(lr.coef_)
    
    result = lr.predict(preprocess(test, keys, features, bin_age = True, bin_pclass = True)[:,1:])
    
    save_result(test[:,0], result.astype(np.int),'predict.csv')    
    
    
if __name__ == "__main__":
    main()