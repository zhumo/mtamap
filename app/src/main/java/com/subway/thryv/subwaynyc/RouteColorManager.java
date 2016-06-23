package com.subway.thryv.subwaynyc;

/**
 * Created by ladmin on 13/06/16.
 */
public class RouteColorManager {

    public int colorForRouteId(String routeId) {

        int color = 0;
        String[] stringArray = {"1", "2", "3", "4", "5", "5X", "6", "7", "7X", "A", "C", "E", "B", "D", "F", "M", "N", "Q", "R"};
        String containString = stringArray.toString();
        if (containString.contains(routeId)) {

        }
        return color;
    }
}
