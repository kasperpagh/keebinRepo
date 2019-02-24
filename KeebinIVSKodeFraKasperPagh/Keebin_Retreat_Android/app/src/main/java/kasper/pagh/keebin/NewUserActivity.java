package kasper.pagh.keebin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import entity.User;
import rest.userReST.NewUser;

/**
 * Created by pelle on 2/14/2017.
 */

public class NewUserActivity extends AppCompatActivity implements AsyncResponse
{
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editRepeatPassword;
    private EditText editTextFirstname;
    private EditText editTextLastname;
    private TextView errorText;
    private RadioGroup radioGrp;
    private int mYear;
    private int mMonth;
    private int mDay;
    private RadioButton selectedSex;
    private TextView mDateDisplay;
    private Button mPickDate;

    static final int DATE_DIALOG_ID = 0;

    boolean isEmailValid(CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
        editTextEmail = (EditText) findViewById(R.id.newEmail);
        editTextFirstname = (EditText) findViewById(R.id.newFirstName);
        editTextLastname = (EditText) findViewById(R.id.newLastname);
        editTextPassword = (EditText) findViewById(R.id.newPassword);
        editRepeatPassword = (EditText) findViewById(R.id.repeatNewPassword);
        errorText = (TextView) findViewById(R.id.newUserError);
        radioGrp = (RadioGroup) findViewById(R.id.radioGrp);


//        sex = (Spinner) findViewById(R.id.newSex);
//        birthday = (Spinner) findViewById(R.id.newBirthDay);
//        String[] items = new String[]{"male", "female"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        String[] items2 = new String[]{"1994", "1995", "1996", "1997"};
//        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items2);
//        sex.setAdapter(adapter);
//        birthday.setAdapter(adapter2);
//        mDateDisplay = (TextView) findViewById(R.id.showMyDate);
        mPickDate = (Button) findViewById(R.id.myDatePickerButton);

        mPickDate.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date
        updateDisplay();
    }

    private void updateDisplay()
    {
        this.mPickDate.setText(new StringBuilder()
                // Month is 0 based so add 1

                .append(mDay).append("-")
                .append(mMonth + 1).append("-")
                .append(mYear).append(" "));

//        this.mDateDisplay.setText(
//                new StringBuilder()
//                        // Month is 0 based so add 1
//
//                        .append(mDay).append("-")
//                        .append(mMonth + 1).append("-")
//                        .append(mYear).append(" "));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener()
            {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth)
                {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }


    public void submitUser(View view)
    {
        if (isEmailValid(editTextEmail.getText() + ""))
        {
            if (!editTextPassword.getText().toString().equalsIgnoreCase("") && editTextPassword.getText().toString().equalsIgnoreCase(editRepeatPassword.getText().toString()))
            {
                int radioId = radioGrp.getCheckedRadioButtonId();
                selectedSex = (RadioButton) findViewById(radioId);
                String sex = selectedSex.getText().toString();
                User userToSave = new User(editTextFirstname.getText() + "", editTextLastname.getText() + "", editTextEmail.getText() + "", mYear + "-" + mMonth + "-" + mDay + " 00:00:00", sex, 2, editTextPassword.getText() + "");
                NewUser newuser = new NewUser(getResources().getString(R.string.loginString), userToSave, this, getApplicationContext());

                newuser.execute();
            }
            else
            {
                errorText.setText("Vær venlig at udfylde alle fælter markeret med stjerne");
            }
        }
        else
        {
            errorText.setText("Vær venlig at indtaste en valid email.");
        }
    }

    @Override
    public void processFinished(String output)
    {
        if (output.startsWith("Fejl:"))
        {
            errorText.setText(output.substring(5));

        }
        else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("newEmail", editTextEmail.getText().toString());
            intent.putExtra("newPw", editTextPassword.getText().toString());
            startActivity(intent);
        }

    }
}
