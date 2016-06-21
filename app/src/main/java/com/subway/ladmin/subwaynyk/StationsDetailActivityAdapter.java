package com.subway.ladmin.subwaynyk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by ladmin on 10/06/16.
 */

import android.widget.ImageView;


public class StationsDetailActivityAdapter extends BaseAdapter {
    String[] result;
    Context context;
    ArrayList<PredictionViewModel> filterPredtionModelView;
    Prediction prediction = new Prediction();
    int[] imageId;
    NYCRouteColorManager routeColor = new NYCRouteColorManager();
    private static LayoutInflater inflater = null;

    public StationsDetailActivityAdapter(Context context, ArrayList<PredictionViewModel> filterPredtionModelView) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.filterPredtionModelView = filterPredtionModelView;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return filterPredtionModelView.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return filterPredtionModelView.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return filterPredtionModelView.indexOf(getItem(position));
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rowView = mInflater.inflate(R.layout.stations_listview, null, true);
        TextView txtTitle0 = (TextView) rowView.findViewById(R.id.textView0);
        TextView txtTitle1 = (TextView) rowView.findViewById(R.id.textView1);
        TextView txtTitle2 = (TextView) rowView.findViewById(R.id.textView2);
        TextView txtTitle3 = (TextView) rowView.findViewById(R.id.textView3);
        TextView txtTitle4 = (TextView) rowView.findViewById(R.id.textView4);
        ImageView imageView1 = (ImageView) rowView.findViewById(R.id.imageView1);

        PredictionViewModel model = filterPredtionModelView.get(position);
        Prediction innerPrediction = model.prediction;
        Route route = innerPrediction.route;

        txtTitle0.setText(route.objectId);
        txtTitle0.setTextColor(Color.parseColor("#ffffff"));

        if (innerPrediction.direction == 0) {
            imageView1.setImageResource(R.drawable.uptown);
            imageView1.setBackgroundColor(Color.rgb(255, 255, 255));
            imageView1.setColorFilter(routeColor.colorForRouteId(route.objectId));
        }

        if (innerPrediction.direction == 1) {
            imageView1.setImageResource(R.drawable.downtown);
            imageView1.setBackgroundColor(Color.rgb(255, 255, 255));
            imageView1.setColorFilter(routeColor.colorForRouteId(route.objectId));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
        String dateString = formatter.format(innerPrediction.timeOfArrival);
        txtTitle1.setText(dateString);

        Prediction nextPrediction = model.onDeckPrediction;
        String dateStringOnDeckPrediction = formatter.format(nextPrediction.timeOfArrival);
        txtTitle2.setText(dateStringOnDeckPrediction);

        Prediction finalPrediction = model.inTheHolePrediction;
        String dateStringinTheHolePrediction = formatter.format(finalPrediction.timeOfArrival);
        txtTitle3.setText(dateStringinTheHolePrediction);
        String interval = differenceBetween(innerPrediction.timeOfArrival);
        txtTitle4.setText(interval);

//        Date curDate = new Date();
//        long curMillis = curDate.getTime();
//
//        int sec = (int) (innerPrediction.timeOfArrival.getTime() / 60);
//        String intToString = Integer.toString(sec);
//        txtTitle4.setText(intToString);

        return rowView;
    }
    private String differenceBetween(Date start){
        long diffInMs = start.getTime() - new Date().getTime();

        long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
        return diffInMin+"m";

    }
}