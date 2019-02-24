package entity;

import android.graphics.Bitmap;

/**
 * Created by kaspe on 08-03-2017.
 */

public class ShopPicture
{
    private String shopEmail;
    private Bitmap shopImage;

    public ShopPicture(String shopEmail, Bitmap shopImage)
    {
        this.shopEmail = shopEmail;
        this.shopImage = shopImage;
    }

    public String getShopEmail()
    {
        return shopEmail;
    }

    public void setShopEmail(String shopEmail)
    {
        this.shopEmail = shopEmail;
    }

    public Bitmap getShopImage()
    {
        return shopImage;
    }

    public void setShopImage(Bitmap shopImage)
    {
        this.shopImage = shopImage;
    }

    @Override
    public String toString()
    {
        return "ShopPicture{" +
                "shopEmail='" + shopEmail + '\'' +
                ", shopImage=" + shopImage +
                '}';
    }
}
