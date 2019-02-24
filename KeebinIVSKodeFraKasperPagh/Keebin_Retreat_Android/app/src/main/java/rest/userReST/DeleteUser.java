package rest.userReST;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;

/**
 * Created by kaspe on 2016-10-29.
 */

public class DeleteUser extends AsyncTask<String, Void, String>
{
    private String userEmail;
    public AsyncResponse delegate = null;
    private String baseUrl;
    private DatabaseHandler dbh;

    public DeleteUser(String baseUrl, String userEmail, AsyncResponse delegate, Context context)
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
            return deleteUser();
        } catch (IOException e)
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

    private String deleteUser() throws IOException
    {
        URL url = new URL(baseUrl + "users/user/" + userEmail);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());
        connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());
        connection.connect();
        String code = "" +connection.getResponseCode();

        if(code.equalsIgnoreCase("200"));
        {
            String refreshToken = connection.getHeaderField("refreshToken");
            String accessToken = connection.getHeaderField("accessToken");

            if(!dbh.getTokenByName("refreshToken").getTokenData().equals(refreshToken))
            {
                dbh.updateToken("refreshToken", refreshToken);
            }
            if(!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
            {
                dbh.updateToken("accessToken", accessToken);
            }

        }
        if(code.equalsIgnoreCase("500"))
        {
            ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Forkert input", "500");
            return error.getErrorMessage();
        }
        if(code.equalsIgnoreCase("401"))
        {
            ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Manglende rettigheder", "500");
            return error.getErrorMessage();
        }

        return code;
    }
}
