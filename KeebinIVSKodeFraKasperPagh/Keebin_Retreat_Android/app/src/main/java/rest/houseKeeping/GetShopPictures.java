package rest.houseKeeping;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import DB.DatabaseHandler;
import entity.CoffeeShop;
import entity.ShopPicture;
import kasper.pagh.keebin.ShopPictureDelegate;

/**
 * Created by kaspe on 08-03-2017.
 */

public class GetShopPictures extends AsyncTask<String, List<ShopPicture>, List<ShopPicture>>
{
    private ShopPictureDelegate pd;
    private List<ShopPicture> result;
    private List<CoffeeShop> shopList;
    private DatabaseHandler dbh;
    private String baseUrl;


    public GetShopPictures(String baseUrl, List<CoffeeShop> shopList, ShopPictureDelegate pd, Context context)
    {
        this.pd = pd;
        dbh = new DatabaseHandler(context);
        this.shopList = shopList;
        result = new ArrayList<ShopPicture>();
        this.baseUrl = baseUrl;
    }

    @Override
    protected List<ShopPicture> doInBackground(String... params)
    {
        try
        {
            for (CoffeeShop shop : shopList)
            {
                getPic(shop.getEmail().toLowerCase());
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return result;
        }

    }

    @Override
    protected void onPostExecute(List<ShopPicture> outcome)
    {
        pd.sendShopPic(outcome);
    }

    public String getPic(String shopEmail) throws IOException, InterruptedException
    {
        InputStream input = null;

        try
        {
            URL url = new URL(baseUrl + "housekeeping/image/" + shopEmail);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());
            connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());

            connection.connect();
            String code = "" +connection.getResponseCode();
            if(code.equalsIgnoreCase("200"));
            {
                String refreshToken = connection.getHeaderField("refreshToken");
                String accessToken = connection.getHeaderField("accessToken");
                try
                {
                    if(!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
                    {
                        dbh.updateToken("accessToken", accessToken);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            input = connection.getInputStream();
            BitmapFactory bmf = new BitmapFactory();
            ShopPicture sp = new ShopPicture(shopEmail,bmf.decodeStream(input));
            result.add(sp);
            if (connection.getResponseCode() == 200)
            {
                return "ok";

            } else
            {
                return "fejl";
            }
        } finally
        {
            if (input != null)
            {
                input.close();
            }
        }
    }
}
