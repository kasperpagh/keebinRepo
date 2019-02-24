package entity;

import java.util.Date;
import java.util.List;

/**
 * Created by kaspe on 2016-10-26.
 */

public class Order
{
    private int userId;
    private int coffeeShopId;
    private String platform;
    private List<OrderItem> orderedItemsList;
    private int fullPriceKroner;
    private int fullPriceØre;

    public Order(int userId, int coffeeShop, String platform)
    {
        this.userId = userId;
        this.coffeeShopId = coffeeShop;
        this.platform = platform;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public int getCoffeeShop()
    {
        return coffeeShopId;
    }

    public void setCoffeeShop(int coffeeShop)
    {
        this.coffeeShopId = coffeeShop;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public List<OrderItem> getOrderedItemsList()
    {
        return orderedItemsList;
    }

    public void setOrderedItemsList(List<OrderItem> orderedItemsList)
    {
        this.orderedItemsList = orderedItemsList;
    }

    public int getCoffeeShopId()
    {
        return coffeeShopId;
    }

    public void setCoffeeShopId(int coffeeShopId)
    {
        this.coffeeShopId = coffeeShopId;
    }

    public int getFullPriceKroner()
    {
        return fullPriceKroner;
    }

    public void setFullPriceKroner(int fullPriceKroner)
    {
        this.fullPriceKroner = fullPriceKroner;
    }

    public int getFullPriceØre()
    {
        return fullPriceØre;
    }

    public void setFullPriceØre(int fullPriceØre)
    {
        this.fullPriceØre = fullPriceØre;
    }
}
