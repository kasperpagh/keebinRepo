package rest.houseKeeping;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


import entity.BrandPicture;
import kasper.pagh.keebin.AsyncResponse;
import utils.security.CertificateTrustHandler;

import static kasper.pagh.keebin.R.string.baseUrl;

/**
 * Created by kaspe on 27-02-2017.
 */

public class GetCertificate extends AsyncTask<String, Void, String>
{
    private Context context;
    private String certificateBaseURL;
    private AsyncResponse delegate;

    public GetCertificate(Context context, AsyncResponse delegate, String certificateBaseURL)
    {
        this.delegate = delegate;
        this.certificateBaseURL = certificateBaseURL;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return getCertificateFromServer();
        }
        catch (IOException e)
        {

            e.printStackTrace();
            return "fejl";
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }


    public String getCertificateFromServer() throws IOException
    {
        InputStream is = null;
        URL url = new URL(certificateBaseURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoInput(true);

        connection.connect();
        String code = "" +connection.getResponseCode();

        if(code.equalsIgnoreCase("200"))
        {
            try
            {
                CertificateTrustHandler.trustCertificate(connection.getInputStream(), context);
                return "ok";
            }
            catch (Exception e)
            {
                Log.d("exception" , "i kaldet til certHandler");
                e.printStackTrace();
                return "fejl";
            }
        }
        else
        {
            Log.d("koden er", "ikke 200");
            return "fejl";
        }

    }
}
