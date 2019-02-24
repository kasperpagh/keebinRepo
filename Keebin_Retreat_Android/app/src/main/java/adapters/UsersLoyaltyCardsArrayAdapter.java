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

import java.util.ArrayList;
import java.util.List;

import DB.DatabaseHandler;
import entity.LoyaltyCard;
import kasper.pagh.keebin.AddCoffeeToLoyaltycardFragment;
import kasper.pagh.keebin.R;
import kasper.pagh.keebin.SelectedLoyaltyCardFragment;

/**
 * Created by mrlef on 12/1/2016.
 */

public class UsersLoyaltyCardsArrayAdapter extends ArrayAdapter<LoyaltyCard>{

    private Context context;
    private List<LoyaltyCard> loyaltyCards;
    DatabaseHandler dbh = new DatabaseHandler(getContext());


    public UsersLoyaltyCardsArrayAdapter(Context context, List<LoyaltyCard> loyaltyCards)
    {
        super(context, R.layout.loyaltycardrow, loyaltyCards);
        this.context = context;
        this.loyaltyCards = loyaltyCards;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d("her er jeg i users:","   !!!!  " + loyaltyCards.get(position).getId());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View viewRow = inflater.inflate(R.layout.loyaltycardrow, parent, false);
        int coffeesBought = loyaltyCards.get(position).getNumberOfCoffeesBought();
        Integer maxCoffes = loyaltyCards.get(position).getMaxCoffee();
        int gratisKopper = loyaltyCards.get(position).getNumberOfFreeCoffeeAvailable();
        final int pos = position;
        int cardBrand = context.getResources().getIdentifier("logo", "drawable", "kasper.pagh.keebin");
        int bean = context.getResources().getIdentifier("fullbean", "drawable", "kasper.pagh.keebin");
        int noBean = context.getResources().getIdentifier("emptybean", "drawable", "kasper.pagh.keebin");
        int kopKaffe = context.getResources().getIdentifier("ikkebrugt", "drawable", "kasper.pagh.keebin");
        int brugt = context.getResources().getIdentifier("emptycoffee", "drawable", "kasper.pagh.keebin");

        ImageView viewLogo = (ImageView) viewRow.findViewById(R.id.logo);
        ImageView viewBean1 = (ImageView) viewRow.findViewById(R.id.bean1);
        ImageView viewBean2 = (ImageView) viewRow.findViewById(R.id.bean2);
        ImageView viewBean3 = (ImageView) viewRow.findViewById(R.id.bean3);
        ImageView viewBean4 = (ImageView) viewRow.findViewById(R.id.bean4);
        ImageView viewBean5 = (ImageView) viewRow.findViewById(R.id.bean5);
        ImageView viewBean6 = (ImageView) viewRow.findViewById(R.id.bean6);
        ImageView viewBean7 = (ImageView) viewRow.findViewById(R.id.bean7);
        ImageView viewBean8 = (ImageView) viewRow.findViewById(R.id.bean8);
        ImageView viewBean9 = (ImageView) viewRow.findViewById(R.id.bean9);
        ImageView viewBean10 = (ImageView) viewRow.findViewById(R.id.bean10);
        TextView antalGratis = (TextView) viewRow.findViewById(R.id.gratisKopper);

        viewLogo.setImageBitmap(dbh.getBrandPicture(dbh.getBrandbyId(loyaltyCards.get(position).getBrandName()).getBrandName()));

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
        while(i<maxCoffes){
            if(i.equals(maxCoffes-1)) {
                beanViews.get(9).setImageDrawable(ContextCompat.getDrawable(context, brugt));

            }else {

                beanViews.get(i).setImageDrawable(ContextCompat.getDrawable(context, noBean));
            }
            i++;
        }
        i=0;
         while(i<coffeesBought){
             if(gratisKopper>0){
                 beanViews.get(9).setImageDrawable(ContextCompat.getDrawable(context, kopKaffe));
             }
             if(i.equals(maxCoffes-1) ){
                 beanViews.get(9).setImageDrawable(ContextCompat.getDrawable(context, kopKaffe));

             }else {
                 beanViews.get(i).setImageDrawable(ContextCompat.getDrawable(context, bean));
             }
            i++;
        }
if(gratisKopper>0) {
    beanViews.get(9).setImageDrawable(ContextCompat.getDrawable(context, kopKaffe));
    antalGratis.setText("X" + gratisKopper);
}


        viewRow.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, SelectedLoyaltyCardFragment.newInstance(loyaltyCards.get(pos))).commit();
            }
        });

        return viewRow;

    }






}

