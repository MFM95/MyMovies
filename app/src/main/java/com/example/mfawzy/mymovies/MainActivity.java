package com.example.mfawzy.mymovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

public class MainActivity extends AppCompatActivity {
    String LOG_TAG = getClass().getSimpleName();
    MoviesAdapter adapter;

    ArrayList imageUrls = new ArrayList();
    ArrayList titles = new ArrayList();
    ArrayList releaseDates = new ArrayList();
    ArrayList overviews = new ArrayList();
    ArrayList ratings = new ArrayList();
    ArrayList ids = new ArrayList();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.top_rated :
            {
                startActivity(new Intent(MainActivity.this, Top_rated.class));
                break;
            }
            case R.id.settings :
            {

                break;
            }
        }
        return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoviesAsyncTask task = new MoviesAsyncTask();
        if(isNetworkAvailable())
            task.execute("now_playing");
        else
        {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG);

            snackbar.show();
        }


        GridView gridView = (GridView) findViewById(R.id.gridView);
        adapter = new MoviesAdapter(this, R.layout.grid_item, imageUrls);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(myOnItemClickListener );
    }
    AdapterView.OnItemClickListener myOnItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String imageUrl = String.valueOf(imageUrls.get(position));
            String title = String.valueOf(titles.get(position));
            String overview = String.valueOf(overviews.get(position));
            String rating = String.valueOf(ratings.get(position));
            String releaseDate = String.valueOf(releaseDates.get(position));
            String id_movie = String.valueOf(ids.get(position));


            Intent intent = new Intent(MainActivity.this, details.class);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("title", title);
            intent.putExtra("overview", overview);
            intent.putExtra("releaseDate", releaseDate);
            intent.putExtra("rating", rating);
            intent.putExtra("id" , id_movie);
            startActivity(intent);
        }

    };


    private class MoviesAsyncTask extends AsyncTask<String, ArrayList, String> {

        String LOG_TAG = getClass().getSimpleName();

        String image_base_url = "http://image.tmdb.org/t/p/w500/";

       // MoviesAdapter adapter;
        /*
        ArrayList imageUrls = new ArrayList();
        ArrayList titles = new ArrayList();
        ArrayList releaseDates = new ArrayList();
        ArrayList overviews = new ArrayList();
        ArrayList ratings = new ArrayList();
        ArrayList ids = new ArrayList();
*/

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

            Log.v(LOG_TAG, jsonStr);


            for (int i = 0; i < moviesList.length(); i++) {
                String poster_path = image_base_url + moviesList.getJSONObject(i).getString("poster_path");
                String title = moviesList.getJSONObject(i).getString("original_title");
                String overview = moviesList.getJSONObject(i).getString("overview");
                double rating = moviesList.getJSONObject(i).getDouble("vote_average");
                String releaseDate = moviesList.getJSONObject(i).getString("release_date");
                Long id = moviesList.getJSONObject(i).getLong("id");

                Log.v(LOG_TAG, poster_path + "  " + i + '\n');

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



}