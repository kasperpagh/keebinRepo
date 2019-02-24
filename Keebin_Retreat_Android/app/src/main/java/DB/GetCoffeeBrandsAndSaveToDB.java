package DB;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;

import kasper.pagh.keebin.AsyncResponse;
import kasper.pagh.keebin.DBListener;
import kasper.pagh.keebin.R;
import rest.coffeeRest.GetAllBrands;
import entity.CoffeeBrand;

/**
 * Created by mrlef on 12/4/2016.
 */

public class GetCoffeeBrandsAndSaveToDB implements AsyncResponse
{
    private Gson gson = new Gson();
    DatabaseHandler dbh;
    Context context;
    private DBListener delegate;

    public GetCoffeeBrandsAndSaveToDB(Context context, DBListener delegate)
    {
        this.context = context;
        dbh = new DatabaseHandler(context);
        this.delegate = delegate;
    }


    public void getAllCoffeeBrands(Context context)
    {
        GetAllBrands g = new GetAllBrands(context.getResources().getString(R.string.baseUrl), this, context);
        g.execute();
    }

    @Override
    public void processFinished(String output)
    {
        CoffeeBrand[] cBrand = gson.fromJson(output, CoffeeBrand[].class);
        for (CoffeeBrand eachBrand : cBrand)
        {
            CoffeeBrand cBrandForDB = new CoffeeBrand(eachBrand.getId(), eachBrand.getBrandName(), eachBrand.getNumberOfCoffeeNeeded());
            dbh.addCoffeeBrand(cBrandForDB);

        }

        delegate.DBReady();
    }
}
