package com.subway.ladmin.subwaynyk;

import android.database.SQLException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    DBHelper DBHelper;
    SearchView searchView;
    ListView listView;
    ArrayList stationIds;
    ArrayList stationIdsFilteredList;
    MainActivityAdapter mainAdapter;
    ImageView mapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (SearchView) findViewById(R.id.search_view);
        mapImageView = (ImageView) findViewById(R.id.subwaymap);
        listView = (ListView) findViewById(R.id.list);
        listView.setVisibility(listView.INVISIBLE);
        DBHelper = new DBHelper(this);

        try {
            DBHelper.createDatabase();
            DBHelper.openDatabase();
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
            stationIds = DBHelper.getAllStops();
            stationIdsFilteredList = new ArrayList(stationIds);
            mainAdapter = new MainActivityAdapter(MainActivity.this, stationIdsFilteredList, DBHelper);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(mainAdapter);
            searchView.setOnQueryTextListener(MainActivity.this);
            listView.setOnItemClickListener(mainAdapter);
        }
    }

    @Override
    public boolean onQueryTextChange(String text) {
        if (text.length() > 0) {
            mainAdapter.getFilter().filter(text);
            listView.setVisibility(listView.VISIBLE);
            mapImageView.setVisibility(mapImageView.INVISIBLE);
        } else {
            listView.setVisibility(listView.INVISIBLE);
            mapImageView.setVisibility(mapImageView.VISIBLE);
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(this.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }

        }


        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}


