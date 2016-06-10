package com.subway.ladmin.subwaynyk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class StationsDetails extends AppCompatActivity {
    Station stationObj;
    DBHelper dbHelpObj;
    ListView stationDetailsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_details);

        Intent intent = getIntent();

        stationObj = (Station) intent.getSerializableExtra("station");

        stationDetailsList = (ListView) findViewById(R.id.list);

        Toast.makeText(getApplicationContext(), stationObj.name, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }
}
