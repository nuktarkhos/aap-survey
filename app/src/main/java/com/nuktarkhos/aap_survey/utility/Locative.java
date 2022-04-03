package com.nuktarkhos.aap_survey.utility;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.location.Location;

import java.text.DecimalFormat;

public class Locative {

    private static final DecimalFormat locFormat = new DecimalFormat("000.00000");

    public static String[] gpsToString(Location loc){
        String[] locs = new String[2];

        locs[0] = locFormat.format(loc.getLatitude()) + "N";
        locs[1] = locFormat.format(loc.getLongitude()) + "E";

        return locs;
    }

    public static String gpsToString(String lat, String loc){
        return lat + "N / " + loc + "E";
    }

    static final double DEG = Math.PI/180;
    static final double AIRY1830_A = 6377563.396, AIRY1830_B = 6356256.910;
    static final double WGS84_A = 6378137.000, WGS84_B = 6356752.3141;
    static final double NATGRID_F0 = 0.9996012717, NATGRID_LAT0 = 49*DEG;
    static final double NATGRID_LNG0 = -2*DEG, NATGRID_E0 = 400000, NATGRID_N0 = -100000;

    private static final String[] NATGRID_LETTERS = {"VWXYZ","QRSTU","LMNOP","FGHJK","ABCDE"};

    public static String gpsToNatGridString(Location loc){

        double[] temp = new double[3];

        latLngTo3D(WGS84_A, WGS84_B, loc.getLatitude(), loc.getLongitude(), 53, temp);

        double x = temp[0], y = temp[1], z = temp[2];

        double tx = -446.448;
        double ty = +125.157;
        double tz = -542.060;
        double s =  +20.4894e-6;
        final double sec = Math.PI/180/60/60;
        double rx = -0.1502*sec;
        double ry = -0.2470*sec;
        double rz = -0.8421*sec;

        double xp = tx + (1+s)*x - rz*y + ry*z;
        double yp = ty + (1+s)*y - rx*z + rz*x;
        double zp = tz + (1+s)*z - ry*x + rx*y;

        latLngFrom3D(AIRY1830_A, AIRY1830_B, xp, yp, zp, temp);

        latLngToEastNorth(AIRY1830_A, AIRY1830_B, NATGRID_N0, NATGRID_E0, NATGRID_F0, NATGRID_LAT0, NATGRID_LNG0, temp[0], temp[1], temp);

        return eastNorthToString(temp[0], temp[1], 5);
    }

    private static void latLngTo3D(double a, double b, double lat, double lng, double h, double[] xyzOut) {
        double asq = a*a;
        double bsq = b*b;
        double esq = (asq-bsq)/asq;

        lat = Math.toRadians(lat);
        lng = Math.toRadians(lng);
        double sinlat = sin(lat);
        double coslat = cos(lat);
        double sinlng = sin(lng);
        double coslng = cos(lng);
        double v = a/Math.sqrt(1-esq*sinlat*sinlat);
        xyzOut[0] = (v+h)*coslat*coslng;
        xyzOut[1] = (v+h)*coslat*sinlng;
        xyzOut[2] = ((1-esq)*v+h)*sinlat;
    }

    private static void latLngFrom3D(double a, double b, double x, double y, double z, double[] latLngHeightOut) {
        double asq = a*a;
        double bsq = b*b;
        double esq = (asq-bsq)/asq;

        double lng = Math.atan2(y, x);
        double p = Math.sqrt(x*x+y*y);
        double lat = Math.atan(z/p*(1-esq));

        for (double oldDiff = Double.POSITIVE_INFINITY; /**/ ; /**/) {
            double sinlat = sin(lat);
            double v = a/Math.sqrt(1-esq*sinlat*sinlat);
            double newlat = Math.atan2(z+esq*v*sinlat,p);
            double diff = Math.abs(newlat - lat);
            lat = newlat;

            if (diff >= oldDiff) {
                break;
            }
            oldDiff = diff;
        }

        double sinlat = sin(lat);
        double v = a/Math.sqrt(1-esq*sinlat*sinlat);
        double h = p/ cos(lat)-v;

        latLngHeightOut[0] = Math.toDegrees(lat);
        latLngHeightOut[1] = Math.toDegrees(lng);
        latLngHeightOut[2] = h;
    }

    private static void latLngToEastNorth(double a, double b, double n0, double e0, double f0, double lat0,
                                          double lng0, double lat, double lng, double[] eastNorthOut) {
        double asq = a*a;
        double bsq = b*b;
        double esq = (asq-bsq)/asq;

        lat = Math.toRadians(lat);
        lng = Math.toRadians(lng);

        double sinlat = sin(lat);
        double coslat = cos(lat);
        double tanlat = sinlat/coslat;

        double n = (a-b)/(a+b);
        double v = a*f0/Math.sqrt(1-esq*sinlat*sinlat);
        double p = a*f0*(1-esq)*Math.pow(1-esq*sinlat*sinlat,-1.5);
        double etasq = v/p-1;

        double m = calculateM(b, f0, n, lat0, lat);
        double _I = m + n0;
        double _II = v/2 * sinlat * coslat;
        double _III = v/24 * sinlat * coslat * coslat * coslat * (5-tanlat*tanlat + 9*etasq);
        double _IIIA = v/720 * sinlat * coslat * coslat * coslat * coslat * coslat * (61-58*tanlat*tanlat + tanlat*tanlat*tanlat*tanlat);
        double _IV = v*coslat;
        double _V = v/6 * coslat * coslat * coslat * (v/p - tanlat*tanlat);
        double _VI = v/120 * coslat * coslat * coslat * coslat * coslat * (5-18*tanlat*tanlat + tanlat*tanlat*tanlat*tanlat + 14*etasq - 58*tanlat*tanlat*etasq);
        eastNorthOut[1] = _I + _II*(lng-lng0)*(lng-lng0) + _III*(lng-lng0)*(lng-lng0)*(lng-lng0)*(lng-lng0) + _IIIA*(lng-lng0)*(lng-lng0)*(lng-lng0)*(lng-lng0)*(lng-lng0)*(lng-lng0);
        eastNorthOut[0] = e0 + _IV*(lng-lng0) + _V*(lng-lng0)*(lng-lng0)*(lng-lng0) + _VI*(lng-lng0)*(lng-lng0)*(lng-lng0)*(lng-lng0)*(lng-lng0);
    }

    private static double calculateM(double b, double f0, double n, double lat0, double lat) {
        double m = b*f0*(
                (1 + n + 5.0/4*n*n + 5.0/4*n*n*n)*(lat-lat0)
                        - (3*n + 3*n*n + 21.0/8*n*n*n)*sin(lat-lat0)*cos(lat+lat0)
                        + (15.0/8)*(n*n + n*n*n)*sin(2*(lat-lat0))*cos(2*(lat+lat0))
                        - 35.0/24*n*n*n*sin(3*(lat-lat0))*cos(3*(lat+lat0))
        );
        return m;
    }

    private static String eastNorthToString(double x, double y, int digits)
    {
        int e = (int)x;
        int n = (int)y;

        if (digits < 0) {
            return e + "," + n;
        }

        if (e < 0 || n < 0) { return null; }

        String ret = "";

        int big = 500000;
        int small = big/5;
        int firstdig = small/10;

        int es = e/big;
        int ns = n/big;
        e = e % big;
        n = n % big;

        es += 2;
        ns += 1;
        if (es > 4 || ns > 4) { return null; }
        ret = ret + NATGRID_LETTERS[ns].charAt(es);

        es = e/small;
        ns = n/small;
        e = e % small;
        n = n % small;
        ret= ret + NATGRID_LETTERS[ns].charAt(es);

        // Only add spaces if there are digits too. This lets us have "zero-figure" grid references, e.g. "SK"
        if (digits > 0)
        {
            ret += ' ';

            for (int dig = firstdig, i = 0; dig != 0 && i < digits; i++, dig /= 10) {
                ret += (e/dig%10);
            }

            ret += ' ';

            for (int dig = firstdig, i = 0; dig != 0 && i < digits; i++, dig /= 10) {
                ret += (n/dig%10);
            }
        }

        return ret;
    }
}
