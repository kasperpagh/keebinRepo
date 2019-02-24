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

import DB.DatabaseHandler;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;



public class UsePrepaidCard extends AsyncTask<String, Void, String>
{
    private int userId;
    private int prePaidCardId;
    private int purhcasedAmount;
    public AsyncResponse delegate = null;
    private String baseUrl;
    private Gson gson;
    private DatabaseHandler dbh;

    public UsePrepaidCard(String baseUrl, int userId, int prePaidCardId, int purhcasedAmount, AsyncResponse delegate, Context context)
    {
        this.userId = userId;
        this.prePaidCardId = prePaidCardId;
        this.purhcasedAmount = purhcasedAmount;
        this.delegate = delegate;
        this.baseUrl = baseUrl;
        gson = new Gson();
        dbh = new DatabaseHandler(context);
    }


    private String putBrand() throws IOException
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("purchasedAmount",1);
        jo.addProperty("userId", userId);
        InputStream input = null;
        OutputStream output = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = null;
        String outputToUser = "0";

        try
        {
            URL url = new URL(baseUrl + "coffee/klippekort/" + prePaidCardId);
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
            connection.connect();
            String code = "" +connection.getResponseCode();
            if(code.equalsIgnoreCase("200"));
            {
                outputToUser = "alt gik godt";
                String accessToken = connection.getHeaderField("accessToken");
                if(!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
                {
                    dbh.updateToken("accessToken", accessToken);
                }

            }
            if(code.equalsIgnoreCase("500"))
            {
                ApiErrorMessage error = new ApiErrorMessage("brand", "Fejl: Forkert input", "500");
                return error.getErrorMessage();
            }
            if(code.equalsIgnoreCase("401"))
            {
                ApiErrorMessage error = new ApiErrorMessage("brand", "Fejl: Manglende rettigheder", "500");
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
            return outputToUser;
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
            return putBrand();
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
