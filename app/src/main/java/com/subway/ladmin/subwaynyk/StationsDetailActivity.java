package com.subway.ladmin.subwaynyk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class StationsDetailActivity extends AppCompatActivity {
    Station stationObj;
    ListView listView;
    StationsDetailActivityAdapter stationAdapter;
    ArrayList<Prediction> predictionArray;
    ListView stationDetailsList;
    PredictionViewModel predictionModel;

    ArrayList<PredictionViewModel> filterPredtionModelView = new ArrayList<PredictionViewModel>();
    ArrayList<PredictionViewModel> modelsArray = new ArrayList<PredictionViewModel>();
    ArrayList<Prediction> sortedPredictionArray = new ArrayList<Prediction>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_details);
        stationDetailsList = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        stationObj = (Station) intent.getSerializableExtra("station");
        predictionArray = (ArrayList<Prediction>) intent.getSerializableExtra("preArray");

        sortedPredictionArray = sortPredictionList(predictionArray);
        ArrayList<Prediction> upTownArray = new ArrayList<Prediction>();
        for (Prediction d : sortedPredictionArray) {
            if (d.direction == 0) {
                upTownArray.add(d);
            }
        }
        ArrayList<Prediction> downTownArray = new ArrayList<Prediction>();
        for (Prediction d : sortedPredictionArray) {
            if (d.direction == 1) {
                downTownArray.add(d);
            }
        }
        //Uptown
        for (Prediction uptownPrediction : upTownArray) {
            predictionModel = new PredictionViewModel(uptownPrediction.route.objectId, uptownPrediction.direction);
            predictionModel.timeOfArrival = uptownPrediction.timeOfArrival;
            boolean found = true;
            for (int i = 0; i < modelsArray.size(); i++) {
                PredictionViewModel innerModel = modelsArray.get(i);
                if (innerModel.direction == uptownPrediction.direction && innerModel.routeId.equals(uptownPrediction.route.objectId)) {
                    found = false;
                }
            }
            if (found) {
                predictionModel.setupWithPredictions(sortedPredictionArray);
                modelsArray.add(predictionModel);
            }
        }
        //Downtown
        for (Prediction downtownPrediction : downTownArray) {
            predictionModel = new PredictionViewModel(downtownPrediction.route.objectId, downtownPrediction.direction);
            predictionModel.timeOfArrival = downtownPrediction.timeOfArrival;
            boolean found = true;
            for (int i = 0; i < modelsArray.size(); i++) {
                PredictionViewModel innerModel = modelsArray.get(i);
                if (innerModel.direction == downtownPrediction.direction && innerModel.routeId.equals(downtownPrediction.route.objectId)) {
                    found = false;
                }
            }
            if (found) {
                predictionModel.setupWithPredictions(sortedPredictionArray);
                modelsArray.add(predictionModel);
            }
        }
        modelsArray = sortModelList(modelsArray);
        filterPredtionModelView = modelsArray;
        setLineModels();
        listView = (ListView) findViewById(R.id.stationsDetail);
        stationAdapter = new StationsDetailActivityAdapter(getApplicationContext(), filterPredtionModelView);
        listView.setAdapter(stationAdapter);
    }

    public void setLineModels() {

//        for(PredictionViewModel predictionModel : predictionModels){
//        let predictionRouteIds = predictionModels!.map {$0.routeId}

//            for (PredictionViewModel routeId : predictionModels)
//            {
//
//                NYCRouteColorManager colorManager;
//                LineViewModel lineModel = new LineViewModel();
//                lineModel.routeIds = [routeId];
//                lineModel.color = colorManager.colorForRouteId(routeId.toString());
//                int lineIndex = lineModels.indexOf(lineModel);
//                int index = lineIndex;
//
//                if(index){
//                    if(!lineModels.toString().contains((CharSequence) routeId)){
//                        lineModels.add(routeId);
//                    }
//                }
//                else {
//                    lineModels.add(lineModel);
//                }
//
//                let lineModel = LineViewModel()
//                lineModel.routeIds = [routeId]
//                lineModel.color = NYCRouteColorManager.colorForRouteId(routeId)
//                let lineIndex = lineModels.indexOf(lineModel)
//                if let index = lineIndex {
//                    if !lineModels[index].routeIds.contains(routeId) {
//                        lineModels[index].routeIds.append(routeId)
//                    }
//                }else{
//                    lineModels.append(lineModel)
//                }
//            }
    }
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
