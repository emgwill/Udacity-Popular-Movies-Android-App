package com.udacity.caraher.emma.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;

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
            }

            return rootView;
        }
    }
}
