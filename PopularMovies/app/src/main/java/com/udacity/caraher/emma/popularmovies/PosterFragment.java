package com.udacity.caraher.emma.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


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

        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.popular_movies_grid);

        /* START from https://developer.android.com/guide/topics/ui/layout/gridview.html */
        gridView.setAdapter(new ImageAdapter(getContext()));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        /* END from https://developer.android.com/guide/topics/ui/layout/gridview.html */

        return rootView;
    }
}
