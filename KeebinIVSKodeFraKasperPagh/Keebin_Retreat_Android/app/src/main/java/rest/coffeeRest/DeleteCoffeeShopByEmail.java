package rest.coffeeRest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;


public class DeleteCoffeeShopByEmail extends AsyncTask<String, Void, String>
{
    private String userEmail;
    public AsyncResponse delegate = null;
    private String baseUrl;
    private DatabaseHandler dbh;

    public DeleteCoffeeShopByEmail(String baseUrl, String userEmail, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.userEmail = userEmail;
        this.delegate = delegate;
        dbh = new DatabaseHandler(context);
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return deleteShop();
        } catch (IOException e)
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

    private String deleteShop() throws IOException
    {
        URL url = new URL(baseUrl + "coffee/shop/" + userEmail);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("DELETE");
        connection.connect();
        String code = "" + connection.getResponseCode();
        if (code.equalsIgnoreCase("200"))
            ;
        {
            String accessToken = connection.getHeaderField("accessToken");
            if (!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
            {
                dbh.updateToken("accessToken", accessToken);
                Log.d("accessToken", " er opdateret til " + accessToken);
            }

        }
        if (code.equalsIgnoreCase("500"))
        {
            ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: Der er ingen kaffebarer der matcher den givne email", "500");
            return error.getErrorMessage();
        }
        if (code.equalsIgnoreCase("401"))
        {
            ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: Manglende rettigheder", "500");
            return error.getErrorMessage();
        }
        return code;
    }
}
