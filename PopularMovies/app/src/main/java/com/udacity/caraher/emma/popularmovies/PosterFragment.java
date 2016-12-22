package com.udacity.caraher.emma.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

public class PosterFragment extends Fragment {

    ImageAdapter imageAdapter;

    public PosterFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_fragment, menu);
    }

    public void updateMovies() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular));

        //if (sortPref.equals(getString(R.string.pref_sort_favorites))) {
            //imageAdapter.clear();
            //imageAdapter.add(Utility.getFavorites(getContext()));
        //} else {
            (new FetchMoviesTask()).execute();
        //}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.popular_movies_grid);

        /* START from https://developer.android.com/guide/topics/ui/layout/gridview.html */
        imageAdapter = new ImageAdapter(getContext());
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedMovieId = imageAdapter.getItemAtPosition(i);

                if (selectedMovieId != null) {
                    Context context = view.getContext();
                    Intent detailIntent = new Intent(context, DetailActivity.class)
                            .putExtra("selectedMovie", selectedMovieId);
                    startActivity(detailIntent);
                } else {

                    Toast.makeText(getContext(), getString(R.string.no_connection),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        /* END from https://developer.android.com/guide/topics/ui/layout/gridview.html */

        /*SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_favorites));

        if (sortPref.equals(getString(R.string.pref_sort_favorites))) {
            imageAdapter.clear();
            imageAdapter.add(Utility.getFavorites(getContext()));
        }*/

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private String sortPref;

        private String[] getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            String tableName = MovieContract.MovieEntry.TABLE_NAME;

            final String OWM_RESULTS = getString(R.string.OWM_RESULTS);
            final String OWM_TITLE = getString(R.string.OWM_TITLE);
            final String OWM_PLOT = getString(R.string.OWM_PLOT);
            final String OWM_RATING = getString(R.string.OWM_RATING);
            final String OWM_DATE = getString(R.string.OWM_DATE);
            final String OWM_POSTER = getString(R.string.OWM_POSTER);
            final String OWM_ID = getString(R.string.OWM_ID);

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);
            int numMovies = moviesArray.length();
            String[] resultMoviePosters = new String[numMovies];

            for (int i = 0; i < numMovies; i++) {
                ContentValues values = new ContentValues();

                JSONObject movieObject = moviesArray.getJSONObject(i);
                String title = movieObject.get(OWM_TITLE).toString();
                String plot = movieObject.get(OWM_PLOT).toString();
                double rate = movieObject.getDouble(OWM_RATING);
                String rating = Double.toString(movieObject.getDouble(OWM_RATING));
                String date = movieObject.get(OWM_DATE).toString();
                String poster = movieObject.get(OWM_POSTER).toString();
                String id = movieObject.get(OWM_ID).toString();

                Utility.putInContentValue(values, MovieContract.MovieEntry.COLUMN_API_ID, id);
                Utility.putInContentValue(values, MovieContract.MovieEntry.COLUMN_TITLE, title);
                Utility.putInContentValue(values, MovieContract.MovieEntry.COLUMN_PLOT, plot);
                Utility.putInContentValue(values, MovieContract.MovieEntry.COLUMN_RATING, rating);
                Utility.putInContentValue(values, MovieContract.MovieEntry.COLUMN_RELEASE_DATE, date);
                Utility.putInContentValue(values, MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
                Utility.putInContentValue(values, MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster);

                Utility.insertValuesInTable(getContext(), tableName, values);
                resultMoviePosters[i] = id;
            }
            return resultMoviePosters;
        }

        @Override
        protected String[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                String sortPrefString;

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                sortPref = sharedPref.getString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popular));

                Log.e(LOG_TAG, sortPref);

                if (sortPref.equals(getString(R.string.pref_sort_popular))) {
                    sortPrefString = getString(R.string.popular_url);
                } else {
                    sortPrefString = getString(R.string.rated_url);
                }

                String baseUrl = getString(R.string.base_url) + sortPrefString;
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(getString(R.string.api), getString(R.string.api_key))
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
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] movies) {
            super.onPostExecute(movies);

            if (movies != null) {
                if (sortPref.equals(getString(R.string.pref_sort_favorites))) {
                    movies = Utility.getFavorites(getContext());
                }

                imageAdapter.clear();
                imageAdapter.add(movies);
            }
        }
    }
}
