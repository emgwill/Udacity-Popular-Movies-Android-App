package com.udacity.caraher.emma.popularmovies;

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
        (new FetchMoviesTask()).execute();
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
                MovieClass selectedMovie = imageAdapter.getItemAtPosition(i);

                Context context = view.getContext();
                Intent detailIntent = new Intent(context, DetailActivity.class)
                        .putExtra("selectedMovie", selectedMovie);
                startActivity(detailIntent);
            }
        });
        /* END from https://developer.android.com/guide/topics/ui/layout/gridview.html */

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, MovieClass[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private MovieClass[] getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_TITLE = "original_title";
            final String OWM_PLOT = "overview";
            final String OWM_RATING = "vote_average";
            final String OWM_DATE = "release_date";
            final String OWM_POSTER = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);
            int numMovies = moviesArray.length();
            MovieClass[] resultMovies = new MovieClass[numMovies];

            for (int i = 0; i < numMovies; i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                String title = movieObject.get(OWM_TITLE).toString();
                String plot = movieObject.get(OWM_PLOT).toString();
                double rating = movieObject.getDouble(OWM_RATING);
                String date = movieObject.get(OWM_DATE).toString();
                String poster = movieObject.get(OWM_POSTER).toString();

                resultMovies[i] = new MovieClass(title, plot, rating, date, poster);
            }

            return resultMovies;
        }

        @Override
        protected MovieClass[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                String sortPrefString;

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sortPref = sharedPref.getString(getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popular));

                Log.e(LOG_TAG, sortPref);

                if (sortPref.equals(getString(R.string.pref_sort_popular))) {
                    sortPrefString = "popularity.desc";
                } else {
                    sortPrefString = "vote_average.desc";
                }

                String baseUrl = "https://api.themoviedb.org/3/discover/movie?";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("api_key", "8d5d5aaec6797f2b46352b8844d64f6f")
                        .appendQueryParameter("sort_by", sortPrefString)
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
        protected void onPostExecute(MovieClass[] moviesList) {
            super.onPostExecute(moviesList);

            if (moviesList != null) {
                imageAdapter.clear();
                imageAdapter.add(moviesList);
            }
        }
    }
}
