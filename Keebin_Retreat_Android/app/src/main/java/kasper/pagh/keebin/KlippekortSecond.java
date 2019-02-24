package kasper.pagh.keebin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import adapters.BrandArrayAdapter;
import adapters.KlippekortsArrayAdapter;
import adapters.SelectKlippeKortAdapter;
import entity.CoffeeBrand;
import entity.PrePaidCard;
import rest.coffeeRest.GetAllBrands;
import rest.coffeeRest.GetKlippekortByBrand;


public class KlippekortSecond extends Fragment implements AsyncResponse {
    SelectKlippeKortAdapter adapter;
    List<PrePaidCard> klippekort;
    ListView listview;

    private Gson gson = new Gson();
    public static KlippekortSecond newInstance(int id,String name)
    {
        Bundle args = new Bundle();
        KlippekortSecond fragment = new KlippekortSecond();
        args.putInt("brandId",id);
        args.putString("brandName",name);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.klippekort_decide, container, false);
        klippekort = new ArrayList<PrePaidCard>();
        GetKlippekortByBrand g = new GetKlippekortByBrand(getResources().getString(R.string.baseUrl),getArguments().getInt("brandId"), this, getActivity());
        g.execute();
        listview = (ListView) view.findViewById(R.id.listOfPrepaidCards);
        adapter = new SelectKlippeKortAdapter(getActivity(), klippekort,getArguments().getString("brandName"));
        listview.setAdapter(adapter);



        return view;
    }
    @Override
    public void processFinished(String output) {
        Log.d("her er output fra ","klippekortSecond" + output);
        PrePaidCard[] prepaidcards = gson.fromJson(output, PrePaidCard[].class);

        for (PrePaidCard eachCard : prepaidcards)
        {
            PrePaidCard newPrepaidCard = new PrePaidCard(eachCard.getId(),eachCard.getCount(),eachCard.getPrice(),eachCard.getCents(),eachCard.getName(),eachCard.getCoffeeBrandId());

            klippekort.add(newPrepaidCard);
            adapter.notifyDataSetChanged();
        }


    }
}
