package com.example.jack.assignment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements OnConnectionFailedListener {

    //UI components
    TextView mLatitudeText;
    TextView mLongitudeText;
    Button getLoc;
    Button getPlaces;
    Location mCurrentLocation;
    TextView placesText;

    //Variables
    private GoogleApiClient mGoogleApiClient;
    String placesApiSvrKey = "AIzaSyDgN0nSZgvQ_3ZZpF1NRka-VRwguKk8y7A";
    String radius = "2000";
    String type = "restaurant";
    String placeData = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLatitudeText = (TextView) findViewById(R.id.latText);
        mLongitudeText = (TextView) findViewById(R.id.lonText);
        getLoc = (Button) findViewById(R.id.getLoc);
        getPlaces = (Button) findViewById(R.id.getPlaces);
        placesText = (TextView) findViewById(R.id.placesText);
        placesText.setMovementMethod(new ScrollingMovementMethod());


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if we have location and internet permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, " need location permissions", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        return;
                    }
                    if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, " need internet permissions", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                        return;
                    }
                }

                //get the current location
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                //get the latitude and longitude values from the current location
                String latitude = String.valueOf(mCurrentLocation.getLatitude());
                String longitude = String.valueOf(mCurrentLocation.getLongitude());

                mLatitudeText.setText(latitude);
                mLongitudeText.setText(longitude);

                //build url based on location, radius and place type
                //Build string of url
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + latitude + "," + longitude);
                googlePlacesUrl.append("&radius=" + radius);
                googlePlacesUrl.append("&types=" + type);
                googlePlacesUrl.append("&key=" + placesApiSvrKey);

                //create URL
                URL url = null;
                try {
                    url = new URL(googlePlacesUrl.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    //connect to webpage and read it back
                    con.connect();
                    InputStream in = new BufferedInputStream(con.getInputStream());

                    //convert data read from webpage to string
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer stringBuffer = new StringBuffer();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    placeData = "";
                    placeData = stringBuffer.toString();
                    bufferedReader.close();
                    placesText.setText(placeData);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally{
                        con.disconnect();
                }


            }
        });

        getPlaces.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                placesText.setText("");
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, " need permissions", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(mGoogleApiClient, null);
                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                             placesText.setText(placesText.getText() + String.format("\n Place: '%s' \n Has likelihood: %g",
                                     placeLikelihood.getPlace().getName(),
                                     placeLikelihood.getLikelihood()));
                        }
                        likelyPlaces.release();
                    }
                });



            }
        });

        // Create the location client to start receiving updates
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
