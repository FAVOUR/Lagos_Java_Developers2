package com.example.android.lagos_java_developers.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.lagos_java_developers.model.DevProfile;
import com.example.android.lagos_java_developers.model.Developers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Return the information of a {@link Developers} that has been built up from
 * parsing a JSON response.
 */
public final class DeveloperProfile {

    /** Tag for the log messages */

    public static final String LOG_TAG = DeveloperProfile.class.getSimpleName();


    /**
     * Helper methods related to requesting and receiving a user data from GitHub.
     */

    public static DevProfile fetchProfilePageInfo(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Developers} object
        DevProfile developersInformation = extractDevelopersInfo(jsonResponse);

        // Return the {@link  Developers}
        return developersInformation;
    }


    private static DevProfile extractDevelopersInfo (String earthquakeJSON) {

        if(TextUtils.isEmpty(earthquakeJSON)) {

            Log.v(LOG_TAG, earthquakeJSON);
        }

        //Extracts developers details

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);


            String developersNamne = baseJsonResponse.getString("name");
            String bio = baseJsonResponse.getString("bio");
            String public_repos =baseJsonResponse.getString("public_repos");
            String followers = baseJsonResponse.getString("followers");
            String following = baseJsonResponse.getString("following");
            String  hireable = baseJsonResponse.getString("hireable");
            String location = baseJsonResponse.getString("location");
            String email = baseJsonResponse.getString("email");


            return  new DevProfile(developersNamne,bio,public_repos,followers,following,location,email,hireable);



        }


        catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private static String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }

            }
            return output.toString();
        }




}