package com.example.stockwatch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = "MainActivity";
    public RecyclerView recyclerView;
    StockAdapter stockAdapter;
    List<Stock> stockList = new ArrayList<Stock>();
    HashMap<String, String> initialMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doAsyncLoadInitialData();

        stockList.add(new Stock("Amz", "Amazon", 12.30, 0.50, 2.4));
        recyclerView = findViewById(R.id.recyclerView);
        stockAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //This method will load initial data and store it in a hashmap
    private void doAsyncLoadInitialData()
    {
        MyAsyncTask at = new MyAsyncTask(this);
        at.execute("");
    }

    //This method will populate map returned from service call by async task
      void populateInitialMap(HashMap<String, String> initialMap)
      {
          this.initialMap = initialMap;
      }

    //This method ensures that menu is visible on the main activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }
}
