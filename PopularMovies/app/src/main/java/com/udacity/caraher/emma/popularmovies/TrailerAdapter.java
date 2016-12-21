package com.udacity.caraher.emma.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TrailerAdapter extends BaseAdapter {
    private Context mContext;
    private int count;
    private TrailerClass trailers[];

    public TrailerAdapter(Context c) {
        mContext = c;
    }

    public void clear() {
        trailers = null;
        setCount(0);
        notifyDataSetChanged();
    }

    public void add(TrailerClass[] trailerList) {
        trailers = trailerList;
        setCount(trailers.length);
        notifyDataSetChanged();
    }

    public int getCount() {
        if (trailers == null)
            return 0;
        return count;
    }

    public void setCount(int newCount) {
        count = newCount;
    }

    public Object getItem(int position) {
        return this.getItem(position);
    }

    public TrailerClass getItemAtPosition(int position) {
        if (trailers != null)
            return trailers[position];
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutId = R.layout.trailer_list_item;

        return LayoutInflater.from(mContext).inflate(layoutId, parent, false);
    }
}