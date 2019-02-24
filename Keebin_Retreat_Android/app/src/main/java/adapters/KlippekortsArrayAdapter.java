package adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DB.DatabaseHandler;
import entity.LoyaltyCard;
import entity.PrePaidCard;
import entity.UserPrepaid;
import kasper.pagh.keebin.AsyncResponse;
import kasper.pagh.keebin.R;
import kasper.pagh.keebin.SelectedKlippekortFragment;
import kasper.pagh.keebin.SelectedLoyaltyCardFragment;
import rest.coffeeRest.GetAllPrepaidcardVariations;


public class KlippekortsArrayAdapter extends ArrayAdapter<UserPrepaid> {

    private Context context;
    private List<UserPrepaid> klippekort;
    private List<PrePaidCard> variation = new ArrayList<>();
    DatabaseHandler dbh = new DatabaseHandler(getContext());
    int pos;
    ImageView viewLogo;


    public KlippekortsArrayAdapter(Context context, List<UserPrepaid> klippekort, List<PrePaidCard> prepaidVar) {
        super(context, R.layout.prepaid_card_layout, klippekort);
        this.context = context;
        this.klippekort = klippekort;
        this.variation = prepaidVar;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        pos = position;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View viewRow = inflater.inflate(R.layout.prepaid_card_layout, parent, false);

        viewLogo = (ImageView) viewRow.findViewById(R.id.logo2);

        for (PrePaidCard card : variation) {
            Integer i = card.getId();
            if (i.equals(klippekort.get(position).getPrePaidCoffeeCardId()))
            {
                viewLogo.setImageBitmap(dbh.getBrandPicture(dbh.getBrandbyId(Integer.parseInt(card.getCoffeeBrandId())).getBrandName()));


            }
        }




            int count = klippekort.get(position).getUsesleft();
            int kopKaffe = context.getResources().getIdentifier("ikkebrugt", "drawable", "kasper.pagh.keebin");
            int brugt = context.getResources().getIdentifier("brugtklip", "drawable", "kasper.pagh.keebin");


            ImageView viewBean1 = (ImageView) viewRow.findViewById(R.id.kaffe1);
            ImageView viewBean2 = (ImageView) viewRow.findViewById(R.id.kaffe2);
            ImageView viewBean3 = (ImageView) viewRow.findViewById(R.id.kaffe3);
            ImageView viewBean4 = (ImageView) viewRow.findViewById(R.id.kaffe4);
            ImageView viewBean5 = (ImageView) viewRow.findViewById(R.id.kaffe5);
            ImageView viewBean6 = (ImageView) viewRow.findViewById(R.id.kaffe6);
            ImageView viewBean7 = (ImageView) viewRow.findViewById(R.id.kaffe7);
            ImageView viewBean8 = (ImageView) viewRow.findViewById(R.id.kaffe8);
            ImageView viewBean9 = (ImageView) viewRow.findViewById(R.id.kaffe9);
            ImageView viewBean10 = (ImageView) viewRow.findViewById(R.id.kaffe10);
            TextView antalGratis = (TextView) viewRow.findViewById(R.id.gratisKopper2);
            TextView cardName = (TextView) viewRow.findViewById(R.id.klippekortName);
            int coffeesAbove10 = count - 10;


            if (coffeesAbove10 > 0)
            {
                antalGratis.setText("X " + coffeesAbove10);
            }

            ArrayList<ImageView> beanViews = new ArrayList<ImageView>();
            beanViews.add(viewBean1);
            beanViews.add(viewBean2);
            beanViews.add(viewBean3);
            beanViews.add(viewBean4);
            beanViews.add(viewBean5);
            beanViews.add(viewBean6);
            beanViews.add(viewBean7);
            beanViews.add(viewBean8);
            beanViews.add(viewBean9);
            beanViews.add(viewBean10);
            Integer i = 0;
            while (i < 10) {
                if (i > count - 1 ) {
                    beanViews.get(i).setImageDrawable(ContextCompat.getDrawable(context, brugt));

                } else {

                    beanViews.get(i).setImageDrawable(ContextCompat.getDrawable(context, kopKaffe));
                }
                i++;
            }

            viewRow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                     ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, SelectedKlippekortFragment.newInstance(klippekort.get(position),variation)).commit();                }
            });


            return viewRow;

        }


    }

