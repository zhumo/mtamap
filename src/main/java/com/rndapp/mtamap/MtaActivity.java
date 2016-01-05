package com.rndapp.mtamap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;

import com.crashlytics.android.Crashlytics;
import com.xone.XoneManager;
import com.xone.XoneTipStyle;

import io.fabric.sdk.android.Fabric;

public class MtaActivity extends ActionBarActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.main);

        XoneManager.init(this, "3a550ccccdd44d968f68a0f6c27427b3");
        XoneManager.enableAutoTips(this, XoneTipStyle.newBuilder().setShowFromBottom(true).build());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    XoneManager.locationPermissionGranted();
                } else {
                    XoneManager.locationPermissionDenied();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}