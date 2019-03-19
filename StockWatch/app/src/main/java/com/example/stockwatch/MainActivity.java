package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    public RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;

    private StockAdapter stockAdapter;
    List<Stock> stockList = new ArrayList<Stock>();
    HashMap<String, String> initialMap;
    private DatabaseHandler databaseHandler;

    String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        doAsyncLoadInitialData();
        flag = "";

        Log.d(TAG, "onCreate: stockList: "+ stockList.size());
        recyclerView = findViewById(R.id.recyclerView);
        stockAdapter = new StockAdapter(stockList, this);
        Collections.sort(stockList, Collections.<Stock>reverseOrder());
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockAdapter.notifyDataSetChanged();

        databaseHandler = new DatabaseHandler(this);


        swiper = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(doNetCheck())
                {
                    flag = "";
                    refreshData();
                    //Set this to false else when the view is refreshed even if its task its done it shows the refreshing symbol
                }
                swiper.setRefreshing(false);
            }
        });
    }


    //This method will fetch new data by service call.
    private void refreshData()
    {
        ArrayList<Stock> list = databaseHandler.loadStocks();
        list.addAll(stockList);
        stockList.clear();
        Log.d(TAG, "refreshData: stockList after clear: "+ stockList);
        for(Stock st:list)
        {
            //Delete old stock from db.
            databaseHandler.deleteStock(st.getSymbol());

            MyAsyncTaskLoadFinancialDetails at = new MyAsyncTaskLoadFinancialDetails(this);
            at.execute(st.getSymbol());
        }
    }



    @Override
    protected void onResume() {
        databaseHandler.dumpDbToLog();
        ArrayList<Stock> list = databaseHandler.loadStocks();
        stockList.clear();
       //Log.d(TAG, "onResume: stockList " + stockList);
        stockList.addAll(list);
        //Log.d(TAG, "onResume: " + list);
        //Collections.sort(stockList, Collections.<Stock>reverseOrder());
        //stockAdapter.notifyDataSetChanged();

        if(! doNetCheck())
        {
            ArrayList<Stock> l = new ArrayList<>();
            l.addAll(stockList);
            stockList.clear();
            for(Stock st:list)
            {
                st.setPrice(0);
                st.setPriceChange(0);
                st.setPercentageChange(0);
                stockList.add(new Stock(st.getSymbol(), st.getCompanyName(), st.getPrice(), st.getPriceChange(),
                        st.getPercentageChange()));
            }
        }
        Log.d(TAG, "onResume: " + stockList);
        Collections.sort(stockList, Collections.<Stock>reverseOrder());
        stockAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Override
    protected void onDestroy() {

        List<Stock> list = stockList;
        stockList.clear();
        if(doNetCheck())
        {
            for(Stock st:list)
            {
                //Delete old stock from db.
                databaseHandler.deleteStock(st.getSymbol());
                flag = "";

                MyAsyncTaskLoadFinancialDetails at = new MyAsyncTaskLoadFinancialDetails(this);
                at.execute(st.getSymbol());
            }
        }
        for(Stock st:stockList)
        {
            Log.d(TAG, "onDestroy: stock details: "+ st);
            databaseHandler.updateStock(st);
        }
        databaseHandler.shutDown();
        super.onDestroy();
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
          Log.d(TAG, "populateInitialMap: " + this.initialMap);
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
                if(doNetCheck()) {

                    if(stockList.size() > 0)
                    {
                        if(stockList.get(0).getPrice() == 0)
                        {
                            refreshData();
                        }
                    }

                    flag = "add";
                    if(initialMap.size() < 1)
                    {
                        doAsyncLoadInitialData();
                    }
                    addStock();
                }
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
        //et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //
        ArrayList<InputFilter> curInputFilters = new ArrayList<InputFilter>(Arrays.asList(et.getFilters()));
        curInputFilters.add(0, new AlphabetClass());
        curInputFilters.add(1, new InputFilter.AllCaps());
        InputFilter[] newInputFilters = curInputFilters.toArray(new InputFilter[curInputFilters.size()]);
        et.setFilters(newInputFilters);
        //

        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        //builder.setIcon(R.drawable.icon1);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id)
            {
                String symbol;
                if(! et.getText().toString().trim().isEmpty())
                    searchStock(et.getText().toString().trim().toLowerCase());
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
//        if(initialMap.containsKey(symbol.toLowerCase().trim()))
//        {
//            Log.d(TAG, "searchStock: found match symbol: "+ symbol);
//            getSymbolDetails(symbol);
//            //return symbol;
//        }
        if(true)
        {
            String resultSymbol = checkList(symbol.toLowerCase());
        }

        //return "";
    }

    //This method will populate list of possible stock symbols to the user.
    private String checkList(String symbol)
    {
        List<String> dialogList = new ArrayList<>();
        for(String key:initialMap.keySet())
        {

            String value = (String)initialMap.get(key);
            if(((String)key).contains(symbol))
            {
                Log.d(TAG, "checkList: key: "+ key);
                dialogList.add((String)key.toUpperCase() + " - " + value);
            }

            //This code is for matching the input string with company names too.
            else if(value.contains(symbol))
            {
                dialogList.add(key.toUpperCase() + " - " + value);
            }
        }
        Log.d(TAG, "checkList: dialogList: "+ dialogList);
        if(dialogList.size() < 1)
        {
            displayDialog();
            return "";
        }
        else if(dialogList.size() == 1)
        {
            getSymbolDetails(dialogList.get(0).split(" - ")[0]);
            return"";
        }
        else
            displaySymbolList(dialogList);
        return "";
    }

    private void displayDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Not Found ! !");
        alertDialog.setMessage("There are NO stocks found with this symbol.");
        alertDialog.show();
    }

    //This method will display doalog with list of comapnies and allow user to select one.
    private String displaySymbolList(final List<String> dialogList)
    {
        Collections.sort(dialogList);

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
            MyAsyncTaskLoadFinancialDetails at = new MyAsyncTaskLoadFinancialDetails(this);
            at.execute(symbol);
            Toast.makeText(MainActivity.this, "Stock found: " + symbol, Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MainActivity.this, "Stock Name is empty!", Toast.LENGTH_SHORT).show();
    }

    public void addStocktoList(Stock stock)
    {
        //Check for duplicates
        if(doNetCheck()) {
            if (stockList.contains(stock))
            {
                stockList.remove(stock);
                Log.d(TAG, "addStocktoList: stock deleted: "+ stock.getSymbol());
                databaseHandler.deleteStock(stock.getSymbol());
                Log.d(TAG, "addStocktoList: stockNames:" + stockList);
                if (flag.equalsIgnoreCase("add"))
                {
                    //error dialog
                    //flag = "";
                    showWarningDialog();
                }
                //Toast.makeText(MainActivity.this, "Duplicate Stocke Entry! "+ stock, Toast.LENGTH_SHORT).show();
            }
            this.stockList.add(stock);
            Collections.sort(stockList, Collections.<Stock>reverseOrder());
            stockAdapter.notifyDataSetChanged();
            Log.d(TAG, "addStocktoList: stock added: "+ stock.toString());
            //this will add stock to db.
            databaseHandler.addStock(stock);
            stockAdapter.notifyDataSetChanged();
        }
        flag = "";

    }

    private void showWarningDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Duplicate Stock !");
        alertDialog.setMessage("Stock symbol " +""+ " is already displayed.");
        //alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
        alertDialog.show();
    }

    // From OnClickListener
    // This method will open the link and take it to the website.
    @Override
    public void onClick(View v)
    {  // click listener called by ViewHolder clicks

        Log.d(TAG, "onClick: ");
        int pos = recyclerView.getChildLayoutPosition(v);
        String address = "http://www.marketwatch.com/investing/stock/" + stockList.get(pos).getSymbol();
        Log.d(TAG, "onClick:  address: " + address);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(address));
        startActivity(i);
    }

    // From OnLongClickListener
    //This is for deleting stocks.
    @Override
    public boolean onLongClick(View v)
    {  // long click listener called by ViewHolder long clicks
        //Log.d(TAG, "onLongClick: list size: "+ noteList.size());
        flag = "";
        checkDialogBox(v);
        return false;
    }

    void checkDialogBox(final View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.icon1);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                int pos = recyclerView.getChildLayoutPosition(v);
                Stock stock = stockList.get(pos);
                stockList.remove(stock);
                databaseHandler.deleteStock(stock.getSymbol());
                //Log.d(TAG, "onLongClick: list size: "+ stockList.size());
                stockAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // Do Nothing.
            }
        });

        builder.setMessage("Are you sure you want to delete this stock?");
        builder.setTitle("DELETE?");

        AlertDialog dialog = builder.create();
        dialog.show();
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
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("No Network Connection !!!");
        alertDialog.setMessage("Please Check Your Internet Connection and Try Again.");
        alertDialog.show();
    }

}


