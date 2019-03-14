package com.example.stockwatch;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

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

        //stockList.add(new Stock("Amz", "Amazon", 12.30, 0.50, 2.4));
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

    //This method performs action when any of the menu item is clicked.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.addStock:
                addStock();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addStock()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        //builder.setIcon(R.drawable.icon1);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String symbol;
                if(! et.getText().toString().trim().isEmpty())
                    searchStock(et.getText().toString().trim());
                else
                    Toast.makeText(MainActivity.this, "Company Name cannot be empty!", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setMessage("Please Enter a Stock Symbol:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //This method will search for stocks from stock map and will return correct symbol.
    private void searchStock(String symbol)
    {
        if(initialMap.containsKey(symbol))
        {
            getSymbolDetails(symbol);
            //return symbol;
        }
        else
        {
            String resultSymbol = checkList(symbol);
        }

        //return "";
    }

    //This method will populate list of possible stock symbols to the user.
    private String checkList(String symbol)
    {
        List<String> dialogList = new ArrayList<>();
        for(String key:initialMap.keySet())
        {
            String value = initialMap.get(key);
            if(key.contains(symbol))
            {
                dialogList.add(key + " - " + value);
            }
            else if(value.contains(symbol))
            {
                dialogList.add(key + " - " + value);
            }
        }
        Log.d(TAG, "checkList: dialogList: "+ dialogList);
        String selectedSymbol = displaySymbolList(dialogList);
        return "";
    }

    //This method will display doalog with list of comapnies and allow user to select one.
    private String displaySymbolList(final List<String> dialogList)
    {
         //covert list into array of char sequence.
        final CharSequence[] sArray = new CharSequence[dialogList.size()];
        for (int i = 0; i < dialogList.size(); i++)
        {
            sArray[i] = dialogList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        //builder.setIcon(R.drawable.icon2);

        // Set the builder to display the string array as a selectable
        // list, and add the "onClick" for when a selection is made
        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String symbol = (dialogList.get(which));
                getSymbolDetails(symbol.split(" - ")[0]);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return "";
    }

    //This method will call service endpoint to fetch financial details of the symbol.
    private void getSymbolDetails(String symbol)
    {
        if(! symbol.isEmpty())
        {

        }
        else
            Toast.makeText(MainActivity.this, "Stock Name is empty!", Toast.LENGTH_SHORT).show();
    }
}
