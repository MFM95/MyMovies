package com.example.mfawzy.mymovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by M.Fawzy on 9/8/2016.
 */
public class MoviesAsyncTask extends AsyncTask<String, ArrayList, String> {

    String LOG_TAG = getClass().getSimpleName();

    String image_base_url = "http://image.tmdb.org/t/p/w500/";

    MoviesAdapter adapter;

    ArrayList imageUrls = new ArrayList();
    ArrayList titles = new ArrayList();
    ArrayList releaseDates = new ArrayList();
    ArrayList overviews = new ArrayList();
    ArrayList ratings = new ArrayList();
    ArrayList ids = new ArrayList();


    @Override
    protected String doInBackground(String... params) {

        String type = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        final String APPID_PARAM = "api_key";

        String jsonStr = null;
        try {

            //  URL url = new URL("http://api.themoviedb.org/3/movie/now_playing?api_key=ecb24c01e61e837c0f2f8a067e193390");

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(type)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_TMDB_API_KEY);
            URL url = new URL(builder.toString());
            Log.v(LOG_TAG, url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            jsonStr = buffer.toString();
            // Log.v(LOG_TAG, jsonStr);
            // return jsonStr;


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }


        return jsonStr;

    }

    @Override
    protected void onPostExecute(String jsonStr) {
        try {
            help(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void help(String jsonStr) throws JSONException {
        JSONObject json = new JSONObject(jsonStr);
        JSONArray moviesList = json.getJSONArray("results");


        for (int i = 0; i < moviesList.length(); i++) {
            String poster_path = image_base_url + moviesList.getJSONObject(i).getString("poster_path");
            String title = moviesList.getJSONObject(i).getString("original_title");
            String overview = moviesList.getJSONObject(i).getString("overview");
            double rating = moviesList.getJSONObject(i).getDouble("vote_average");
            String releaseDate = moviesList.getJSONObject(i).getString("release_date");
            Long id = moviesList.getJSONObject(i).getLong("id");

            imageUrls.add(String.valueOf(poster_path));
            titles.add(String.valueOf(title));
            overviews.add(String.valueOf(overview));
            ratings.add(String.valueOf(rating));
            releaseDates.add(String.valueOf(releaseDate));
            ids.add(String.valueOf(id));
            adapter.notifyDataSetChanged();
        }

    }
}
