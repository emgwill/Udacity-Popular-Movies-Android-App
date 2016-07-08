package com.udacity.caraher.emma.popularmovies;

/* class from https://developer.android.com/guide/topics/ui/layout/gridview.html */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int count;
    private MovieClass movies[];

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void clear() {
        movies = null;
        setCount(0);
        notifyDataSetChanged();
    }

    public void add(MovieClass[] movieList) {
        movies = movieList;
        setCount(movies.length);
        notifyDataSetChanged();
    }

    public int getCount() {
        if (movies == null)
            return 8;
        return count;
    }

    public String getMovieTitle(int position) {
        if (movies == null)
            return "Placeholder Title";
        return movies[position].getOriginalTitle();
    }

    public void setCount(int newCount) {
        count = newCount;
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
            imageView.setLayoutParams(new GridView.LayoutParams(350, 350));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(6, 6, 6, 6);
        } else {
            imageView = (ImageView) convertView;
        }

        if (movies == null) {
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            try {
                String baseUrl = "http://image.tmdb.org/t/p/w500"
                        + movies[position].getPosterPath() + "?";
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

        return imageView;
    }

    /* START http://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview */
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

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