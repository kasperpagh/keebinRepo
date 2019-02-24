package rest.userReST;

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

public class NewRole extends AsyncTask<String, Void, String>
{
    public AsyncResponse delegate = null;
    private String baseUrl;
    private Gson gson;
    private String roleName;
    private DatabaseHandler dbh;

    public NewRole(String baseUrl, String roleName, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        this.delegate = delegate;
        this.roleName = roleName;
        dbh = new DatabaseHandler(context);

    }
    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return newRole();
        } catch (IOException e)
        {
            ApiErrorMessage error = new ApiErrorMessage("role", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }
    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }


    private String newRole() throws IOException
    {
        JsonObject jo = new JsonObject();
        jo.addProperty("roleName", roleName);
        OutputStream output = null;

        URL url = new URL(baseUrl + "users/role/new");
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
            ApiErrorMessage error = new ApiErrorMessage("roles", "Fejl: Forkert input", "500");
            return error.getErrorMessage();
        }
        if(code.equalsIgnoreCase("401"))
        {
            ApiErrorMessage error = new ApiErrorMessage("roles", "Fejl: Manglende rettigheder", "401");
            return error.getErrorMessage();
        }
        return code;
    }
}