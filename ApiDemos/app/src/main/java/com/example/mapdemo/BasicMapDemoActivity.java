/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class BasicMapDemoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static String TAG = "BasicMapDemoActivity";
    private LocationManager locationManager;
    private Location mLocation;
    // private BackGroundActivity mBackGroundActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
       // mapFragment.getMapAsync(this);

        //Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants");

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
        if ( mapIntent.resolveActivity(getPackageManager()) != null ) {
            Log.i (TAG, "resolvedActivity");
            startActivity(mapIntent);
        }





       // mBackGroundActivity.execute(this);



    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {

          mMap = map;
         mMap.addMarker(new MarkerOptions().position(new LatLng(-34, 151)).title("Marker in Sidney"));
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng( -34, 151 )), 10.2f));

        Log.i(TAG, "onMapReady ");
       // BackGroundActivity mBackGroundActivity = new BackGroundActivity();//.execute();
      //  mBackGroundActivity.execute();
        Log.i(TAG, "mBackGroundActivity executed ");

    }




    private class BackGroundActivity extends AsyncTask<Void, Integer, Location> {

        private String TAG = "BackGroundActivity";
        private Location mLocation;
        public LocationManager locationManager;




        @Override
        protected Location doInBackground(Void... voids ) {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



            /*********************************/

            Log.i (TAG, "doInBackground A1");

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                Log.i (TAG, "isProviderEnabled");
                if (ActivityCompat.checkSelfPermission(BasicMapDemoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BasicMapDemoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    Log.i (TAG, "Request Permission for COARSE Location");
                    //ActivityCompat.requestPermissions(BasicMapDemoActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                   ActivityCompat.requestPermissions(BasicMapDemoActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                   // return null;
                }

                Log.i (TAG, "RrequestLocationUpdates");
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        Log.i(TAG, "Network Provider onLocationChanged ");
                        mLocation = location;
                        double latitude = location.getLatitude();
                        double logitude = location.getLongitude();
                        // Instantiate latitude and longitude
                        LatLng latLng = new LatLng(latitude, logitude);

                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, logitude, 1);
                            String str = addressList.get(0).getLocality();
                            str += addressList.get(0).getCountryName();

                            Log.i(TAG, "onLocationChanged " + str);

                         //   mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, logitude)).title(str));
                         //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(latitude, logitude)), 10.2f));

                        } catch (IOException e) {
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
                });

            }
            else if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // WORK on UI thread here

                        Log.i (TAG, "Runnable on UI thread ");
                        if (ActivityCompat.checkSelfPermission(BasicMapDemoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BasicMapDemoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.

                            Log.i (TAG, "Request Permission for COARSE Location");
                            //ActivityCompat.requestPermissions(BasicMapDemoActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                            ActivityCompat.requestPermissions(BasicMapDemoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                            // return null;
                        }

                    }
                });

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        Log.i (TAG, "GPS Provider onLocationChanged ");
                        mLocation = location;
                        double latitude = location.getLatitude();
                        double logitude = location.getLongitude();
                        // Instantiate latitude and longitude
                        LatLng latLng = new LatLng( latitude, logitude );

                        Geocoder geocoder = new Geocoder( getApplicationContext());
                        try {
                            List<Address> addressList =  geocoder.getFromLocation( latitude, logitude, 1 );
                            String str = addressList.get(0).getLocality();
                            str += addressList.get(0).getCountryName();
                            Log.i (TAG, "onLocationChanged " + str );
                           /// mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, logitude)).title(str));
                           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng( latitude, logitude )), 10.2f));

                        } catch (IOException e) {
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
                });
            }


            /********************************/

            return mLocation;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

        }



        @Override
        protected void onPostExecute(Location  location) {
            super.onPostExecute(location);

            Log.i (TAG, "onPostExecute "  );
            double latitude = location.getLatitude();
            double logitude = location.getLongitude();
            // Instantiate latitude and longitude
            LatLng latLng = new LatLng( latitude, logitude );

            Geocoder geocoder = new Geocoder( getApplicationContext());
            try {
                List<Address> addressList =  geocoder.getFromLocation( latitude, logitude, 1 );
                String str = addressList.get(0).getLocality();
                str += addressList.get(0).getCountryName();

                Log.i (TAG, "onLocationChanged " + str );

                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, logitude)).title(str));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng( latitude, logitude )), 10.2f));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
