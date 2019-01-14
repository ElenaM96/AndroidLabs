package edu.ktu.myfirstapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirstActivity extends AppCompatActivity {
    private Button myButton ;
    //private TextView myTextField;
    private EditText title;
    private EditText description;
    private Context context;
    private Intent intent;
    private List<ListItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstactivitydesign);
        this.context = getApplicationContext();
        intent = new Intent(context,SecondActivity.class);
        title = (EditText) findViewById(R.id.title1);
        description = (EditText) findViewById(R.id.description);
        Button addButton = (Button) findViewById(R.id.addButton);
        //myButton = (Button) findViewById(R.id.button);
       // myTextField = (TextView) findViewById(R.id.textfield);
        Button secondActivityButton = (Button) findViewById(R.id.secondActivityButton);
       // myButton.setOnClickListener(myButtonClick);
        secondActivityButton.setOnClickListener(startSecondActivity);
        secondActivityButton.setOnLongClickListener(startSecondActivityLong);
        addButton.setOnClickListener(addButtonListener);

    }

   /* View.OnClickListener myButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myTextField.setText(myTextField.getText()+"\n"+"Next line");
        }
    };*/

   View.OnClickListener addButtonListener = new View.OnClickListener() {
       @Override
       public void onClick(View v) {
            if(!title.getText().toString().equals("") && !description.getText().toString().equals("")) {
                ListItem item = new ListItem(title.getText().toString(), description.getText().toString());
                list.add(item);
            }else{
                new AlertDialog.Builder(FirstActivity.this)
                        .setTitle("Info")
                        .setMessage("Fill both fields before advancing")
                        .setPositiveButton("OK", null)
                        .show();
            }

       }
   };

    public void runSecondActivity(boolean b){
        intent.putExtra("flag",b);
        createList();
        intent.putExtra("arrayList",(Serializable)list);
        context.startActivity(intent);
    }

    View.OnClickListener startSecondActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runSecondActivity(true);
        }
    };

    View.OnLongClickListener startSecondActivityLong = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            runSecondActivity(false);
            return true;
        }

    };
    
    public void createList(){
        list.add(new ListItem("Adrien",R.drawable.ic_3d_rotation_black_48dp,"Mathematics, Chemistry"));
        list.add(new ListItem("Harry",R.drawable.ic_announcement_black_48dp,"Physics, Informatics"));
        list.add(new ListItem("Louis",R.drawable.ic_alarm_black_48dp,"Geography, Chemistry"));
        list.add(new ListItem("Mark",R.drawable.ic_account_box_black_48dp,"Mathematics, Chemistry"));
        list.add(new ListItem("Luke",R.drawable.ic_accessibility_black_48dp,"Mathematics, Physics"));
    }
}
