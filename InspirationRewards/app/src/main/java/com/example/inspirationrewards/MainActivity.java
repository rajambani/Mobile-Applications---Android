package com.example.inspirationrewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.Utility.LoginAPIAyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private LocationManager locationManager;
    private Location currentLocation;
    private Criteria criteria;

    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefsEditor;
    private ProgressBar progressBar;

    private static int MY_LOCATION_REQUEST_CODE = 329;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Populate icon on left side
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(R.drawable.icon);

        myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        if(myPrefs != null)
        {
            String uname = myPrefs.getString("uname", "-----");
            String pwd = myPrefs.getString("pwd", "-----");
            boolean remember = myPrefs.getBoolean("remember", false);

            if(remember)
            {
                ((EditText) findViewById(R.id.userNameText)).setText(uname);
                ((EditText) findViewById(R.id.pwdText)).setText(pwd);
                ((CheckBox) findViewById(R.id.rememberCheck)).setChecked(true);
            }
        }


        //check location is activated.
        checkLocation();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    }

    void checkLocation()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager == null)
            showNoLocationDialog();
        else {
            criteria = new Criteria();
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            //criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
            //criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            //
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_LOCATION_REQUEST_CODE);
            } else {
                setLocation();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);

        currentLocation = locationManager.getLastKnownLocation(bestProvider);
        if (currentLocation != null) {
           // ((TextView) findViewById(R.id.locText)).setText(
                    //currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
            String place = getPlace(currentLocation);
            //((TextView) findViewById(R.id.placeText)).setText(place);

        } else {
            //((TextView) findViewById(R.id.locText)).setText("Location Unavailable");
        }
    }

    private String getPlace(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            return city + ", " + state;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //on Sign Up Click
    public void onSignUpClick(View v)
    {
        Intent intent = new Intent(this, create_profile.class);
        startActivity(intent);
    }



    //Async call for login user.
    public void onLoginClick(View v) {

        if(doNetCheck()) {
            progressBar.setVisibility(View.VISIBLE);

            String userName = ((EditText) findViewById(R.id.userNameText)).getText().toString();
            String pswd = ((EditText) findViewById(R.id.pwdText)).getText().toString();
            boolean isChecked = ((CheckBox) findViewById(R.id.rememberCheck)).isChecked();

            myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
            prefsEditor = myPrefs.edit();
            prefsEditor.putString("uname", userName);
            prefsEditor.putString("pwd", pswd);
            prefsEditor.putBoolean("remember", isChecked);
            prefsEditor.apply();

            new LoginAPIAyncTask(this).execute(userName, pswd);
        }


    }

    public void sendResults(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
        //((TextView) findViewById(R.id.resultsText)).setText(s);
    }
    public void sendResults(User user)
    {
        progressBar.setVisibility(View.GONE);

        myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        prefsEditor = myPrefs.edit();
        prefsEditor.putString("name", user.getfName() + " " + user.getlName());
        prefsEditor.apply();

        Intent intent = new Intent(MainActivity.this, your_profile.class);
        intent.putExtra("userObj",user);
        startActivity(intent);
        //Toast.makeText(this, "Success !", Toast.LENGTH_LONG).show();

    }

    //This method is to check network connectivity
    private boolean doNetCheck()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
        {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
        {
            Log.d(TAG, "doNetCheck: Internet is Connected !");
            return true;
        }
        else {
            showNoNetworkDialog();
            return false;
        }
    }

    private void showNoNetworkDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("No Network Connection !!!");
        alertDialog.setMessage("Please Check Your Internet Connection and Try Again.");
        alertDialog.show();
    }

    private void showNoLocationDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("No Location Available !!!");
        alertDialog.setMessage("Please Check Your Location Service and Try Again.");
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //This will help in exiting from applicaiton.
        finishAffinity();
        finish();
    }
}
