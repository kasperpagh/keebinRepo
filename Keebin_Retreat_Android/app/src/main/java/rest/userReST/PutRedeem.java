package rest.userReST;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import DB.DatabaseHandler;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;

import android.content.Context;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;

/**
 * Created by kaspe on 2016-10-29.
 */

public class PutRedeem  extends AsyncTask<String, Void, String>
{

    private int userId;
    private int loyaltyCardId;
    private int numberOfCoffeeRedeems;
    public AsyncResponse delegate = null;
    private String baseUrl;
    private Gson gson;
    private DatabaseHandler dbh;

    public PutRedeem(String baseUrl,int loyaltyCardId,int userId,int numberOfCoffeeRedeems, AsyncResponse delegate, Context context)
    {
        this.userId = userId;
        this.numberOfCoffeeRedeems = numberOfCoffeeRedeems;
        this.loyaltyCardId = loyaltyCardId;
        this.delegate = delegate;
        this.baseUrl = baseUrl;
        gson = new Gson();
        dbh = new DatabaseHandler(context);
    }


    private String putLoyaltyCard() throws IOException
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("userId", userId);
        jo.addProperty("numberOfCoffeeRedeems", numberOfCoffeeRedeems);
        InputStream input = null;
        OutputStream output = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = null;

        try
        {
            URL url = new URL(baseUrl + "users/cardRedeem/" + loyaltyCardId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());
            connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());
            output = connection.getOutputStream();
            output.write(gson.toJson(jo).getBytes("UTF-8"));
            output.close();

            connection.connect();

            input = connection.getInputStream();
            String code = "" +connection.getResponseCode();
            if(code.equalsIgnoreCase("200"));
            {
                String accessToken = connection.getHeaderField("accessToken");
                if(!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
                {
                    dbh.updateToken("accessToken", accessToken);
                }
            }
            if(code.equalsIgnoreCase("500"))
            {
                ApiErrorMessage error = new ApiErrorMessage("loyaltyCard", "Fejl: Forkert input", "500");
                return error.getErrorMessage();
            }
            if(code.equalsIgnoreCase("401"))
            {
                ApiErrorMessage error = new ApiErrorMessage("loyaltyCard", "Fejl: Manglende rettigheder", "401");
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
        } finally
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
            return putLoyaltyCard();
        } catch (IOException e)
        {
            ApiErrorMessage error = new ApiErrorMessage("loyaltyCard", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }

}
