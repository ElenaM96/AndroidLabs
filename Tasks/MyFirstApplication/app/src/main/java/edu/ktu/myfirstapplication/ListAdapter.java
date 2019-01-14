package edu.ktu.myfirstapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ListAdapter extends ArrayAdapter<ListItem> {
    private int [] colorArray ={android.R.color.holo_red_light,
            android.R.color.holo_orange_dark,
            android.R.color.holo_orange_light,
            android.R.color.holo_green_light,
            android.R.color.holo_blue_bright,
            android.R.color.holo_purple,
    };
    private Random random = new Random();

    public ListAdapter(Context context, List<ListItem> objects) {
        super(context, R.layout.listitemdesign, objects);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listitemdesign,null);
        }

        int color = random.nextInt(colorArray.length);
        while (color < 0){
            color = random.nextInt(5);
        }
        TextView title =(TextView) v.findViewById(R.id.title);
        title.setTextColor(getContext().getResources().getColor(colorArray[color]));

        TextView description =(TextView) v.findViewById(R.id.description);
        description.setTextColor(colorArray[color]);
        ImageView image = (ImageView) v.findViewById(R.id.image);

        ListItem item = getItem(position);

        title.setText(item.getTitle());
        description.setText(item.getDescription());
        image.setImageResource(item.getImageId());

        return v;
    }


}
