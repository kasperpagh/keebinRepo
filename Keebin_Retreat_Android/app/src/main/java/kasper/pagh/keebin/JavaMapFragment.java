package kasper.pagh.keebin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import DB.DatabaseHandler;
import entity.CoffeeShop;
import entity.UserLocation;
import rest.coffeeRest.GetAllShopsWithBrandName;
import utils.Loaders.CustomProgressbar;
import utils.locationAndDistance.*;

/**
 * Created by kaspe on 2017-01-26.
 */

public class JavaMapFragment extends Fragment implements AsyncResponse, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private boolean searchBool = false;
    private boolean searchSpecificBool = false;
    private Bundle bundle;
    private GoogleApiClient mGoogleApiClient;
    private Context context = getActivity();
    private EditText searchtext;
    private AsyncResponse mapInstance = this;
    private GoogleMap gMap;
    private Gson gson = new Gson();
    private DatabaseHandler dbh;
    private CoffeeShop[] coffeeList;
    UserLocation userLocation;
    LatLng locationFromDb;
    private ProgressDialog progress;
    private String searchString;


    public JavaMapFragment()
    {

    }

    public static JavaMapFragment newInstance()
    {
        Bundle args = new Bundle();
        JavaMapFragment fragment = new JavaMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static JavaMapFragment newInstance(CoffeeShop shop)
    {
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", shop.getLatitude());
        bundle.putDouble("long", shop.getLongitude());
        bundle.putString("addr", shop.getAddress());
        bundle.putString("email", shop.getEmail());
        bundle.putString("name", shop.getActualBrandName());
        bundle.putString("phone", shop.getPhone());
        JavaMapFragment fragment = new JavaMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        showLoadingDialog();
        MainActivity.whiteHome();
        MainActivity.whiteLoyal();
        MainActivity.blackMap();
        dbh = new DatabaseHandler(getActivity());
        final View view = inflater.inflate(R.layout.map_layout, container, false);
        bundle = getArguments();
        view.findViewById(R.id.searchbutton_maps).setVisibility(View.INVISIBLE);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            buildApiGoogle();
            mGoogleApiClient.connect();
        }
        if (bundle.getDouble("long") != 0.0 && bundle.getDouble("lat") != 0.0)
        {
            container.findViewById(R.id.fab).setVisibility(View.INVISIBLE);
//            searchBool = true;
            searchSpecificBool = true;
        }
        new android.os.Handler().postDelayed(
                new Runnable()
                {
                    public void run()
                    {
                        Log.i("tag", "This'll run 300 milliseconds later");
                    }
                },
                300);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        //HER TIL KOM KASPER d. 26/1
//        ImageButton search = (ImageButton) view.findViewById(R.id.searchbutton_maps);
        Button delBtn = (Button) view.findViewById(R.id.searchbutton_maps);
        searchtext = (EditText) view.findViewById(R.id.easyGGmofo);
        GetAllShopsWithBrandName getAllShops = new GetAllShopsWithBrandName(getResources().getString(R.string.baseUrl), mapInstance, getActivity());
        getAllShops.execute();
        delBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                searchtext.setText("");
            }
        });



                searchtext.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                searchString = null;
                searchString = charSequence.toString();
                GetAllShopsWithBrandName getAllShops = new GetAllShopsWithBrandName(getResources().getString(R.string.baseUrl), mapInstance, getActivity());
                getAllShops.execute();
                searchBool = true;

                if(charSequence.toString().equalsIgnoreCase(""))
                {
                    view.findViewById(R.id.searchbutton_maps).setVisibility(View.INVISIBLE);
                    searchBool = false;
//                    getAllShops.execute();
                }
                else
                {
                    view.findViewById(R.id.searchbutton_maps).setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
//        search.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                GetAllShopsWithBrandName getAllShops = new GetAllShopsWithBrandName(getResources().getString(R.string.baseUrl), mapInstance, getActivity());
//                getAllShops.execute();
//                searchBool = true;
//
//            }
//        });


        return view;
    }


    @Override
    public void processFinished(String output)
    {

//        Toast.makeText(ctx, "updater curLoc, her er loc: " + dbh.getLocation(1).toString(), Toast.LENGTH_LONG).show();
        Log.d("MAPS", "I PROC FINSHED");
        gMap.clear();
        coffeeList = gson.fromJson(output, CoffeeShop[].class);
        try
        {
            Log.d("MAPS", "I PROC FINSHED TRY");
            userLocation = dbh.getLocation(1);

            locationFromDb = new LatLng(userLocation.getLat(), userLocation.getLng());

            if (searchBool)
            {
                Log.d("MAPS", "VI ER I JOHN");
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.coffeshopiconmaps);
                for (CoffeeShop shop : coffeeList)
                {

                    if (shop.getActualBrandName().toLowerCase().contains(searchString.toLowerCase()) || shop.getAddress().toLowerCase().contains(searchString.toLowerCase()) || shop.getEmail().toLowerCase().contains(searchString.toLowerCase()))
                    {
                        LatLng tempLatLng = new LatLng(shop.getLatitude(), shop.getLongitude());
                        MarkerOptions options = new MarkerOptions();
                        gMap.addMarker(options.position(tempLatLng).title(shop.getActualBrandName()).snippet(shop.getAddress() + "\n" + " Afstand til dig: " + DistanceCalculator.stringifyDistance(DistanceCalculator.getDistanceToCoffeeShop(userLocation.getLat(), userLocation.getLng(), shop.getLatitude(), shop.getLongitude()))));
//                        gMap.addMarker(new MarkerOptions().position(locationFromDb).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Du er her!"));
                        CircleOptions circleOptions = new CircleOptions()
                                .center(locationFromDb)
                                .radius(1000)
                                .strokeWidth(2)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.parseColor("#500084d3"));
//                        gMap.addCircle(circleOptions);
                        CircleOptions circleOptionsDot = new CircleOptions()
                                .center(locationFromDb)
                                .radius(50)
                                .strokeWidth(2)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.parseColor("#500084d3"));
//                        gMap.addCircle(circleOptionsDot);
                        Log.d("MAPS", "HAR KOORD, her er loc: " + userLocation);
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shop.getLatitude(), shop.getLongitude()), 15.2f));

                    }

                }
                searchBool = false;
            }
            else
            {


                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.coffeshopiconmaps);
                for (CoffeeShop shop : coffeeList)
                {
                    gMap.addMarker(new MarkerOptions().position(new LatLng(shop.getLatitude(), shop.getLongitude())).title(shop.getActualBrandName()).snippet("Adresse: " + shop.getAddress()+"\n Afstand til dig: " + DistanceCalculator.stringifyDistance(DistanceCalculator.getDistanceToCoffeeShop(userLocation.getLat(), userLocation.getLng(), shop.getLatitude(), shop.getLongitude()))));
                }
                Log.d("MAPS", "her er loc: " + locationFromDb);
                if (locationFromDb.longitude == 0 || locationFromDb.latitude == 0)
                {
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.89772, 10.61297), 6.2f));
                }
                else
                {
                    CircleOptions circleOptions = new CircleOptions()
                            .center(locationFromDb)
                            .radius(1000)
                            .strokeWidth(2)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.parseColor("#500084d3"));
//                    gMap.addCircle(circleOptions);
                    CircleOptions circleOptionsDot = new CircleOptions()
                            .center(locationFromDb)
                            .radius(50)
                            .strokeWidth(2)
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.parseColor("#500084d3"));
//                    gMap.addCircle(circleOptionsDot);
//                    gMap.addMarker(new MarkerOptions().position(locationFromDb).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Du er her!"));

                }
            }
        }
        catch (CursorIndexOutOfBoundsException e)
        {

            Log.d("MAPS", "I PROC FINSHED CATCH");
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.coffeshopiconmaps);
            for (CoffeeShop shop : coffeeList)
            {
                if (shop.getActualBrandName().toLowerCase().contains(searchString.toLowerCase()))
                {
                    LatLng tempLatLng = new LatLng(shop.getLatitude(), shop.getLongitude());
                    MarkerOptions options = new MarkerOptions();
                    gMap.addMarker(options.position(tempLatLng).title(shop.getActualBrandName()).snippet(shop.getAddress() + ", " + shop.getEmail()));
                    Toast.makeText(getActivity(), "Vi kunne ikke finde din lokation", Toast.LENGTH_LONG);
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.89772, 10.61297), 6.2f));
                }
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        gMap.setTrafficEnabled(true);
        gMap.setIndoorEnabled(true);
        gMap.setBuildingsEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        try
        {

            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setCompassEnabled(true);

        }
        catch(SecurityException e)
        {

        }

        //creds for the adapter below goes to http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet/31629308#31629308
        //It makes the mapMarkers into LinierLayouts, thus allowing for multi line snippets and titles :)
        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker marker)
            {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                LinearLayout info = new LinearLayout(getActivity());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getActivity());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(getActivity());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setGravity(Gravity.CENTER);
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });

        if (searchSpecificBool)
        {
            Log.d("I MAPS", "SearchSpecBool er " + searchSpecificBool + searchBool);
            double lat = bundle.getDouble("lat");
            double lng = bundle.getDouble("long");
            String addr = bundle.getString("addr");
            String email = bundle.getString("email");
            String name = bundle.getString("name");
            String phone = bundle.getString("phone");
            LatLng shoploc = new LatLng(lat, lng);
            gMap.addMarker(new MarkerOptions().position(shoploc).title(name).snippet(addr + ", " + email + ", " + phone));
//            gMap.moveCamera(CameraUpdateFactory.newLatLng(shoploc));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shoploc, 15.2f));
//            gMap.setMinZoomPreference(11.0f);

            searchSpecificBool = false;

        }
        else
        {
            try
            {

                userLocation = dbh.getLocation(1);
                locationFromDb = new LatLng(userLocation.getLat(), userLocation.getLng());
                Log.d("I MAPS", "prøver at sætte thinghy her er den " + locationFromDb);
                gMap.addMarker(new MarkerOptions().position(locationFromDb).title("Du er her!"));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationFromDb, 13.0f));
                gMap.resetMinMaxZoomPreference();
            }
            catch (Exception e)
            {
                Toast.makeText(getActivity(), "Vi kunne ikke finde din lokation", Toast.LENGTH_LONG);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.89772, 10.612969000000021), 6.2f));
            }
        }
        dismissLoadingDialog();


    }


    public void buildApiGoogle()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        android.location.Location mLastLocation = null;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (mLastLocation == null)
        {

        }
        else
        {
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            gMap.addMarker(new MarkerOptions().position(latLng).title("vi ser vores loc"));
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gMap.setMinZoomPreference(11.0f);
        }

    }

    @Override
    public void onConnectionSuspended(int i)
    {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        throw new UnsupportedOperationException("not implemented");
    }

    public void showLoadingDialog()
    {
        CustomProgressbar.showProgressBar(getContext(), false);

    }

    public void dismissLoadingDialog()
    {
        CustomProgressbar.hideProgressBar();

    }
}
