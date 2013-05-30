package com.rndapp.mtamap;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Trip implements Serializable{
	public String line;
	String destination;
	ArrayList<Stop> stops;
	
	public Trip(JSONObject trip, String line){
		super();
		destination = "";
		this.line = line;
		stops = new ArrayList<Stop>();
		try{
			this.destination = trip.getString("Destination");
			initStops();
			incorporatePredictions(trip.getJSONArray("Predictions"));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void incorporatePredictions(JSONArray predictions){
		for (int i = 0; i<predictions.length(); i++){
			boolean added = false;
			for (int j = 0; j<stops.size(); j++){
				Stop s = stops.get(j);
				try{
					JSONObject jsonp = predictions.getJSONObject(i);
					Stop p = new Stop(jsonp);
					if (p.equals(s)){
						s.seconds.add(jsonp.getLong("Seconds"));
						added = true;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if (!added){
				try{
					Stop p = new Stop(predictions.getJSONObject(i));
					stops.add(p);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean equals(Object o){
		if (o.getClass() != Trip.class)
			return false;
		if (!((Trip)o).destination.equalsIgnoreCase(this.destination))
			return false;
		return true;
	}
	
	private void initStops(){
		String[][] lines = {{"Wonderland",
			"Revere Beach",
			"Beachmont",
			"Suffolk Downs",
			"Orient Heights",
			"Wood Island",
			"Airport",
			"Maverick",
			"Aquarium",
			"State Street",
			"Government Center",
			"Bowdoin"},
			{"Oak Grove",
			"Malden Center",
			"Wellington",
			"Sullivan",
			"Community College",
			"North Station",
			"Haymarket",
			"State Street",
			"Downtown Crossing",
			"Chinatown",
			"Tufts Medical",
			"Back Bay",
			"Mass Ave",
			"Ruggles",
			"Roxbury Crossing",
			"Jackson Square",
			"Stony Brook",
			"Green Street",
			"Forest Hills"},
			{"Alewife",
			"Davis",
			"Porter Square",
			"Harvard Square",
			"Central Square",
			"Kendall/MIT",
			"Charles/MGH",
			"Park Street",
			"Downtown Crossing",
			"South Station",
			"Broadway",
			"Andrew",
			"JFK/UMass",
			"North Quincy",
			"Wollaston",
			"Quincy Center",
			"Quincy Adams",
			"Braintree",
			"Savin Hill",
			"Fields Corner",
			"Shawmut",
			"Ashmont"},
			{"Alewife",
				"Davis",
				"Porter Square",
				"Harvard Square",
				"Central Square",
				"Kendall/MIT",
				"Charles/MGH",
				"Park Street",
				"Downtown Crossing",
				"South Station",
				"Broadway",
				"Andrew",
				"JFK/UMass",
				"North Quincy",
				"Wollaston",
				"Quincy Center",
				"Quincy Adams",
				"Braintree"},
			{"Alewife",
					"Davis",
					"Porter Square",
					"Harvard Square",
					"Central Square",
					"Kendall/MIT",
					"Charles/MGH",
					"Park Street",
					"Downtown Crossing",
					"South Station",
					"Broadway",
					"Andrew",
					"JFK/UMass",
					"Savin Hill",
					"Fields Corner",
					"Shawmut",
					"Ashmont"}};
		String[] list = {""};
		if (line.equalsIgnoreCase("blue")){
			list = lines[0];
		}else if (line.equalsIgnoreCase("orange")){
			list = lines[1];
		}else if (line.equalsIgnoreCase("red")){
			if (destination.equals("Alewife")){
				list = lines[2];
			}else if (destination.equals("Braintree")){
				list = lines[3];
			}else if (destination.equals("Ashmont")){
				list = lines[4];
			}
		}
		
		if (!destination.equalsIgnoreCase(list[0])){
			for (int i = 0; i < list.length; i++){
				String s = list[i];
				Stop stop = new Stop(s);
				stops.add(stop);
			}
		}else{
			for (int i = 0; i < list.length; i++){
				String s = list[list.length - 1 - i];
				Stop stop = new Stop(s);
				stops.add(stop);
			}
		}
		
	}
}
