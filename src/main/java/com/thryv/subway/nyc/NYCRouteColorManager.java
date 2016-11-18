package com.thryv.subway.nyc;

import android.graphics.Color;

import com.thryv.subway.abstractions.RouteColorManager;

/**
 * Created by ladmin on 13/06/16.
 */
public class NYCRouteColorManager extends RouteColorManager {

    public int colorForRouteId(String routeId) {

        int color = Color.parseColor("#2F4F4F");

        if ((routeId.equals("1")) || (routeId.equals("2")) || (routeId.equals("3"))) {
            color = Color.parseColor("#ED3B43");
        }
        if (routeId.equals("4") || routeId.equals("5") || routeId.equals("5X") || routeId.equals("6") || routeId.equals("6X")) {
            color = Color.parseColor("#00A55E");
        }
        if ((routeId.equals("7")) || (routeId.equals("7X"))) {
            color = Color.parseColor("#A23495");
        }
        if ((routeId.equals("A")) || (routeId.equals("C")) || (routeId.equals("E"))) {
            color = Color.parseColor("#006BB7");
        }
        if ((routeId.equals("B")) || (routeId.equals("D")) || (routeId.equals("F")) || (routeId.equals("M"))) {
            color = Color.parseColor("#F58120");
        }
        if (routeId.equals("N") || routeId.equals("Q") || routeId.equals("R") || routeId.equals("W")) {
            color = Color.parseColor("#FFD51D");
        }
        if ((routeId.equals("JZ"))) {
            color = Color.parseColor("#B1730E");
        }

        return color;
    }
}
