package com.example.mfawzy.mymovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by M.Fawzy on 9/4/2016.
 */

public class MoviesAdapter extends ArrayAdapter {


    ArrayList imageUrls;
    private Context context;
    private LayoutInflater inflater;

    public MoviesAdapter(Context context, int resource, ArrayList imageUrls) {
        super(context, resource, imageUrls);
        this.context = context;
        this.imageUrls = imageUrls;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
        }
        Log.v("image : " , String.valueOf(imageUrls.get(position)));
        Picasso
                .with(context)
                .load(String.valueOf(imageUrls.get(position)))
                .fit()
                .into((ImageView) convertView);

        return convertView;
    }
}
