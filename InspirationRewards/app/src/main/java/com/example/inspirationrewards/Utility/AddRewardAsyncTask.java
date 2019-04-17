package com.example.inspirationrewards.Utility;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.MainActivity;
import com.example.inspirationrewards.activity_add_reward;
import com.example.inspirationrewards.create_profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.net.HttpURLConnection.HTTP_OK;

public class AddRewardAsyncTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "LoginAPIAyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint = "/rewards";
    private activity_add_reward create_profile;
    private User sourceUser;
    private User targetUser;

    public AddRewardAsyncTask(activity_add_reward activity_add_reward, User sourceUser, User targetUser)
    {
        this.create_profile = activity_add_reward;
        this.sourceUser = sourceUser;
        this.targetUser = targetUser;

        Log.d(TAG, "AddRewardAsyncTask: sourceUser: "+ sourceUser.getUserName());
        Log.d(TAG, "AddRewardAsyncTask: targetUser: "+ targetUser.getUserName());
    }


    @Override
    protected String doInBackground(String... strings) {

        try {

            int pointsAwarded = Integer.parseInt(strings[0]);
            String comments = strings[1];

            //Target obj.
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", "A20396925");
            jsonObject.put("username", targetUser.getUserName());
            jsonObject.put("name", sourceUser.getfName());
            //format date
            Date date = new Date();
            String DATE_FORMAT = "MM/dd/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            jsonObject.put("date", sdf.format(date));
            jsonObject.put("notes", comments);
            jsonObject.put("value", pointsAwarded);

            //Source obj
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("studentId", "A20396925");
            jsonObject1.put("username", sourceUser.getUserName());
            jsonObject1.put("password", sourceUser.getPwd());

            //Final obj.
            JSONObject sendObj = new JSONObject();
            sendObj.put("target", jsonObject);
            sendObj.put("source", jsonObject1);

            Log.d(TAG, "doInBackground: sendObj: "+sendObj.toString());

            return doAPICall(sendObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseUrl + loginEndPoint;  // Build the full URL

            Uri uri = Uri.parse(urlString);    // Convert String url to URI
            URL url = new URL(uri.toString()); // Convert URI to URL

            connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");  // POST - others might use PUT, DELETE, GET

            // Set the Content-Type and Accept properties to use JSON data
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            // Write the JSON (as String) to the open connection
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            // If successful (HTTP_OK)
            if (responseCode == HTTP_OK) {

                // Read the results - use connection's getInputStream
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
                return result.toString();

            } else {
                // Not HTTP_OK - some error occurred - use connection's getErrorStream
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
                return result.toString();
            }

        } catch (Exception e) {
            // Some exception occurred! Log it.
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally { // Close everything!
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        return "Some error has occurred"; // Return an error message if Exception occurred
    }

    @Override
    protected void onPostExecute(String connectionResult) {

        // Normally we would parse the results and make use of the data
        // For this example, we just use the returned string size - empty is fail
        if (connectionResult.contains("errordetails")) // If there is "error" in the results...
        {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(connectionResult);
                JSONObject myResponse = jsonObj.getJSONObject("errordetails");
                String tsmresponse = (String) myResponse.get("message");
                Log.d(TAG, "onPostExecute: errormsg: "+ tsmresponse.toString());
                create_profile.sendResults(tsmresponse);
                //create_profile.sendResults("Failed");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
        {
            Log.d(TAG, "onPostExecute: connectionResult: Add"+connectionResult);
            create_profile.sendResults("Success");
            Log.d(TAG, "onPostExecute: connectionResult: Add"+connectionResult);
        }


    }

}
