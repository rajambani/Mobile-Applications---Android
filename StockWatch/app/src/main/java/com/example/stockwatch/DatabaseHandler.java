package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.stockwatch.Stock;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    // DB Name
    private static final String DATABASE_NAME = "StockDB";
    // DB Table Name
    private static final String TABLE_NAME = "Stock";

    ///DB Columns
    private static final String SYMBOL = "Symbol";
    private static final String COMPANY_NAME = "CompanyName";
    private static final String PRICE = "Price";
    private static final String PRICE_CHANGE = "PriceChange";
    private static final String PERCENT_CHANGE = "PercentageChange";

    //DB Table Create statement
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY_NAME + " TEXT not null, " +
                    PRICE + " REAL not null, " +
                    PRICE_CHANGE + " REAL not null, " +
                    PERCENT_CHANGE + " DOUBLE not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler:");
    }

    //This method is only called if table does note exist.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d(TAG, "onCreate: creating new table");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Stock> loadStocks()
    {
        // Load Stock - return ArrayList of loaded stocks
        Log.d(TAG, "loadStocks: START");
        ArrayList<Stock> stockArrayList = new ArrayList<>();

        Cursor cursor = database.query
                (
                    TABLE_NAME,  // The table to query
                    new String[]{SYMBOL, COMPANY_NAME, PRICE, PRICE_CHANGE, PERCENT_CHANGE},//The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null
                ); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++)
            {
                String symbol = cursor.getString(0);
                String companyName = cursor.getString(1);
                double price = cursor.getDouble(2);
                double priceChange = cursor.getDouble(3);
                double percentageChange = cursor.getDouble(4);
                Stock s = new Stock(symbol, companyName, price, priceChange, percentageChange);
                stockArrayList.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE");

        return stockArrayList;
    }

    public void addStock(Stock stock)
    {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY_NAME, stock.getCompanyName());
        values.put(PRICE, stock.getPrice());
        values.put(PRICE_CHANGE, stock.getPriceChange());
        values.put(PERCENT_CHANGE, stock.getPercentageChange());

        //deleteCountry(country.getName());

        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: completed : " + key);
    }

    public void updateStock(Stock stock)
    {
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY_NAME, stock.getCompanyName());
        values.put(PRICE, stock.getPrice());
        values.put(PRICE_CHANGE, stock.getPriceChange());
        values.put(PERCENT_CHANGE, stock.getPercentageChange());

        //deleteCountry(country.getName());

        long key = database.update(TABLE_NAME, values,SYMBOL + " = ?", new String[]{stock.getSymbol()});
        Log.d(TAG, "updateStock: completed : " + key);
    }

    public void deleteStock(String name)
    {
        Log.d(TAG, "deleteStock: " + name);

        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{name});

        Log.d(TAG, "deleteStock: " + cnt);
    }

    public void shutDown()
    {
        database.close();
    }

    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String title = cursor.getString(0);
                String author = cursor.getString(1);
//                String isbn = cursor.getString(2);
//                String publisher = cursor.getString(3);
//                int year = cursor.getInt(4);
                double cost = cursor.getFloat(4);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", SYMBOL + ":", title) +
                        String.format("%s %-18s", COMPANY_NAME + ":", author) +
//                        String.format("%s %-18s", PRICE + ":", isbn) +
//                        String.format("%s %-18s", PUBLISHER + ":", publisher) +
//                        String.format("%s %-18s", YEAR + ":", year) +
                        String.format("%s %-18s", PERCENT_CHANGE + ":", cost));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
}
