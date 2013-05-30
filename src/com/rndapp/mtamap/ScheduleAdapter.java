package com.rndapp.mtamap;

import java.util.ArrayList;
import java.util.Calendar;

import com.rndapp.mtamap.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter {
	Context context;
	int itemResID;
	long time;
	int color;
	ArrayList<Trip> trips;
	
	public ScheduleAdapter(Context ctxt, int itemResID, JSONObject json){
		this.context = ctxt;
		this.itemResID = itemResID;
		trips = new ArrayList<Trip>();
		try {
			JSONObject tripList = json.getJSONObject("TripList");
			time = tripList.getLong("CurrentTime");
			setColor(tripList.getString("Line"));
			JSONArray trips = tripList.getJSONArray("Trips");
			for (int i = 0; i<trips.length(); i++){
				JSONObject jsontrip = trips.getJSONObject(i);
				boolean added = false;
				for (int j = 0; j<this.trips.size(); j++){
					Trip trip = this.trips.get(j);
					if (trip.destination.equalsIgnoreCase(jsontrip.getString("Destination"))){
						trip.incorporatePredictions(jsontrip.getJSONArray("Predictions"));
						added = true;
					}
				}
				if (!added){
					Trip t = new Trip(jsontrip, tripList.getString("Line"));
					this.trips.add(t);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setColor(String line){
		if (line.equalsIgnoreCase("BLUE")){
			color = context.getResources().getColor(R.color.blue);
		}else if(line.equalsIgnoreCase("orange")){
			color = context.getResources().getColor(R.color.orange);
		}else if(line.equalsIgnoreCase("red")){
			color = context.getResources().getColor(R.color.red);
		}
	}

	@Override
	public int getCount() {
		int result = 0;
		for (int i = 0; i<trips.size(); i++){
			Trip trip = trips.get(i);
			result += trip.stops.size();
		}
		return result;
	}

	@Override
	public Object getItem(int position) {
		Object item = null;
		int result = 0;
		for (int i = 0; i<trips.size(); i++){
			Trip trip = trips.get(i);
			for (int j = 0; j<trip.stops.size(); j++){
				if (result == position && j == 0){
					item = trip;
				}else if (result == position){
					item = trip.stops.get(j);
				}
				result++;
			}
		}
		return item;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    public boolean isEnabled(int position) { 
    	return false; 
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object o = getItem(position);
		
		StopHolder holder;
		
		if (convertView == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(itemResID, parent, false);
			
			holder = new StopHolder();
			holder.tv = (TextView)convertView.findViewById(R.id.trip_name);
			holder.name = (TextView)convertView.findViewById(R.id.stop_name);
			holder.nextTrain = (TextView)convertView.findViewById(R.id.stop_time);
			
			convertView.setTag(holder);
		}else{
			holder = (StopHolder)convertView.getTag();
		}
		
		if (o.getClass() == Stop.class){
			Stop s = (Stop)o;
			
			holder.tv.setVisibility(View.GONE);
			holder.name.setText(s.name);
			holder.nextTrain.setText(formattedPredict(s));
		}else{
			Trip t = (Trip)o;
			Stop s = t.stops.get(0);
			
			holder.tv.setText("To "+t.destination);
			holder.tv.setVisibility(View.VISIBLE);
			holder.tv.setBackgroundColor(color);
			holder.name.setText(s.name);
			holder.nextTrain.setText(formattedPredict(s));
		}
			
		return convertView;
	}
	
	private String formattedPredict(Stop s){
		String result = "";
		long min = s.minSec();
		if (min != Long.MAX_VALUE){
			long current = Calendar.getInstance().getTimeInMillis();
			long offset = current/1000 - time;
			long r = (min - offset)/60;
			result = Long.toString(r) + "min.";
			}
		return result;
	}
	
	static class StopHolder {
		TextView tv;
		TextView name;
		TextView nextTrain;
	}
}
