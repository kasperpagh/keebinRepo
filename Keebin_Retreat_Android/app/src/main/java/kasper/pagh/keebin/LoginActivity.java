package kasper.pagh.keebin;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import DB.DatabaseHandler;
import rest.NetworkChecker;
import rest.userReST.GetUser;
import rest.userReST.PostLogin;
import utils.Loaders.CustomProgressbar;

public class LoginActivity extends AppCompatActivity implements AsyncResponse
{
    private DatabaseHandler dbh;
    private Gson gson = new Gson();
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView errorText;
    private ProgressDialog progress;
    private int MY_PERMISSION_REQUEST = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dbh = new DatabaseHandler(getApplicationContext());

        String[] permissions = new String[2];
        permissions[0] = android.Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[1] = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_REQUEST);
        errorText = (TextView) findViewById(R.id.loginError);
        if (dbh.hasObject("accessToken") || dbh.hasObject("refreshToken"))
        {
//            showLoadingDialog();
            GetUser getuser = new GetUser(getResources().getString(R.string.baseUrl), dbh.getTokenByName("email").getTokenData(), this, getApplicationContext());
            getuser.execute();
        }
        else
        {
            setContentView(R.layout.activity_login);
            editTextEmail = (EditText) findViewById(R.id.loginEmail);
            editTextPassword = (EditText) findViewById(R.id.loginPassword);
            if(getIntent().getStringExtra("newEmail") != null && getIntent().getStringExtra("newPw") != null)
            {
                editTextEmail.setText(getIntent().getStringExtra("newEmail"));
                editTextPassword.setText(getIntent().getStringExtra("newPw"));
            }
            else
            {
                editTextEmail.setText("tester@email.dk");
                editTextPassword.setText("123");
            }
            NetworkChecker nwc = new NetworkChecker(this);
            if (!nwc.networkChecker())
            {
                errorText.setText("ingen forbindelse, tjek internet");
            }
        }
    }

    public void login(View view)
    {
//        showLoadingDialog();
        Button button = (Button) findViewById(R.id.button_login);
        button.setEnabled(false);
        PostLogin postLogin = new PostLogin(getResources().getString(R.string.baseUrl), "https://keebin-keebin.rhcloud.com/", editTextEmail.getText().toString(), editTextPassword.getText().toString(), this, getApplicationContext());
        button.setEnabled(true);
        postLogin.execute();

    }

    public void newUser(View view)
    {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }

    @Override
    public void processFinished(String output)
    {

        if (output.startsWith("Fejl:"))
        {
//            dismissLoadingDialog();
            if (output.substring(5).endsWith("senere"))
            {
                errorText.setText(output);
            }
            if (output.substring(5).endsWith("input"))
            {
                errorText.setText("Forkert brugernavn eller password");
            }

        }
        else
        {


//            dismissLoadingDialog();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("unparsedCurrentUser", output);
            startActivity(intent);
        }
    }

    public void showLoadingDialog()
    {
        CustomProgressbar.showProgressBar(this, false);

    }

    public void dismissLoadingDialog()
    {
        CustomProgressbar.hideProgressBar();

    }
}
