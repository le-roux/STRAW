package straw.polito.it.straw.utils;

import android.location.Location;

import java.util.Comparator;

import straw.polito.it.straw.data.Manager;

/**
 * Created by Sylvain on 10/05/2016.
 */
public class DistanceComparator implements Comparator<Manager> {

    private double userLatitude;
    private double userLongitude;

    public DistanceComparator(double userLatitude, double userLongitude) {
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    @Override
    public int compare(Manager lhs, Manager rhs) {
        float[] result = new float[3];
        float distance1, distance2;
        Location.distanceBetween(lhs.getLatitude(), lhs.getLongitude(), this.userLatitude, this.userLongitude, result);
        distance1 = result[0];
        Location.distanceBetween(rhs.getLatitude(), rhs.getLongitude(), this.userLatitude, this.userLongitude, result);
        distance2 = result[0];
        if (distance1 < distance2) {
            return -1;
        } else if (distance1 == distance2) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}