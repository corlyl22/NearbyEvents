package com.example.android.nearbyevents;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    private GoogleMap map = null;

    private double myLatitude, myLongitude;
    public boolean noLocation = false;

    private final static float zoomLevel = 18;  //up to 21

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        setContentView(R.layout.activity_main);

        if(map == null)
        {
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.ourMap)).getMap();

            if(map != null)
            {
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                map.setMyLocationEnabled(true);

                UiSettings settings;
                settings = map.getUiSettings();
                settings.setMapToolbarEnabled(true);
                settings.setCompassEnabled(true);
                settings.setZoomControlsEnabled(true);
            }
        }
    }

    private void updateLocation()
    {
        LatLng lastLatLng = new LatLng(myLatitude, myLongitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, zoomLevel), 2000, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if(mLastLocation == null)
        {
            Toast t = Toast.makeText(this, "Can't access location!", Toast.LENGTH_SHORT);
            t.show();
            noLocation = true;
            return;
        }

        myLatitude = mLastLocation.getLatitude();
        myLongitude = mLastLocation.getLongitude();

        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    public GoogleApiClient getmGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    public Location getmLastLocation()
    {
        return mLastLocation;
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
