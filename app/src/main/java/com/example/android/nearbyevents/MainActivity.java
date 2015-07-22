package com.example.android.nearbyevents;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

public class MainActivity extends AppCompatActivity {

    private GoogleMap map = null;
    private FragmentManager fragMan;
    private TaskFragment taskFragment;

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
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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
            Log.v("MainActivity", "No fragment yet");
        }

        else
        {
            if(!taskFragment.safeToUpdate) {
                taskFragment.needsUpdate = true;
                return;
            }
            taskFragment.activityChanged();
            Log.v("MainActivity", "Fragment Exists");
        }
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
}
