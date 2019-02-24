package rest.coffeeRest;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import DB.DatabaseHandler;
import entity.Order;
import entity.OrderItem;
import errors.ApiErrorMessage;
import kasper.pagh.keebin.AsyncResponse;

/**
 * Created by kaspe on 14-05-2017.
 */

public class NewOrder extends AsyncTask<String, Void, String>
{
    public AsyncResponse delegate = null;
    private String baseUrl;
    private Gson gson;
    private Order order;
    private DatabaseHandler dbh;

    public NewOrder(String baseUrl, AsyncResponse delegate, Context context, Order order)
    {
        this.baseUrl = baseUrl;
        this.gson = new Gson();
        this.delegate = delegate;
        this.order = order;
        dbh = new DatabaseHandler(context);
    }

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return newShop();
        }
        catch (IOException e)
        {
            ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: netværksfejl, prøv igen senere", "500");
            return error.getErrorMessage();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }


    private String newShop() throws IOException
    {

        JsonArray ja = new JsonArray();

        for (OrderItem oi : order.getOrderedItemsList())
        {
            JsonObject jo = new JsonObject();
            jo.addProperty("isPremiumUsed", oi.isPremiumUsed());
            jo.addProperty("prepaidCardId", oi.getPrepaidCardId());
            jo.addProperty("orderId", oi.getOrderId());
            jo.addProperty("menuItemId", oi.getMenuItemId());
            ja.add(jo);

        }

        JsonObject jo = new JsonObject();
        jo.addProperty("userId", order.getUserId());
        jo.addProperty("coffeeShopId", order.getCoffeeShop());
        jo.add("orderItemList", ja);
        jo.addProperty("platform", "Android");


        OutputStream output = null;

        URL url = new URL(baseUrl + "order/newOrder");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        output = connection.getOutputStream();
        output.write(gson.toJson(jo).getBytes("UTF-8"));
        output.close();

        connection.connect();

        String code = "" + connection.getResponseCode();
        if (code.equalsIgnoreCase("200"))
        {
            ;
        }
        {
            String accessToken = connection.getHeaderField("accessToken");
            if (!dbh.getTokenByName("accessToken").getTokenData().equals(accessToken))
            {
                dbh.updateToken("accessToken", accessToken);
            }

        }
        if (code.equalsIgnoreCase("500"))
        {
            ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: kaffebaren findes allerede/Forkert input", "500");
            return error.getErrorMessage();
        }
        if (code.equalsIgnoreCase("401"))
        {
            ApiErrorMessage error = new ApiErrorMessage("shop", "Fejl: Manglende rettigheder", "500");
            return error.getErrorMessage();
        }
        return code;
    }
}