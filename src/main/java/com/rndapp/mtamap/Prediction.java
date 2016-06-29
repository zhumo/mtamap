package com.rndapp.mtamap;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ladmin on 08/06/16.
 */

public class Prediction implements Serializable {

    public Date timeOfArrival;
    public int direction;
    public Route route;

    Prediction() {
        super();
    }

    Prediction(Date time) {
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