package kasper.pagh.keebin;

import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DB.DatabaseHandler;
import adapters.IndexAdapter;
import entity.CoffeeShop;
import rest.coffeeRest.GetAllShopsWithBrandName;
import utils.locationAndDistance.DistanceSorter;

/**
 * Created by kaspe on 20-02-2017.
 */

public class NewIndex extends Fragment implements AsyncResponse
{

    private DatabaseHandler dbh;
    private List<CoffeeShop> coffeeList = new ArrayList<>();

    private IndexAdapter adapter;
    private Gson gson = new Gson();
    EditText indexSearchBar;


    public NewIndex()
    {
    }

    public static NewIndex newInstance()
    {
        Bundle args = new Bundle();
        NewIndex fragment = new NewIndex();
        fragment.setArguments(args);
        return fragment;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.new_index, container, false);

        MainActivity.callFloatingActionButton();

        MainActivity.blackHome();
        MainActivity.whiteLoyal();
        MainActivity.whiteMap();
        dbh = new DatabaseHandler(getActivity());
        indexSearchBar = (EditText) view.findViewById(R.id.indexSearchBar);
        view.findViewById(R.id.searchbutton_index).setVisibility(View.INVISIBLE);
        Button delBtn = (Button) view.findViewById(R.id.searchbutton_index);
        delBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                indexSearchBar.setText("");
            }
        });
        indexSearchBar.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                String searchString = charSequence.toString();
                Log.d("INDEX", "her er søgestring: " + searchString);
                List<CoffeeShop> resultList = new ArrayList<CoffeeShop>();
                for (int iterator = 0; iterator < coffeeList.size(); iterator++)
                {
                    if (coffeeList.get(iterator).getActualBrandName().toLowerCase().contains(searchString.toLowerCase()) || coffeeList.get(iterator).getAddress().toLowerCase().contains(searchString.toLowerCase()) || coffeeList.get(iterator).getEmail().toLowerCase().contains(searchString.toLowerCase()))
                    {
                        resultList.add(coffeeList.get(iterator));
                        Log.d("INDEX", "adder " + coffeeList.get(iterator).toString() + " til resList");
                    }
                }
                if (searchString.equalsIgnoreCase(""))
                {
                    view.findViewById(R.id.searchbutton_index).setVisibility(View.INVISIBLE);
                    Log.d("INDEX", "nullstring list sat til size: " + coffeeList.size());
                    adapter.clear();
                    adapter.addAll(coffeeList);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    view.findViewById(R.id.searchbutton_index).setVisibility(View.VISIBLE);
                    Log.d("INDEX", "ej null string list sat til size: " + resultList.size());
                    adapter.clear();
                    adapter.addAll(resultList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
        GetAllShopsWithBrandName allshops = new GetAllShopsWithBrandName(getResources().getString(R.string.baseUrl), this, getActivity());
        allshops.execute();
        adapter = new IndexAdapter(getActivity(), coffeeList);
        adapter.clear();
        ListView listView = (ListView) view.findViewById(R.id.indexListView);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void processFinished(String output)
    {
        if (output.startsWith("Fejl:"))
        {
            TextView errorText = (TextView) getActivity().findViewById(R.id.searchErrorText);
            errorText.setText("På grund af en fejl, kan vi desværre ikke finde nogen kaffebarer lige nu. Prøv igen senere!");
        }
        else
        {
            CoffeeShop[] coffeeArray = gson.fromJson(output, CoffeeShop[].class);
            coffeeList = Arrays.asList(coffeeArray);
            try
            {
                coffeeList = DistanceSorter.sortCoffeeList(dbh.getLocation(1), Arrays.asList(coffeeArray));
                adapter.addAll(coffeeList);
                adapter.notifyDataSetChanged();
            }
            catch (CursorIndexOutOfBoundsException e)
            {
                adapter.addAll(coffeeList);
                adapter.notifyDataSetChanged();
            }


        }
    }
}
