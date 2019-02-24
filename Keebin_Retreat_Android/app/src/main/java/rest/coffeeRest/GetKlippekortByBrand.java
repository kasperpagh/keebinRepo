package rest.coffeeRest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import DB.DatabaseHandler;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;


public class GetKlippekortByBrand extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    private Integer brandId;
    private String baseUrl;
    private DatabaseHandler dbh;

    public GetKlippekortByBrand(String baseUrl,int brandId, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.delegate = delegate;
        this.brandId=brandId;
        dbh = new DatabaseHandler(context);
    }


    private String GetKlippekort() throws IOException
    {
        InputStream input = null;
        BufferedReader bufferedReader = null;
        StringBuilder sb = null;

        try
        {
            URL url = new URL(baseUrl + "coffee/klippekortvariation/" + brandId);
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
            if (code.equalsIgnoreCase("500"))
            {
                ApiErrorMessage error = new ApiErrorMessage("userID", "Fejl: Der er ingen shops", "500");
                return error.getErrorMessage();
            }
            if (code.equalsIgnoreCase("401"))
            {
                ApiErrorMessage error = new ApiErrorMessage("userID", "Fejl: Manglende rettigheder", "401");
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
            return GetKlippekort();
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
