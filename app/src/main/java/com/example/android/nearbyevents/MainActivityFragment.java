package com.example.android.nearbyevents;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation = null;
    private MainActivity activity = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        activity = ((MainActivity)getActivity());
        mGoogleApiClient = activity.getmGoogleApiClient();

        return rootView;
    }
}
