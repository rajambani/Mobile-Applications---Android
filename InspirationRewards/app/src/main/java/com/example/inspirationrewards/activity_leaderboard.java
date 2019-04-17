package com.example.inspirationrewards;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.inspirationrewards.Bean.LeaderBoard;
import com.example.inspirationrewards.Bean.Reward;
import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.Utility.AllUserAsyncTask;
import com.example.inspirationrewards.Utility.LeaderBoardAdapter;
import com.example.inspirationrewards.Utility.RewardAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class activity_leaderboard extends AppCompatActivity {

    private static final String TAG = "activity_leaderboard";
    private static final int B_REQUEST_CODE = 1;

    RecyclerView recyclerView;
    LeaderBoardAdapter leaderBoardAdapter;
    List<User> userList;
    User user;

    private SharedPreferences myPrefs;
    private SharedPreferences.Editor prefsEditor;
    private User sourceUser;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setIcon(R.drawable.icon);
        getSupportActionBar().setTitle("Inspiration Leaderboard");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        //setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("Inspiration Leaderboard");

        populateUserList();

    }

    private void populateUserList()
    {
        String userName = "";
        String password = "";

        myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        //prefsEditor = myPrefs.edit();
        String uname = myPrefs.getString("uname", "-----");
        String pwd = myPrefs.getString("pwd", "-----");
        String name = myPrefs.getString("name", "-----");

        sourceUser = new User();
        sourceUser.setUsername(uname);
        sourceUser.setPassword(pwd);
        sourceUser.setFirstName(name);

        Intent intent = getIntent();
        if (intent.hasExtra("userObj")) {
            user = (User) intent.getSerializableExtra("userObj");

            if (user == null)
            {
                //do nothing
                Log.d(TAG, "onCreate: User object is Null");
            }
            else
            {
                Log.d(TAG, "populateUserList: setting uname and pwd");
                userName = user.getUserName();
                password = user.getPwd();
            }
        }

        Log.d(TAG, "populateUserList: calling  AllUserAsyncTask");
        if(doNetCheck())
            new AllUserAsyncTask(this).execute(userName, password);
    }

    //on failure call this method.
    public void sendResults(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
    //on success call this method.
    public void sendResults(LeaderBoard board)
    {
        userList = board.getUserList();

        Collections.sort(userList);
        for(User u:userList)
            Log.d(TAG, "sendResults: user u:" + u.toString());

        //recyclerView
        recyclerView = findViewById(R.id.recyclerViewLeaderBoard);
        //Log.d(TAG, "sendResults: User currentUser: "+ sourceUser.toString());
        leaderBoardAdapter = new LeaderBoardAdapter(userList, activity_leaderboard.this, sourceUser);
        recyclerView.setAdapter(leaderBoardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Log.d(TAG, "sendResults: userList: "+ userList.size());
        leaderBoardAdapter.notifyDataSetChanged();
        Log.d(TAG, "sendResults: list updated"+ userList.size());
        Toast.makeText(this, "Success !", Toast.LENGTH_LONG).show();
    }


    // From OnClickListener
    public void onUserClick(View v)
    {  // click listener called by ViewHolder clicks

        int pos = recyclerView.getChildLayoutPosition(v);
        User user1 = userList.get(pos);

        Intent intent = new Intent(this, activity_add_reward.class);
        intent.putExtra("targetUser", user1);

        //Check is user is current user
        if(user1.getUserName().equalsIgnoreCase(sourceUser.getUserName()))
        {
            Toast.makeText(v.getContext(), "Cannot Assign Rewards to Self !!!", Toast.LENGTH_LONG).show();
            return;
        }

        intent.putExtra("sourceUser", sourceUser);
        //Log.d(TAG, "onUserClick: sourceUser: "+ sourceUser.get);
        startActivityForResult(intent, B_REQUEST_CODE);

        //Toast.makeText(v.getContext(), "SHORT " + user.getUserName(), Toast.LENGTH_SHORT).show();
    }

    //This method is called when data is returned from edit activity.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == B_REQUEST_CODE)
        {
            if (resultCode == 1)
            {
                //Toast.makeText(this, "Changes saved ! ", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: Added new reward: " + resultCode);

                //recreate();
                Intent refresh = new Intent(this, activity_leaderboard.class);
                refresh.putExtra("userObj", sourceUser);
                startActivity(refresh);
                this.finish();
            }
            //This is when user press cancel.
            else if(resultCode == 0){
                //Toast.makeText(this, "Changes discarded ! ", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onActivityResult: Discarded result Code: " + resultCode);
            }
        }
        else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
        //getSupportActionBar().setTitle("Multi Notes(" + noteList.size() + ")");
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
        super.onBackPressed();
        Intent intent = new Intent(this, your_profile.class);
        Log.d(TAG, "onBackPressed: this.user: "+ this.user);
        intent.putExtra("userObj",this.user);
        startActivity(intent);
        Toast.makeText(this, "Success !", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //do whatever
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
