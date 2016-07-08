package com.udacity.caraher.emma.popularmovies;

/* class from https://developer.android.com/guide/topics/ui/layout/gridview.html */

import android.content.Context;
import android.graphics.Movie;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int count;
    private MovieClass movies[];

    // references to our images
    private Integer[] mThumbIds = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher
    };

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void clear() {
        //idk something later
    }

    public void add(MovieClass[] movieList) {
        movies = movieList;
        setCount(movies.length);
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public String getMovieTitle(int position) {
        return movies[position].getOriginalTitle();
    }

    public void setCount(int newCount) {
        count = newCount;
    }

    public void setPics() {
        //set pic stuff
    }

    public Object getItem(int position) {
        return this.getItem(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            //"http://image.tmdb.org/t/p/w500/{path}?key"
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
}