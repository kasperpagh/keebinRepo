package entity;

import android.graphics.Bitmap;

/**
 * Created by kaspe on 2017-02-10.
 */

public class BrandPicture
{
    private String brandName;
    private Bitmap brandImage;

    public BrandPicture(String brandName, Bitmap brandImage)
    {
        this.brandName = brandName;
        this.brandImage = brandImage;
    }

    public String getBrandName()
    {
        return brandName;
    }

    public void setBrandName(String brandName)
    {
        this.brandName = brandName;
    }

    public Bitmap getBrandImage()
    {
        return brandImage;
    }

    public void setBrandImage(Bitmap brandImage)
    {
        this.brandImage = brandImage;
    }


    @Override
    public String toString()
    {
        return "BrandPicture{" +
                "brandName='" + brandName + '\'' +
                '}';
    }
}
