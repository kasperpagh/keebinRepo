package utils.locationAndDistance;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import entity.CoffeeShop;
import entity.UserLocation;

/**
 * Created by kaspe on 2017-02-03.
 */

/*
Takes a list of CoffeeShop objects and the location of a user, sorts the collection and then returns a new one, with the closets shop as index 0
 */
public class DistanceSorter
{
    public static ArrayList<CoffeeShop> sortCoffeeList(final UserLocation userLocation, List<CoffeeShop> shops)
    {
        try
        {
            Collections.sort(shops, new Comparator<CoffeeShop>()
            {
                @Override
                public int compare(CoffeeShop o1, CoffeeShop o2)
                {
                    Float x1 = DistanceCalculator.getDistanceToCoffeeShop(userLocation.getLat(), userLocation.getLng(), o1.getLatitude(), o1.getLongitude());
                    Float x2 = DistanceCalculator.getDistanceToCoffeeShop(userLocation.getLat(), userLocation.getLng(), o2.getLatitude(), o2.getLongitude());
                    int comp = x1.compareTo(x2);
                    return comp;
                }
            });
        } catch (Exception e)
        {
        }

        ArrayList<CoffeeShop> list = new ArrayList<>(shops);

        return list;

    }
}
