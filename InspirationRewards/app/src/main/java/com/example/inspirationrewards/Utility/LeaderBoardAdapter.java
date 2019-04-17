package com.example.inspirationrewards.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inspirationrewards.Bean.Reward;
import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.R;
import com.example.inspirationrewards.activity_leaderboard;
import com.example.inspirationrewards.your_profile;

import java.util.List;

public class LeaderBoardAdapter  extends  RecyclerView.Adapter<LeaderBoardViewHolder>
{
    private static final String TAG="RewardAdapter";
    private List<User> userList;
    private activity_leaderboard mainActivity;
    private User currentUser;

    public LeaderBoardAdapter(List<User> userList, activity_leaderboard mainActivity, User currentUser)
    {
        this.userList = userList;
        this.mainActivity = mainActivity;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_row, parent, false);

        //rowView.setOnClickListener(mainActivity);
        //rowView.setOnLongClickListener((View.OnLongClickListener) mainActivity);

        return new LeaderBoardViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder: Filling View Holder");

        User user = userList.get(position);
        Log.d(TAG, "onBindViewHolder: user: "+ user.toString());
        int total = user.getTotal(user);

        holder.name.setText(user.getlName() + "," + user.getfName());
        holder.position.setText(user.getPosition() + "," + user.getDepartment());
        holder.points.setText(total + "");
        holder.imageView.setImageBitmap(convertStringToImage(user.getImage()));

        //assign color to self user
        if(currentUser!=null && currentUser.getUserName().equalsIgnoreCase(user.getUserName()))
        {
            holder.name.setTextColor(Color.CYAN);
            holder.position.setTextColor(Color.CYAN);
            holder.points.setTextColor(Color.CYAN);
        }

    }

    //setImage
    public Bitmap convertStringToImage(String imgString) {

        byte[] imageBytes = Base64.decode(imgString,  Base64.DEFAULT);
        Log.d(TAG, "doConvert: Image byte array length: " + imgString.length());

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Log.d(TAG, "doConvert: Bitmap created");

        return bitmap;
        //imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
