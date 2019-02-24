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
import entity.User;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;


public class AddPrepaidCard extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    private String baseUrl;
    private Integer id = null;
    private String coffeeCode;
    private String storeCardId;
    private Gson gson;
    private DatabaseHandler dbh;

    public AddPrepaidCard(String baseUrl, User user, String coffeeCode, String storeCardId, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        this.id = user.getId();
        this.coffeeCode = coffeeCode;
        this.storeCardId = storeCardId;
        this.delegate = delegate;
        dbh = new DatabaseHandler(context);
    }
    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return addPrepaidCardToUser();
        } catch (IOException e)
        {
            e.printStackTrace();
            ApiErrorMessage error = new ApiErrorMessage("PrepaidCard", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }
    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }


    private String addPrepaidCardToUser() throws IOException
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("userId", id);
        jo.addProperty("storeCardId", storeCardId);
        jo.addProperty("coffeeCode", coffeeCode);
        OutputStream output = null;

        URL url = new URL(baseUrl + "users/card/coffeeBought");
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
        String code = connection.getResponseCode()+"";

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
        if(code.equalsIgnoreCase("501"))
        {
            ApiErrorMessage error = new ApiErrorMessage("loyaltyCard", "Fejl: Overskrider grænse af købte kopper", "501");
            return error.getErrorMessage();
        }
        return code;
    }
}
