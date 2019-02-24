package rest.coffeeRest;

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

public class PutShopByEmail extends AsyncTask<String, Void, String>
{

    private String coffeeShopEmail;
    private String email;
    private String address;
    private int phone;
    private int brandID;
    public AsyncResponse delegate = null;
    private String baseUrl;
    private Gson gson;

    private DatabaseHandler dbh;

    public PutShopByEmail(String baseUrl, String coffeeShopEmail, String email, int brandID, String address, int phone, AsyncResponse delegate, Context context)
    {
        this.coffeeShopEmail = coffeeShopEmail;
        this.email = email;
        this.brandID = brandID;
        this.address = address;
        this.phone = phone;
        this.delegate = delegate;
        this.baseUrl = baseUrl;
        gson = new Gson();
        dbh = new DatabaseHandler(context);
    }

    private String putShop() throws IOException
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("email", email);
        jo.addProperty("brandID", brandID);
        jo.addProperty("address", address);
        jo.addProperty("phone", phone);

        InputStream input = null;
        OutputStream output = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = null;
        try
        {
            URL url = new URL(baseUrl + "coffee/shop/" + coffeeShopEmail);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            output = connection.getOutputStream();
            output.write(gson.toJson(jo).getBytes("UTF-8"));
            output.close();

            connection.connect();

            input = connection.getInputStream();
            String code = "" + connection.getResponseCode();
            if (code.equalsIgnoreCase("200"))
                ;
            {
                String accessToken = connection.getHeaderField("accessToken");
                if (!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
                {
                    dbh.updateToken("accessToken", accessToken);
                }
            }
            if (code.equalsIgnoreCase("500"))
            {
                ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: Forkert input", "500");
                return error.getErrorMessage();
            }
            if (code.equalsIgnoreCase("401"))
            {
                ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: Manglende rettigheder", "500");
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
            return putShop();
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

}
