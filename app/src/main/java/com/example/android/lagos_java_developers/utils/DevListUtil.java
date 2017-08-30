package com.example.android.lagos_java_developers.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.lagos_java_developers.model.Developers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Return a list of {@link Developers} objects that has been built up from
 * parsing a JSON response.
 */

public class DevListUtil {

    /** Tag for the log messages */
    private static final String LOG_TAG = DevListUtil.class.getSimpleName();
    static String image;

    /**
     * Helper methods related to requesting and receiving a user data from GitHub.
     */
    public static List<Developers> fetchAllDevInfo(String requestUrl) {


        // Create URL object
        URL url = NetworkCall.createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = NetworkCall.makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Developers} object
        List<Developers> allDevInfo = extractAllDevInfo(jsonResponse);

        // Return the extracted developers information
        return allDevInfo;
    }

    private static List<Developers> extractAllDevInfo(String developerJSON) {

        if(TextUtils.isEmpty(developerJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(developerJSON);


            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            //Get the all developers on the page
            List<Developers> allJavaDevelopers = new ArrayList<>();

            // If there are results in the items Array return developers: API Page,username ,url to image values and url to developers main page
            for (int i = 0; itemsArray.length() > i; i++) {

                // Extract out the developers info
                JSONObject firstFeature = itemsArray.getJSONObject(i);


                // Extract out the url to developers
                String devJsonPage =firstFeature.getString("url");
                String username = firstFeature.getString("login");
                image =firstFeature.getString("avatar_url");
                String devMainPage=firstFeature.getString("html_url");



                allJavaDevelopers.add(new Developers(username,image,devJsonPage, devMainPage) );

            }
            return allJavaDevelopers;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the developer JSON results", e);
        }
        return null;
    }

}
