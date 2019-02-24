package rest.userReST;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import DB.DatabaseHandler;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;

/**
 * Created by pelle on 3/29/2017.
 */

public class CreateSubscription extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    private java.lang.String baseUrl;
    private DatabaseHandler dbh;

    //ville anbefale vi lavede backend om til at virke på email og ikke id
    public CreateSubscription(String baseUrl, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.delegate = delegate;
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
        OutputStream output = null;
        URL url = new URL(baseUrl + "users/createPremiumSubscription");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());
        connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());
        output = connection.getOutputStream();
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
