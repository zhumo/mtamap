package com.rndapp.mtamap.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rndapp.mtamap.R;
import com.rndapp.mtamap.adapters.StationAdapter;
import com.rndapp.mtamap.models.PredictionViewModel;
import com.thryv.subway.abstractions.Prediction;
import com.thryv.subway.abstractions.Station;
import com.thryv.subway.abstractions.StationManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StationDetailActivity extends AppCompatActivity {
    protected AdView adView;
    protected ProgressBar progressBar;
    ListView stationsDetailListView;
    ListView stationDetailsList;

    Station stationObj;
    StationAdapter stationAdapter;
    ArrayList<Prediction> predictionArrayList;
    PredictionViewModel predictionModel;

    ArrayList<PredictionViewModel> filterPredtionModelView = new ArrayList<PredictionViewModel>();
    ArrayList<PredictionViewModel> modelsArrayList = new ArrayList<PredictionViewModel>();
    ArrayList<Prediction> sortedPredictionArrayList = new ArrayList<Prediction>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_details);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.rgb(255, 255, 255));

        setupAds();

        progressBar = (ProgressBar) findViewById(R.id.pb_station);
        stationsDetailListView = (ListView) findViewById(R.id.stationsDetail);

        Intent intent = getIntent();
        stationObj = (Station) intent.getSerializableExtra("station");
        setTitle(stationObj.name+"");

        if (savedInstanceState != null && savedInstanceState.getSerializable("preArray") != null){
            predictionArrayList = (ArrayList<Prediction>) savedInstanceState.getSerializable("preArray");
        }

        new StationTask().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("preArray",predictionArrayList);
        super.onSaveInstanceState(outState);
    }

    protected void setupAds(){
        adView = (AdView) findViewById(R.id.ad);
        if (!getResources().getString(R.string.version).equals("paid")) {
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
    }

    //Sorting
    private ArrayList<Prediction> sortPredictionList(ArrayList<Prediction> relevantPredictions) {
        ArrayList<Prediction> innerPrediction = new ArrayList<Prediction>(relevantPredictions);
        for (int i = 0; i < innerPrediction.size(); i++) {
            for (int j = i; j < innerPrediction.size(); j++) {
                Prediction a = innerPrediction.get(i);
                Prediction b = innerPrediction.get(j);
                if (a.timeOfArrival.after(b.timeOfArrival)) {
                    Prediction temp = a;
                    innerPrediction.set(i, b);
                    innerPrediction.set(j, temp);
                }
            }
        }
        return innerPrediction;
    }

    private ArrayList<PredictionViewModel> sortModelList(ArrayList<PredictionViewModel> relevantPredictions) {
        ArrayList<PredictionViewModel> innerPredictionModel = new ArrayList<PredictionViewModel>(relevantPredictions);
        for (int i = 0; i < innerPredictionModel.size(); i++) {
            for (int j = i; j < innerPredictionModel.size(); j++) {
                PredictionViewModel a = innerPredictionModel.get(i);
                PredictionViewModel b = innerPredictionModel.get(j);
                if (a.timeOfArrival.after(b.timeOfArrival)) {
                    PredictionViewModel temp = a;
                    innerPredictionModel.set(i, b);
                    innerPredictionModel.set(j, temp);
                }
            }
        }
        return innerPredictionModel;
    }

    class StationTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            stationDetailsList = (ListView) findViewById(R.id.list);

            predictionArrayList = StationManager.getStationManager().getPredictions(stationObj, new Date());
            sortedPredictionArrayList = sortPredictionList(predictionArrayList);
            ArrayList<Prediction> upTownArray = new ArrayList<Prediction>();
            for (Prediction d : sortedPredictionArrayList) {
                if (d.direction == 0) {
                    upTownArray.add(d);
                }
            }
            ArrayList<Prediction> downTownArray = new ArrayList<Prediction>();
            for (Prediction d : sortedPredictionArrayList) {
                if (d.direction == 1) {
                    downTownArray.add(d);
                }
            }
            //Uptown
            for (Prediction uptownPrediction : upTownArray) {
                predictionModel = new PredictionViewModel(uptownPrediction.route.objectId, uptownPrediction.direction);
                predictionModel.timeOfArrival = uptownPrediction.timeOfArrival;
                boolean found = true;
                for (int i = 0; i < modelsArrayList.size(); i++) {
                    PredictionViewModel innerModel = modelsArrayList.get(i);
                    if (innerModel.direction == uptownPrediction.direction && innerModel.routeId.equals(uptownPrediction.route.objectId)) {
                        found = false;
                    }
                }
                if (found) {
                    predictionModel.setupWithPredictions(sortedPredictionArrayList);
                    modelsArrayList.add(predictionModel);
                }
            }
            //Downtown
            for (Prediction downtownPrediction : downTownArray) {
                predictionModel = new PredictionViewModel(downtownPrediction.route.objectId, downtownPrediction.direction);
                predictionModel.timeOfArrival = downtownPrediction.timeOfArrival;
                boolean found = true;
                for (int i = 0; i < modelsArrayList.size(); i++) {
                    PredictionViewModel innerModel = modelsArrayList.get(i);
                    if (innerModel.direction == downtownPrediction.direction && innerModel.routeId.equals(downtownPrediction.route.objectId)) {
                        found = false;
                    }
                }
                if (found) {
                    predictionModel.setupWithPredictions(sortedPredictionArrayList);
                    modelsArrayList.add(predictionModel);
                }
            }
            modelsArrayList = sortModelList(modelsArrayList);
            filterPredtionModelView = modelsArrayList;
            stationAdapter = new StationAdapter(getApplicationContext(), filterPredtionModelView);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            stationsDetailListView.setAdapter(stationAdapter);
        }
    }
}
