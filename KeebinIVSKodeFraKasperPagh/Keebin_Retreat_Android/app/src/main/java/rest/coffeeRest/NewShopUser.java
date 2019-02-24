package rest.coffeeRest;

import android.content.Context;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;

/**
 * Created by kaspe on 2016-10-29.
 */

public class NewShopUser extends AsyncTask<String, Void, String>
{
    public AsyncResponse delegate = null;
    private String baseUrl;
    private String userEmail;
    private String coffeeShopEmail;
    private Gson gson;
    private DatabaseHandler dbh;

    public NewShopUser(String baseUrl, String userEmail, String coffeeShopEmail, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        this.userEmail = userEmail;
        this.coffeeShopEmail = coffeeShopEmail;
        this.delegate = delegate;
        dbh = new DatabaseHandler(context);

    }
    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return newShopUser();
        } catch (IOException e)
        {
            ApiErrorMessage error = new ApiErrorMessage("shopUser", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }
    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }


    private String newShopUser() throws IOException
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("userEmail", userEmail);
        jo.addProperty("coffeeShopEmail", coffeeShopEmail);
        OutputStream output = null;

        URL url = new URL(baseUrl + "coffee/shopuser/new");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        output = connection.getOutputStream();
        output.write(gson.toJson(jo).getBytes("UTF-8"));
        output.close();

        connection.connect();
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
            ApiErrorMessage error = new ApiErrorMessage("shopUser", "Fejl: brugeren eller kaffebaren findes ikke!", "500");
            return error.getErrorMessage();
        }
        return code;
    }
}
