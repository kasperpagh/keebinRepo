package rest.houseKeeping;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import DB.DatabaseHandler;
import entity.BrandPicture;
import entity.CoffeeBrand;
import kasper.pagh.keebin.AsyncResponse;
import kasper.pagh.keebin.BrandPictureDelegate;

/**
 * Created by kaspe on 24-04-2017.
 */

public class GetDBVersion extends AsyncTask<String, Void, String>
{

    private AsyncResponse delegate;
    private DatabaseHandler dbh;
    private String baseUrl;


    public GetDBVersion(String baseUrl, Context context, AsyncResponse delegate)
    {
        dbh = new DatabaseHandler(context);
        this.delegate = delegate;
        this.baseUrl = baseUrl;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            Log.d("DBVERS", "kalder getDB");
            return getDBVersion();
        } catch (IOException e)
        {
            e.printStackTrace();
            return "Fejl";
        } catch (InterruptedException e)
        {
            e.printStackTrace();
            return "Fejl";
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        Log.d("DBVERS", "returns DBVers");
        delegate.processFinished(result);
    }

    public String getDBVersion() throws IOException, InterruptedException
    {
        InputStream input = null;
        BufferedReader bufferedReader;
        StringBuilder sb;

        try
        {
            URL url = new URL(baseUrl + "users/getdbversion");


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
            bufferedReader = new BufferedReader(new InputStreamReader(input));
            sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            bufferedReader.close();
            return sb.toString();
        } finally
        {
            if (input != null)
            {
                input.close();
            }
        }
    }
}