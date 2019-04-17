package com.example.inspirationrewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.Utility.SaveProfileAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class create_profile extends AppCompatActivity
{
    private static final String TAG = "create_profile";

    private User user;
    private int REQUEST_IMAGE_GALLERY = 1; //gallery
    private int REQUEST_IMAGE_CAPTURE = 2; //camera
    private File currentImageFile;
    private ImageView imageView;
    private EditText yourStoryET;
    private TextView yourStoryTV;

    private LocationManager locationManager;
    private Location currentLocation;
    private Criteria criteria;
    private String locationString;

    private static int MY_LOCATION_REQUEST_CODE = 329;
    int type = 1;
    public final static int CREATE_PROFILE = 1;
    public final static int UPDATE_PROFILE = 2;
    public final static int MAX_CHARS = 360;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        imageView = findViewById(R.id.profileImg);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //set title
        getSupportActionBar().setTitle("Create Profile");

        //call for edit profile
        Intent intent = getIntent();
        if (intent.hasExtra("editUser"))
        {
            user = (User) intent.getSerializableExtra("editUser");
            editProfile(user);

            yourStoryET = findViewById(R.id.yourStoryEditText);
            yourStoryET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARS)});
            yourStoryTV = findViewById(R.id.yourStoryText);
            addTextListener();
        }
        //char count
        else
        {
            yourStoryET = findViewById(R.id.yourStoryEditText);
            yourStoryET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARS)});
            yourStoryTV = findViewById(R.id.yourStoryText);
            addTextListener();
        }
    }

    private void addTextListener() {
        yourStoryET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do here
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // Nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                int len = s.toString().length();
                String countText = "(" + len + " of " + MAX_CHARS + ")";
                yourStoryTV.setText("Your Story: " + countText);
            }
        });
    }

    //This method will be called when user clicks on edit profile.
    private void editProfile(User user)
    {
        this.type = 2;
        getSupportActionBar().setTitle("Edit Profile");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //for icon
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setIcon(R.drawable.icon);

        ((ImageView) findViewById(R.id.imageView6)).setVisibility(View.VISIBLE);

        ((EditText)findViewById(R.id.userName)).setText(user.getUserName());
        ((EditText)findViewById(R.id.userName)).setEnabled(false);

        ((EditText)findViewById(R.id.passwordText)).setText(user.getPwd());
        ((EditText)findViewById(R.id.fName)).setText(user.getfName());
        ((EditText)findViewById(R.id.lName)).setText(user.getlName());
        ((EditText)findViewById(R.id.deptEditText)).setText(user.getDepartment());
        ((EditText)findViewById(R.id.posEditText)).setText(user.getPosition());
        ((EditText)findViewById(R.id.yourStoryEditText)).setText(user.getStory());

        //checkbox
        ((CheckBox)findViewById(R.id.AdminCheck)).setChecked(user.isAdmin());

        //image
        String imgString = user.getImage();
        byte[] imageBytes = Base64.decode(imgString,  Base64.DEFAULT);
        Log.d(TAG, "doConvert: Image byte array length: " + imgString.length());
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Log.d(TAG, "doConvert: Bitmap created");
        imageView.setImageBitmap(bitmap);

    }

    void onProfileImageClick(View v)
    {
        checkDialogBox(v);
        //doGallery();
    }

    //show dialog box
    void checkDialogBox(final View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.icon1);
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                doCamera(v);
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Log.d(TAG, "onClick: No image selected");
            }
        });

        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                doGallery();
                Log.d(TAG, "onClick: Image from gallery");
            }
        });

        builder.setMessage("Take Picture from: ");
        builder.setTitle("Profile Picture");

        AlertDialog dialog = builder.create();
        dialog.show();
        //getSupportActionBar().setTitle("Multi Notes");
    }

    //Read from gallery
    public void doGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    //camera
    public void doCamera(View v) {
        currentImageFile = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: "+ e.getMessage());
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                processCamera();
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        //((SeekBar) findViewById(R.id.seekBar)).setProgress(100);
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "processGallery: " + e.getMessage());
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        imageView.setImageBitmap(selectedImage);
//        makeCustomToast(this,
//                String.format(Locale.getDefault(), "%,d", selectedImage.getByteCount()),
//                Toast.LENGTH_LONG);

        //Add imageview
        ((ImageView) findViewById(R.id.imageView6)).setVisibility(View.INVISIBLE);
        Log.d(TAG, "processGallery: Image set from gallery");
    }

    private void processCamera() {
        Uri selectedImage = Uri.fromFile(currentImageFile);
        imageView.setImageURI(selectedImage);
        Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        makeCustomToast(this,
//                String.format(Locale.getDefault(), "%,d", bm.getByteCount()),
//                Toast.LENGTH_LONG);

        currentImageFile.delete();
        ((ImageView) findViewById(R.id.imageView6)).setVisibility(View.INVISIBLE);
        Log.d(TAG, "processCamera: Image set from camera");
    }

    //convert image to string
    public String convertImageToString() {

        if (imageView.getDrawable() == null)
            return null;

        //int jpgQuality = ((SeekBar) findViewById(R.id.seekBar)).getProgress();
        //Log.d(TAG, "doConvert: JPG Quality: " + jpgQuality);


        Bitmap origBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        origBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bitmapAsByteArrayStream);

        String imgString = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "doConvert: Image in Base64 size: " + imgString.length());

        return imgString;

//        byte[] imageBytes = Base64.decode(imgString,  Base64.DEFAULT);
//        Log.d(TAG, "doConvert: Image byte array length: " + imgString.length());
//
//        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        Log.d(TAG, "doConvert: Bitmap created");
//
//        imageView.setImageBitmap(bitmap);
    }

    //This method ensures that menu is visible on the main activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.profile_save_menu, menu);
        return true;
    }

    //This method performs action when any of the menu item is clicked.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.saveProfile:
                //Toast.makeText(this, "Save Profile clicked", Toast.LENGTH_SHORT).show();
                checkDialogBox();
                return true;
            case android.R.id.home:
                //do whatever
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void checkDialogBox()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.icon1);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                saveProfile();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                //Do Nothing
                //saveProfile();

                if(user != null)
                {
                    Intent intent = new Intent(create_profile.this, your_profile.class);
                    intent.putExtra("userObj", user);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(create_profile.this, MainActivity.class);
                    //intent.putExtra("userObj", user);
                    startActivity(intent);
                }

                makeCustomToast(create_profile.this, "Changes Discarded !", Toast.LENGTH_SHORT);
            }
        });

        builder.setMessage("Do you want to save changes?");
        builder.setTitle("SAVE?");

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    void saveProfile()
    {
        String userId = ((EditText)findViewById(R.id.userName)).getText().toString();
        String pwd = ((EditText)findViewById(R.id.passwordText)).getText().toString();
        String fName = ((EditText)findViewById(R.id.fName)).getText().toString();
        String lName = ((EditText)findViewById(R.id.lName)).getText().toString();
        String department = ((EditText)findViewById(R.id.deptEditText)).getText().toString();
        String position = ((EditText)findViewById(R.id.posEditText)).getText().toString();
        String story = ((EditText)findViewById(R.id.yourStoryEditText)).getText().toString();
        boolean isAdmin = ((CheckBox)findViewById(R.id.AdminCheck)).isChecked();

        Log.d(TAG, "saveProfile: admin updated to:"+ isAdmin);

        String imageString = convertImageToString();

        checkLocation();

        User user = new User(userId,pwd,isAdmin,fName,lName,department,position,story,imageString,
                1000,locationString,new ArrayList());

        if(type == CREATE_PROFILE && doNetCheck())
            new SaveProfileAsyncTask(this, user, CREATE_PROFILE).execute();
        else if(doNetCheck())
            new SaveProfileAsyncTask(this, user, UPDATE_PROFILE).execute();
    }

    //return from async call
    public void sendResults(User user) {
        //Toast.makeText(this, "Saved Profile" , Toast.LENGTH_LONG).show();

        if(user != null)
        {
            Intent intent = new Intent(create_profile.this, your_profile.class);
            intent.putExtra("userObj",user);
            startActivity(intent);

//            Intent intent = new Intent(create_profile.this, your_profile.class);
//            intent.putExtra("noteObject", user);
//            startActivityForResult(intent, 1);

            //startActivity(intent);
            //Toast.makeText(this, "User Created Successfully !", Toast.LENGTH_LONG).show();
            makeCustomToast(this, "User Created/Edited Successfully !", Toast.LENGTH_LONG);
        }
        else
            makeCustomToast(this, "Error in Creating Profile !", Toast.LENGTH_LONG);
    }

    public void sendResults(String str)
    {
        makeCustomToast(this, str, Toast.LENGTH_LONG);
    }

    void checkLocation()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
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
    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);

        currentLocation = locationManager.getLastKnownLocation(bestProvider);
        if (currentLocation != null) {
            locationString = getPlace(currentLocation);

        }
        else {
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

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context,  message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
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
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("No Network Connection !!!");
        alertDialog.setMessage("Please Check Your Internet Connection and Try Again.");
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        checkDialogBox();
    }
}
