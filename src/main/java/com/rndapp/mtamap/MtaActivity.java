package com.rndapp.mtamap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.xone.XoneManager;
import com.xone.XoneTipStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.senab.photoview.PhotoView;

import io.fabric.sdk.android.Fabric;

public class MtaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    protected AdView adView;

    DBHelper dbHelper;
    SearchView searchView;
    ListView stationsListView;
    ArrayList stationIdsArrayList;
    ArrayList stationIdsFilteredList;
    MtaActivityAdapter mainAdapter;
    PhotoView mapImageView;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //setContentView(R.layout.main);
        setContentView(R.layout.activity_main);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.rgb(255, 255, 255));

        mapImageView = (PhotoView) findViewById(R.id.subwaymap);
        mapImageView.setMaximumScale(6.0f);
        searchView = (SearchView) findViewById(R.id.search_view);
        stationsListView = (ListView) findViewById(R.id.list);
        stationsListView.setVisibility(stationsListView.INVISIBLE);
        dbHelper = new DBHelper(this);

            try {
                dbHelper.createDatabase();
                dbHelper.openDatabase();
                new StopsTask().execute();

            } catch (IOException ioe) {

                throw new Error("Unable to create database");
            } catch (SQLException sqle) {
                throw sqle;
            }

        adView = (AdView) findViewById(R.id.ad);
        if (!getResources().getString(R.string.version).equals("paid")) {
            XoneManager.init(this, "3a550ccccdd44d968f68a0f6c27427b3");
            XoneManager.enableAutoTips(this, XoneTipStyle.newBuilder().setShowFromBottom(true).build());

            boolean hasChecked = getSharedPreferences("xone", Activity.MODE_PRIVATE).getBoolean("checked_permissions", false);
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_DENIED && !hasChecked) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            }

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("124C0C8E23FB2264186BB5819F6A0D57")
                    .build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("onTop", "false");
                    FlurryAgent.logEvent("AdClicked", map);
                }
            });
        } else {
            adView.setVisibility(View.GONE);
        }

//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        fragmentManager.beginTransaction()
//                .add(R.id.fl_fragments, new MapFragment(), "Map")
//                .setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out)
//                .commit();

        Analytics.activityCreated(this);
    }

    class StopsTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            stationIdsArrayList = dbHelper.getAllStops();
            stationIdsFilteredList = new ArrayList(stationIdsArrayList);
            mainAdapter = new MtaActivityAdapter(MtaActivity.this, stationIdsFilteredList, dbHelper);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stationsListView.setAdapter(mainAdapter);
            searchView.setOnQueryTextListener(MtaActivity.this);
            stationsListView.setOnItemClickListener(mainAdapter);
        }
    }

    @Override
    public boolean onQueryTextChange(String text) {
        if (text.length() > 0) {
            mainAdapter.getFilter().filter(text);
            stationsListView.setVisibility(stationsListView.VISIBLE);
            mapImageView.setVisibility(mapImageView.INVISIBLE);
        } else {
            stationsListView.setVisibility(stationsListView.INVISIBLE);
            mapImageView.setVisibility(mapImageView.VISIBLE);
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(this.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!getResources().getString(R.string.version).equals("paid")) {
            new Nagger(this).startNag();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Analytics.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Analytics.onStop(this);
    }

    @Override
    public void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://teschrock.wordpress.com/transit-app-privacy-policy/")));
        return true;
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
                getSharedPreferences("xone", Activity.MODE_PRIVATE).edit().putBoolean("checked_permissions", true).apply();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}