package com.example.anike.areaupdator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main2Activity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Set<String> hash_Set;
    private ArrayList<String> areaList;
    private ArrayAdapter adapter;
    private Upload upload;
    private ListView myarealist;


    public void uploadchange(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mDatabase = FirebaseDatabase.getInstance().getReference("uploads");
        hash_Set = new HashSet<>();
        areaList = new ArrayList<>();
        upload = new Upload();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever dat a at this location is updated.
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    upload = ds.getValue(Upload.class);
                    hash_Set.add(upload.getArea());
                }
                for(String a: hash_Set){
                    areaList.add(a);
                }
                adapter = new ArrayAdapter<String>(Main2Activity.this,android.R.layout.simple_expandable_list_item_1,areaList);
                myarealist =(ListView) findViewById(R.id.arealist);
                myarealist.setAdapter(adapter);
                myarealist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                        intent.putExtra("area", areaList.get(position));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.v("CREATION", "Failed to read value.", error.toException());
            }
        });

    }

}
