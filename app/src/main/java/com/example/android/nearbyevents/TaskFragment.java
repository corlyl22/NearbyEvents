package com.example.android.nearbyevents;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


/**
 * A placeholder fragment containing a simple view.
 */
public class TaskFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    private GoogleMap map = null;
    private LocationRequest mLocationRequest = null;
    private Marker[] previousMarkers = null;
    private ArrayList<Marker> previousMarkers2 = null;
    private MarkerOptions[] previousPlaces = null;
    private ArrayList<MarkerOptions> previousPlaces2 = null;
    private double myLatitude, myLongitude;
    private boolean moreResults = false;
    boolean needsUpdate = false;
    boolean safeToUpdate = true;

    private final int MAX_PLACES = 60;
    private final float zoomLevel = 16;  //up to 21
    private final float smallestDisplacement = 200;
    private final String baseURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final String placesOrder = "&rankby=distance";  //if rankby == distance, do not include radius parameter
    private final String placesRadius = "&radius=9000"; //in meters
    private final String placesTypes = "&types=food|bar|movie_theater|museum|art_gallery";
    private final String placesAPIKey = "&key=AIzaSyDTf14XqzKl-raiuAnDx34-8rgwY2c_-sw";

    private String pagetoken = "";

    private AsyncTask<String, Void, String> mPlacesTask = null;
    private HashMap<String, Integer> mIcons = null;

    public TaskFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);

        previousMarkers2 = new ArrayList<Marker>();
        previousPlaces2 = new ArrayList<MarkerOptions>();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        buildIconsMap();
        map = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.ourMap)).getMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    private void buildIconsMap()
    {
        mIcons = new HashMap<String, Integer>();

        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/airport-71.png", R.drawable.airport_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/aquarium-71.png", R.drawable.aquarium_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/art_gallery-71.png", R.drawable.art_gallery_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/bar-71.png", R.drawable.bar_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/bowling-71.png", R.drawable.bowling_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/cafe-71.png", R.drawable.cafe_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/casino-71.png", R.drawable.casino_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/dentist-71.png", R.drawable.dentist_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/doctor-71.png", R.drawable.doctor_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/fitness-71.png", R.drawable.fitness_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png", R.drawable.generic_business_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png", R.drawable.generic_recreational_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/library-71.png", R.drawable.library_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png", R.drawable.lodging_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/movies-71.png", R.drawable.movies_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/museum-71.png", R.drawable.museum_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/police-71.png", R.drawable.police_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png", R.drawable.restaurant_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png", R.drawable.shopping_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/stadium-71.png", R.drawable.stadium_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/wine-71.png", R.drawable.wine_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/worship_general-71.png", R.drawable.worship_general_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/worship_hindu-71.png", R.drawable.worship_hindu_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/worship_islam-71.png", R.drawable.worship_islam_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/worship_jewish-71.png", R.drawable.worship_jewish_71);
        mIcons.put("http://maps.gstatic.com/mapfiles/place_api/icons/zoo-71.png", R.drawable.zoo_71);
    }

    private void updateLocation(Marker[] placeMarkers, MarkerOptions[] places)
    {
        if(previousMarkers != null){
            for(int i = 0; i < previousMarkers.length; i++){
                if(previousMarkers[i] != null)
                    previousMarkers[i].remove();
            }
        }

        LatLng myLatLng = new LatLng(myLatitude, myLongitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, zoomLevel), 2000, null);

        if(moreResults)
        {
            if(places != null && placeMarkers != null){
                for(int place = 0; place < places.length && place < placeMarkers.length; place++){
                    if(places[place] != null) {
                        placeMarkers[place] = map.addMarker(places[place]);
                        previousMarkers2.add(placeMarkers[place]);
                        previousPlaces2.add(places[place]);
                    }
                }
            }

            previousMarkers = new Marker[previousMarkers2.size()];
            for(int i=0; i<previousMarkers.length; i++)
            {
                previousMarkers[i] = previousMarkers2.get(i);
            }

            previousPlaces = new MarkerOptions[previousPlaces2.size()];
            for(int i=0; i<previousPlaces.length; i++)
            {
                previousPlaces[i] = previousPlaces2.get(i);
            }

            moreResults = !moreResults;
            previousMarkers2.clear();
            previousPlaces2.clear();
        }

        else
        {
            if(places != null && placeMarkers != null){
                for(int place = 0; place < places.length && place < placeMarkers.length; place++){
                    if(places[place] != null)
                        placeMarkers[place]= map.addMarker(places[place]);
                }
            }

            previousMarkers = placeMarkers;
            previousPlaces = places;
        }

        safeToUpdate = true;

        if(needsUpdate) {
            needsUpdate = false;
            activityChanged();
        }
    }

    private void getMoreResults(Marker[] placeMarkers, MarkerOptions[] places)
    {
        safeToUpdate = false;
        if(previousMarkers != null){
            for(int i = 0; i < previousMarkers.length; i++){
                if(previousMarkers[i] != null)
                    previousMarkers[i].remove();
            }
        }

        LatLng myLatLng = new LatLng(myLatitude, myLongitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, zoomLevel), 2000, null);

        if(places != null && placeMarkers != null){
            for(int place = 0; place < places.length && place < placeMarkers.length; place++){
                if(places[place] != null) {
                    placeMarkers[place] = map.addMarker(places[place]);
                    previousMarkers2.add(placeMarkers[place]);
                    previousPlaces2.add(places[place]);
                }
            }
        }

        String placesUrl = buildQueryUrl();
        startPlacesTask(placesUrl);
    }

    public void activityChanged()
    {
        map = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.ourMap)).getMap();
        updateLocation(previousMarkers, previousPlaces);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(smallestDisplacement);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if(mLastLocation == null)
        {
            Toast t = Toast.makeText(getActivity(), "Can't access location!", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        myLatitude = mLastLocation.getLatitude();
        myLongitude = mLastLocation.getLongitude();

        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        myLatitude = mLastLocation.getLatitude();
        myLongitude = mLastLocation.getLongitude();

        String placesUrl = buildQueryUrl();
        startPlacesTask(placesUrl);
    }

    private synchronized String buildQueryUrl()
    {
        String location = "&location=" + String.valueOf(myLatitude) + "," + String.valueOf(myLongitude);
        if(!pagetoken.contains("pagetoken="))
            pagetoken = "pagetoken=" + pagetoken;

        String placesUrl;

        if(placesOrder.equals("&rankby=distance")) {
            placesUrl = baseURL
                    + pagetoken
                    + location
                    + placesOrder
                    + placesTypes
                    + placesAPIKey;
        }

        else {
            placesUrl = baseURL
                    + pagetoken
                    + location
                    + placesRadius
                    + placesTypes
                    + placesAPIKey;
        }

        return placesUrl;
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

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity(), this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    private synchronized void startPlacesTask(String placesUrl) {
        if(mPlacesTask != null) {
            mPlacesTask.cancel(true);
        }

        mPlacesTask = new PlacesTask().execute(placesUrl);
    }

    public class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... placesURL) {

            if(moreResults)
                SystemClock.sleep(2000);

            StringBuilder placesBuilder = new StringBuilder();

            for (String placeUrl : placesURL) {
                Log.v("TaskFragment", placeUrl);
                try
                {
                    URL url = new URL(placeUrl);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    if(urlConnection.getResponseCode() == 200)
                    {
                        InputStream placesContent = urlConnection.getInputStream();
                        InputStreamReader placesInput = new InputStreamReader(placesContent);
                        BufferedReader placesReader = new BufferedReader(placesInput);
                        String line;

                        while ((line = placesReader.readLine()) != null) {
                            if(isCancelled())
                            {
                                return null;
                            }
                            placesBuilder.append(line);
                        }
                    }

                    //further implement error handling
                    else
                    {
                        Log.v("TaskFragment", "could not connect");
                    }
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            return placesBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            MarkerOptions[] places = null;
            Marker[] placeMarkers = new Marker[MAX_PLACES];
            try {
                //parse JSON

                JSONObject resultObject = new JSONObject(result);

                try {
                    pagetoken = resultObject.getString("next_page_token");
                    moreResults = true;
                }
                catch (Exception e) {
                    pagetoken = "";
                }

                JSONArray placesArray = resultObject.getJSONArray("results");
                places = new MarkerOptions[placesArray.length()];

                for (int place = 0; place < placesArray.length(); place++) {
                    boolean incomplete = false; //is place info incomplete?
                    LatLng placeCoordinates = null;
                    String placeName = "";
                    String vicinity = "";
                    int icon = R.drawable.generic_business_71;

                    try{
                        incomplete = false;
                        JSONObject placeObject = placesArray.getJSONObject(place);
                        JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");

                        placeCoordinates = new LatLng(Double.valueOf(loc.getString("lat")), Double.valueOf(loc.getString("lng")));
                        vicinity = placeObject.getString("vicinity");
                        placeName = placeObject.getString("name");

                        if(mIcons.get(placeObject.getString("icon")) != null)
                            icon = mIcons.get(placeObject.getString("icon"));
                    }

                    catch(JSONException jse){
                        Log.v("PLACES", "missing value");
                        incomplete = true;
                        jse.printStackTrace();
                    }

                    if(incomplete)
                        places[place] = null;

                    else {
                        places[place] = new MarkerOptions()
                                .position(placeCoordinates)
                                .title(placeName)
                                .snippet(vicinity)
                                .icon(BitmapDescriptorFactory.fromResource(icon));
                    }
                }

                if(moreResults && !pagetoken.equals(""))
                    getMoreResults(placeMarkers, places);
                else
                    updateLocation(placeMarkers, places);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(getActivity().isFinishing())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            mPlacesTask.cancel(true);
        }
    }
}