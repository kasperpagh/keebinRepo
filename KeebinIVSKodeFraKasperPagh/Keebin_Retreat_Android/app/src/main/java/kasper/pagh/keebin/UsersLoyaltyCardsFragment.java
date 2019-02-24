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
import adapters.UsersLoyaltyCardsArrayAdapter;
import entity.LoyaltyCard;
import rest.userReST.GetAllLoyaltyCards;


public class UsersLoyaltyCardsFragment extends Fragment implements AsyncResponse
{
    List<LoyaltyCard> loyaltyList;
    private Gson gson = new Gson();
    UsersLoyaltyCardsArrayAdapter adapter;
    private TextView errorText;

    public UsersLoyaltyCardsFragment()
    {
    }

    public static UsersLoyaltyCardsFragment newInstance()
    {
        UsersLoyaltyCardsFragment fragment = new UsersLoyaltyCardsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        loyaltyList = new ArrayList<>();
        GetAllLoyaltyCards g = new GetAllLoyaltyCards(getResources().getString(R.string.baseUrl), this, getActivity());
        g.execute();
        final View view = inflater.inflate(R.layout.users_loyalty_cards, container, false);
        ListView listview = (ListView) view.findViewById(R.id.listOfUsersLoyaltyCards);
        adapter = new UsersLoyaltyCardsArrayAdapter(getActivity(), loyaltyList);
        listview.setAdapter(adapter);
        return view;
    }

    @Override
    public void processFinished(String output)
    {
        if (output.startsWith("Fejl:"))
        {
            errorText = (TextView) getActivity().findViewById(R.id.errorLoyaltyCard);
            errorText.setText("Der skete desværre en fejl, så vi ikke kan finde dine loyalitetskort - Tjek din internet forbindelse eller prøv igen senere!");
        } else
        {
            if(output.isEmpty()){
                errorText.setText("Du har ikke nogen loyaltycards");
            }
            DatabaseHandler dbh = new DatabaseHandler(getContext());
            Log.d("her er card", output);
            LoyaltyCard[] card = gson.fromJson(output, LoyaltyCard[].class);
            for (LoyaltyCard eachCard : card)
            {
//                LoyaltyCard cardForRow = new LoyaltyCard(dbh.getBrandbyServerId(eachCard.getShopEmail()).getShopEmail().toLowerCase(), "b" + eachCard.getNumberOfCoffeesBought());
                LoyaltyCard cardForRow = new LoyaltyCard(eachCard.getId(),dbh.getBrandbyId(eachCard.getBrandName()), null, eachCard.getCreatedAt(), eachCard.isValid(), eachCard.getNumberOfFreeCoffeeAvailable(), eachCard.getNumberOfCoffeesBought());
                loyaltyList.add(cardForRow);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

