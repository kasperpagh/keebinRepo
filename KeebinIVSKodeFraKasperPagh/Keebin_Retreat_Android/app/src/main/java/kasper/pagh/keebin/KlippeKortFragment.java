package kasper.pagh.keebin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import DB.DatabaseHandler;
import adapters.KlippekortsArrayAdapter;
import entity.UserPrepaid;
import rest.coffeeRest.GetKlippekortByUserID;


public class KlippeKortFragment extends Fragment implements AsyncResponse {
    List<UserPrepaid> klippekort;
    private Gson gson = new Gson();
    KlippekortsArrayAdapter adapter;
    private TextView errorText;
    private KlippekortMellemLed mellemled;

    public KlippeKortFragment()
    {
    }

    public static KlippeKortFragment newInstance()
    {

        KlippeKortFragment fragment = new KlippeKortFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int d = MainActivity.currentUser.getId();
        String url = getResources().getString(R.string.baseUrl);
        mellemled = new KlippekortMellemLed(url,getContext());

        // Inflate the layout for this fragment

        klippekort = new ArrayList<>();

        GetKlippekortByUserID g = new GetKlippekortByUserID(url,d,this,getActivity());
        g.execute();
        final View view = inflater.inflate(R.layout.users_loyalty_cards, container, false);

        ListView listview = (ListView) view.findViewById(R.id.listOfUsersLoyaltyCards);
        adapter = new KlippekortsArrayAdapter(getActivity(), klippekort, mellemled.getCardVariations());
        listview.setAdapter(adapter);
        return view;
    }

    @Override
    public void processFinished(String output)
    {
        Log.d("vi er nu i klippekort"," hej");
        Log.d("her er output:  ",output);

        if (output.startsWith("Fejl:"))
        {
            errorText = (TextView) getActivity().findViewById(R.id.errorLoyaltyCard);
            errorText.setText("Der skete desværre en fejl, så vi ikke kan finde dine loyalitetskort - Tjek din internet forbindelse eller prøv igen senere!");
        } else
        {
            if(output.isEmpty()){
                errorText.setText("Du har ikke nogen Klippekort");
            }
            DatabaseHandler dbh = new DatabaseHandler(getContext());
            Log.d("her er card", output);
            UserPrepaid[] card = gson.fromJson(output, UserPrepaid[].class);
            for (UserPrepaid eachCard : card)
            {
                UserPrepaid cardForRow = new UserPrepaid(eachCard.getId(), eachCard.getUsesleft(), eachCard.getUserId(), eachCard.getPrePaidCoffeeCardId());
                klippekort.add(cardForRow);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
