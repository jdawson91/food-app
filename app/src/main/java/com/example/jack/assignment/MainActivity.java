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
import android.util.Log;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnConnectionFailedListener {

    //UI components
    Button rndLoc;
    Location mCurrentLocation;
    TextView placesText;

    //Variables
    private GoogleApiClient mGoogleApiClient;
    String placesApiSvrKey = "AIzaSyDgN0nSZgvQ_3ZZpF1NRka-VRwguKk8y7A";
    String radius = "2000";
    String type = "bar";
    String placeData = "";
    int numRes = 0;
    JSONArray results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rndLoc = (Button) findViewById(R.id.rndLoc);
        placesText = (TextView) findViewById(R.id.placesText);
        placesText.setMovementMethod(new ScrollingMovementMethod());


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        rndLoc.setOnClickListener(new View.OnClickListener() {
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

                    JSONObject googlePlacesJson = new JSONObject((String) placeData);
                    results = googlePlacesJson.getJSONArray("results");

                    dispRndmPlace();

                    // placesText.setText(placeData);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally{
                        con.disconnect();
                }


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

    public void dispRndmPlace() throws JSONException {

        numRes = results.length();
        String[] name = new String[numRes];
        String[] addr = new String[numRes];
        String[] photo = new String[numRes];
        Double[] lat = new Double[numRes];
        Double[] lng = new Double[numRes];


        for(int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);

            name[i] = result.getString("name");
            addr[i] = result.getString("vicinity");
            JSONObject geometry = result.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            lat[i] = location.getDouble("lat");
            lng[i] = location.getDouble("lng");
        }


        Random random = new Random();
        int rn = random.nextInt(numRes);
        placesText.setText(placesText.getText() + "\n\n Name = " + name[rn]);
        placesText.setText(placesText.getText() + "\n Address = " + addr[rn]);
       // placesText.setText(placesText.getText() + "\n" + photo[rn]);
        placesText.setText(placesText.getText() + "\n Coords = " + lat[rn] + ", "+ lng[rn]);


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
