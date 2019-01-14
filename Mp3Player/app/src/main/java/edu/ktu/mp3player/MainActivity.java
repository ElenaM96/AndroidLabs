package edu.ktu.mp3player;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button btnPause;
    private Button btnPlay;
    private Button btnStop;
    private Button btnNext,btnPrev;
    private  int[] resID = {R.raw.sound1, R.raw.sound2, R.raw.sound3};
    private int i = 0;
    private int length;
    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPause = (Button) findViewById(R.id.button_pause);
        btnPlay = (Button) findViewById(R.id.button_play);
        btnStop = (Button) findViewById(R.id.button_stop);
        btnNext = (Button) findViewById(R.id.button_next);
        btnPrev = (Button) findViewById(R.id.button_prev);


        btnPause.setEnabled(true);
        btnPlay.setEnabled(true);
        btnStop.setEnabled(true);

        btnPlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
        btnStop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop, 0, 0, 0);
        btnPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);
        btnNext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_skip_next_black_24dp,0,0,0);
        btnPrev.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_skip_previous_black_24dp,0,0,0);


        btnPlay.setOnClickListener(playClick);
        btnStop.setOnClickListener(stopClick);
        btnPause.setOnClickListener(pauseClick);
        btnNext.setOnClickListener(nextClick);
        btnPrev.setOnClickListener(prevClick);

    }


    View.OnClickListener playClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if(!isPause) {
                    play();
                } else {
                    mediaPlayer.seekTo(length);
                    mediaPlayer.start();
                    isPause = false;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Sorry!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    View.OnClickListener stopClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                releaseMediaPlayer();
            } catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener pauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaPlayer.pause();
            length = mediaPlayer.getCurrentPosition();
            isPause = true;
        }
    };

    View.OnClickListener nextClick =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            i++;
            if(i > resID.length - 1){
                i = 0;
            }
            releaseMediaPlayer();
            play();
        }
    };

    View.OnClickListener prevClick =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            i--;
            if(i < 0){
                i = resID.length - 1;
            }
            releaseMediaPlayer();
            play();
        }
    };

    private  void releaseMediaPlayer(){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
    }

    protected  void onDestroy(){
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void play(){
           mediaPlayer = MediaPlayer.create(this,resID[i]);
           mediaPlayer.start();
           mediaPlayer.setOnCompletionListener(listener);
    }

    MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if(i < resID.length - 1){
                i++;
                play();
            } else{
                i = 0;
                play();
            }
        }
    };
}
