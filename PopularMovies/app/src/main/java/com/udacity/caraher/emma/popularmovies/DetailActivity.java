package com.udacity.caraher.emma.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jemma on 7/8/2016.
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

                String ratingText = "Rating: " + Double.toString(selectedMovie.getUserRating());
                ((TextView) rootView.findViewById(R.id.rating_text))
                        .setText(ratingText);

                String dateText = "Release Date: " + selectedMovie.getReleaseDate();
                ((TextView) rootView.findViewById(R.id.date_text))
                        .setText(dateText);

                ImageView imageView = ((ImageView) rootView.findViewById(R.id.imageView));

                try {
                    String baseUrl = "http://image.tmdb.org/t/p/w500"
                            + selectedMovie.getPosterPath() + "?";
                    Uri builtUri = Uri.parse(baseUrl).buildUpon()
                            .appendQueryParameter("api_key", "8d5d5aaec6797f2b46352b8844d64f6f")
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.v("ImageAdapter", builtUri.toString());

                /* START http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
                    new ImageLoadTask(builtUri.toString(), imageView).execute();

                /* END http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */

                    Log.v("ImageAdapter", "set image drawable");
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
