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
import java.util.HashMap;

public class MyAsyncTask extends AsyncTask<String, Void, Void>
{
    private static final String TAG = "MyAsyncTask";
    private MainActivity mainActivity;
    HashMap<String, String> map = new HashMap<>();
    private final String stockURL = "https://api.iextrading.com/1.0/ref-data/symbols/";
    //private final String stockURL = "https://www.google.com";

    public MyAsyncTask(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(String ... str)
    {
        Uri.Builder buildURL = Uri.parse(this.stockURL).buildUpon();
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        }
        catch (Exception e) {
            Log.e(TAG, "Error in doInBackground: ", e);
            e.printStackTrace();
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
                JSONArray jsonarray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonarray.length(); i++)
                {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String symnol = jsonobject.getString("symbol");
                    String companyName = jsonobject.getString("name");

                    map.put(symnol.toLowerCase().trim(), companyName);
                }
                Log.d(TAG, "parseJSON: map size: " + map.size());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        mainActivity.populateInitialMap(map);
    }
}
