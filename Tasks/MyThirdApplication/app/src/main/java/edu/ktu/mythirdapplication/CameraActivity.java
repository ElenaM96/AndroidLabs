package edu.ktu.mythirdapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.widget.ImageView;

import java.io.File;

public class CameraActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        String path = Environment.getExternalStorageDirectory()+ "/pic.jpg";

        File imgFile = new File(path);

        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.pic);

            myImage.setImageBitmap(myBitmap);

        }
    }
}
