package com.subway.ladmin.subwaynyk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Created by ladmin on 07/06/16.
 */
public class Utilities implements Serializable {

    //    Filter Results
    public ArrayList<String> stationsForSearchString(String stationName, ArrayList<Stop> allStations) {

        ArrayList<String> filteredResult = new ArrayList<String>();
        for (Stop stops : allStations) {
            String string = stops.name;
            if (string.contains(stationName)) {
                filteredResult.add(string);
            }
        }
        return filteredResult;
    }

    public String dateToTime(Date time) {

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        String formattedDate = formatter.format(time);
        return formattedDate;
    }

    public Date timeToDate(String time, Date referenceDate) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        String timeString = dateFormatter.format(referenceDate) + time;
        return formatter.parse(timeString);
    }
}
