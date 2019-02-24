package rest.userReST;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import entity.User;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;

import static kasper.pagh.keebin.R.string.loginString;

/**
 * Created by kaspe on 2016-10-29.
 */

public class NewUser extends AsyncTask<String, Void, String>
{
    public AsyncResponse delegate = null;
    private String baseUrl;
    private String userToCreate = null;
    private Gson gson;
    private DatabaseHandler dbh;

    public NewUser(String baseUrl, User newUser, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        this.userToCreate = gson.toJson(newUser, User.class);
        this.delegate = delegate;
        dbh = new DatabaseHandler(context);
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return newUser();
        }
        catch (IOException e)
        {
            ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }


    private String newUser() throws IOException
    {
        OutputStream output = null;
        URL url = new URL(baseUrl + "login/user/new");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        output = connection.getOutputStream();
        output.write(userToCreate.getBytes("UTF-8"));
        output.close();

        connection.connect();
        String code = connection.getResponseCode() + "";
        Log.d("NEWUSER", "her er koden: " + code + " og tokens: " + connection.getHeaderField("accessToken")+ connection.getHeaderField("refreshToken"));
        if(code.equalsIgnoreCase("200"))
        {
//            String refreshToken = connection.getHeaderField("refreshToken");
//            String accessToken = connection.getHeaderField("accessToken");
//
//            if(!dbh.getTokenByName("refreshToken").getTokenData().equals(refreshToken))
//            {
//                dbh.updateToken("refreshToken", refreshToken);
//            }
//            if(!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
//            {
//                dbh.updateToken("accessToken", accessToken);
//            }
        }
        if (code.equalsIgnoreCase("500"))
        {
            ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Forkert input", "500");
            return error.getErrorMessage();
        }
        if (code.equalsIgnoreCase("501"))
        {
            ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Bruger eksisterer allerede", "501");
            return error.getErrorMessage();
        }
        if (code.equalsIgnoreCase("401"))
        {
            ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Manglende rettigheder", "401");
            return error.getErrorMessage();
        }

        return code;

    }
}
