package com.crossal.recordreader.utils;

public class MathUtil {
//    public double getGreatCircleDistanceInKms(double latA, double lngA, double latB, double lngB) {
//        double pk = Math.PI / 180;
//
//        double latARadians = latA * pk;
//        double lngARadians = lngA * pk;
//        double latBRadians = latB * pk;
//        double lngBRadians = lngB * pk;
//
//        double dL = (lngA-lngB) / pk;
//
//        double t1 = Math.sin(latARadians) * Math.sin(latBRadians);
//        double t2 = Math.cos(latARadians) * Math.cos(latBRadians) * Math.cos(dL);
//
//        double tt = Math.acos(t1 + t2);
//    }

    public static double distanceInKms(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;

            return (dist);
        }
    }

    public static boolean distanceWithinKms(double lat1, double lon1, double lat2, double lon2, int distance) {
        return distanceInKms(lat1, lon1, lat2, lon2) < distance;
    }
}
