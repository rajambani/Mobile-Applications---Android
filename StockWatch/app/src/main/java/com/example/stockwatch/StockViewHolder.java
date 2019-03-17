package com.example.stockwatch;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StockViewHolder extends ViewHolder
{
  public TextView symbol;
  public TextView companyName;
  public TextView price;
  public TextView priceChange;
  public TextView percentageChange;
  public ImageView changeIcon;
  public ConstraintLayout rowConstraintLayout;

  public StockViewHolder(View itemView)
  {
    super(itemView);

    symbol = itemView.findViewById(R.id.symbol);
    companyName = itemView.findViewById(R.id.companyName);
    price = itemView.findViewById(R.id.price);
    priceChange = itemView.findViewById(R.id.priceChange);
    percentageChange = itemView.findViewById(R.id.percentageChange);
    changeIcon = itemView.findViewById(R.id.changeIcon);
    rowConstraintLayout = itemView.findViewById(R.id.rowConstraintLayout);

  }
}