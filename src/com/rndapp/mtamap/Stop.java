package com.rndapp.mtamap;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;

public class Stop implements Serializable{
	public String name;
	public ArrayList<Long> seconds;
	
	public Stop(String s){
		super();
		seconds = new ArrayList<Long>();
		name = s;
	}
	
	public Stop(JSONObject stop){
		super();
		seconds = new ArrayList<Long>();
		try{
			this.name = stop.getString("Stop");
			this.seconds.add(stop.getLong("Seconds"));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public long minSec(){
		long min = Long.MAX_VALUE;
		for (int i = 0; i<seconds.size(); i++){
			Long l = seconds.get(i);
			if (l<min){
				min = l;
			}
		}
		return min;
	}
	
	@Override
	public boolean equals(Object o){
		if (o.getClass() != Stop.class)
			return false;
		if (!((Stop)o).name.equalsIgnoreCase(this.name))
			return false;
		return true;
	}

}
