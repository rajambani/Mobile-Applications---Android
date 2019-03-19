package com.example.stockwatch;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder>
{
    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> stockList, MainActivity mainActivity)
    {
        this.stockList = stockList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder: Making New ");

        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_row, parent, false);

        rowView.setOnClickListener(mainActivity);
        rowView.setOnLongClickListener(mainActivity);

        return new StockViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder: Filling View Holder");

        Stock stock = stockList.get(position);

        holder.symbol.setText(stock.getSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.price.setText(String.valueOf(stock.getPrice()));
        holder.priceChange.setText(String.valueOf(stock.getPriceChange()));

        double percentageChange = (double)( (stock.getPercentageChange()) * 100.00 / 100.00);
        String perc = String.format("%.2f", percentageChange);

        //holder.percentageChange.setText("("+ String.valueOf(percentageChange) + "%)");
        holder.percentageChange.setText("("+ perc + "%)");

        if(stock.getPriceChange() < 0)
        {
            holder.rowConstraintLayout.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.Red));
            //holder.changeIcon.setImageIcon(R.drawable.baseline_arrow_drop_down_black_18dp);
            holder.priceChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.down, 0,0,0);
            //mainActivity.getResources().getDrawable(R.drawable.d);
            //holder.tvPriceChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_down_24, 0,0,0);
        }
        else
        {
            holder.rowConstraintLayout.setBackgroundColor(ContextCompat.getColor(mainActivity, R.color.Green));
            holder.priceChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.up, 0,0,0);
        }
        //uncomment this to set icon for change in stock price.
        //holder.changeIcon.setImageIcon();
    }

    @Override
    public int getItemCount()
    {
        return stockList.size();
    }
}