package com.example.inspirationrewards.Utility;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.inspirationrewards.R;

public class RewardViewHolder extends RecyclerView.ViewHolder
{
    public TextView date;
    public TextView name;
    public TextView points;
    public TextView comments;

    public RewardViewHolder(View view)
    {
        super(view);

        date = view.findViewById(R.id.dateTV);
        name = view.findViewById(R.id.nameTV1);
        points = view.findViewById(R.id.pointsTV);
        comments = view.findViewById(R.id.commentsTV);
    }


}
