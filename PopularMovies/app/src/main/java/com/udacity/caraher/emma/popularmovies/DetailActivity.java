package com.udacity.caraher.emma.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.caraher.emma.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.Inflater;

/**
 * Shows more detailed information about the selected movie.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    public void favoriteMovie(View view) {

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedMovie")) {
            String apiId = (String) intent.getSerializableExtra("selectedMovie");
            ContentValues values = Utility.getMovieContentValues(this, apiId);
            int prevValue = values.getAsInteger(MovieContract.MovieEntry.COLUMN_FAVORITE);
            int updatedValue = (prevValue + 1) % 2;
            values.remove(MovieContract.MovieEntry.COLUMN_FAVORITE);
            values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, updatedValue);
            Utility.updateValuesInTable(this, MovieContract.MovieEntry.TABLE_NAME, values, apiId);

            String favButtonText;
            if (values.getAsInteger(MovieContract.MovieEntry.COLUMN_FAVORITE) == 0) {
                favButtonText = "favorite";
            } else {
                favButtonText = "unfavorite";
            }

            ((Button) findViewById(R.id.favorite_button)).setText(favButtonText);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        public DetailFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("selectedMovie")) {
                String apiId = (String)intent.getSerializableExtra("selectedMovie");
                ContentValues values = Utility.getMovieContentValues(getContext(), apiId);

                ((TextView) rootView.findViewById(R.id.title_text))
                        .setText(values.getAsString(MovieContract.MovieEntry.COLUMN_TITLE));


                String favButtonText;
                if (values.getAsInteger(MovieContract.MovieEntry.COLUMN_FAVORITE) == 0) {
                    favButtonText = "favorite";
                } else {
                    favButtonText = "unfavorite";
                }

                ((Button) rootView.findViewById(R.id.favorite_button)).setText(favButtonText);

                ((TextView) rootView.findViewById(R.id.plot_text))
                        .setText(values.getAsString(MovieContract.MovieEntry.COLUMN_PLOT));

                String ratingText = getString(R.string.rating_text)
                        + values.getAsString(MovieContract.MovieEntry.COLUMN_RATING);
                ((TextView) rootView.findViewById(R.id.rating_text))
                        .setText(ratingText);

                String dateText = getString(R.string.date_text)
                        + values.getAsString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                ((TextView) rootView.findViewById(R.id.date_text))
                        .setText(dateText);

                ImageView imageView = ((ImageView) rootView.findViewById(R.id.imageView));

                try {
                    String baseUrl = getString(R.string.base_poster_url)
                            + values.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH) + "?";
                    Uri builtUri = Uri.parse(baseUrl).buildUpon()
                            .appendQueryParameter(getString(R.string.api), getString(R.string.api_key))
                            .build();

                /* START http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
                    new ImageLoadTask(builtUri.toString(), imageView).execute();

                /* END http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
                } catch (Exception e) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }

                LinearLayout listView = (LinearLayout) rootView.findViewById(R.id.trailer_list);
                (new FetchTrailersTask(getContext(), listView, inflater, apiId)).execute();

                LinearLayout reviewList = (LinearLayout) rootView.findViewById(R.id.review_list);
                (new FetchReviewsTask(getContext(), reviewList, inflater, apiId)).execute();
            }

            return rootView;
        }
    }

    /* START http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
    public static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

    /* END http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */

    public static class FetchTrailersTask extends AsyncTask<Void, Void, TrailerClass[]> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
        private Context context;
        private LinearLayout listView;
        private LayoutInflater inflater;
        private String mId;

        public FetchTrailersTask(Context thisContext, LinearLayout rootList, LayoutInflater i, String movieId) {
            context = thisContext;
            listView = rootList;
            inflater = i;
            mId = movieId;
        }

        private TrailerClass[] getTrailerDataFromJson(String trailersJsonStr)
                throws JSONException {

            final String OWM_RESULTS = context.getString(R.string.OWM_RESULTS);
            final String OWM_ID = context.getString(R.string.OWM_ID);
            final String OWM_KEY = context.getString(R.string.OWM_KEY);
            final String OWM_NAME = context.getString(R.string.OWM_NAME);

            JSONObject trailersJson = new JSONObject(trailersJsonStr);
            JSONArray trailersArray = trailersJson.getJSONArray(OWM_RESULTS);
            int numTrailers = trailersArray.length();
            TrailerClass[] resultTrailers = new TrailerClass[numTrailers];

            for (int i = 0; i < numTrailers; i++) {
                JSONObject movieObject = trailersArray.getJSONObject(i);
                String id = movieObject.get(OWM_ID).toString();
                String key = movieObject.get(OWM_KEY).toString();
                String name = movieObject.get(OWM_NAME).toString();

                resultTrailers[i] = new TrailerClass(id, key, name);
            }

            return resultTrailers;
        }

        @Override
        protected TrailerClass[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                String baseUrl = context.getString(R.string.base_trailer_url_1)
                        .concat(mId).concat(context.getString(R.string.base_trailer_url_2));
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(context.getString(R.string.api),
                                context.getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

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
                moviesJsonStr = buffer.toString();
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

            try {
                return getTrailerDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(TrailerClass[] trailers) {
            super.onPostExecute(trailers);

            if (trailers != null) {
                int numTrailers = trailers.length;
                for (int i = 0; i < numTrailers; i++) {
                    View view  = inflater.inflate(R.layout.trailer_list_item, listView, false);
                    String id = trailers[i].getTrailerKey();
                    view.setTag(R.string.OWM_ID, id);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String id = (String) view.getTag(R.string.OWM_ID);

                            String trailerURL = context.getString(R.string.base_video_url)
                                    .concat(id);

                            /* referencing http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent */
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerURL)));
                            /* referencing http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent */
                        }
                    });

                    String name = trailers[i].getTrailerName();
                    ((TextView) view.findViewById(R.id.trailer_name)).setText(name);
                    // set item content in view
                    listView.addView(view);
                }
            }
        }
    }

    public static class FetchReviewsTask extends AsyncTask<Void, Void, TrailerClass[]> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();
        private Context context;
        private LinearLayout listView;
        private LayoutInflater inflater;
        private String mId;

        public FetchReviewsTask(Context thisContext, LinearLayout rootList, LayoutInflater i, String movieId) {
            context = thisContext;
            listView = rootList;
            inflater = i;
            mId = movieId;
        }

        private TrailerClass[] getReviewDataFromJson(String trailersJsonStr)
                throws JSONException {

            final String OWM_RESULTS = context.getString(R.string.OWM_RESULTS);
            final String OWM_ID = context.getString(R.string.OWM_ID);
            final String OWM_CONTENT = context.getString(R.string.OWM_CONTENT);

            JSONObject trailersJson = new JSONObject(trailersJsonStr);
            JSONArray trailersArray = trailersJson.getJSONArray(OWM_RESULTS);
            int numTrailers = trailersArray.length();
            TrailerClass[] resultTrailers = new TrailerClass[numTrailers];

            for (int i = 0; i < numTrailers; i++) {
                JSONObject movieObject = trailersArray.getJSONObject(i);
                String id = movieObject.get(OWM_ID).toString();
                String name = movieObject.get(OWM_CONTENT).toString();

                resultTrailers[i] = new TrailerClass(id, null, name);
            }

            return resultTrailers;
        }

        @Override
        protected TrailerClass[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                String baseUrl = context.getString(R.string.base_trailer_url_1)
                        .concat(mId).concat(context.getString(R.string.base_review_url_2));
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(context.getString(R.string.api),
                                context.getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

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
                moviesJsonStr = buffer.toString();
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

            try {
                return getReviewDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(TrailerClass[] trailers) {
            super.onPostExecute(trailers);

            if (trailers != null) {
                int numTrailers = trailers.length;
                for (int i = 0; i < numTrailers; i++) {
                    View view  = inflater.inflate(R.layout.trailer_list_item, listView, false);
                    String id = trailers[i].getTrailerKey();
                    view.setTag(R.string.OWM_ID, id);

                    String name = trailers[i].getTrailerName();
                    ((TextView) view.findViewById(R.id.trailer_name)).setText(name);
                    // set item content in view
                    listView.addView(view);
                }
            }
        }
    }
}
