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

import java.util.List;

import DB.DatabaseHandler;
import entity.LoyaltyCard;
import entity.PrePaidCard;
import entity.UserPrepaid;
import rest.coffeeRest.UsePrepaidCard;
import rest.userReST.PutRedeem;



public class SelectedKlippekortFragment extends Fragment implements AsyncResponse
{
    private DatabaseHandler dbh;
    private ImageView brandPic;
    private TextView infoBox;
    private Button redeem;
    private Gson gson = new Gson();
    AsyncResponse thisPage = this;
    private int brandId;
    private TextView errorText;
    private int usesLeft;
    private String prepaidName;

    public SelectedKlippekortFragment()
    {
    }


    public static SelectedKlippekortFragment newInstance(UserPrepaid card, List<PrePaidCard> prepaidCard) {
        Bundle args = new Bundle();
        for (PrePaidCard vari : prepaidCard) {
            Integer id = vari.getId();
            if (id.equals(card.getPrePaidCoffeeCardId())) {
                args.putString("brandId", vari.getCoffeeBrandId());
                args.putString("prepaidName", vari.getName());
            }
        }
            args.putInt("usesLeft", card.getUsesleft());
            args.putInt("prepaidId", card.getId());
            SelectedKlippekortFragment fragment = new SelectedKlippekortFragment();
            fragment.setArguments(args);
            Log.d("her er kortet fra pre: ", card.toString());
            return fragment;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.klippekort_info_layout, container, false);
        brandPic = (ImageView) view.findViewById(R.id.SelectedklippekortBrandImage);
        infoBox = (TextView) view.findViewById(R.id.SelectedklippekortInfoBox);
        redeem = (Button) view.findViewById(R.id.selectedklippekortRedeem);

        dbh = new DatabaseHandler(getActivity());
        final Bundle args = getArguments();
        prepaidName = args.getString("prepaidName");
        usesLeft = args.getInt("usesLeft");
        errorText = (TextView)view.findViewById(R.id.selectedklippekortErrortext);
        brandId = Integer.parseInt(args.getString("brandId"));
        if(args.getInt("usesLeft")<1){
            redeem.setVisibility(View.INVISIBLE);
        }

        redeem.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                Log.d("her er info: ", "userID: " + MainActivity.currentUser.getId() + " PrepaidId: " + args.getInt("prepaidId"));
                UsePrepaidCard redeem = new UsePrepaidCard(getResources().getString(R.string.baseUrl),MainActivity.currentUser.getId(), args.getInt("prepaidId"),1 ,thisPage, getContext());
                redeem.execute();
            }
        });



        if(usesLeft>0)
        {
            brandPic.setImageBitmap(dbh.getBrandPicture(dbh.getBrandbyId(brandId).getBrandName()));
            if(usesLeft<2){
                infoBox.setText("Dette klippekort er til: " + dbh.getBrandbyId(brandId).getBrandName() + "\n" +
                        "Kortet hedder: " + prepaidName + "\n" +
                        "På dette kort er der et klip tilbage.");
            }else {
                infoBox.setText("Dette klippekort er til: " + dbh.getBrandbyId(brandId).getBrandName() + "\n" +
                        "Kortet hedder: " + prepaidName + "\n" +
                        "På dette kort er der " + usesLeft + " klip tilbage.");
            }


        }else
        {
            infoBox.setText("Dette klippekort er til: " + dbh.getBrandbyId(brandId).getBrandName() + "\n" +
                    "Kortet hedder: " + prepaidName + "\n" +
                    "Du har desværre ikke flere klip tilbage på dette kort.");
        }


        return view;
    }


    @Override
    public void processFinished(String output)
    {
        Log.d("her er outpu: ", " og det er far lort" + output );
        if (output.startsWith("Fejl:"))
        {
            errorText.setText("Der skete en fejl. Prøv igen senere");

        }else if(output.equals("alt gik godt")){
            errorText.setText("Tak fordi du indløste en kop kaffe");
            usesLeft += -1;
            if (usesLeft>0){
                if(usesLeft<2){
                    infoBox.setText("Dette klippekort er til: " + dbh.getBrandbyId(brandId).getBrandName() + "\n" +
                            "Kortet hedder: " + prepaidName + "\n" +
                            "På dette kort er der et klip tilbage.");
                }else {
                    infoBox.setText("Dette klippekort er til: " + dbh.getBrandbyId(brandId).getBrandName() + "\n" +
                            "Kortet hedder: " + prepaidName + "\n" +
                            "På dette kort er der " + usesLeft + " klip tilbage.");
                }
            }else{
                redeem.setVisibility(View.INVISIBLE);
                infoBox.setText("Dette klippekort er til: " + dbh.getBrandbyId(brandId).getBrandName() + "\n" +
                        "Kortet hedder: " + prepaidName + "\n" +
                        "Du har desværre ikke flere klip tilbage på dette kort.");
            }
        }else{
            errorText.setText("Noget gik galt. Prøv igen senere");
        }

    }
}