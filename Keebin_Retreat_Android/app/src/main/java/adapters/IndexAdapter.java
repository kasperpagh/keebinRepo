package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import DB.DatabaseHandler;
import entity.CoffeeShop;
import entity.UserLocation;
import kasper.pagh.keebin.JavaMapFragment;
import kasper.pagh.keebin.MainActivity;
import kasper.pagh.keebin.R;
import kasper.pagh.keebin.SelectedshopFragment;
import utils.locationAndDistance.DistanceCalculator;

/**
 * Created by kaspe on 20-02-2017.
 */

public class IndexAdapter extends ArrayAdapter<CoffeeShop>
{
    private Context context;
    private DatabaseHandler dbh;
    private List<CoffeeShop> shops;

    public IndexAdapter(Context context, List<CoffeeShop> shops)
    {
        super(context, R.layout.index_row, shops);
        this.context = context;
        this.shops = shops;
        dbh = new DatabaseHandler(context);
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final View viewRow = inflater.inflate(R.layout.index_row, parent, false);
        final int pos = position;
        final ViewGroup par2 = parent;

//        ImageView shopLogo = (ImageView) viewRow.findViewById(R.id.indexLogo);
        LinearLayout shopPic = (LinearLayout) viewRow.findViewById(R.id.shopPictureBackground);
        final ImageButton mapsButton = (ImageButton) viewRow.findViewById(R.id.IndexMapsButton);


        TextView shopTextBrandName = (TextView) viewRow.findViewById(R.id.IndexCoffeShopInfoBrandName);
        TextView shopTextAdress = (TextView) viewRow.findViewById(R.id.IndexCoffeShopInfoAddress);
        TextView shopTextDistance = (TextView) viewRow.findViewById(R.id.IndexCoffeShopInfoDistance);

//        shopLogo.setImageBitmap(dbh.getBrandPicture(shops.get(position).getActualBrandName().toLowerCase()));
        shopPic.setBackground(new BitmapDrawable(dbh.getShopPicture(shops.get(position).getEmail().toLowerCase())));

        shopTextBrandName.setText("\n" + shops.get(position).getActualBrandName());
        shopTextAdress.setText(shops.get(position).getAddress());
        UserLocation usrLoc = dbh.getLocation(1);
        shopTextDistance.setText(DistanceCalculator.stringifyDistance(DistanceCalculator.getDistanceToCoffeeShop(usrLoc.getLat(), usrLoc.getLng(), shops.get(position).getLatitude(), shops.get(position).getLongitude())));

        final ImageButton homeBtn = (ImageButton) viewRow.findViewById(R.id.home_menu);
        final ImageButton mapsBtn = (ImageButton) viewRow.findViewById(R.id.maps_menu);
        final ImageButton loyalBtn = (ImageButton) viewRow.findViewById(R.id.loyaltycards_menu);
        mapsButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mapsButton.setEnabled(false);

                MainActivity.blackHome();
                MainActivity.whiteMap();
                MainActivity.blackLoyal();
//                homeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_home_white_24dp));
//                mapsBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_place_black_24dp));
//                loyalBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_loyalty_white_24dp));
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, JavaMapFragment.newInstance(shops.get(pos))).commit();
                mapsButton.setEnabled(true);
            }
        });


        final String dist;
        if (DistanceCalculator.stringifyDistance(DistanceCalculator.getDistanceToCoffeeShop(usrLoc.getLat(), usrLoc.getLng(), shops.get(position).getLatitude(), shops.get(position).getLongitude())) != null)
        {
            dist = DistanceCalculator.stringifyDistance(DistanceCalculator.getDistanceToCoffeeShop(usrLoc.getLat(), usrLoc.getLng(), shops.get(position).getLatitude(), shops.get(position).getLongitude()));
        }
        else
        {
            dist = null;
        }

        final String shopEMAIL = shops.get(position).getEmail();
        final int shopID = shops.get(position).getId();
        final String brandName = shops.get(position).getActualBrandName();


        viewRow.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                FloatingActionButton fab = (FloatingActionButton) viewRow.findViewById(R.id.fab);
                if (dist != null)
                {
                    MainActivity.whiteHome();

                    MainActivity.dismissFloatingActionButton();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment, SelectedshopFragment.newInstance(shopID, shopEMAIL, dist, brandName)).addToBackStack("A_B_TAG").commit();
                }
                else
                {
                    MainActivity.whiteHome();
                    MainActivity.dismissFloatingActionButton();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment, SelectedshopFragment.newInstance(shopID, shopEMAIL, brandName)).addToBackStack("A_B_TAG").commit();
                }
            }
        });

        return viewRow;
    }


}
