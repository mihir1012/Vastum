package com.example.vastum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

    int getIntentKey;
    private TextView name, email, number, currentLocation;
    String str = "";
    FirebaseAuth mAuth;
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mlocationRequest;
    Fragment active,fr1,fr2,fr3,fr4;
    BottomNavigationView bnv;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        getIntentKey = getIntent().getIntExtra("Flag", 10);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        currentLocation = findViewById(R.id.Location);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Toolbar tb = (Toolbar) findViewById(R.id.inc1);
        setSupportActionBar(tb);


        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginAcitivity.class));
            finish();
        }

        bnv = findViewById(R.id.nav);

        //to handle navigation by android itself : it will create new fragment every time old values will be destroyed
        /*AppBarConfiguration abc = new AppBarConfiguration.Builder(
                R.id.home, R.id.sell, R.id.redeem, R.id.profile
        ).build();
        NavController navc = Navigation.findNavController(this, R.id.fragment2);
        NavigationUI.setupActionBarWithNavController(this, navc, abc);
        NavigationUI.setupWithNavController(bnv, navc);
        */

        //to customly resume fragments after events like backpress or change fragment
        fr1=new home();
        fr2=new sell();
        fr3=new redeem();
        fr4=new profile();
        active=fr1;
        fm=getSupportFragmentManager();

        fm.beginTransaction().add(R.id.main_cont,fr2,"2").hide(fr2).commit();
        fm.beginTransaction().add(R.id.main_cont,fr3,"3").hide(fr3).commit();
        fm.beginTransaction().add(R.id.main_cont,fr4,"4").hide(fr4).commit();
        fm.beginTransaction().add(R.id.main_cont,fr1,"1").commit();

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        fm.beginTransaction().hide(active).show(fr1).commit();
                        active=fr1;
                        return true;
                    case R.id.sell:
                        fm.beginTransaction().hide(active).show(fr2).commit();
                        active=fr2;
                        return true;
                    case R.id.redeem:
                        fm.beginTransaction().hide(active).show(fr3).commit();
                        active=fr3;
                        return true;
                    case R.id.profile:
                        fm.beginTransaction().hide(active).show(fr4).commit();
                        active=fr4;
                        return true;
                }
                return false;
            }
        });

        location();

    }

    private void location() {

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocatinPermission();
            }

        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (getApplicationContext() != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        //System.out.println(latLng);

                        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            List<Address> myList = myLocation.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            Address address = myList.get(0);
                            address.getLocality();
                            currentLocation.setText(address.getLocality());
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println(e.toString());
                        }

                    }
                }
                mFusedLocationClient.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
            }
        };
    }


    private void checkLocatinPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission to access your location")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
                    } else {
                        Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();

                    }
                    break;
                }
            }
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    protected void onResume() {
        super.onResume();
        {
            startLocationUpdates();
        }

    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}