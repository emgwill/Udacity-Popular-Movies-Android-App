package com.udacity.caraher.emma.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private MovieClass selectedMovie;

        public DetailFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("selectedMovie")) {
                selectedMovie = (MovieClass)intent.getSerializableExtra("selectedMovie");

                ((TextView) rootView.findViewById(R.id.title_text))
                        .setText(selectedMovie.getOriginalTitle());

                ((TextView) rootView.findViewById(R.id.plot_text))
                        .setText(selectedMovie.getPlotSynopsis());

                String ratingText = getString(R.string.rating_text)
                        + Double.toString(selectedMovie.getUserRating());
                ((TextView) rootView.findViewById(R.id.rating_text))
                        .setText(ratingText);

                String dateText = getString(R.string.date_text)
                        + selectedMovie.getReleaseDate();
                ((TextView) rootView.findViewById(R.id.date_text))
                        .setText(dateText);

                ImageView imageView = ((ImageView) rootView.findViewById(R.id.imageView));

                try {
                    String baseUrl = getString(R.string.base_poster_url)
                            + selectedMovie.getPosterPath() + "?";
                    Uri builtUri = Uri.parse(baseUrl).buildUpon()
                            .appendQueryParameter(getString(R.string.api), getString(R.string.api_key))
                            .build();

                /* START http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
                    new ImageLoadTask(builtUri.toString(), imageView).execute();

                /* END http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
                } catch (Exception e) {
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
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
}
