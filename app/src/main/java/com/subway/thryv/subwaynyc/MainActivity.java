package com.subway.thryv.subwaynyc;

import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import java.io.IOException;
import java.util.ArrayList;
import uk.co.senab.photoview.PhotoView;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    DBHelper dbHelper;
    SearchView searchView;
    ListView stationsListView;
    ArrayList stationIdsArrayList;
    ArrayList stationIdsFilteredList;
    MainActivityAdapter mainAdapter;
    PhotoView mapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.rgb(255, 255, 255));

        searchView = (SearchView) findViewById(R.id.search_view);
        mapImageView = (PhotoView) findViewById(R.id.subwaymap);
        mapImageView.setMaximumScale(12.0f);
        stationsListView = (ListView) findViewById(R.id.list);
        stationsListView.setVisibility(stationsListView.INVISIBLE);
        dbHelper = new DBHelper(this);

        try {
            dbHelper.createDatabase();
            dbHelper.openDatabase();
            new StopsTask().execute();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    class StopsTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            stationIdsArrayList = dbHelper.getAllStops();
            stationIdsFilteredList = new ArrayList(stationIdsArrayList);
            mainAdapter = new MainActivityAdapter(MainActivity.this, stationIdsFilteredList, dbHelper);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stationsListView.setAdapter(mainAdapter);
            searchView.setOnQueryTextListener(MainActivity.this);
            stationsListView.setOnItemClickListener(mainAdapter);
        }
    }

    @Override
    public boolean onQueryTextChange(String text) {
        if (text.length() > 0) {
            mainAdapter.getFilter().filter(text);
            stationsListView.setVisibility(stationsListView.VISIBLE);
            mapImageView.setVisibility(mapImageView.INVISIBLE);
        } else {
            stationsListView.setVisibility(stationsListView.INVISIBLE);
            mapImageView.setVisibility(mapImageView.VISIBLE);
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(this.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}


