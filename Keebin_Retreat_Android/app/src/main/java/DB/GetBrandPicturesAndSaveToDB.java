package DB;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entity.BrandPicture;
import entity.CoffeeShop;
import entity.ShopPicture;
import kasper.pagh.keebin.AsyncResponse;
import kasper.pagh.keebin.DBListener;
import kasper.pagh.keebin.BrandPictureDelegate;
import kasper.pagh.keebin.R;
import kasper.pagh.keebin.ShopPictureDelegate;
import rest.coffeeRest.GetAllShops;
import rest.houseKeeping.GetBrandPictures;
import rest.houseKeeping.GetShopPictures;

/**
 * Created by kaspe on 2017-02-15.
 */

public class GetBrandPicturesAndSaveToDB implements BrandPictureDelegate, ShopPictureDelegate, DBListener, AsyncResponse
{
    private DatabaseHandler dbh;
    private Context context;
    private DBListener dbListener;

    public GetBrandPicturesAndSaveToDB(Context context, DBListener dbListener)
    {
        this.context = context;
        dbh = new DatabaseHandler(context);
        this.dbListener = dbListener;
    }

    public void saveBrandPictures()
    {

    }


    @Override
    public void sendBrandPic(List<BrandPicture> bmList)
    {
        try
        {
            for (BrandPicture bm : bmList)
            {
                try
                {
                    dbh.updateBrandPicture(bm.getBrandImage(), bm.getBrandName());

                }
                catch (android.database.CursorIndexOutOfBoundsException e)
                {
                    dbh.addBrandPicture(bm.getBrandImage(), bm.getBrandName());
                }
            }
        }
        finally
        {
            GetAllShops gas = new GetAllShops(context.getResources().getString(R.string.baseUrl), this, context);
            gas.execute();
        }


    }


    @Override
    public void DBReady()
    {
        GetBrandPictures gp = new GetBrandPictures(context.getResources().getString(R.string.baseUrl), this, context);
        gp.execute();
    }


    @Override
    public void sendShopPic(List<ShopPicture> bmList)
    {
        for (ShopPicture bm : bmList)
        {
            dbh.addShopPicture(bm.getShopImage(), bm.getShopEmail().toLowerCase());
        }

        dbListener.DBReady();

    }

    @Override
    public void processFinished(String output)
    {
        Gson gson = new Gson();
        CoffeeShop[] shops = gson.fromJson(output, CoffeeShop[].class);
        List<CoffeeShop> shopList = Arrays.asList(shops);
        GetShopPictures gsp = new GetShopPictures(context.getResources().getString(R.string.baseUrl), shopList, this, context);
        gsp.execute();
    }
}
