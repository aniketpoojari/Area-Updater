package com.example.anike.areaupdator;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri uri;
    private StorageTask mUploadTask;
    private ProgressBar mprogressBar;
    private Geocoder geocoder;
    private List<Address> addresses;

    private double lat, lon;
    private static final int REQUEST_LOCATION = 1;
    private EditText areaField;
    LocationManager mLocManager;

    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;


    public void viewAreas(View view) {
        startActivity(new Intent(MainActivity.this, Main2Activity.class));
    }

    public void viewLatest(View view) {
        startActivity(new Intent(MainActivity.this, Main4Activity.class));
    }

    public void show(View view) {
        Intent newIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(newIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ImageView imageView = findViewById(R.id.imageView3);
            imageView.setImageBitmap(bitmap);
            uri = getImageUri(this, bitmap);
        }
    }

    public static Uri getImageUri(Activity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void upload(View view) {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(this, "File Getting Uploaded", Toast.LENGTH_SHORT).show();
        } else {
            if (uri != null) {
                final String area = areaField.getText().toString();
                if (area.equals("GPS LOADING...") || area.equals("")) {
                    Toast.makeText(this, "ADD AREA", Toast.LENGTH_SHORT).show();
                } else {
                    mprogressBar.setVisibility(ProgressBar.VISIBLE);
                    String name = System.currentTimeMillis() + "." + getFileExtension(uri);
                    final StorageReference fileReference = mStorageRef.child(name);
                    mUploadTask = fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String link = String.valueOf(uri);
                                    String time = DateFormat.getDateTimeInstance().format(new Date());
                                    Upload upload = new Upload(area, link, time);
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    mprogressBar.setVisibility(ProgressBar.INVISIBLE);
                                    startActivity(new Intent(MainActivity.this, Main4Activity.class));
                                }
                            });
                        }
                    });
                }
            } else {
                Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getArea(View view) {
        getAREA();
    }

    public void getAREA() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        if (!mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "TURN ON GPS", Toast.LENGTH_SHORT).show();
        } else {

            if(lon == 0.0){
                areaField.setText("GPS LOADING...");
            } else {
                geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(lat, lon, 1);
                    String area = "";
                    area = addresses.get(0).getSubLocality();
                    areaField.setText(area.toUpperCase());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mprogressBar = findViewById(R.id.progressBar);
        mprogressBar.setVisibility(ProgressBar.INVISIBLE);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        areaField = findViewById(R.id.editText);
        lat = 0.0;
        lon = 0.0;

        try {
            mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        } catch (SecurityException e){ }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(0).setFastestInterval(0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        String check = areaField.getText().toString();
        if( check.equals("GPS LOADING...")){
            getAREA();
        }
    }
}






