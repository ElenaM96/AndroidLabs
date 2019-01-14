package edu.ktu.mysecondapplication;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements RequestOperator.RequestOperatorListener{
    Button sendRequestButton;
    TextView title,bodyText,circle;
    private List<ModelPost> publication = new ArrayList<>();
    private List<ModelPost> list = new ArrayList<>();
    private PublicationAdapter adapter;
    private ListView listView;
    private IndicatingView indicator;
    private ProgressIndicator progIndicator;
    private ProgressBar progressBar;
    private Timer timer;
    private TimerTask task;
    private int progressStatus = 20;
    private boolean isTimerRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivitydesign);
        circle = (TextView) findViewById(R.id.circle);
        circle.setVisibility(View.INVISIBLE);

        sendRequestButton = (Button) findViewById(R.id.send_request);
        sendRequestButton.setOnClickListener(requestButtonClicked);
        //title = (TextView) findViewById(R.id.title);
        //bodyText = (TextView) findViewById(R.id.body_text);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        indicator = (IndicatingView) findViewById(R.id.generated_graphic);
        progIndicator = (ProgressIndicator) findViewById(R.id.progress_indicator);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new PublicationAdapter(this,list);
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener requestButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sendRequest();
            if (isTimerRunning){
                timer.cancel();
                timer.purge();
                progressStatus = 20;
                isTimerRunning = false;
            }
            timer = new Timer();
            timer.schedule(new PeriodicTask(),0);
            isTimerRunning = true;
        }
    };

    private class PeriodicTask extends TimerTask {
        @Override
        public void run() {
            progIndicator.setState(progressStatus);
            progressStatus += 20;
            progIndicator.invalidate();
            try {
                Thread.sleep(700);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            timer.schedule(new PeriodicTask(), 0);
        }
    }


    private void sendRequest(){
        RequestOperator ro = new RequestOperator();
        ro.setListener(this);
        ro.start();
    }

    public void updatePublication(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(publication != null){
                    circle.setVisibility(View.VISIBLE);
                    circle.setText(String.valueOf(publication.size()));
                    list.addAll(publication);
                    adapter.notifyDataSetChanged();

                } /* else {
                    title.setText("");
                    bodyText.setText("");
                }*/
            }
        });
    }

    @Override
    public void success(List<ModelPost> publication) {
        this.publication = publication;
        setIndicatorStatus(IndicatingView.SUCCESS);
        updatePublication();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void failed(int responseCode) {
        this.publication = null;
        setIndicatorStatus(IndicatingView.FAILED);
        updatePublication();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void loading() {
        setIndicatorStatus(IndicatingView.LOADING);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }


    public void setIndicatorStatus(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.setState(status);
                indicator.invalidate();
            }
        });
    }
}
