package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

public class MyAsyncTaskLoadFinancialDetails extends AsyncTask<String, Void, String>
{
    private static final String TAG = "MyAsyncTaskLoadFinancia";
    private MainActivity ma;
    private final String FINANCIAL_URL = "https://api.iextrading.com/1.0/stock/";
    private List<Stock> stockList = new ArrayList<>();

    public MyAsyncTaskLoadFinancialDetails(MainActivity ma)
    {
        this.ma = ma;
    }

    @Override
    protected String doInBackground(String... symbol)
    {
        Uri.Builder buildURL = Uri.parse(FINANCIAL_URL).buildUpon();
        buildURL.appendPath(symbol.toString().trim());
        buildURL.appendPath("quote");
        buildURL.appendQueryParameter("displayPercent", "true");

        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: the url obtained is "+urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        parseJSON(sb.toString());

        return null;
    }

    private void parseJSON(String jsonStr)
    {
        try {
            if (!(jsonStr.isEmpty()))
            {
                JSONObject jsonobject = new JSONObject(jsonStr);
                String symnol = jsonobject.getString("symbol");
                String companyName = jsonobject.getString("companyName");
                String price = jsonobject.getString("latestPrice");
                String priceChange = jsonobject.getString("change");
                String percentChange = jsonobject.getString("changePercent");
                stockList.add(new Stock(symnol, companyName, Double.parseDouble(price),
                        Double.parseDouble(priceChange), Double.parseDouble(percentChange)));
            }
            Log.d(TAG, "parseJSON: map size: " + stockList.size());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
