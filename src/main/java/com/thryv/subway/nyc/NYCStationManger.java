package com.thryv.subway.nyc;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.thryv.subway.abstractions.DBHelper;
import com.thryv.subway.abstractions.Prediction;
import com.thryv.subway.abstractions.Route;
import com.thryv.subway.abstractions.Station;
import com.thryv.subway.abstractions.StationManager;
import com.thryv.subway.abstractions.Stop;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ell on 7/7/16.
 */
public class NYCStationManger extends StationManager {
    private DBHelper dbHelper;

    //Stop Columns
    public static final String STOP_STOP_NAME = "stop_name";
    public static final String STOP_LOCATION_TYPE = "location_type";
    public static final String STOP_STOP_ID = "stop_id";
    public static final String STOP_PARENT_STATION = "parent_station";

    //Route Columns
    public static final String ROUTE_ID = "route_id";
    public static final String ROUTE_LONG_NAME = "route_long_name";
    public static final String ROUTE_SHORT_NAME = "route_short_name";

    //Trips Columns
    public static final String TRIP_ID = "trip_id";
    public static final String DEPARTURE_TIME = "departure_time";
    public static final String ROUTE_ID_TRIP = "route_id";
    public static final String DIRECTION_ID = "direction_id";

    ArrayList<Station> allStationsArrayList = new ArrayList<Station>();
    ArrayList<Route> allRoutesArrayList = new ArrayList<Route>();

    public NYCStationManger(Context context){
        dbHelper = new DBHelper(context);
        try {
            dbHelper.createDatabase();
            dbHelper.openDatabase();
            populateStationsAndRoutes();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    public ArrayList<Station> getAllStations() {
        return allStationsArrayList;
    }

    private void populateStationsAndRoutes() {
        ArrayList<String> stationsIdsArrayList = new ArrayList<String>();

        String queryString = "SELECT stop_name, stop_id, parent_station FROM stops WHERE location_type = 1";
        Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery(queryString, null);
        queryCursor.moveToFirst();

        while (!queryCursor.isAfterLast()) {
            Stop stop = new Stop();
            stop.name = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_NAME));
            stop.objectId = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_ID));
            stop.parentId = queryCursor.getString(queryCursor.getColumnIndex(STOP_PARENT_STATION));

            if (!isContainValue(stationsIdsArrayList, stop.objectId)) {
                Station station = new Station(stop.name);
                station.stops.add(stop);
                stationsIdsArrayList.add(stop.objectId);
                String replacedString = station.name.replace("'s", "");
                String appendQuery = queryForNameArray(replacedString.split("\\s+"));

                String innerQueryString = "SELECT stop_name, stop_id, parent_station FROM stops WHERE location_type = 1" + appendQuery;
                Cursor innerQueryCursor = dbHelper.getSqLiteDatabase().rawQuery(innerQueryString, null);
                innerQueryCursor.moveToFirst();

                while (innerQueryCursor.isAfterLast() == false) {

                    Stop parent = new Stop();
                    parent.name = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_STOP_NAME));
                    parent.objectId = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_STOP_ID));
                    parent.parentId = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_PARENT_STATION));
                    Station innerStation = new Station();
                    innerStation.name = parent.name;

                    if (station.equals(innerStation)) {
                        station.stops.add(parent);
                        stationsIdsArrayList.add(parent.objectId);
                    }
                    innerQueryCursor.moveToNext();
                }
                innerQueryCursor.close();
                allStationsArrayList.add(station);
            }
            queryCursor.moveToNext();
        }
        queryCursor.close();
        getAllRoutes();
    }

    private String queryForNameArray(String[] array) {
        String queryString = "";
        for (String nameComponent : array)
            queryString += " AND stop_name LIKE '%" + nameComponent + "%'";

        return queryString;
    }

    public ArrayList<Route> getAllRoutes() {
        Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery("select * from routes", null);
        queryCursor.moveToFirst();

        while (queryCursor.isAfterLast() == false) {
            Route route = new Route(queryCursor.getString(queryCursor.getColumnIndex(ROUTE_ID)));
            allRoutesArrayList.add(route);
            queryCursor.moveToNext();
        }
        queryCursor.close();
        return allRoutesArrayList;
    }

    public ArrayList<Stop> stopForStation(Station station) {
        ArrayList<Stop> stopsArrayList = new ArrayList<Stop>();
        for (Stop parent : station.stops) {

            String queryString = "SELECT stop_name, stop_id FROM stops WHERE parent_station = " + "'" + parent.objectId + "'";
            Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery(queryString, null);
            queryCursor.moveToFirst();
            while (queryCursor.isAfterLast() == false) {

                Stop stop = new Stop();
                stop.name = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_NAME));
                stop.objectId = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_ID));
                stop.parentId = parent.objectId;
                stopsArrayList.add(stop);
                queryCursor.moveToNext();
            }
            queryCursor.close();
        }

        if (stopsArrayList.size() == 0) {
            return null;
        } else {
            return stopsArrayList;
        }
    }

    public ArrayList<String> routeIdsForStation(Station station) {
        ArrayList<String> routeIdsArrayList = new ArrayList<String>();
        ArrayList<Stop> stopsArrayList = stopForStation(station);

        String stopObjectIds = new String();
        if(stopsArrayList==null)
            return new ArrayList<>();
        for (Stop stop : stopsArrayList) {
            stopObjectIds = stopObjectIds + "\"" + stop.objectId + "\"" + ",";
        }
        stopObjectIds = stopObjectIds.substring(0, stopObjectIds.length() - 1);

        if (stopsArrayList.size() > 0) {
            String queryString = "SELECT trips.route_id FROM trips INNER JOIN stop_times ON stop_times.trip_id = trips.trip_id WHERE stop_times.stop_id IN (" + stopObjectIds + ") GROUP BY trips.route_id";
            Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery(queryString, null);
            queryCursor.moveToFirst();

            while (queryCursor.isAfterLast() == false) {
                routeIdsArrayList.add(queryCursor.getString(queryCursor.getColumnIndex(ROUTE_ID)));
                queryCursor.moveToNext();
            }
            queryCursor.close();
        }
        return routeIdsArrayList;
    }

    public ArrayList<Prediction> getPredictions(Station station, Date time) {
        ArrayList<Prediction> predtionsArrayList = new ArrayList<Prediction>();
        ArrayList<Stop> stopsArrayList = stopForStation(station);
        String stopObjectIds = new String();
        for (Stop stop : stopsArrayList) {
            stopObjectIds = stopObjectIds + "\"" + stop.objectId + "\"" + ",";
        }
        stopObjectIds = stopObjectIds.substring(0, stopObjectIds.length() - 1);
        String startTime = dateToTime(time);
        long minutesInMillis = 20 * 60 * 1000;
        String endTimeString = dateToTime(new Date(time.getTime()+minutesInMillis));

        String queryString = "SELECT trip_id, departure_time FROM stop_times WHERE stop_id IN (" + stopObjectIds + ") AND departure_time BETWEEN " + "'" + startTime + "'" + " AND " + "'" + endTimeString + "'";
        Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery(queryString, null);
        queryCursor.moveToFirst();
        while (queryCursor.isAfterLast() == false) {
            String tripId = queryCursor.getString(queryCursor.getColumnIndex(TRIP_ID));
            String departureTime = queryCursor.getString(queryCursor.getColumnIndex(DEPARTURE_TIME));
            Date dateObj = new Date();
            try {
                dateObj = timeToDate(departureTime, time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Prediction predictionObj = new Prediction(dateObj);
            String innerQueryString = "SELECT direction_id, route_id FROM trips WHERE trip_id = " + "'" + tripId + "'";
            Cursor innerQueryCursor = dbHelper.getSqLiteDatabase().rawQuery(innerQueryString, null);
            innerQueryCursor.moveToFirst();

            while (innerQueryCursor.isAfterLast() == false) {
                String routeIdTrip = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(ROUTE_ID_TRIP));
                int direction = innerQueryCursor.getInt(innerQueryCursor.getColumnIndex(DIRECTION_ID));

                if (direction == 0) {
                    predictionObj.direction = 0;
                } else {
                    predictionObj.direction = 1;
                }

                ArrayList<Route> innerRouteArray = new ArrayList<Route>();
                for (Route route : allRoutesArrayList) {
                    String routeString = route.objectId;
                    if (routeString.equals(routeIdTrip)) {
                        innerRouteArray.add(route);
                    }
                }
                predictionObj.route = innerRouteArray.get(0);
                innerQueryCursor.moveToNext();
            }
            innerQueryCursor.close();
            if(!containsElement(predtionsArrayList,predictionObj))
                predtionsArrayList.add(predictionObj);
            queryCursor.moveToNext();
        }
        queryCursor.close();
        return predtionsArrayList;
    }

    public Date timeToDate(String time, Date referenceDate) throws ParseException {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormatter.format(referenceDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString = dateFormatter.format(referenceDate) + " " + time;
        return formatter.parse(timeString);
    }

    public String dateToTime(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }

    private boolean containsElement(ArrayList<Prediction> predictions,Prediction newPrediction){
        for(Prediction prediction:predictions ){
            if(prediction.route.objectId .equals(newPrediction.route.objectId) && prediction.timeOfArrival.getTime()== newPrediction.timeOfArrival.getTime()
                    && prediction.direction == newPrediction.direction){
                return true;
            }
        }
        return false;
    }

    private boolean isContainValue(ArrayList<String> list, String value) {
        for (String element : list) {
            if (element.equals(value))
                return true;
        }
        return false;
    }
}
