package com.rndapp.mtamap.adapters;

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

import com.rndapp.mtamap.models.PredictionViewModel;
import com.rndapp.mtamap.R;
import com.thryv.subway.abstractions.Prediction;
import com.thryv.subway.abstractions.Route;
import com.thryv.subway.abstractions.RouteColorManager;
import com.thryv.subway.nyc.NYCRouteColorManager;
import com.thryv.subway.paris.ParisRouteColorManager;


public class StationAdapter extends BaseAdapter {
    Context context;
    ArrayList<PredictionViewModel> filterPredtionModelView;
    RouteColorManager routeColor = null;

    public StationAdapter(Context context, ArrayList<PredictionViewModel> filterPredtionModelView) {
        this.context = context;
        this.filterPredtionModelView = filterPredtionModelView;

        if (context.getResources().getString(R.string.city).equals("nyc")){
            routeColor = new NYCRouteColorManager();
        }else if (context.getResources().getString(R.string.city).equals("paris")){
            routeColor = new ParisRouteColorManager();
        }
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.stations_listview, null, true);
        TextView downRouteTxtView = (TextView) rowView.findViewById(R.id.tv_down_line);
        TextView upRouteTxtView = (TextView) rowView.findViewById(R.id.tv_up_line);
        TextView timeTxtView = (TextView) rowView.findViewById(R.id.textView1);
        TextView onDeckTxtView = (TextView) rowView.findViewById(R.id.textView2);
        TextView inTheHoleTxtView = (TextView) rowView.findViewById(R.id.textView3);
        TextView deltaTxtView = (TextView) rowView.findViewById(R.id.textView4);
        ImageView routeImageView = (ImageView) rowView.findViewById(R.id.imageView1);

        PredictionViewModel model = filterPredtionModelView.get(position);
        Prediction innerPrediction = model.prediction;
        Route route = innerPrediction.route;

        if (innerPrediction.direction == 0) {
            downRouteTxtView.setText(route.objectId);
            upRouteTxtView.setText("");
            routeImageView.setImageResource(R.drawable.uptown);
            routeImageView.setBackgroundColor(Color.rgb(255, 255, 255));
            routeImageView.setColorFilter(routeColor.colorForRouteId(route.objectId));
        }

        if (innerPrediction.direction == 1) {
            upRouteTxtView.setText(route.objectId);
            downRouteTxtView.setText("");
            routeImageView.setImageResource(R.drawable.downtown);
            routeImageView.setBackgroundColor(Color.rgb(255, 255, 255));
            routeImageView.setColorFilter(routeColor.colorForRouteId(route.objectId));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        String dateString = formatter.format(innerPrediction.timeOfArrival);
        timeTxtView.setText(dateString);

        Prediction nextPrediction = model.onDeckPrediction;
        if(nextPrediction.timeOfArrival!=null) {
            String dateStringOnDeckPrediction = formatter.format(nextPrediction.timeOfArrival);
            onDeckTxtView.setText(dateStringOnDeckPrediction);
        }
        else
            onDeckTxtView.setText("");

        Prediction finalPrediction = model.inTheHolePrediction;
        if(finalPrediction.timeOfArrival!=null) {
            String dateStringinTheHolePrediction = formatter.format(finalPrediction.timeOfArrival);
            inTheHoleTxtView.setText(dateStringinTheHolePrediction);
        }
        else
            inTheHoleTxtView.setText("");
        if(innerPrediction.timeOfArrival!=null) {
            String interval = differenceBetween(innerPrediction.timeOfArrival);
            deltaTxtView.setText(interval);
        }
        else
            deltaTxtView.setText("");
        return rowView;
    }
    private String differenceBetween(Date start){
        long diffInMs = start.getTime() - new Date().getTime();
        long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
        return diffInMin+"m";
    }
}