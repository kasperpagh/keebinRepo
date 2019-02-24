package adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import DB.DatabaseHandler;
import entity.CoffeeBrand;
import entity.PrePaidCard;
import kasper.pagh.keebin.KlippekortFinal;
import kasper.pagh.keebin.KlippekortSecond;
import kasper.pagh.keebin.R;


public class SelectKlippeKortAdapter extends ArrayAdapter<PrePaidCard> {

    private Context context;
    private List<PrePaidCard> prepaidcards;
    private String brandName;
    private DatabaseHandler dbh;

    public SelectKlippeKortAdapter(Context context, List<PrePaidCard> prepaidcards,String brandName)
    {
        super(context, R.layout.brand_layout, prepaidcards);
        this.prepaidcards = prepaidcards;
        this.context = context;
        this.brandName = brandName;
        dbh = new DatabaseHandler(context);
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View viewRow = inflater.inflate(R.layout.klippekort_layout, parent, false);

        TextView klippekortCount = (TextView) viewRow.findViewById(R.id.klippekortCount);
        TextView klippekortName = (TextView) viewRow.findViewById(R.id.klippekortNavn);
        final TextView klippekortPrice = (TextView) viewRow.findViewById(R.id.klippekortPrice);

        klippekortCount.setText("Antal klip: " + prepaidcards.get(position).getCount()+"");
        klippekortName.setText(prepaidcards.get(position).getName());
        klippekortPrice.setText("Pris: " + prepaidcards.get(position).getPrice() + "," + prepaidcards.get(position).getCents() + " kr.-");

        viewRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Da dette ikke er et Activity er vi nødt til at caste til FragmentActivity for at
                //kunnebruge supportFragmentManager
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, KlippekortFinal.newInstance(prepaidcards.get(position).getId(),prepaidcards.get(position).getName(),klippekortPrice.getText()+"",brandName)).commit();

            }
        });

        return viewRow;
    }
}
