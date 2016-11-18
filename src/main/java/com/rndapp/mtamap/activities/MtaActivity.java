package com.rndapp.mtamap.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rndapp.mtamap.R;
import com.rndapp.mtamap.adapters.StationListAdapter;
import com.rndapp.mtamap.models.Analytics;
import com.rndapp.mtamap.models.Nagger;
import com.thryv.subway.abstractions.StationManager;
import com.thryv.subway.nyc.NYCStationManger;
import com.thryv.subway.paris.ParisStationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import uk.co.senab.photoview.PhotoView;

public class MtaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    protected AdView adView;
    protected ProgressBar progressBar;

    protected StationManager stationManager;
    protected SearchView searchView;
    protected ListView stationsListView;
    protected ArrayList stationIdsArrayList;
    protected ArrayList stationIdsFilteredList;
    protected StationListAdapter mainAdapter;
    protected PhotoView mapImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.rgb(255, 255, 255));

        progressBar = (ProgressBar) findViewById(R.id.pb_search);
        mapImageView = (PhotoView) findViewById(R.id.subwaymap);
        searchView = (SearchView) findViewById(R.id.search_view);

        Resources resources = getResources();
        boolean hasSchedules = resources.getString(R.string.schedules).equals("true");
        boolean isFree = resources.getString(R.string.version).equals("free");

        if (hasSchedules){
            stationsListView = (ListView) findViewById(R.id.list);
            new StopsTask().execute();
        }else {
            searchView.setVisibility(View.GONE);
        }

        switch (resources.getString(R.string.city)){
            case "london":
            case "nyc":
                mapImageView.setMaximumScale(7.0f);
                break;
            case "berlin":
                mapImageView.setMaximumScale(5.0f);
                break;
            case "paris":
                mapImageView.setMaximumScale(5.0f);
                break;
            default:
                mapImageView.setMaximumScale(3.0f);
                break;
        }

        adView = (AdView) findViewById(R.id.ad);
        if (isFree) {
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

        Analytics.activityCreated(this);
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
            String urlPart = "market://details?id=";
            String pkgName = getApplicationContext().getPackageName();
            if (getResources().getString(R.string.city).equals("nyc") && getResources().getString(R.string.schedules).equals("true")){
                new Nagger(this).startNag(urlPart + pkgName, urlPart + pkgName + ".paid");
            }else {
                new Nagger(this).startNag(urlPart + pkgName, null);
            }
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

    class StopsTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getResources().getString(R.string.city).equals("nyc")){
                StationManager.setStationManager(new NYCStationManger(MtaActivity.this));
            }else if (getResources().getString(R.string.city).equals("paris")){
                StationManager.setStationManager(new ParisStationManager(MtaActivity.this));
            }

            stationManager = StationManager.getStationManager();
            stationIdsArrayList = stationManager.getAllStations();
            stationIdsFilteredList = new ArrayList(stationIdsArrayList);
            mainAdapter = new StationListAdapter(MtaActivity.this, stationIdsFilteredList, stationManager);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);

            stationsListView.setAdapter(mainAdapter);
            searchView.setOnQueryTextListener(MtaActivity.this);
            stationsListView.setOnItemClickListener(mainAdapter);
        }
    }
}