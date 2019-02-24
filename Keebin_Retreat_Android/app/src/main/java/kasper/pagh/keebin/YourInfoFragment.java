package kasper.pagh.keebin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import android.support.v4.app.DialogFragment;

import java.util.Calendar;

import com.google.gson.Gson;

import entity.User;
import rest.userReST.PutUser;

/**
 * Created by pelle on 12/6/2016.
 */


public class YourInfoFragment extends Fragment implements AsyncResponse
{
    private EditText newEmail;
    private EditText firstName;
    private EditText lastName;
    private EditText newPassword;
    private EditText repeatNewPassword;
    private Button saveEditButton;
    private EditText oldPassword;
    private AsyncResponse delegate;

    public static YourInfoFragment newInstance()
    {
        Bundle args = new Bundle();
        YourInfoFragment fragment = new YourInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.edit_user, container, false);

        delegate = this;
        newEmail = (EditText) view.findViewById(R.id.newEmail);

        firstName = (EditText) view.findViewById(R.id.newFirstName);
        lastName = (EditText) view.findViewById(R.id.newLastname);

        newPassword = (EditText) view.findViewById(R.id.newPassword);
        repeatNewPassword = (EditText) view.findViewById(R.id.repeatNewPassword);
        oldPassword = (EditText) view.findViewById(R.id.oldPassword);

        newEmail.setHint(MainActivity.currentUser.getEmail());
        firstName.setHint(MainActivity.currentUser.getFirstName());
        lastName.setHint(MainActivity.currentUser.getLastName());


        saveEditButton = (Button) view.findViewById(R.id.saveEditButton);

        saveEditButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d("EDIT", "her er curUsr: " + MainActivity.currentUser.toString());

                String forNavn;
                if (firstName.getText().toString().equalsIgnoreCase(""))
                {
//                    forNavn = MainActivity.currentUser.getFirstName();
                    forNavn = "";
                }
                else
                {
                    forNavn = firstName.getText().toString();
                }

                String efterNavn = "";
                if (lastName.getText().toString().equalsIgnoreCase(""))
                {
//                    efterNavn = MainActivity.currentUser.getFirstName();
                    forNavn = "";
                }
                else
                {
                    efterNavn = lastName.getText().toString();
                }


                String ePost;
                if (newEmail.getText().toString().equalsIgnoreCase(""))
                {
//                    ePost = MainActivity.currentUser.getEmail();
                    ePost = "";
                }

                else
                {
                    ePost = newEmail.getText().toString();
                }


                int roleId = 2;

                String password;
                if (newPassword.getText().toString() != null && !newPassword.getText().toString().equals("") && newPassword.equals(repeatNewPassword))
                {
                    password = newPassword.getText().toString();
                }
                else

                {
                    password = "";
                }

                String gamelPassword;
                if (oldPassword.getText().toString() != null && !oldPassword.getText().toString().equals(""))
                {
                    gamelPassword = oldPassword.getText().toString();
                }
                else

                {
                    gamelPassword = "";
                }


                User userToSave = new User(forNavn, efterNavn, ePost, "", "", 2, password);

                PutUser pu = new PutUser(gamelPassword, userToSave, delegate, getActivity(), getResources().getString(R.string.baseUrl), MainActivity.currentUser.getEmail());
                pu.execute();
            }
        });


        return view;
    }


    @Override
    public void processFinished(String output)
    {
        Log.d("EDIT", "her er res: " + output);
        if (output.startsWith("Fejl:"))
        {
            Toast.makeText(getActivity(), output.substring(5), Toast.LENGTH_LONG).show();
        }
        else
        {
            Gson gson = new Gson();
            User user = gson.fromJson(output, User.class);
            Log.d("EDIT", "ny usr: " + user.toString());
            Log.d("EDIT", "gammel usr: " + MainActivity.currentUser.toString());
            Toast.makeText(getActivity(), "Profil redigeret", Toast.LENGTH_LONG).show();
//            MainActivity.currentUser = user;
        }
    }


}
