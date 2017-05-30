package com.tomgehrke.geekguardiannews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static com.tomgehrke.geekguardiannews.NewsActivity.LOG_TAG;

public final class QueryUtils {

    // Not intended to be instantiated
    public QueryUtils() {
    }

    // Create ArrayList of articles
    public static ArrayList<Article> fetchArticleData(String stringUrl) {
        URL requestUrl = createUrlFromString(stringUrl);

        // Attempt to create JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpsRequest(requestUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request.", e);
        }

        // Return ArrayList
        return extractFeatureFromJson(jsonResponse);
    }

    //Extract an ArrayList of Article objects from the JSON response
    @Nullable
    private static ArrayList<Article> extractFeatureFromJson(String jsonResponse) {

        // Is there something to parse?
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Prepare an empty ArrayList
        ArrayList<Article> articles = new ArrayList<>();

        // Attempt to parse the JSON response
        try {
            JSONObject guardianArticlesObject = new JSONObject(jsonResponse);
            JSONObject response = guardianArticlesObject.getJSONObject("response");

            if (response.getInt("total") > 0) {
                JSONArray results = response.getJSONArray("results");

                // Iterate through results array
                for (int i = 0; i < results.length(); i++) {

                    // Get the current article item
                    JSONObject currentArticle = results.getJSONObject(i);

                    String title = currentArticle.getString("webTitle");
                    String sectionName = currentArticle.getString("sectionName");
                    String url = currentArticle.getString("webUrl");
                    Date publicationDate = createDateFromString(currentArticle.getString("webPublicationDate"));

                    JSONObject fields = currentArticle.getJSONObject("fields");

                    String byLine = "";
                    if (fields.has("byline")) {
                        byLine = fields.getString("byline");
                    }

                    Bitmap thumbnail = null;

                    if (fields.has("thumbnail")) {
                        URL thumbnailUrl = createUrlFromString(fields.getString("thumbnail"));

                        try {
                            thumbnail = BitmapFactory.decodeStream(thumbnailUrl.openConnection().getInputStream());
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Problem downloading cover image.", e);
                        }
                    }

                    // Create new Article object and add it to the ArrayList
                    Article newArticle = new Article(title, sectionName, publicationDate, url, byLine, thumbnail);
                    articles.add(newArticle);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON response for article information.", e);
        }

        return articles;
    }

    // Create URL object from String URL
    private static URL createUrlFromString(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem converting string into a URL.", e);
        }

        return url;
    }

    // Make the HTTP request in order to get the JSON response
    private static String makeHttpsRequest(URL url)
            throws IOException {

        String jsonResponse = "";

        // Make sure we've got a URL
        if (url != null) {
            HttpsURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream and parse if the request was successful
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = createStringFromInputStream(inputStream);
                } else {
                    // If the request was not successful log the response code
                    Log.e(LOG_TAG, "HTTP request for JSON returned the following code: " + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving JSON response.", e);

            } finally {
                // Do some cleanup
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }

        return jsonResponse;
    }

    // Convert input stream JSON response into a string
    @NonNull
    private static String createStringFromInputStream(InputStream inputStream)
            throws IOException {

        StringBuilder responseStringBuilder = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                responseStringBuilder.append(line);
                line = reader.readLine();
            }
        }

        return responseStringBuilder.toString();
    }

    private static Date createDateFromString(String dateString) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault());

        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing date string.", e);
        }

        return date;
    }
}
