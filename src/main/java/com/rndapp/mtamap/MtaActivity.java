package com.rndapp.mtamap;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;
import io.fabric.sdk.android.Fabric;

public class MtaActivity extends ActionBarActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fl_fragments, new MapFragment(), "Map")
                .setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out)
                .commit();

        Analytics.activityCreated(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!getResources().getString(R.string.version).equals("paid")) {
            new Nagger(this).startNag();
        }
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
        Analytics.onStart(this);
    }
     
    @Override
    protected void onStop(){
    	super.onStop();
        Analytics.onStop(this);
    }
}