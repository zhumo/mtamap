package com.subway.ladmin.subwaynyk;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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

    public long printDifference() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        Date endDate;
        long different = 0;
        try {
            Log.v("TimeOfArrival", String.valueOf(timeOfArrival.getTime()));
            endDate = simpleDateFormat.parse("01/01/1970 00:00:00");
            different = endDate.getTime() - timeOfArrival.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //milliseconds
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedSeconds;
    }

    public int secondsToArrival() {
        return 0;
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