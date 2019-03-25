package com.example.conav;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String provider;
    double latitude,longitude;
    static final String TAG="Map";
    private final long MIN_TIME=1000; //per second
    private final long MIN_DIST= 5; //5 meter
    LatLng latLng;
    LatLng currentLocation;
    double distance;
    PolylineOptions rectOptions;
    Polyline polyline;
    private ArrayList<LatLng> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

    }
    //To DRAW POLYLINE
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onMapReady: " + "No Access");
            return;
        }
        mMap = googleMap;
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME,MIN_DIST,this);
        Location location=locationManager.getLastKnownLocation(provider);
        if(location != null){
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            currentLocation=latLng;
            rectOptions = new PolylineOptions().add(latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
        }
        points = new ArrayList<LatLng>();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if(latLng!=null && currentLocation!=null){
                        distance += SphericalUtil.computeDistanceBetween(currentLocation, latLng);
                    }
                    currentLocation=latLng;
                    Toast.makeText(MapsActivity.this, "You've moved " + distance + "  meter/s", Toast.LENGTH_LONG).show();
                    points.add(latLng);
                    redrawLine();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18f));

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DIST,locationListener);
        }
        catch(SecurityException e){
            e.printStackTrace();
        }
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME,MIN_DIST,this);
        Location current= locationManager.getLastKnownLocation(provider);
        if(location!= null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

       // mMap.addMarker(new MarkerOptions().position(new LatLng(longitude, longitude)).title("Marker"));

    }
    private void redrawLine(){
        mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            Log.d(TAG, "redrawLine: " + points.get(i));
            options.add(point);
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here!")); //add Marker in current position
        polyline = mMap.addPolyline(options); //add Polyline
    }
}