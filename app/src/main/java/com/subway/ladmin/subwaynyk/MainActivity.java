package com.subway.ladmin.subwaynyk;

import android.content.Intent;
import android.database.SQLException;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.content.res.TypedArray;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener  {

    DBHelper myDbHelper;
    SearchView search_view;
    ListView list;
    ArrayList stationIds;
    Utilities util;
    Station station = new Station();
    EditText inputSearch;
    CustomAdapter adapter;
    ArrayList<String> getAllStops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDbHelper = new DBHelper(this);
        getAllStops = new ArrayList<String>();

        try {
            myDbHelper.createDatabase();
        } catch (IOException ioe) {

            throw new Error("Unable to create database");
        }

        try {

            util = new Utilities();
            myDbHelper.openDatabase();
            stationIds = myDbHelper.getAllStops();
            Log.v("stationArray", String.valueOf(stationIds));

            Toast.makeText(getApplicationContext(), "stationIds :" +stationIds.size() , Toast.LENGTH_LONG)
                    .show();

            search_view = (SearchView) findViewById(R.id.search_view);
            list = (ListView) findViewById(R.id.list);
            adapter = new CustomAdapter(getApplicationContext(), stationIds);
            Log.v("Adapter", String.valueOf(  adapter));

            list.setAdapter(adapter);

            search_view.setOnQueryTextListener(this);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    Station station = (Station) stationIds.get(position);
                    Intent intent = new Intent(MainActivity.this, StationsDetails.class);
                    intent.putExtra("station",  station); //Optional parameters
                    startActivity(intent);
                }
            });
        }catch(SQLException sqle){

            throw sqle;
        }








//        Stop stop1 = new Stop();
//        stop1.name = "Van Cortlandt Park - 242 St";
//        stop1.objectId = "101";
//        stop1.parentId = "";
//
//        Stop stop2 = new Stop();
//        stop2.name = "Van Cortlandt Park - 242 St";
//        stop2.objectId = "101";
//        stop2.parentId = "";
//
//        Station station = new Station();
//        station.name = "Van Cortlandt Park - 242 St";
//        station.parents.add(stop1);
//        station.parents.add(stop2);
//        myDbHelper.routeIdsForStation(station);
//
//        Date date = new Date();
//        Log.v("Date", String.valueOf(date));
//        myDbHelper.getPredictions(station, date);







//        String[] List = new String[] { "Android List View",
//                "Adapter implementation",
//                "Simple List View In Android",
//                "Create List View Android",
//                "Android Example",
//                "List View Source Code",
//                "List View Array Adapter",
//                "Android Example List View"
//        };
//
//         adapter = new ArrayAdapter<String>(this,
//                 R.layout.list_item, R.id.product_name, getAllStops);
//
//
//        // Assign adapter to ListView
//        listView.setAdapter(adapter);
//
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                // ListView Clicked item index
//                int itemPosition     = position;
//
//                // ListView Clicked item value
//                String  itemValue    = (String) listView.getItemAtPosition(position);
//
//                // Show Alert
//                Toast.makeText(getApplicationContext(),
//                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
//                        .show();
//
//            }
//        });
    }

    @Override
    public boolean onQueryTextChange(String text) {
        adapter.getFilter().filter(text);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}


