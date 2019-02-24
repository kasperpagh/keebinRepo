package adapters;

import android.content.Context;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import DB.DatabaseHandler;
import entity.CoffeeBrand;
import entity.CoffeeShop;
import entity.PrePaidCard;
import kasper.pagh.keebin.JavaMapFragment;
import kasper.pagh.keebin.KlippekortSecond;
import kasper.pagh.keebin.KlippekortSecond;
import kasper.pagh.keebin.R;



public class BrandArrayAdapter extends ArrayAdapter<CoffeeBrand> {

    private Context context;
    private List<CoffeeBrand> brands;
    private DatabaseHandler dbh;

    public BrandArrayAdapter(Context context, List<CoffeeBrand> brands)
    {
        super(context, R.layout.brand_layout, brands);
        this.brands = brands;
        this.context = context;
        dbh = new DatabaseHandler(context);
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View viewRow = inflater.inflate(R.layout.brand_layout, parent, false);

        TextView coffeeShopName = (TextView) viewRow.findViewById(R.id.brandName_layout);
        ImageView image = (ImageView) viewRow.findViewById(R.id.brandLogo);
        Log.d("her er brand:!!!!!",brands.get(position).getBrandName());
        coffeeShopName.setText(brands.get(position).getBrandName());
        int cardBrand = context.getResources().getIdentifier("logo", "drawable", "kasper.pagh.keebin");
        image.setImageBitmap(dbh.getBrandPicture(brands.get(position).getBrandName()));

        viewRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Da dette ikke er et Activity er vi nødt til at caste til FragmentActivity for at
                //kunnebruge supportFragmentManager
                Log.d("vi er i viewROW!!!"," KOM NU VIRK");
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, KlippekortSecond.newInstance(brands.get(position).getId(),brands.get(position).getBrandName())).commit();

            }
        });

        return viewRow;
    }
}
