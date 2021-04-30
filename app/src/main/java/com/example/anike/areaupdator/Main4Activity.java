package com.example.anike.areaupdator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageAdapterLatest mAdapter;
    private List<Upload> mUploads, mUploadsnew;
    private DatabaseReference mDatabase;
    private Upload upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        mRecyclerView = findViewById(R.id.recycler_view_latest);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();
        mUploadsnew = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    upload = ds.getValue(Upload.class);
                    mUploads.add(0,upload);
                }
                int i = 0;
                for(Upload u: mUploads){
                    if(i == 10) {
                        break;
                    }
                    mUploadsnew.add(u);
                    i++;
                }
                mAdapter = new ImageAdapterLatest(Main4Activity.this, mUploadsnew);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.v("CREATION", "Failed to read value.", error.toException());
            }
        });
    }
}
