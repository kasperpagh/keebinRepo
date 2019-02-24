package kasper.pagh.keebin;

/**
 * Created by dino on 08-12-2016.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import rest.coffeeRest.GetBrandByID;
import rest.coffeeRest.GetShopByEmail;
import DB.DatabaseHandler;
import entity.CoffeeBrand;
import entity.CoffeeShop;
import utils.HtmlParser;

public class SelectedshopFragment extends Fragment implements AsyncResponse
{

    public SelectedshopFragment()
    {

    }

    DatabaseHandler dbh;
    View View;
    TextView InfoBox;
    String distanceToShop;
    CoffeeBrand brand;
    TextView SpecificShopBrandNameBox;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.selectedshop, container, false);
        View = view;
        Bundle bundle = getArguments();
        int ID = bundle.getInt("shopIDs");
        String email = bundle.getString("shopMail");
        if (bundle.getString("distance") != null)
        {
            distanceToShop = bundle.getString("distance");
        }
        else
        {
            distanceToShop = "ukendt";
        }

        GetBrandName bn = new GetBrandName();
        bn.getfromID(ID);
        GetShopByEmail s = new GetShopByEmail(getResources().getString(R.string.baseUrl), email, this, getActivity());
        s.execute();

        ImageView shopPic = (ImageView) view.findViewById(R.id.ShopPicture);
//        TextView shopPicOverlay = (TextView) view.findViewById(R.id.ShopPictureOverlay);
        InfoBox = (TextView) view.findViewById(R.id.InfoBox);

        dbh = new DatabaseHandler(getActivity());
        shopPic.setImageBitmap(dbh.getBrandPicture(bundle.getString("brandName")));
        InfoBox.setTextColor(Color.BLACK);
        SpecificShopBrandNameBox = (TextView) view.findViewById(R.id.SpecificShopBrandNameBox);
        return view;
    }

    public static SelectedshopFragment newInstance(Integer shopID, String shopMail, String distanceToUser, String brandName)
    {
        Bundle args = new Bundle();
        args.putInt("shopIDs", shopID);
        args.putString("shopMail", shopMail);
        args.putString("distance", distanceToUser);
        args.putString("brandName", brandName);
        SelectedshopFragment fragment = new SelectedshopFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectedshopFragment newInstance(Integer shopID, String shopMail, String brandName)
    {
        Bundle args = new Bundle();
        args.putInt("shopIDs", shopID);
        args.putString("shopMail", shopMail);
        args.putString("brandName", brandName);
        SelectedshopFragment fragment = new SelectedshopFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public class GetBrandName implements AsyncResponse
    {
        public void getfromID(int ShopID)
        {
            GetBrandByID s = new GetBrandByID(getResources().getString(R.string.baseUrl), ShopID, this, getActivity());
            s.execute();
        }

        @Override
        public void processFinished(String output)
        {
            if (output.startsWith("Fejl:"))
            {
                TextView errorText = (TextView) getActivity().findViewById(R.id.InfoBox);
                errorText.setText("Der skete desværre en fejl!");
            }
            else
            {
                String s = output.replaceAll("brandId", "brandName");
                brand = gson.fromJson(s, CoffeeBrand.class);
            }
        }
    }


    private Gson gson = new Gson();

    @Override
    public void processFinished(String output)
    {
        if (output.startsWith("Fejl:"))
        {
            TextView errorText = (TextView) getActivity().findViewById(R.id.InfoBox);
            errorText.setText("Der skete desværre en fejl!");
        }
        else
        {
            String s = output.replaceAll("brandId", "brandName"); // gør kun det her fordig cba at lave det om i serveren's respons eller hente det nye ned lol
            CoffeeShop shop = gson.fromJson(s, CoffeeShop.class);
            String phone = shop.getPhone();
            if (shop.getPhone().length() == 8) // if the shop length is equal to 8 characters, split it up.
            {
                phone = shop.getPhone().substring(0, 2) + " " + shop.getPhone().substring(2, 4) + " " + shop.getPhone().substring(4, 6) + " " + shop.getPhone().substring(6, 8);
            }
            SpecificShopBrandNameBox.setText(brand.getBrandName());

            InfoBox.setText(HtmlParser.fromHtml(
                    "<i><b> Adresse - </b></i>" + shop.getAddress() + "<br />" +
                    "<br />" +
                    "<i><b> Telefon - </b></i>  +45 " + phone + "<br />" +
                    "<br />" +
                    "<i><b> Email - </b></i>" + shop.getEmail() +
                    "<br />" +
                    "<br />" +
                    "<i><b> Afstand fra dig - </b></i> " + distanceToShop));
        }
    }
}