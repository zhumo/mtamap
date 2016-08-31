package com.thryv.subway.abstractions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ell on 7/7/16.
 */
public abstract class StationManager {
    private static StationManager stationManager;

    public static StationManager getStationManager() {
        return stationManager;
    }

    public static void setStationManager(StationManager stationManager) {
        StationManager.stationManager = stationManager;
    }

    public abstract ArrayList<Station> getAllStations();
    public abstract ArrayList<Route> getAllRoutes();
    public abstract ArrayList<String> routeIdsForStation(Station station);
    public abstract ArrayList<Prediction> getPredictions(Station station, Date time);
}
