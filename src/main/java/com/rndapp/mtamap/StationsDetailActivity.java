package com.rndapp.mtamap;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class StationsDetailActivity extends AppCompatActivity {
    Station stationObj;
    ListView stationsDetailListView;
    StationsDetailActivityAdapter stationAdapter;
    ArrayList<Prediction> predictionArrayList;
    ListView stationDetailsList;
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

        stationDetailsList = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        stationObj = (Station) intent.getSerializableExtra("station");
        setTitle(stationObj.name+"");
        predictionArrayList = (ArrayList<Prediction>) intent.getSerializableExtra("preArray");

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
        stationsDetailListView = (ListView) findViewById(R.id.stationsDetail);
        stationAdapter = new StationsDetailActivityAdapter(getApplicationContext(), filterPredtionModelView);
        stationsDetailListView.setAdapter(stationAdapter);
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
}
