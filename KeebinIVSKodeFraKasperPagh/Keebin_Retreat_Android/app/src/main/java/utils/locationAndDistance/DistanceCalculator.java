package utils.locationAndDistance;

import android.location.Location;

/**
 * Created by kaspe on 2017-02-02.
 */
public class DistanceCalculator
{
    public static float getDistanceToCoffeeShop(double userLat, double userLng, double shopLat, double shopLng)
    {
        Location userLocation = new Location("");
        Location shopLocation = new Location("");
        userLocation.setLatitude(userLat);
        userLocation.setLongitude(userLng);
        shopLocation.setLatitude(shopLat);
        shopLocation.setLongitude(shopLng);
        float distance = shopLocation.distanceTo(userLocation);
        //returns the distance between the shop and the user in Meters
        return Math.round(shopLocation.distanceTo(userLocation));
    }
    public static String stringifyDistance(float distance)
    {
        if (distance > 2000)
        {
            //inherently inaccurate because floats are strange!
            //converts results above 2000 to kilometers
            return Math.round((distance / 1000) * 10f) / 10f + " km";
        } else
        {
            return distance + " m";
        }
    }
}

