package rest.coffeeRest;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;

/**
 * Created by kaspe on 2016-10-29.
 */

public class GetAllShops extends AsyncTask<String, Void, String>
{
    public AsyncResponse delegate = null;
    private String baseUrl;
    private Gson gson = new Gson();
    private DatabaseHandler dbh;

    public GetAllShops(String baseUrl, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.delegate = delegate;
        dbh = new DatabaseHandler(context);
    }


    private String getShops() throws IOException
    {
        InputStream input = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = null;

        try
        {
            URL url = new URL(baseUrl + "coffee/allshops/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());
            connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());

            connection.connect();

            input = connection.getInputStream();

            String code = "" + connection.getResponseCode();
            if (code.equalsIgnoreCase("200"))
            {
                String accessToken = connection.getHeaderField("accessToken");
                if (!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
                {
                    dbh.updateToken("accessToken", accessToken);
                }

            }
            if (code.equalsIgnoreCase("500"))
            {
                ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: Der er ingen shops", "500");
                return error.getErrorMessage();
            }

            bufferedReader = new BufferedReader(new InputStreamReader(input));
            sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            bufferedReader.close();
            return sb.toString();
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
        }
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getShops();
        }
        catch (IOException e)
        {
            ApiErrorMessage error = new ApiErrorMessage("brand", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }

}
