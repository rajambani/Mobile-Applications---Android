package com.example.stockwatch;

import android.support.annotation.NonNull;
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

        //rowView.setOnClickListener(mainActivity);
        //rowView.setOnLongClickListener(mainActivity);

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
        holder.percentageChange.setText("("+ String.valueOf(stock.getPercentageChange()) + "%)");
        //uncomment this to set icon for change in stock price.
        //holder.changeIcon.setImageIcon();
    }

    @Override
    public int getItemCount()
    {
        return stockList.size();
    }
}
