package com.subway.thryv.subwaynyc;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ladmin on 10/06/16.
 */
public class PredictionViewModel {

    public Date timeOfArrival;
    String routeId;
    int direction;
    ArrayList<Prediction> sortedPredictionArray = new ArrayList<Prediction>();
    Prediction prediction = new Prediction();
    Prediction onDeckPrediction = new Prediction();
    Prediction inTheHolePrediction = new Prediction();

    PredictionViewModel(String routeId, int direction) {
        this.routeId = routeId;
        this.direction = direction;
    }

    PredictionViewModel(String routeId, int direction, Date timeOfArrival) {
        this.routeId = routeId;
        this.direction = direction;
        this.timeOfArrival = timeOfArrival;
    }

    public void setupWithPredictions(ArrayList<Prediction> predictionsArray) {
        ArrayList<Prediction> relevantPredictions = new ArrayList<Prediction>();
        for (Prediction predict : predictionsArray) {
            if ((predict.direction == this.direction) && predict.route.objectId.equals(this.routeId)) {
                relevantPredictions.add(predict);
            }
        }
        sortedPredictionArray = sortList(relevantPredictions);
        if (sortedPredictionArray.size() > 0) {
            prediction = sortedPredictionArray.get(0);
        }

        if (sortedPredictionArray.size() > 1) {
            onDeckPrediction = sortedPredictionArray.get(1);
        }

        if (sortedPredictionArray.size() > 2) {
            inTheHolePrediction = sortedPredictionArray.get(2);
        }
    }

    private ArrayList<Prediction> sortList(ArrayList<Prediction> relevantPredictions) {
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
}
