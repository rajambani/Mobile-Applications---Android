package com.example.inspirationrewards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inspirationrewards.Bean.Reward;
import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.Utility.RewardAdapter;

import java.util.List;

public class your_profile extends AppCompatActivity {

    private static final String TAG = "your_profile";
    private static final int EDIT_PROFILE_ACTIVITY = 1;

    private ImageView imageView;
    private User user;
    RecyclerView recyclerView;
    RewardAdapter rewardAdapter;
    List<Reward> rewardList;

    private TextView yourStoryET;
    private TextView yourStoryTV;
    public final static int MAX_CHARS = 360;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);

        //Populate icon on left side
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setIcon(R.drawable.icon);
        getSupportActionBar().setTitle("Your Profile");

        Intent intent = getIntent();
        if (intent.hasExtra("userObj"))
        {
            user = (User) intent.getSerializableExtra("userObj");

            //This case will handle adding new notes
            if(user == null)
            {
                //do nothing
                Log.d(TAG, "onCreate: User object is Null");
            }
            //This case will handle editing notes.
            else
            {
                Log.d(TAG, "onCreate: populating data" + user.toString());
                //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                ((TextView)findViewById(R.id.nameTextView)).setText(user.getfName() +","+ user.getlName());
                ((TextView)findViewById(R.id.userIdTestView)).setText("(" + user.getUserName() + ")");
                ((TextView)findViewById(R.id.placeTextView)).setText(user.getLocation());
                ((TextView)findViewById(R.id.pointsA)).setText(user.getTotal(user) + "");
                ((TextView)findViewById(R.id.departmentTextView)).setText(user.getDepartment());
                ((TextView)findViewById(R.id.positionTextView)).setText(user.getPosition());
                ((TextView)findViewById(R.id.pointsToAward)).setText(user.getPointsToAward() + "");
                ((TextView)findViewById(R.id.yourStoryTextView)).setText(user.getStory());

                //set checkbox
                Log.d(TAG, "onCreate: isadmin:"+ user.isAdmin());
                CheckBox cb = ((CheckBox) findViewById(R.id.AdminCheck));
                if(cb != null)
                        cb.setEnabled(user.isAdmin());

                //set recyclerview for rewards history.
                rewardList = user.getRewardList();

                //set image
                imageView = findViewById(R.id.imageView4);
                convertStringToImage(user.getImage());

                ((TextView)findViewById(R.id.rewardPointsToSend)).setText("Reward History(" + rewardList.size() + ")");
            }

            recyclerView = findViewById(R.id.recyclerViewRewards);
            rewardAdapter = new RewardAdapter(rewardList, your_profile.this);
            recyclerView.setAdapter(rewardAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

//            //textView.setText("ActivityB\nOpened from " + text);
//            yourStoryET = findViewById(R.id.yourStoryTextView);
//            yourStoryET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARS)});
//            yourStoryTV = findViewById(R.id.textView12);
//            Log.d(TAG, "onCreate: addTextListener");
//            addTextListener();
        }

    }

//    private void addTextListener() {
//        Log.d(TAG, "addTextListener: ");
//        yourStoryET.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // Nothing to do here
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//                // Nothing to do here
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//                int len = s.toString().length();
//                String countText = "(" + len + " of " + MAX_CHARS + ")";
//                yourStoryTV.setText("Your Story: " + countText);
//            }
//        });
//    }


    //setImage
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
        getMenuInflater().inflate(R.menu.your_profile_menu, menu);
        return true;
    }

    //This method performs action when any of the menu item is clicked.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.editProfile:
                //Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
                editProfile(user);
                return true;
            case R.id.leaderBoard:
                //Toast.makeText(this, "Leaderboard clicked", Toast.LENGTH_SHORT).show();
                showLeaderBoard();
                return true;
            case android.R.id.home:
                //do whatever
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void showLeaderBoard()
    {
        Intent intent = new Intent(this, activity_leaderboard.class);
        intent.putExtra("userObj",user);
        startActivity(intent);
    }

    public void editProfile(User user) {
        Intent intent = new Intent(your_profile.this, create_profile.class);
        intent.putExtra("editUser", user);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
    }

}
