package com.example.inspirationrewards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.Utility.AddRewardAsyncTask;

public class activity_add_reward extends AppCompatActivity {

    private static final String TAG = "activity_add_reward";

    private User targetUser;
    private User sourceUser;
    private ImageView imageView;
    private String pointsAwarded;
    private String comments;

    public final static int MAX_CHARS = 80;
    private EditText yourStoryET;
    private TextView yourStoryTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reward);

        populateFields();
    }

    private void populateFields()
    {
        Intent intent = getIntent();
        if (intent.hasExtra("targetUser")) {
            targetUser = (User) intent.getSerializableExtra("targetUser");
            sourceUser = (User) intent.getSerializableExtra("sourceUser");

            //This case will handle adding new notes
            if (targetUser == null) {
                //do nothing
                Log.d(TAG, "onCreate: User object is Null");
            }
            //This case will handle editing notes.
            else {
                Log.d(TAG, "onCreate: populating data");

                //set title
                getSupportActionBar().setTitle(targetUser.getfName() + " " + targetUser.getlName());
                //Populate icon on left side
                getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                        ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
                getSupportActionBar().setIcon(R.drawable.icon);

                ((TextView) findViewById(R.id.nameTextView)).setText(targetUser.getfName() + "," + targetUser.getlName());
                ((TextView) findViewById(R.id.pointsA)).setText(targetUser.getTotal(targetUser) + "");
                ((TextView) findViewById(R.id.departmentTextView)).setText(targetUser.getDepartment());
                ((TextView) findViewById(R.id.positionTextView)).setText(targetUser.getPosition());
                ((TextView) findViewById(R.id.yourStoryTextView)).setText(targetUser.getStory());

                //set image
                imageView = findViewById(R.id.imageView4);
                convertStringToImage(targetUser.getImage());

                //Char count
                yourStoryET = findViewById(R.id.commentET);
                yourStoryET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARS)});
                yourStoryTV = findViewById(R.id.commentTV);
                addTextListener();

                //((TextView) findViewById(R.id.rewardPointsToSend)).setText("Reward History(" + rewardList.size() + ")");
            }
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
                yourStoryTV.setText("Comment: " + countText);
            }
        });
    }

    public void convertStringToImage(String imgString) {

        byte[] imageBytes = Base64.decode(imgString,  Base64.DEFAULT);
        Log.d(TAG, "doConvert: Image byte array length: " + imgString.length());

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Log.d(TAG, "doConvert: Bitmap created");
        imageView.setImageBitmap(bitmap);
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

                checkDialogBox();
                //Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_LONG).show();
                //read fields from UI
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Return call from async task here.
    public void sendResults(String s)
    {
        if(s.equalsIgnoreCase("Success"))
        {
            makeCustomToast(activity_add_reward.this, "Add Reward Successful !", Toast.LENGTH_SHORT);
            Intent data = new Intent(); // Used to hold data to be returned to original activity
            setResult(1, data);
            finish();
        }
        else
        {
            makeCustomToast(activity_add_reward.this, s, Toast.LENGTH_SHORT);
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

                if(doNetCheck()) {
                    pointsAwarded = ((EditText) findViewById(R.id.sendRewardsET)).getText().toString();
                    comments = ((EditText) findViewById(R.id.commentET)).getText().toString();
                    Log.d(TAG, "onClick: sourceUser: "+ sourceUser.getUserName());
                    Log.d(TAG, "onClick: targetUser: "+ targetUser.getfName());
                    new AddRewardAsyncTask(activity_add_reward.this, sourceUser, targetUser).execute(pointsAwarded,
                            comments);
                }

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                //Do Nothing
                Intent data = new Intent(); // Used to hold data to be returned to original activity
                setResult(0, data);
                finish();
                makeCustomToast(activity_add_reward.this, "Changes Discarded !", Toast.LENGTH_SHORT);
            }
        });

        builder.setMessage("Do you want to save changes?");
        builder.setTitle("SAVE?");

        AlertDialog dialog = builder.create();
        dialog.show();
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

        Intent data = new Intent(); // Used to hold data to be returned to original activity
        setResult(0, data);
        finish();
        makeCustomToast(activity_add_reward.this, "Changes Discarded !", Toast.LENGTH_SHORT);
    }
}
