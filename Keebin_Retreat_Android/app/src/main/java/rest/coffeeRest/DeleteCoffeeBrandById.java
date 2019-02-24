package rest.coffeeRest;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.common.api.Api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;
import DB.DatabaseHandler;


public class DeleteCoffeeBrandById extends AsyncTask<String, Void, String>
{
    private String BrandID;
    public AsyncResponse delegate = null;
    private String baseUrl;
    private DatabaseHandler dbh;
    private ApiErrorMessage errorHandler;

    public DeleteCoffeeBrandById(String baseUrl, String BrandID, AsyncResponse delegate, Context context)
    {
        this.baseUrl = baseUrl;
        this.BrandID = BrandID;
        this.delegate = delegate;
        dbh = new DatabaseHandler(context);
    }
    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return deleteBrand();
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

    private String deleteBrand() throws IOException
    {
        URL url = new URL(baseUrl + "coffee/shop/" + BrandID);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("refreshToken", dbh.getTokenByName("refreshToken").getTokenData());
        connection.setRequestProperty("accessToken", dbh.getTokenByName("accessToken").getTokenData());
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
            ApiErrorMessage error = new ApiErrorMessage("brand", "Fejl: Der er ingen brands der matcher det givne ID", "500");
            return error.getErrorMessage();
        }
        if(code.equalsIgnoreCase("401"))
        {
            ApiErrorMessage error = new ApiErrorMessage("brand", "Fejl: Manglende rettigheder", "500");
            return error.getErrorMessage();
        }

        return code;
    }
}
