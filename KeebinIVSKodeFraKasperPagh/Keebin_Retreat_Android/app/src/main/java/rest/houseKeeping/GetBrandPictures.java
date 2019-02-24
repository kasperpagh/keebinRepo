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
import entity.BrandPicture;
import entity.CoffeeBrand;
import kasper.pagh.keebin.BrandPictureDelegate;

/**
 * Created by kaspe on 2017-02-09.
 */

public class GetBrandPictures extends AsyncTask<String, Void, String>
{
    private BrandPictureDelegate pd;
    private List<BrandPicture> bmList;
    private List<CoffeeBrand> brandList;
    private DatabaseHandler dbh;
    private String baseUrl;


    public GetBrandPictures(String baseUrl, BrandPictureDelegate pd, Context context)
    {
        this.pd = pd;
        dbh = new DatabaseHandler(context);
        brandList = dbh.getAllCoffeeBrands();
        bmList = new ArrayList<BrandPicture>();
        this.baseUrl = baseUrl;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            Log.d("BrandPics", "kalder getBrandPics");
            for (CoffeeBrand brand : brandList)
            {
                getPic(brand.getBrandName().toLowerCase());
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return "abekat";
    }

    @Override
    protected void onPostExecute(String result)
    {
        Log.d("BrandPics", "returnerer getBrandPics");
        for(BrandPicture bm : bmList)
        {
            dbh.addBrandPicture(bm.getBrandImage(), bm.getBrandName());
        }
        pd.sendBrandPic(bmList);
    }

    public String getPic(String brandName) throws IOException, InterruptedException
    {
        InputStream input = null;

        try
        {
            URL url = new URL(baseUrl + "housekeeping/picture/" + brandName);
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
            BrandPicture bp = new BrandPicture(brandName,bmf.decodeStream(input));
            bmList.add(bp);
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
