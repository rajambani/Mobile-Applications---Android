package com.example.inspirationrewards.Utility;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.inspirationrewards.Bean.User;
import com.example.inspirationrewards.MainActivity;
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

import static com.example.inspirationrewards.create_profile.CREATE_PROFILE;
import static java.net.HttpURLConnection.HTTP_OK;

public class SaveProfileAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "LoginAPIAyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint = "/profiles";
    private create_profile create_profile;
    private User user;
    private int type;

    public SaveProfileAsyncTask(create_profile create_profile, User user, int type)
    {
        this.create_profile = create_profile;
        this.user = user;
        this.type = type;
    }


    @Override
    protected String doInBackground(Void... strings) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("studentId", "A20396925");
            jsonObject.put("username", user.getUserName());
            jsonObject.put("password", user.getPwd());
            jsonObject.put("firstName", user.getfName());
            jsonObject.put("lastName", user.getlName());
            jsonObject.put("pointsToAward", user.getPointsToAward());
            jsonObject.put("department", user.getDepartment());
            jsonObject.put("story", user.getStory());
            jsonObject.put("position", user.getPosition());
            jsonObject.put("admin", user.isAdmin());
            jsonObject.put("location", user.getLocation());
            jsonObject.put("imageBytes", user.getImage());
            jsonObject.put("rewardRecords", user.getRewardList());

            Log.d(TAG, "doInBackground: "+user.toString());

            return doAPICall(jsonObject);

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

            if(type == CREATE_PROFILE)
                connection.setRequestMethod("POST");  // POST - others might use PUT, DELETE, GET
            else
                connection.setRequestMethod("PUT");  // PUT - to update profile

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
            //user.setStory();
            try
            {
                JSONObject jsonObj = null;
                jsonObj = new JSONObject(connectionResult);
                JSONObject myResponse = jsonObj.getJSONObject("errordetails");
                String tsmresponse = (String) myResponse.get("message");
                Log.d(TAG, "onPostExecute: errormsg: "+ tsmresponse.toString());
                create_profile.sendResults(tsmresponse);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            Log.d(TAG, "onPostExecute: error" + connectionResult);
        }
        else
        {
            create_profile.sendResults(user);
            Log.d(TAG, "onPostExecute: "+connectionResult);
        }


    }

}
