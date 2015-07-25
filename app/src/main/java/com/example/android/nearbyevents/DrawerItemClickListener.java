package com.example.android.nearbyevents;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private MainActivity activity;

    public DrawerItemClickListener(MainActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
    }

    public void setTitle(CharSequence title) {
        activity.getSupportActionBar().setTitle(title);
    }
}

