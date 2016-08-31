package com.thryv.subway.abstractions;

import java.io.Serializable;
import java.util.Date;

public class Prediction implements Serializable {

    public Date timeOfArrival;
    public int direction;
    public Route route;

    public Prediction() {
        super();
    }

    public Prediction(Date time) {
        super();
        timeOfArrival = time;
    }

    public boolean isEqual(Object object) {
        Prediction prediction = new Prediction();

        if (prediction instanceof Prediction) {
            return this.route.objectId .equals(prediction.route.objectId) && this.timeOfArrival.getTime()== prediction.timeOfArrival.getTime() && this.direction == prediction.direction;
        } else {
            return false;
        }
    }
}