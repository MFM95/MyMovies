package com.example.mfawzy.mymovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class details extends AppCompatActivity {

    String key = "#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String imageUrl = intent.getExtras().getString("imageUrl");
        String title = intent.getExtras().getString("title");
        String overview = intent.getExtras().getString("overview");
        String releaseDate = intent.getExtras().getString("releaseDate");
        String rating = intent.getExtras().getString("rating");
        final String id = intent.getExtras().getString("id");

        TextView titleText = (TextView) findViewById(R.id.title_textView);
        TextView overviewText = (TextView) findViewById(R.id.overview_textView);
        TextView dateText = (TextView) findViewById(R.id.date_textView);
        TextView ratingText = (TextView) findViewById(R.id.rating_textView2);
        ImageView image = (ImageView) findViewById(R.id.imageView);
        ImageButton trailerBtn = (ImageButton) findViewById(R.id.trailer_button);

        titleText.setText(title);
        overviewText.setText(overview);
        dateText.setText("   " + releaseDate);
        ratingText.setText("   " + rating + " / 10");
        Picasso
                .with(this)
                .load(imageUrl)
                // .fit()
                .resize(200, 320)
                .into(image);


        trailerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MoviesDetailsAsyncTask task = new MoviesDetailsAsyncTask();
                task.execute(id);
            }
        });

    }


    class MoviesDetailsAsyncTask extends AsyncTask<String, Void, String> {

        String LOG_TAG = getClass().getSimpleName();


        @Override
        protected String doInBackground(String... params) {

            String id = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;
            try {

                final String BASE_URL = "http://api.themoviedb.org/3/movie/297761/videos?api_key=ecb24c01e61e837c0f2f8a067e193390";
                final String APPID_PARAM = "api_key";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(id)
                        .appendPath("videos")
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v(LOG_TAG, s);

            try {
                JSONObject json = null;

                json = new JSONObject(s);
                JSONArray result = json.getJSONArray("results");
                if(result.length()==0)
                {
                    Toast.makeText(details.this, "No available trailers for this movie right now !", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < result.length(); i++) {
                    if (result.getJSONObject(i).getString("type") == "Trailer") {
                        key = result.getJSONObject(i).getString("key");
                        break;
                    }
                }
                if(key.equals("#")) key = result.getJSONObject(0).getString("key");

               // String base = "http://www.youtube.com/watch?v=cxLG2wtE7TM";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("www.youtube.com")
                        .appendPath("watch")
                       .appendQueryParameter("v", key);
                URL url;
                try {
                     url = new URL(builder.toString());
                    Log.v(LOG_TAG, String.valueOf(url));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(String.valueOf(url)));
                    startActivity(intent);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
