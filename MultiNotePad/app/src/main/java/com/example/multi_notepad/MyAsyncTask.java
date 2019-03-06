package com.example.multi_notepad;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAsyncTask extends AsyncTask<String, Void, String>
{
    private static final String TAG = "MyAsyncTask";
    ArrayList<Note> noteList = new ArrayList<>();

    private MainActivity mainActivity;

    public MyAsyncTask(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... longs)
    {
        Log.d(TAG, "doInBackground: Starting background execution");

        Note note = new Note();
        String jsonStr = "";
        try
        {
            InputStream is = mainActivity.getApplicationContext().openFileInput(mainActivity.getString(R.string.file_name));

            //new code for reading json array
            BufferedReader buff = new BufferedReader(new InputStreamReader(is, mainActivity.getString(R.string.encoding)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = buff.readLine()) != null)
            {
                Log.d(TAG, "loadData: line:" + line);
                sb.append(line);
            }

            jsonStr = sb.toString();
            Log.d(TAG, "doInBackground: "+ jsonStr);

            Log.d(TAG, "doInBackground: Completed background execution");
            return jsonStr;
        }
        catch (FileNotFoundException e)
        {
            Log.d(TAG, "doInBackground: file note found");
            jsonStr = "file not found";
            //Toast.makeText(mainActivity, mainActivity.getString(R.string.no_file), Toast.LENGTH_LONG).show();
        }
        catch(Exception ex)
        {
            Log.d(TAG, "loadData: error reading file: ");
            ex.printStackTrace();
        }

        return jsonStr;
    }

    @Override
    protected void onPostExecute(String jsonString)
    {
        if(jsonString.equalsIgnoreCase("file not found"))
        {
            noteList.add(new Note("file not found", "", new Date()));
            mainActivity.populateList(noteList);
        }
        else
        {
            try
            {
                if(!(jsonString.isEmpty()))
                {
                    JSONArray jsonarray = new JSONArray(jsonString);
                    for (int i = 0; i < jsonarray.length(); i++)
                    {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String title = jsonobject.getString("title");
                        String description = jsonobject.getString("description");
                        String date = jsonobject.getString("date");

                        noteList.add(new Note(title, description, new Date(date)));
                    }
                    mainActivity.populateList(noteList);
                }

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
