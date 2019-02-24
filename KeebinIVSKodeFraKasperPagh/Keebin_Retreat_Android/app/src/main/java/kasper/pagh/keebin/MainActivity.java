package kasper.pagh.keebin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DB.DatabaseHandler;
import DB.GetBrandPicturesAndSaveToDB;
import DB.GetCoffeeBrandsAndSaveToDB;
import entity.OrderItem;
import entity.UserLocation;
import entity.User;
import rest.NetworkChecker;
import rest.houseKeeping.GetDBVersion;
import utils.Loaders.CustomProgressbar;

//All activities that deal with network MUST implement the AsyncResponse interface to get the data from the request. See: http://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
public class MainActivity extends AppCompatActivity implements AsyncResponse, ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, DBListener
{
    private Gson gson = new Gson();
    public static User currentUser;
    public static List<OrderItem> globalOrderList;
    FloatingActionButton fab;
    private DatabaseHandler dbh = new DatabaseHandler(this);
    private GoogleApiClient mGoogleApiClient;
    private UserLocation userLocation;
    LocationRequest locationRequest;
    FusedLocationProviderApi fusedLocationProviderApi;
    private boolean firstMenuClickHasHappened = false;
    List<ImageButton> buttons = new ArrayList<ImageButton>();
    static FloatingActionButton fabShare;
    static ImageButton home_menu;
    static ImageButton loyal_menu;
    static ImageButton map_menu;
    static Context ctx;
    DBListener dbListener = this;


    public void resetOrder()
    {
        globalOrderList = new ArrayList<OrderItem>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        globalOrderList = Collections.synchronizedList(new ArrayList<OrderItem>());
        setContentView(R.layout.activity_main);
        showLoadingDialog();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabShare = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                fab.setEnabled(false);
//                getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, AddCoffeeToLoyaltycardFragment.newInstance()).commit();
//                getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, OrderFragment.newInstance()).commit();
                fab.setEnabled(true);
            }
        });

        Intent intent = getIntent();
        String unparsedCurrentUser = intent.getStringExtra("unparsedCurrentUser");
        currentUser = gson.fromJson(unparsedCurrentUser, User.class);


        home_menu = (ImageButton) findViewById(R.id.home_menu);
        loyal_menu = (ImageButton) findViewById(R.id.loyaltycards_menu);
        map_menu = (ImageButton) findViewById(R.id.maps_menu);
        ctx = this;


        GetDBVersion getDBVers = new GetDBVersion(getResources().getString(R.string.baseUrl), this, this);
        getDBVers.execute();


//        GetBrandPicturesAndSaveToDB picSaver = new GetBrandPicturesAndSaveToDB(this, this);
//        GetCoffeeBrandsAndSaveToDB yes = new GetCoffeeBrandsAndSaveToDB(getApplicationContext(), picSaver);
//        yes.getAllCoffeeBrands(getApplicationContext());


        if (!firstMenuClickHasHappened)
        {
            ImageButton button = (ImageButton) findViewById(R.id.home_menu);
            button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_black_24dp));
            buttons.add(button);
        }


        getLocation();
    }

    @Override
    protected void onStart()
    {

        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void processFinished(String output)
    {
        Log.d("MAIN", "her er output: " + output);
        //fra http://stackoverflow.com/a/13237848
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        try
        {
            int dbNoFromServer = Integer.parseInt(output.trim());
            if (settings.getBoolean("my_first_time", true))
            {
                //the app is being launched for first time, do something
                Log.d("MAIN", "First time");

                dbh.addOrUpdateDbVersion(Integer.parseInt(output.trim()));

                // first time task
                GetBrandPicturesAndSaveToDB picSaver = new GetBrandPicturesAndSaveToDB(ctx, dbListener);
                GetCoffeeBrandsAndSaveToDB yes = new GetCoffeeBrandsAndSaveToDB(getApplicationContext(), picSaver);
                yes.getAllCoffeeBrands(getApplicationContext());

                // record the fact that the app has been started at least once
                settings.edit().putBoolean("my_first_time", false).commit();
            }
            else if (dbNoFromServer == (dbh.getDatabaseVersion()))
            {
                //Make same as above, without the inet stuff, do nothing mb?
                Log.d("MAIN", "Equals not first time");
                DBReady();


            }
            else if (dbNoFromServer != (dbh.getDatabaseVersion()))
            {
                Log.d("MAIN", "Not equals not first time, her er værdi: " + dbh.getDatabaseVersion() + "");

                dbh.addOrUpdateDbVersion(Integer.parseInt(output.trim()));
                GetBrandPicturesAndSaveToDB picSaver = new GetBrandPicturesAndSaveToDB(ctx, dbListener);
                GetCoffeeBrandsAndSaveToDB yes = new GetCoffeeBrandsAndSaveToDB(getApplicationContext(), picSaver);
                yes.getAllCoffeeBrands(getApplicationContext());
            }


        }
        catch (android.database.CursorIndexOutOfBoundsException e)
        {
            Log.d("MAIN", "kunne ikke få DB vers, kører med den vi har!");
            dismissLoadingDialog();
            e.printStackTrace();
        }
        catch (NumberFormatException nFE)
        {
            dismissLoadingDialog();
            nFE.printStackTrace();
        }

    }


    public void link_home(View v)
    {
        ImageButton button = (ImageButton) findViewById(R.id.home_menu);
        Log.d("link_home", button.getId() + "");
        button.setEnabled(false);
        getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, NewIndex.newInstance()).commit();
        firstMenuClickHasHappened = true;

        for (ImageButton btn : buttons)
        {
            switch (btn.getId())
            {
                case R.id.home_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_black_24dp));
                    break;
                case R.id.maps_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_white_24dp));
                    break;
                case R.id.loyaltycards_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_white_24dp));
                    break;
            }
        }
        button.setEnabled(true);
        fab.setVisibility(View.VISIBLE);

    }

    public void link_maps(View v)
    {

        ImageButton button = (ImageButton) findViewById(R.id.maps_menu);
        buttons.add(button);
        Log.d("link_maps", button.getId() + "");
        button.setEnabled(false);
        getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, JavaMapFragment.newInstance()).commit();
        firstMenuClickHasHappened = true;
        for (ImageButton btn : buttons)
        {
            switch (btn.getId())
            {
                case R.id.home_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_white_24dp));
                    break;
                case R.id.maps_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_black_24dp));
                    break;
                case R.id.loyaltycards_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_white_24dp));
                    break;
            }
        }
        button.setEnabled(true);
        fab.setVisibility(View.INVISIBLE);

    }


    public void link_loyaltycards(View v)
    {

        ImageButton button = (ImageButton) findViewById(R.id.loyaltycards_menu);
        buttons.add(button);
        button.setEnabled(false);
        getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, SwipeFunction.newInstance()).commit();
        firstMenuClickHasHappened = true;
        for (ImageButton btn : buttons)
        {
            switch (btn.getId())
            {
                case R.id.home_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_white_24dp));
                    break;
                case R.id.maps_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_white_24dp));
                    break;
                case R.id.loyaltycards_menu:
                    btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_black_24dp));
                    break;
            }
        }
        button.setEnabled(true);
        fab.setVisibility(View.VISIBLE);

    }


    PopupMenu settingsPopup;

    public void SettingsPopup(View v)
    {
        if (settingsPopup == null)
        {
            settingsPopup = new PopupMenu(this, v);
            MenuInflater inflater = settingsPopup.getMenuInflater();
            inflater.inflate(R.menu.settings, settingsPopup.getMenu());
            settingsPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(android.view.MenuItem item)
                {

                    fab.setVisibility(View.INVISIBLE);
                    switch (item.getItemId())
                    {
                        case R.id.settings1:
                            for (ImageButton btn : buttons)
                            {
                                switch (btn.getId())
                                {
                                    case R.id.home_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_white_24dp));
                                        break;
                                    case R.id.maps_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_white_24dp));
                                        break;
                                    case R.id.loyaltycards_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_white_24dp));
                                        break;
                                }
                            }
                            YourInfo();
                            return true;
                        case R.id.settings2:
                            for (ImageButton btn : buttons)
                            {
                                switch (btn.getId())
                                {
                                    case R.id.home_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_white_24dp));
                                        break;
                                    case R.id.maps_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_white_24dp));
                                        break;
                                    case R.id.loyaltycards_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_white_24dp));
                                        break;
                                }
                            }
                            KeebinInfo();
                            return true;
                        case R.id.Setting3:
                            for (ImageButton btn : buttons)
                            {
                                switch (btn.getId())
                                {
                                    case R.id.home_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_white_24dp));
                                        break;
                                    case R.id.maps_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_white_24dp));
                                        break;
                                    case R.id.loyaltycards_menu:
                                        btn.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_white_24dp));
                                        break;
                                }
                            }
                            logout();
                            return true;
                    }
                    return false;
                }
            });

        }
        settingsPopup.show();
    }


    public void YourInfo()
    {
        ImageButton button = (ImageButton) findViewById(R.id.settingsBut);
        button.setEnabled(false);
        fab.setVisibility(View.INVISIBLE);
        getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, YourInfoFragment.newInstance()).commit();
        button.setEnabled(true);
    }

    public void KeebinInfo()
    {
        ImageButton button = (ImageButton) findViewById(R.id.settingsBut);
        button.setEnabled(false);
        fab.setVisibility(View.INVISIBLE);
//        getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, KeebinInfoFragment.newInstance()).commit();
        getSupportFragmentManager().beginTransaction().addToBackStack("").replace(R.id.fragment, OrderFragment.newInstance()).commit();
        button.setEnabled(true);

    }


    public void logout()
    {
        ImageButton button = (ImageButton) findViewById(R.id.settingsBut);
        resetApp();
        button.setEnabled(false);
    }

    //reset of the db for logout
    public void resetApp()
    {
        try
        {
            currentUser = null;
            dbh.deleteToken("accessToken");
            dbh.deleteToken("refreshToken");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Intent mStartActivity = new Intent(this, LoginActivity.class);
            startActivity(mStartActivity);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        try
        {
            fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
        catch (SecurityException e)
        {
            Log.d("MAIN", "ingen tilladelse til LOC");
        }
    }

    private void getLocation()
    {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000L);
        locationRequest.setFastestInterval(5000L);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onLocationChanged(Location location)
    {
        if (location.getLatitude() == 0 || location.getLongitude() == 0)
        {
        }
        else
        {
            userLocation = new UserLocation(1, location.getLatitude(), location.getLongitude());
            dbh.addLocation(userLocation);
        }
    }


    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void DBReady()
    {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, NewIndex.newInstance()).commit();
        dismissLoadingDialog();
    }

    public void showLoadingDialog()
    {
        CustomProgressbar.showProgressBar(this, false);

    }

    public void dismissLoadingDialog()
    {
        CustomProgressbar.hideProgressBar();

    }

    public static void dismissFloatingActionButton()
    {

        fabShare.setVisibility(View.INVISIBLE);
    }

    public static void callFloatingActionButton()
    {
        fabShare.setVisibility(View.VISIBLE);
    }


    public static void whiteHome()
    {
        home_menu.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_white_24dp));
    }

    public static void blackHome()
    {
        home_menu.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_home_black_24dp));
    }

    public static void whiteLoyal()
    {
        loyal_menu.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_white_24dp));
    }

    public static void blackLoyal()
    {
        loyal_menu.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_loyalty_black_24dp));
    }

    public static void whiteMap()
    {
        map_menu.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_white_24dp));
    }

    public static void blackMap()
    {
        map_menu.setBackground(ContextCompat.getDrawable(ctx, R.drawable.ic_place_black_24dp));
    }

}
