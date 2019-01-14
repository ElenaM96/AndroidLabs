package edu.ktu.myfirstapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thirdactivitydesign);
        ListItem item = (ListItem)getIntent().getSerializableExtra("item");
        if(item != null) {
           TextView title = findViewById(R.id.titleView);
            title.setText(item.getTitle());
           TextView description = findViewById(R.id.descriptionView);
            description.setText(item.getDescription());
           ImageView image = findViewById(R.id.image);
            image.setImageResource(item.getImageId());
        }
    }
}
