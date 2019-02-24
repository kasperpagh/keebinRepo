package entity;

/**
 * Created by kaspe on 2017-02-01.
 */

public class UserLocation
{
    private int userId;
    private double lat;
    private double lng;

    public UserLocation(int userId, double lat, double lng)
    {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
    }

    public UserLocation(double lat, double lng)
    {
        this.lat = lat;
        this.lng = lng;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return lng;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
    }

    @Override
    public String toString()
    {
        return "UserLocation{" +
                "userId=" + userId +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
