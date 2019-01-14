package edu.ktu.myfirstapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SecondActivity extends AppCompatActivity{
    private ListView myList;
    private ListAdapter adapter;
    private EditText filter;
    private Button filterBtn;
    private Context context;
    private Intent intent;
    private List<ListItem> items;


    public SecondActivity(){
        this.items = new ArrayList<>();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivitydesign);
        myList = findViewById(R.id.listView);
        filter = findViewById(R.id.filter);
        filterBtn = findViewById(R.id.filterBy);
        Button orderBtn = findViewById(R.id.orderBy);
        this.context = getApplicationContext();
        this.intent = new Intent(context,ThirdActivity.class);
        orderBtn.setOnClickListener(orderListener);
        filterBtn.setOnClickListener(filterListener);

        ArrayList<ListItem> object = (ArrayList<ListItem>) getIntent().getSerializableExtra("arrayList");

        if(object != null){
            items.addAll(object);
        }

        if (getIntent().getBooleanExtra("flag",true)){

            items.add(new ListItem("Jack",R.drawable.ic_3d_rotation_black_48dp,"Mathematics, Chemistry"));
            items.add(new ListItem("Jane",R.drawable.ic_announcement_black_48dp,"Physics, Informatics"));
            items.add(new ListItem("Bob",R.drawable.ic_alarm_black_48dp,"Geography, Chemistry"));
            items.add(new ListItem("Clara",R.drawable.ic_account_box_black_48dp,"Mathematics, Chemistry"));
            items.add(new ListItem("Sam",R.drawable.ic_accessibility_black_48dp,"Mathematics, Physics"));



        } else {
            items.add(new ListItem("Mathematics",R.drawable.ic_3d_rotation_black_48dp,
                    "Mathematics is the study of topics such as quantity, structure, " +
                            "space and change."));

            items.add(new ListItem("Physics",R.drawable.ic_announcement_black_48dp,
                    "Physics is the natural science that involves the study of matter "+
                            " and its motion through space and time along with related "+
                            " concepts such as energy and force"));

            items.add(new ListItem("Chemistry",R.drawable.ic_alarm_black_48dp,
                    "Chemistry is a branch of physical science that studies the composition, "+
                            "structure, proprieties and change of matter." ));

            items.add(new ListItem("Informatics",R.drawable.ic_account_box_black_48dp,
                    "Informatics is the science of information and computer information system."));

            items.add(new ListItem("Geography",R.drawable.ic_accessibility_black_48dp,
                    "Geography is a field of science devoted to the study of the lands, the features, " +
                            "the inhabitants, and the phenomena of Earth."));

        }


        adapter = new ListAdapter(this,items);
        myList.setAdapter(adapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = myList.getItemAtPosition(position);
                ListItem item = (ListItem) object;
                intent.putExtra("item",item);
                startActivity(intent);
            }
        });

    }

    View.OnClickListener orderListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            orderingMethod();
           adapter.notifyDataSetChanged();
        }
    };

    View.OnClickListener filterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String letter = filter.getText().toString().toUpperCase();
            if (letter.length() > 0 && letter.length() < 2 ) {
                List<ListItem> list = new ArrayList<>();
                for (ListItem item : items) {
                    if (item.getTitle().startsWith(letter)) {
                        list.add(item);
                    }
                }
                if(list.isEmpty()){
                    createDialog("No match found");
                } else {
                    setNewAdapter(list);
                }
            } else if (letter.length() < 1) {
                setNewAdapter(items);
            }else{
                createDialog("Insert just one letter !");
            }
        }
    };

    private void orderingMethod(){
        adapter.sort( new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                String s1 = o1.getTitle();
                String s2 = o2.getTitle();
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    private void setNewAdapter(List<ListItem> list){
        adapter = new ListAdapter(context, list);
        myList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void createDialog(String message){
        new AlertDialog.Builder(SecondActivity.this)
                .setTitle("Info")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public void onResume(){
        super.onResume();
    }
}
