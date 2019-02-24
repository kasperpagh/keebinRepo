package adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import DB.DatabaseHandler;
import entity.CoffeeShop;
import kasper.pagh.keebin.JavaMapFragment;
import kasper.pagh.keebin.R;

/**
 * Created by kaspe on 2016-11-27.
 */

public class CoffeeShopArrayAdapter extends ArrayAdapter<CoffeeShop>
{
    private Context context;
    private List<CoffeeShop> shops;
    private DatabaseHandler dbh;

    public CoffeeShopArrayAdapter(Context context, List<CoffeeShop> shops)
    {
        super(context, R.layout.coffee_shop_row, shops);
        this.shops = shops;
        this.context = context;
        dbh = new DatabaseHandler(context);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View viewRow = inflater.inflate(R.layout.coffee_shop_row, parent, false);

        TextView coffeeShopName = (TextView) viewRow.findViewById(R.id.shopName);
        TextView coffeeShopAdress = (TextView) viewRow.findViewById(R.id.address);
        final Button button = (Button) viewRow.findViewById(R.id.mapsButton);
        ImageView image = (ImageView) viewRow.findViewById(R.id.SearchLogo);
//        final ImageButton searchBtn = (ImageButton) viewRow.findViewById(R.id.search_menu);
        final ImageButton mapsBtn = (ImageButton) viewRow.findViewById(R.id.maps_menu);
        final int pos = position;
        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                            //Da dette ikke er et Activity er vi nødt til at caste til FragmentActivity for at
                                          //kunnebruge supportFragmentManager
                                          button.setEnabled(false);
//                                          searchBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_search_white_24dp));
                                          mapsBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_search_white_24dp));
                                          ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, JavaMapFragment.newInstance(shops.get(pos))).commit();
                                          button.setEnabled(true);
                                      }
                                  });

        coffeeShopName.setText(shops.get(position).getActualBrandName());
        coffeeShopAdress.setText(shops.get(position).getAddress());
        int cardBrand = context.getResources().getIdentifier(shops.get(position).getActualBrandName().toLowerCase(), "drawable", "kasper.pagh.keebin");
        image.setImageBitmap(dbh.getBrandPicture(shops.get(position).getActualBrandName().toLowerCase()));


        return viewRow;
    }
}
