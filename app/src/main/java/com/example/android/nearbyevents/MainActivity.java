package com.example.android.nearbyevents;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private GoogleMap map = null;
    private FragmentManager fragMan;
    private TaskFragment taskFragment;
    private Spinner mapTypes;
    private ArrayAdapter<String> mapSpinnerAdapter;
    private Spinner placesRank;
    private ArrayAdapter<String> rankSpinnerAdapter;
    private Spinner placesRadius;
    private ArrayAdapter<String> radiusSpinnerAdapter;
    private MultiSelectionSpinner placesFilter;

    int spinnerInitCount = 1;

    private final String TASK_FRAGMENT_TAG = "task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(map == null)
        {
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.ourMap)).getMap();

            if(map != null)
            {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setMyLocationEnabled(true);

                UiSettings settings;
                settings = map.getUiSettings();
                settings.setMapToolbarEnabled(true);
                settings.setCompassEnabled(true);
                settings.setZoomControlsEnabled(true);
            }
        }

        fragMan = getSupportFragmentManager();
        taskFragment = (TaskFragment) fragMan.findFragmentByTag(TASK_FRAGMENT_TAG);

        if(taskFragment == null)
        {
            taskFragment = new TaskFragment();
            fragMan.beginTransaction().add(taskFragment, TASK_FRAGMENT_TAG).commit();
            spinnerInitCount = 1;
            taskFragment.spinnerInitCount = 4;

            placesFilter = (MultiSelectionSpinner) findViewById(R.id.places_filter);
            placesFilter.setFragment(taskFragment);
            placesFilter.setItems(taskFragment.filterTypes);
            placesFilter.setSelection(taskFragment.selection);
            setUpSpinners();
        }

        else
        {
            spinnerInitCount = 1;
            taskFragment.spinnerInitCount = 4;

            placesFilter = (MultiSelectionSpinner) findViewById(R.id.places_filter);
            placesFilter.setFragment(taskFragment);
            placesFilter.setItems(taskFragment.filterTypes);
            placesFilter.setSelection(taskFragment.selectedFilters);
            setUpSpinners();

            if(!taskFragment.safeToUpdate) {
                taskFragment.activityNeedsUpdate = true;
                return;
            }
            taskFragment.activityChanged();
        }
    }

    private void setUpSpinners()
    {
        mapTypes = (Spinner) findViewById(R.id.map_spinner);
        mapSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        mapSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapSpinnerAdapter.add("Normal");
        mapSpinnerAdapter.add("Satellite");
        mapTypes.setAdapter(mapSpinnerAdapter);
        mapTypes.setOnItemSelectedListener(this);

        placesRank = (Spinner) findViewById(R.id.places_rank);
        rankSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        rankSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rankSpinnerAdapter.add("Distance");
        rankSpinnerAdapter.add("Prominence");
        placesRank.setAdapter(rankSpinnerAdapter);
        placesRank.setSelection(rankSpinnerAdapter.getPosition("Prominence"));
        placesRank.setOnItemSelectedListener(taskFragment);

        placesRadius = (Spinner) findViewById(R.id.places_radius);
        radiusSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        radiusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radiusSpinnerAdapter.add("10000m");
        radiusSpinnerAdapter.add("5000m");
        radiusSpinnerAdapter.add("3000m");
        radiusSpinnerAdapter.add("1000m");
        placesRadius.setAdapter(radiusSpinnerAdapter);
        placesRadius.setSelection(radiusSpinnerAdapter.getPosition("3000m"));
        placesRadius.setOnItemSelectedListener(taskFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(spinnerInitCount != 0) {
            spinnerInitCount--;
            return;
        }

        if(parent.getItemAtPosition(pos).equals("Satellite"))
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void disableRadiusSpinner()
    {
        placesRadius.setEnabled(false);
        placesRadius.getSelectedView().setEnabled(false);
    }

    public void enableRadiusSpinner()
    {
        placesRadius.setEnabled(true);
        placesRadius.getSelectedView().setEnabled(true);
    }

}
