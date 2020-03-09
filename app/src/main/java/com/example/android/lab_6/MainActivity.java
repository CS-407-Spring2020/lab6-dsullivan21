package com.example.android.lab_6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends FragmentActivity {

    private final LatLng mDestinationLatlng = new LatLng(43.0753, -89.4042306);
    private GoogleMap mMap;
    private LatLng curr;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            googleMap.addMarker(new MarkerOptions()
            .position(mDestinationLatlng)
            .title("destination"));

            displayMyLocation();

        });

    }

    private void displayMyLocation(){

        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this,
                    task -> {
                        Location mLastKnownLocation = task.getResult();
                        if(task.isSuccessful() && mLastKnownLocation != null) {
                            mMap.addPolyline(new PolylineOptions().add(
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),
                                    mDestinationLatlng));
                            curr = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(curr)
                                    .title("Current"));
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }
    }
}
