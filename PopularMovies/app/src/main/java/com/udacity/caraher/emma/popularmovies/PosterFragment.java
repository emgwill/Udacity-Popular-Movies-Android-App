package com.udacity.caraher.emma.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PosterFragment extends Fragment {

    public PosterFragment() {
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        String[] data = {
                "Iron Man",
                "Man of Steel",
                "Interstellar",
                "Deadpool",
                "Harry Potter and the Order of the Phoenix",
                "Enders Game",
                "What's Eating Gilbert Grape?"
        };
        List<String> movieTitleList = new ArrayList<>(Arrays.asList(data));

        ArrayAdapter<String> mForecastAdapter =
                new ArrayAdapter<>(
                        getActivity(),
                        R.layout.grid_item_movie_poster,
                        R.id.grid_item_poster_view,
                        movieTitleList);

        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.popular_movies_grid);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }
}
