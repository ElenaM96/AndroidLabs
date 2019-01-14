package edu.ktu.mysecondapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class PublicationAdapter extends ArrayAdapter<ModelPost>{
    private TextView title;
    private TextView body;

    public PublicationAdapter(@NonNull Context context, List<ModelPost> publications) {
        super(context, R.layout.item, publications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item,null);
        }

        final ModelPost item = getItem(position);

        title = v.findViewById(R.id.title);
        body = v.findViewById(R.id.body_text);

        title.setText(item.getTitle());
        body.setText(item.getBodyText());
        return v;
    }
}
