package com.test.service;

/**
 * Created by Administrator on 2018/12/19.
 */
public class TSPLibService {


    public   static  double calculateATTDistance(double lat1, double lon1, double lat2, double lon2) {
        double xd = lat1 - lat2;
        double  yd = lon1 - lon2;
        double rij = Math.sqrt( (xd*xd + yd*yd) / 10.0 );
        double  tij = nint( rij );
        double dij;
        if (tij<rij) dij = tij + 1;
        else dij = tij;
        return dij;
    }


    public static int nint(double x) {
        return (int) (x + 0.5);
    }

}
