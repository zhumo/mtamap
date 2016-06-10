package com.subway.ladmin.subwaynyk;

/**
 * Created by ladmin on 02/06/2016.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper
{
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    Utilities util;


    private static final String DATABASE_NAME = "gtfs.db";
    public final static String DATABASE_PATH ="/data/data/com.subway.ladmin.subwaynyk/databases/";
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


    ArrayList<Station> allStations = new ArrayList<Station>();
    ArrayList<Routes> allRoutes = new ArrayList<Routes>();


    public ArrayList<Station> getAllStops()
    {
        ArrayList<Stop> arrayList = new ArrayList<Stop>();
        ArrayList<String> stationsIds = new ArrayList<String>();

        int counter = 0;
        String queryString = "SELECT stop_name, stop_id, parent_station FROM stops WHERE location_type = 1";
        Cursor queryCursor =  myDataBase.rawQuery( queryString, null );
        queryCursor.moveToFirst();

        while(queryCursor.isAfterLast() == false){

            Stop stop = new Stop();
            stop.name = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_NAME));
            stop.objectId = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_ID));
            stop.parentId = queryCursor.getString(queryCursor.getColumnIndex(STOP_PARENT_STATION));
            arrayList.add(stop);

            //Log.v("Stop Name", String.valueOf(stop.name));

            if (!stationsIds.contains(stop.objectId)){

                Station station = new Station(stop.name);
                station.parents.add(stop);
                stationsIds.add(stop.objectId);

                String replacedString = station.name.replace("'s", "");
                String appendQuery = queryForNameArray(replacedString.split("\\s+"));

//                if let queryForName = queryForNameArray(stationName.componentsSeparatedByCharactersInSet(NSCharacterSet.whitespaceCharacterSet()))
//                String innerQueryString = "SELECT stop_name, stop_id, parent_station FROM stops WHERE location_type = 1 AND stop_name LIKE '" + appendQuery +"'";

                String innerQueryString = "SELECT stop_name, stop_id, parent_station FROM stops WHERE location_type = 1" + appendQuery;
                Cursor innerQueryCursor =  myDataBase.rawQuery( innerQueryString, null );
                innerQueryCursor.moveToFirst();

                while(innerQueryCursor.isAfterLast() == false) {

                    Stop parent = new Stop();
                    parent.name = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_STOP_NAME));
                    parent.objectId = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_STOP_ID));
                    parent.parentId = innerQueryCursor.getString(innerQueryCursor.getColumnIndex(STOP_PARENT_STATION));

                    Station innerStation = new Station();
                    innerStation.name = parent.name;

//                    Log.v("Station Name", String.valueOf(station.name));
//                    Log.v("Inner Station Name", String.valueOf(innerStation.name));
                    if (station.equal(station, innerStation)){
                        station.parents.add(parent);
                        stationsIds.add(parent.objectId);
                    }
                    innerQueryCursor.moveToNext();
                }
                //Log.v("All Stations", String.valueOf(allStations.size()));
                allStations.add(station);
            }
            queryCursor.moveToNext();
        }

//        util.stationsForSearchString("a",arrayList);
        Log.v("AllStations Count", String.valueOf(allStations.size()));
        return allStations;
    }

    public String queryForNameArray(String[] array) {
    String queryString = "";

        for (String nameComponent : array)
            queryString += " AND stop_name LIKE '%"+nameComponent+"%'";

    return queryString;
    }

    //Constructor
    public DBHelper(Context context)
    {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

//    public DBHelper() {
//        /super();
//    }

    //Create a empty database on the system
    public void createDatabase() throws IOException
    {
        boolean dbExist = checkDataBase();
        util = new Utilities();

        if(dbExist)
        {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }

        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try
        {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e)
        {
        }
        return checkDB;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() throws IOException
    {
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    //delete database
    public void db_delete()
    {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if(file.exists())
        {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    //Open database
    public void openDatabase() throws SQLException
    {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        Log.v("DB Path", String.valueOf(myDataBase));
    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db)
    {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }

    //add your public methods for insert, get, delete and update data in database.

    public ArrayList<String> getAllRoutes()
    {
        ArrayList<String> routesArray = new ArrayList<String>();

        Cursor routeCursor =  myDataBase.rawQuery( "select * from routes", null );
        routeCursor.moveToFirst();

        while(routeCursor.isAfterLast() == false){
            routesArray.add(routeCursor.getString(routeCursor.getColumnIndex(ROUTE_ID)));
            Routes route = new Routes(routeCursor.getString(routeCursor.getColumnIndex(ROUTE_ID)));
            allRoutes.add(route);
            routeCursor.moveToNext();
        }
//        Log.v("Roputes Array", String.valueOf(routesArray));
        return routesArray;
    }

    public ArrayList<Stop> stopForStation(Station station){
        ArrayList<Stop> stops = new ArrayList<Stop>();
        for (Stop parent : station.parents){
            String queryString = "SELECT stop_name, stop_id FROM stops WHERE parent_station = "+ parent.objectId;
            Cursor queryCursor =  myDataBase.rawQuery( queryString, null );
            queryCursor.moveToFirst();
            while(queryCursor.isAfterLast() == false) {

                Stop stop = new Stop();
                stop.name = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_NAME));
                stop.objectId = queryCursor.getString(queryCursor.getColumnIndex(STOP_STOP_ID));
//                stop.parentId = queryCursor.getString(queryCursor.getColumnIndex(STOP_PARENT_STATION));
                stops.add(stop);
                queryCursor.moveToNext();
            }
        }

        if (stops.size() == 0) {
            return null;
        }else{
            return stops;
        }

    }

    public  ArrayList<String> routeIdsForStation(Station station){
        ArrayList<String> routeIds = new ArrayList<String>();
        ArrayList<Stop> stops = stopForStation(station);

        String string = new String();
        for (Stop stop:stops){
            string= string + "\"" +stop.objectId + "\"" +"," ;
        }
        string = string.substring(0,string.length()-1);

        if(stops.size()>0){
            String queryString = "SELECT trips.route_id FROM trips INNER JOIN stop_times ON stop_times.trip_id = trips.trip_id WHERE stop_times.stop_id IN (" + string + ") GROUP BY trips.route_id";
//            Log.v("queryString", String.valueOf(queryString));
            Cursor queryCursor =  myDataBase.rawQuery( queryString, null );
            queryCursor.moveToFirst();

            while(queryCursor.isAfterLast() == false) {

                routeIds.add(queryCursor.getString(queryCursor.getColumnIndex(ROUTE_ID)));
                queryCursor.moveToNext();
            }
        }
//        Log.v("Route_ID",String.valueOf(routeIds));
        return routeIds;
    }

    public ArrayList<Prediction> getPredictions(Station station, Date time){

        ArrayList<Prediction> allPredtions = new ArrayList<Prediction>();
        ArrayList<Stop> stops = stopForStation(station);
        String string = new String();
        for (Stop stop:stops){
            string= string + "\"" +stop.objectId + "\"" +"," ;
        }
        string = string.substring(0,string.length()-1);
        String startTime = util.dateToTime(time);
//        Log.v("StartTime",startTime);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(String.valueOf(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, 20);
        String endTime= formatter.format(cal.getTime());
//        Log.v("EndTime",endTime);

        String timesQuery = "SELECT trip_id, departure_time FROM stop_times WHERE stop_id IN (" + string + ") AND departure_time BETWEEN " + "'"+startTime+"'"+ " AND "+"'"+endTime+"'";
        Cursor queryCursor =  myDataBase.rawQuery( timesQuery, null );
        queryCursor.moveToFirst();
        while(queryCursor.isAfterLast() == false) {
            String tripId = queryCursor.getString(queryCursor.getColumnIndex(TRIP_ID));
            String departuretime = queryCursor.getString(queryCursor.getColumnIndex(DEPARTURE_TIME));

//            Log.v("TRIP_ID", String.valueOf(tripId));
            Date date1 = new Date();
            try {
                date1 = timeToDate(departuretime, time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Prediction prediction = new Prediction(date1);

            String tripQuery = "SELECT direction_id, route_id FROM trips WHERE trip_id = "+ "'"+tripId+"'";
//            Log.v("tirpQuery", String.valueOf(tripQuery));
            Cursor queryCursorTrip =  myDataBase.rawQuery( tripQuery, null );
            queryCursorTrip.moveToFirst();

            while(queryCursorTrip.isAfterLast() == false) {

                String routeIdTrip = queryCursorTrip.getString(queryCursorTrip.getColumnIndex(ROUTE_ID_TRIP));
                int direction = queryCursorTrip.getInt(queryCursorTrip.getColumnIndex(DIRECTION_ID));
//                Log.v("routeIdTrip", String.valueOf(routeIdTrip));

                if(direction == 0){
                    prediction.direction = 0;
                }else {
                    prediction.direction = 1;
                }

                ArrayList<Routes> helperRouteArray = new ArrayList<Routes>();
//                Log.v("allRoutes", String.valueOf(allRoutes));
                for(Routes route: allRoutes){
                    String routeString = route.objectId;

                    if(routeString.contains(routeIdTrip)){
                        Routes route1 = new Routes(routeString);
                        helperRouteArray.add(route1);
                    }
                }
                prediction.route = helperRouteArray.get(0);

                queryCursorTrip.moveToNext();
            }

            if (!allPredtions.contains(prediction)) {
                allPredtions.add(prediction);
            }

            queryCursor.moveToNext();
        }
//        Log.v("allPredtions Size: ", String.valueOf(allPredtions.size()));
        return allPredtions;
    }

    public Date timeToDate(String time, Date referenceDate) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        String timeString = dateFormatter.format(referenceDate)+time;
        return formatter.parse(timeString);
    }

}