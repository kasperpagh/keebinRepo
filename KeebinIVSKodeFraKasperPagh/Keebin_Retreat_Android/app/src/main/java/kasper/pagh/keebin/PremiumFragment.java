package kasper.pagh.keebin;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import entity.PrePaidCard;
import entity.PremiumSubscription;
import rest.userReST.CreateSubscription;
import rest.userReST.GetSubcription;
import rest.userReST.UseSubscription;

/**
 * Created by pelle on 3/16/2017.
 */

public class PremiumFragment extends Fragment implements AsyncResponse{
    PremiumFragment thisFragment = this;
    private Gson gson = new Gson();
    PremiumSubscription currentSub = null;
    TextView subOrNot;
    ImageView kop;
    int brugt;
    int ikkeBrugt;

    public static PremiumFragment newInstance()
    {
        PremiumFragment fragment = new PremiumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.premium, container, false);
        subOrNot = (TextView) view.findViewById(R.id.textView10);
        Button knap = (Button)view.findViewById(R.id.addOrUse);
        kop = (ImageView) view.findViewById(R.id.klippekortBillede);

        brugt = getContext().getResources().getIdentifier("brugtklip", "drawable", "kasper.pagh.keebin");
        ikkeBrugt = getContext().getResources().getIdentifier("ikkebrugt", "drawable", "kasper.pagh.keebin");
        GetSubcription sub = new GetSubcription(getResources().getString(R.string.baseUrl),thisFragment,getContext());
        sub.execute();
        knap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(currentSub == null) {
                    CreateSubscription newSub = new CreateSubscription(getResources().getString(R.string.baseUrl), thisFragment, getContext());
                    newSub.execute();
                }else{
                    if(currentSub.getIsValidForPremiumCoffee()){
                        UseSubscription useSub = new UseSubscription(getResources().getString(R.string.baseUrl), thisFragment, getContext());
                        useSub.execute();

                    }
                }
            }
        });

        return view;
    }

    @Override
    public void processFinished(String output) {
        if(currentSub == null) {
            PremiumSubscription subscription = gson.fromJson(output, PremiumSubscription.class);
            currentSub = subscription;
            if(currentSub.getIsValidForPremiumCoffee()) {
                subOrNot.setText("Du kan indløse din gratis kop kaffe for denne uge");
                kop.setImageDrawable(ContextCompat.getDrawable(getContext(), ikkeBrugt));

            }else{
                subOrNot.setText("Du har allerede brugt din kop kaffe for denne uge, men du får en ny igen på mandag.");
                kop.setImageDrawable(ContextCompat.getDrawable(getContext(), brugt));
            }
        }
        if(output.equals("Alt gik vel")){
            subOrNot.setText("Du har allerede brugt din kop kaffe for denne uge, men du får en ny igen på mandag.");
            kop.setImageDrawable(ContextCompat.getDrawable(getContext(), brugt));
             }

    }
}
