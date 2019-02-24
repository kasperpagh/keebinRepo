package kasper.pagh.keebin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import entity.PrePaidCard;
import rest.coffeeRest.NewPrepaidCardRest;


public class KlippekortFinal extends Fragment implements AsyncResponse {
    KlippekortFinal thisFragment;
    EditText coffeeCode;
    TextView done;
    public static KlippekortFinal newInstance(int id , String name,String price, String brandName)
    {
        Bundle args = new Bundle();
        KlippekortFinal fragment = new KlippekortFinal();
        args.putInt("storeCardId",id);
        args.putString("name",name);
        args.putString("price",price);
        args.putString("brandName",brandName);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.klippekort_final, container, false);
        coffeeCode = (EditText)view.findViewById(R.id.coffeeCodeFinal);
        done = (TextView) view.findViewById(R.id.done);
        TextView name = (TextView)view.findViewById(R.id.prepaidName);
        TextView count = (TextView)view.findViewById(R.id.prepaidCost);
        TextView brandName = (TextView)view.findViewById(R.id.brandFinal);
        Button buyKlippekort = (Button)view.findViewById(R.id.buyKlippekort);
        thisFragment = this;

        name.setText(getArguments().getString("name"));
        count.setText(getArguments().getString("price"));
        brandName.setText(getArguments().getString("brandName"));
        buyKlippekort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                NewPrepaidCardRest prepaidCard = new NewPrepaidCardRest(getResources().getString(R.string.baseUrl),coffeeCode.getText() + "",MainActivity.currentUser.getId(),getArguments().getInt("storeCardId"),thisFragment,getContext());
                prepaidCard.execute();

            }
        });



        return view;
    }
    public void processFinished(String output) {
        if (output.startsWith("Fejl:")){
            done.setText("Der er desværre gået noget galt i dit køb af kaffekort. prøve igen på et andet tidspunk");
            }else {
            coffeeCode.setText("");
            done.setText("tak fordi du købte et klippekort hos " + getArguments().getString("brandName"));
        }


    }
}
