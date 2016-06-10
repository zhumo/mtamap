package com.subway.ladmin.subwaynyk;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by ladmin on 08/06/16.
 */
enum  Direction{
    Uptown,
    Downtown;
}
public class Prediction {

    public Date timeOfArrival;
    public int direction;
    public  Routes route;


//    public enum  Direction{
//        Uptown,
//        Downtown;
//    }
   // Prediction obj = new Prediction();


    Prediction()
    {
        super();
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public long printDifference(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = simpleDateFormat.parse(String.valueOf(timeOfArrival));
            endDate = simpleDateFormat.parse("01/01/1970 00:00:00");

        }catch (Exception e){
            e.printStackTrace();
        }
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

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

            int seconds = (int) this.printDifference();
            return seconds;
    }


    Prediction(Date time) {
        super();
        timeOfArrival = time;
    }

    public boolean isEqual(Object object) {

        Prediction prediction = new Prediction();

        if (prediction instanceof Prediction) {
            return this.route.objectId == prediction.route.objectId && this.timeOfArrival == prediction.timeOfArrival && this.direction == prediction.direction;
        }else {
            return false;
        }
    }

}
