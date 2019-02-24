package rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by kasper on 2016-10-26.
 * Class has only one method, meant to check if there is a network connection available.
 * Only one instance of this class should exists at any given time (in MainActivity by the name of 'networkChecker')
 * this instance can be accessed by means of the 'getNetworkChecker' method in MainActivity
 */

public class NetworkChecker
{
    private Context context;

    public NetworkChecker(Context context)
    {
        this.context = context;
    }
    public boolean networkChecker()
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            return true;
        } else
        {
            return false;
        }
    }

}
