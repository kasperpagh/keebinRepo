package rest.userReST;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import entity.LoginData;
import entity.Token;
import entity.User;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;

/**
 * Created by kaspe on 2016-11-30.
 */

public class PostLogin extends AsyncTask<String, Void, String>
{
    private User currentUser;
    public AsyncResponse delegate = null;
    private LoginData loginData;
    private String rawLoginData;
    private String baseUrl;
    private Gson gson;
    private String userEmail;
    private String userPassword;
    private String rawUser;
    private DatabaseHandler dbh;
    private Context context;
    String baseLoginUrl;

    public PostLogin(String baseUrl, String baseLoginUrl, String userEmail, String userPassword, AsyncResponse delegate, Context context)
    {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.delegate = delegate;
        this.baseUrl = baseUrl;
        this.context = context;
        this.baseLoginUrl = baseLoginUrl;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            dbh = new DatabaseHandler(context);
            rawLoginData = postLogin();
            if (rawLoginData.startsWith("Fejl:"))
            {
                return rawLoginData;
            }
            loginData = gson.fromJson(rawLoginData, LoginData.class);
            Token refreshToken = new Token("refreshToken", loginData.getRefreshToken());
            Token accessToken = new Token("accessToken", loginData.getAccessToken());


            if(dbh.hasObject("refreshToken"))
            {

                dbh.updateToken("refreshToken", refreshToken.getTokenData());
                dbh.updateToken("accessToken", accessToken.getTokenData());
            }
            else
            {
                dbh.addToken(refreshToken);
                dbh.addToken(accessToken);
            }
            rawUser = getUser();
            currentUser = gson.fromJson(rawUser, User.class);
            currentUser.setLoginData(loginData);
            Token user = new Token("email",currentUser.getEmail());
            if(dbh.hasObject("email")){
                dbh.updateToken("email", currentUser.getEmail());
            }else{
                dbh.addToken(user);
            }


            return gson.toJson(currentUser);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }


    public String postLogin() throws IOException
    {
        InputStream input = null;
        OutputStream output = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = null;
        gson = new Gson();

        JsonObject jo = new JsonObject();
        jo.addProperty("email", userEmail);
        jo.addProperty("password", userPassword);
        try
        {
            URL url = new URL(baseLoginUrl + "login");
            Log.d("URL HER::::: ", url + "");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
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
            String code = connection.getResponseCode()+"";
            if(code.equalsIgnoreCase("747"))
            {
                ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Forkert input", "747");
                return error.getErrorMessage();
            }

            input = connection.getInputStream();
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

    private String getUser() throws IOException
    {
        InputStream input = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = null;

        try
        {
            URL url = new URL(baseUrl + "users/user/" + userEmail);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());
            connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());

            connection.connect();

            input = connection.getInputStream();

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
                ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Forkert input", "500");
                return error.getErrorMessage();
            }
            if(code.equalsIgnoreCase("401"))
            {
                ApiErrorMessage error = new ApiErrorMessage("user", "Fejl: Manglende rettigheder", "401");
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

}
