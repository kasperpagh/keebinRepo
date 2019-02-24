package kasper.pagh.keebin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import DB.DatabaseHandler;
import entity.LoyaltyCard;
import rest.userReST.PutRedeem;

/**
 * Created by kaspe on 2017-02-08.
 */

public class SelectedLoyaltyCardFragment extends Fragment implements AsyncResponse
{
    private DatabaseHandler dbh;
    private ImageView brandPic;
    private TextView infoBox;
    private Button redeem;
    private Gson gson = new Gson();
    AsyncResponse thisPage = this;
    private String brandName;
    private String dateIssued;
    private TextView errorText;
    private int coffeesBought;
    private int cupsLeftBeforeFreeCup;

    public SelectedLoyaltyCardFragment()
    {
    }


    public static SelectedLoyaltyCardFragment newInstance(LoyaltyCard card)
    {
        Bundle args = new Bundle();
        args.putString("brandName", card.getNameOfBrand());
        Log.d("her er id før args: ",""+ card.getId());
        args.putInt("loyaltycardID",card.getId());
        args.putBoolean("isValid", card.isValid());
        args.putBoolean("readyForFreeCoffee", card.isReadyForFreeCoffee());
        args.putString("dateIssued",card.getCreatedAt().toString().substring(0,9));
        args.putInt("numberOfCoffeesBought", card.getNumberOfCoffeesBought());
        args.putString("numberOfNeededCoffees" , card.getMaxCoffee() +"");
        args.putInt("numberOfFreeCoffeeAvailable",card.getNumberOfFreeCoffeeAvailable());
        SelectedLoyaltyCardFragment fragment = new SelectedLoyaltyCardFragment();
        fragment.setArguments(args);
        Log.d("her er kortet", card.toString());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.loyalty_card_info_screen, container, false);
        brandPic = (ImageView) view.findViewById(R.id.SelectedLoyaltyCardBrandImage);
        infoBox = (TextView) view.findViewById(R.id.SelectedLoyaltyCardInfoBox);
        redeem = (Button) view.findViewById(R.id.selectedLoyaltycardRedeem);
        dbh = new DatabaseHandler(getActivity());
        final Bundle args = getArguments();
        errorText = (TextView)view.findViewById(R.id.selectedErrortext);
        brandName = args.getString("brandName");
        dateIssued = args.getString("dateIssued");
        coffeesBought = Integer.parseInt(args.getString("numberOfNeededCoffees"));
        cupsLeftBeforeFreeCup = coffeesBought - args.getInt("numberOfCoffeesBought");
        if(args.getInt("numberOfFreeCoffeeAvailable")<1){
            redeem.setVisibility(View.INVISIBLE);
        }
        redeem.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                PutRedeem redeem = new PutRedeem(getResources().getString(R.string.baseUrl),args.getInt("loyaltycardID"), MainActivity.currentUser.getId(),1 ,thisPage, getContext());
                redeem.execute();
            }
        });



        if(args.getBoolean("isValid") && args.getInt("numberOfFreeCoffeeAvailable")>0)
        {
            brandPic.setImageBitmap(dbh.getBrandPicture(brandName));
            if(args.getInt("numberOfFreeCoffeeAvailable")<2) {
                infoBox.setText("Dette loyalitetskort er til: " + brandName + "\n" +
                        "Kortet er udstedt d. " + dateIssued + "\n" +
                        "Dette kort er udfyldt, du har en kop kaffe til gode!");
            }else{
                infoBox.setText("Dette loyalitetskort er til: " + brandName + "\n" +
                        "Kortet er udstedt d. " + dateIssued + "\n" +
                        "Dette kort er udfyldt, du har " + args.getInt("numberOfFreeCoffeeAvailable")+ " kopper kaffe til gode!");
            }

        }
        if(args.getBoolean("isValid") && args.getInt("numberOfFreeCoffeeAvailable")<1)
        {
            brandPic.setImageBitmap(dbh.getBrandPicture(brandName));
            infoBox.setText("Dette loyalitetskort er til: " + brandName+ "\n" +
                   "Kortet er udstedt d. " + dateIssued + "\n" +
                    "Du mangler " + cupsLeftBeforeFreeCup + " kopper kaffe før dette kort er fyldt!");

        }
        if(!args.getBoolean("isValid"))
        {
            brandPic.setImageBitmap(dbh.getBrandPicture(brandName));
            infoBox.setText("Dette loyalitetskort er til: " + brandName + "\n" +
                    "Kortet er udstedt d. " + dateIssued + "\n" +
                    "KORTET ER DESVÆRRE IKKE LÆNGERE GYLDIGT, KONTAKT DIN KAFFEBAR");
        }

        return view;
    }


    @Override
    public void processFinished(String output)
    {

        if (output.startsWith("Fejl:"))
        {
            errorText.setText("Der skete en fejl. Prøv igen senere");

        }else{
            errorText.setText("Tak fordi du indløste en kop kaffe");
            LoyaltyCard card = gson.fromJson(output, LoyaltyCard.class);
            if (card.getNumberOfFreeCoffeeAvailable()>0){
                if(card.getNumberOfFreeCoffeeAvailable()<2){
                    infoBox.setText("Dette loyalitetskort er til: " + brandName + "\n" +
                            "Kortet er udstedt d. " + dateIssued + "\n" +
                            "Dette kort er udfyldt, du har en kop kaffe til gode!");
                }else {
                    infoBox.setText("Dette loyalitetskort er til: " + brandName + "\n" +
                            "Kortet er udstedt d. " + dateIssued + "\n" +
                            "Dette kort er udfyldt, du har " + card.getNumberOfFreeCoffeeAvailable()+ " kopper kaffe til gode!");
                }
            }else{
                redeem.setVisibility(View.INVISIBLE);
                infoBox.setText("Dette loyalitetskort er til: " + brandName+ "\n" +
                        "Kortet er udstedt d. " + dateIssued + "\n" +
                        "Du mangler " + cupsLeftBeforeFreeCup + " kopper kaffe før dette kort er fyldt!");
            }
        }
    }
}
