package entity;

import java.util.List;

/**
 * Created by kaspe on 2016-10-26.
 */

public class CoffeeShop
{

    private int id;
    private String email;
    private String address;
    private String phone; //Needs to be a String since someone is gonna be dumb enough to give the number as: +45 whatever
    private String coffeeCode;
    private double longitude;
    private double latitude;
    private int brandName;
    private String actualBrandName;



    public CoffeeShop(String email, String address, String phone, String coffeeCode, double longitude, double latitude, int brandName)
    {
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.coffeeCode = coffeeCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.brandName = brandName;
    }

    public CoffeeShop(int id, String email, String address, String phone, String coffeeCode, double longitude, double latitude, String actualBrandName, int brandName)
    {
        this.id = id;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.coffeeCode = coffeeCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.actualBrandName = actualBrandName;
        this.brandName = brandName;
    }

    public String getActualBrandName()
    {
        return actualBrandName;
    }

    public void setActualBrandName(String actualBrandName)
    {
        this.actualBrandName = actualBrandName;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getCoffeeCode()
    {
        return coffeeCode;
    }

    public void setCoffeeCode(String coffeeCode)
    {
        this.coffeeCode = coffeeCode;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public int getBrandName()
    {
        return brandName;
    }

    public void setBrandName(int brandName)
    {
        this.brandName = brandName;
    }

    @Override
    public String toString()
    {
        return "CoffeeShop{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", coffeeCode='" + coffeeCode + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", brandName=" + brandName +
                ", actualBrandName='" + actualBrandName + '\'' +
                '}';
    }
}
