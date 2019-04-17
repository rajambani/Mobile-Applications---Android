package com.example.inspirationrewards.Utility;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inspirationrewards.Bean.Reward;
import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.MainActivity;
import com.example.inspirationrewards.R;
import com.example.inspirationrewards.your_profile;

import java.util.List;

public class RewardAdapter extends  RecyclerView.Adapter<RewardViewHolder>
{
    private static final String TAG="RewardAdapter";
    private List<Reward> rewardList;
    private your_profile mainActivity;

    public RewardAdapter(List<Reward> rewardList, your_profile mainActivity)
    {
        this.rewardList = rewardList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reward_row, parent, false);

        //rowView.setOnClickListener((View.OnClickListener) mainActivity);
        //rowView.setOnLongClickListener((View.OnLongClickListener) mainActivity);

        return new RewardViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: Filling View Holder");

        Reward reward = rewardList.get(position);

        holder.date.setText(reward.getDate());
        //Log.d(TAG, "onBindViewHolder: "+ reward.getName() +);
        holder.name.setText(reward.getName());
        holder.points.setText(reward.getValue() + "");
        holder.comments.setText(reward.getNotes());

    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }
}
