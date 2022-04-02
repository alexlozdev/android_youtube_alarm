package com.gu.galarm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.gu.galarm.R;

/**
 * Activity to allow user to choose a location with map aid
 */
public class MapsActivity extends FragmentActivity  {

    private final String TAG = "MapsActivity";

    public static final String EXTRA_PLACE = "com.gu.galarm.MapsActivity.PLACE";
    public static final String BUNDLE_POINT = "com.gu.galarm.MapsActivity.BUNDLE.POINT";
    public static final String EXTRA_LATLNG = "com.gu.galarm.MapsActivity.LATLNG";


    private String mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra(BUNDLE_POINT);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }


}
