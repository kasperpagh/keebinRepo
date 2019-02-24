package entity;

/**
 * Created by kaspe on 2016-10-26.
 */

public class OrderItem
{
    private int priceKroner;
    private int priceOre;
    private String coffeeKindName;
    private int orderItemId;
    private int quantity;
    private boolean isPremiumUsed;
    private int prepaidCardId;
    private int orderId;

    //The parameter formerly know as coffeeKind
    private int menuItemId;

    public OrderItem(int priceKroner, int priceOre, String coffeeKindName)
    {
        this.priceKroner = priceKroner;
        this.priceOre = priceOre;
        this.coffeeKindName = coffeeKindName;
        quantity = 0;
    }

    public OrderItem(int priceKroner, int priceOre, String coffeeKindName, int quantity)
    {
        this.priceKroner = priceKroner;
        this.priceOre = priceOre;
        this.coffeeKindName = coffeeKindName;
        this.quantity = quantity;
    }

    public OrderItem(int priceKroner, int priceOre, String coffeeKindName, int orderItemId, int quantity, boolean isPremiumUsed, int prepaidCardId, int orderId, int menuItemId)
    {
        this.priceKroner = priceKroner;
        this.priceOre = priceOre;
        this.coffeeKindName = coffeeKindName;
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.isPremiumUsed = isPremiumUsed;
        this.prepaidCardId = prepaidCardId;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
    }

    public int getOrderItemId()
    {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId)
    {
        this.orderItemId = orderItemId;
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public boolean isPremiumUsed()
    {
        return isPremiumUsed;
    }

    public void setPremiumUsed(boolean premiumUsed)
    {
        isPremiumUsed = premiumUsed;
    }

    public int getPrepaidCardId()
    {
        return prepaidCardId;
    }

    public void setPrepaidCardId(int prepaidCardId)
    {
        this.prepaidCardId = prepaidCardId;
    }

    public int getMenuItemId()
    {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId)
    {
        this.menuItemId = menuItemId;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public int getPriceKroner()
    {
        return priceKroner;
    }

    public void setPriceKroner(int priceKroner)
    {
        this.priceKroner = priceKroner;
    }

    public int getPriceOre()
    {
        return priceOre;
    }

    public void setPriceOre(int priceOre)
    {
        this.priceOre = priceOre;
    }

    public String getCoffeeKindName()
    {
        return coffeeKindName;
    }

    public void setCoffeeKindName(String coffeeKindName)
    {
        this.coffeeKindName = coffeeKindName;
    }

    @Override
    public String toString()
    {
        return "OrderItem{" +
                "priceKroner=" + priceKroner +
                ", priceOre=" + priceOre +
                ", coffeeKindName='" + coffeeKindName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
