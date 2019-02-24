package kasper.pagh.keebin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import rest.userReST.AddCoffee;

/**
 * Created by pelle on 12/12/2016.
 */

public class AddCoffeeToLoyaltycardFragment extends Fragment implements AsyncResponse
{
    EditText coffeeCode;
    EditText numberOfCoffeesBought;
    AsyncResponse thisPage = this;
    private ProgressDialog progress;

    public static AddCoffeeToLoyaltycardFragment newInstance()
    {
        Bundle args = new Bundle();
        AddCoffeeToLoyaltycardFragment fragment = new AddCoffeeToLoyaltycardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.coffee_add, container, false);
        coffeeCode = (EditText) view.findViewById(R.id.coffeeCode);
        numberOfCoffeesBought = (EditText) view.findViewById(R.id.numberOfCoffeesBought);
        final Button button = (Button) view.findViewById(R.id.addCoffee);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                button.setEnabled(false);
                showLoadingDialog();
                AddCoffee addCoffee = new AddCoffee(getResources().getString(R.string.baseUrl), MainActivity.currentUser, coffeeCode.getText().toString(), numberOfCoffeesBought.getText().toString(), thisPage, getContext());
                dismissLoadingDialog();
                button.setEnabled(true);
                addCoffee.execute();
            }
        });
        final Button button2 = (Button) view.findViewById(R.id.addklippekortButton);
        button2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                button2.setEnabled(false);
                showLoadingDialog();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, KlippekortKøbFragment.newInstance()).commit();
                dismissLoadingDialog();
                button2.setEnabled(true);
            }
        });
        return view;
    }


    @Override
    public void processFinished(String output)
    {
        if (output.startsWith("Fejl:"))
        {
            dismissLoadingDialog();
            Toast.makeText(getActivity(), output.substring(5), Toast.LENGTH_LONG).show();
        } else
        {
            dismissLoadingDialog();
            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, UsersLoyaltyCardsFragment.newInstance()).commit();
        }
    }
    public void showLoadingDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this.getContext());
            progress.setTitle("loading");
            progress.setMessage("Loading please stand by");
        }
        progress.show();
    }

    public void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}