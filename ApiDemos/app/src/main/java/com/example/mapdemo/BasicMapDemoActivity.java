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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.PlaceDetectionClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import static com.example.mapdemo.R.drawable.ic_launcher;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class BasicMapDemoActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {



    private GoogleMap mMap;
    private static String TAG = "BasicMapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    Button searchBtn = null;
    Intent locatorService = null;
    AlertDialog alertDialog = null;
    Dialog progDialog;
    private GoogleApiClient mGoogleApiClient;
    public GeoDataClient mGeoDataClient;
    public PlaceDetectionClient mPlaceDetectionClient;
    public  int PLACE_PICKER_REQUEST = 1;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.i(TAG, "start");
       // mapFragment.getMapAsync(this);
       // searchBtn = (Button) findViewById(R.id.currentLoc);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        Log.i(TAG, "getPlaceDetectionClient");
      /*  mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
               .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, (OnConnectionFailedListener) this)
                .build();*/

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        @SuppressLint("MissingPermission") Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);

        Log.i(TAG, "PlacePicker");


        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }



        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override


            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {

                Log.i(TAG, "onComplete");
                Toast.makeText(BasicMapDemoActivity.this,
                        " addOnCompleteListener",
                        Toast.LENGTH_LONG).show();
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });

    }



    public boolean stopService() {
        if (this.locatorService != null) {
            this.locatorService = null;
        }
        return true;
    }

    public boolean startService() {
        try {
            // this.locatorService= new
            // Intent(FastMainActivity.this,LocatorService.class);
            // startService(this.locatorService);

            Log.i("BasicApp", "Starting Service:");
            FetchCordinates fetchCordinates = new FetchCordinates();
            fetchCordinates.execute();
            return true;
        } catch (Exception error) {
            return false;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "onActivityResult");
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult A1");
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                Log.i(TAG, "onActivityResult A2");
            }
        }
    }

    public AlertDialog CreateAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();

        alert.setTitle(title);

        alert.setMessage(message);

        return alert;

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i (TAG, "onMapReady");
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }

    }

    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        ProgressDialog progDailog = null;

        public double lati = 0.0;
        public double longi = 0.0;

        public LocationManager mLocationManager;
        public VeggsterLocationListener mVeggsterLocationListener;

        @Override
        protected void onPreExecute() {
            mVeggsterLocationListener = new VeggsterLocationListener();
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.addTestProvider("Bangalore", false, true, true, true, true, true, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);

            Log.i("BasicApp", "onPreExecute");
            if (ActivityCompat.checkSelfPermission(BasicMapDemoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BasicMapDemoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                PermissionUtils.requestPermission(BasicMapDemoActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);

                return;
            }

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 0, 0,
                        mVeggsterLocationListener);

                Toast.makeText(BasicMapDemoActivity.this,
                        "GPS PROVIDER",
                        Toast.LENGTH_LONG).show();

                progDailog = new ProgressDialog(BasicMapDemoActivity.this, ProgressDialog.STYLE_HORIZONTAL);
                progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        FetchCordinates.this.cancel(true);
                    }
                });
                progDailog.setMessage("Loading...");
                progDailog.setProgress(0);
                progDailog.setIndeterminate( true );
                progDailog.setProgress(25);
                progDailog.setCancelable(true);
                progDailog.show();

            }
            else if ( mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 0, 0,
                        mVeggsterLocationListener);

                Toast.makeText(BasicMapDemoActivity.this,
                        "NETWORK PROVIDER",
                        Toast.LENGTH_LONG).show();

                progDailog = new ProgressDialog(BasicMapDemoActivity.this);
                progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        FetchCordinates.this.cancel(true);
                    }
                });
                progDailog.setProgress(0);
                progDailog.setIndeterminate( true );
                progDailog.setProgress(25);
                progDailog.setCancelable(true);
                progDailog.show();
            }
        }


        @Override
        protected void onCancelled(){
            System.out.println("Cancelled by user!");
            progDialog.dismiss();
            mLocationManager.removeUpdates(mVeggsterLocationListener);
        }

        @Override
        protected void onPostExecute(String result) {
            progDailog.dismiss();

            Toast.makeText(BasicMapDemoActivity.this,
                    "LATITUDE :" + lati + " LONGITUDE :" + longi,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            while (this.lati == 0.0) {

            }
            return null;
        }

        public class VeggsterLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {

                Log.i("BasicApp", "onLocationChanged");
                double lat = (int) location.getLatitude(); // * 1E6);
                double  log = (int) location.getLongitude(); // * 1E6);

                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, log);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                int acc = (int) (location.getAccuracy());

                Log.i("BasicApp", "onLocationChanged latitude " + lat);
                Log.i("BasicApp", "onLocationChanged longitude" + log);
                progDailog.setProgress(progDailog.getMax());
                Toast.makeText(BasicMapDemoActivity.this,
                        " Loading completed",
                        Toast.LENGTH_LONG).show();
                String info = location.getProvider();
                try {

                    // LocatorService.myLatitude=location.getLatitude();

                    // LocatorService.myLongitude=location.getLongitude();

                    lati = location.getLatitude();
                    longi = location.getLongitude();

                } catch (Exception e) {
                    // progDailog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Unable to get Location"
                    // , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("OnProviderDisabled", "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i("onStatusChanged", "onStatusChanged");

            }

        }




    }






    /***********************************************************************************************************************/






}



