package com.rndapp.mtamap;

/**
 * Created by ladmin on 02/06/2016.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper implements Serializable {
    private SQLiteDatabase sqLiteDatabase;
    private final Context context;

    private static final String DATABASE_NAME = "gtfs.db";
    public  static String DATABASE_PATH = "";
    public static final int DATABASE_VERSION = 2;

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


    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    public String getDBPath(){
        if(DATABASE_PATH.isEmpty())
            DATABASE_PATH = context.getResources().getString(R.string.dbpath);
        return DATABASE_PATH + DATABASE_NAME;
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();

        if (!dbExist) {
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = getDBPath();
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
        return checkDB;
    }

    private boolean isContainValue(ArrayList<String> list, String value) {
        for (String element : list) {
            if (element.equals(value))
                return true;
        }
        return false;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() throws IOException {
        String outFileName = getDBPath();
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    //delete database
    public void db_delete() {
        File file = new File(getDBPath());
        if (file.exists()) {
            file.delete();
        }
    }

    //Open database
    public void openDatabase() throws SQLException {
        String myPath = getDBPath();
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase() throws SQLException {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db_delete();
        }
    }

    public ArrayList<Station> getAllStops() {
        ArrayList<Stop> stopsArrayList = new ArrayList<Stop>();
        ArrayList<String> stationsIdsArrayList = new ArrayList<String>();

        String queryString = "SELECT stop_name, stop_id, parent_station FROM stops WHERE location_type = 1";
        Cursor queryCursor = sqLiteDatabase.rawQuery(queryString, null);
        queryCursor.moveToFirst();

        while (queryCursor.isAfterLast() == false) {

            Stop stop = new Stop();
            stop.name = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_NAME));
            stop.objectId = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_ID));
            stop.parentId = queryCursor.getString(queryCursor.getColumnIndex(STOP_PARENT_STATION));
            stopsArrayList.add(stop);

            if (!isContainValue(stationsIdsArrayList, stop.objectId)) {

                Station station = new Station(stop.name);
                station.parents.add(stop);
                stationsIdsArrayList.add(stop.objectId);
                String replacedString = station.name.replace("'s", "");
                String appendQuery = queryForNameArray(replacedString.split("\\s+"));

                String innerQueryString = "SELECT stop_name, stop_id, parent_station FROM stops WHERE location_type = 1" + appendQuery;
                Cursor innerQueryCursor = sqLiteDatabase.rawQuery(innerQueryString, null);
                innerQueryCursor.moveToFirst();

                while (innerQueryCursor.isAfterLast() == false) {

                    Stop parent = new Stop();
                    parent.name = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_STOP_NAME));
                    parent.objectId = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_STOP_ID));
                    parent.parentId = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_PARENT_STATION));
                    Station innerStation = new Station();
                    innerStation.name = parent.name;

                    if (station.equal(station, innerStation)) {
                        station.parents.add(parent);
                        stationsIdsArrayList.add(parent.objectId);
                    }
                    innerQueryCursor.moveToNext();
                }
                allStationsArrayList.add(station);
            }
            queryCursor.moveToNext();
        }
        getAllRoutes();
        return allStationsArrayList;
    }

    private String queryForNameArray(String[] array) {
        String queryString = "";
        for (String nameComponent : array)
            queryString += " AND stop_name LIKE '%" + nameComponent + "%'";

        return queryString;
    }

    public void getAllRoutes() {
        Cursor queryCursor = sqLiteDatabase.rawQuery("select * from routes", null);
        queryCursor.moveToFirst();

        while (queryCursor.isAfterLast() == false) {
            Route route = new Route(queryCursor.getString(queryCursor.getColumnIndex(ROUTE_ID)));
            allRoutesArrayList.add(route);
            queryCursor.moveToNext();
        }
    }

    public ArrayList<Stop> stopForStation(Station station) {
        ArrayList<Stop> stopsArrayList = new ArrayList<Stop>();
        for (Stop parent : station.parents) {

            String queryString = "SELECT stop_name, stop_id FROM stops WHERE parent_station = " + "'" + parent.objectId + "'";
            Cursor queryCursor = sqLiteDatabase.rawQuery(queryString, null);
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
            Cursor queryCursor = sqLiteDatabase.rawQuery(queryString, null);
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
        Cursor queryCursor = sqLiteDatabase.rawQuery(queryString, null);
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
            Cursor innerQueryCursor = sqLiteDatabase.rawQuery(innerQueryString, null);
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
            if(!containsElement(predtionsArrayList,predictionObj))
                predtionsArrayList.add(predictionObj);
            queryCursor.moveToNext();
        }
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
}