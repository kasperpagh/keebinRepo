package rest.coffeeRest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import DB.DatabaseHandler;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;



public class NewPrepaidCardRest extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    private java.lang.String baseUrl;
    private Gson gson;
    private int userId;
    private String coffeeCode;
    private int storeCardId;
    private DatabaseHandler dbh;

    public NewPrepaidCardRest(String baseUrl, String coffeeCode , int userId, int storeCardId, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        this.coffeeCode = coffeeCode;
        this.userId = userId;
        this.delegate = delegate;
        this.storeCardId = storeCardId;
        dbh = new DatabaseHandler(context);
    };

    @Override
    protected java.lang.String doInBackground(java.lang.String... params)
    {
        try
        {
            return newLoyaltyCard();
        } catch (IOException e)
        {
            ApiErrorMessage error = new ApiErrorMessage("loyaltyCard", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }

    @Override
    protected void onPostExecute(java.lang.String result)
    {
        delegate.processFinished(result);
    }


    private String newLoyaltyCard() throws IOException
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("userId", userId);
        jo.addProperty("storeCardId", storeCardId);
        jo.addProperty("coffeeCode", coffeeCode);
        Log.d("her er bruger input:!","userid: " + userId + "  storeCard: " + storeCardId + "  coffeeCode: " + coffeeCode);
        OutputStream output = null;
        URL url = new URL(baseUrl + "coffee/klippekort/new");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());
        connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());
        output = connection.getOutputStream();
        output.write(gson.toJson(jo).getBytes("UTF-8"));
        output.close();

        connection.connect();

        java.lang.String code = connection.getResponseCode() + "";
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
        return code;
    }
}
