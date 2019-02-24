package kasper.pagh.keebin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import adapters.BrandArrayAdapter;
import adapters.UsersLoyaltyCardsArrayAdapter;
import entity.CoffeeBrand;
import entity.LoyaltyCard;
import rest.coffeeRest.GetAllBrands;
import rest.userReST.AddCoffee;
import rest.userReST.GetAllLoyaltyCards;



public class KlippekortKøbFragment extends Fragment implements AsyncResponse {
    List<CoffeeBrand> brands;
    BrandArrayAdapter adapter;
    EditText coffeeCode;
    EditText numberOfCoffeesBought;
    AsyncResponse thisPage = this;
    private ProgressDialog progress;
    private Gson gson = new Gson();

    public static KlippekortKøbFragment newInstance()
    {
        Bundle args = new Bundle();
        KlippekortKøbFragment fragment = new KlippekortKøbFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.klippekortadd, container, false);
        brands = new ArrayList<>();
        GetAllBrands g = new GetAllBrands(getResources().getString(R.string.baseUrl), this, getActivity());
        g.execute();
        ListView listview = (ListView) view.findViewById(R.id.listOfCoffeeBrands);
        adapter = new BrandArrayAdapter(getActivity(), brands);
        listview.setAdapter(adapter);

        return view;
    }
    @Override
    public void processFinished(String output)
    {


            if (output.startsWith("Fejl:")) {
                dismissLoadingDialog();
                Toast.makeText(getActivity(), output.substring(5), Toast.LENGTH_LONG).show();
            } else
                {CoffeeBrand[] brand = gson.fromJson(output, CoffeeBrand[].class);
                for (CoffeeBrand eachBrand : brand)
                {
                    CoffeeBrand coffeeBrandNew = new CoffeeBrand(eachBrand.getId(),eachBrand.getBrandName(),eachBrand.getNumberOfCoffeeNeeded());
                    brands.add(coffeeBrandNew);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    public void showLoadingDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this.getContext());
            progress.setTitle("loading");
            progress.setMessage("Loading please stand by");
        }
        progress.show();
    }

    public void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}
