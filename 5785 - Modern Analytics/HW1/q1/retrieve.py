#!/usr/ibin/env python.
__author__ = 'jhh283'

import util
import numpy as np
# part a
def retrieve_data(filename):
    total = []
    error_count  = 0

    for line in open(filename, 'r'):
        line = line.strip().split(',')
        plong,plat,dlong,dlat=line[-4:]
        try:
            plong = float(plong)
            plat = float(plat)
            dlong = float(dlong)
            dlat = float(dlat)

            # part b
            distance = util.get_distance(plat,plong,dlat,dlong)

            ptime = float(util.change_time(line[5]))
            ttime = float(line[8])
            tdist = float(line[9])

            total.append([ttime, tdist, ptime, distance, plong, plat, dlong, dlat])
        except:
            error_count += 1

    data = np.asarray(total)
    print "number of distances","maximum distance","minimum distance"
    print len(data[:,3]),max(data[:,3]),min(data[:,3]) # distance is measured in miles
    return data

if __name__ == '__main__':
    retrieve_data('example_data.csv')
