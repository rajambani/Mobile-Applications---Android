package com.example.inspirationrewards.Utility;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inspirationrewards.R;

public class LeaderBoardViewHolder extends RecyclerView.ViewHolder
{
    public TextView name;
    public TextView position;
    public TextView points;
    public ImageView imageView;

    public LeaderBoardViewHolder(View view)
    {
        super(view);

        position = view.findViewById(R.id.positionTV1);
        name = view.findViewById(R.id.nameTV1);
        points = view.findViewById(R.id.totalTV);
        imageView = view.findViewById(R.id.imageView5);
    }
}
