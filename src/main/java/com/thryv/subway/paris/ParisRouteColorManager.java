package com.thryv.subway.paris;

import android.graphics.Color;

import com.thryv.subway.abstractions.RouteColorManager;

/**
 * Created by ell on 7/7/16.
 */
public class ParisRouteColorManager extends RouteColorManager{

    @Override
    public int colorForRouteId(String routeId) {
        int color = Color.DKGRAY;

        if ("1".equals(routeId)) color = Color.parseColor("#ffcd01");
        if ("2".equals(routeId)) color = Color.parseColor("#006cb8");
        if ("3".equals(routeId)) color = Color.parseColor("#9b993b");
        if ("3bis".equals(routeId)) color = Color.parseColor("#6dc5e0");
        if ("4".equals(routeId)) color = Color.parseColor("#bb4b9c");
        if ("5".equals(routeId)) color = Color.parseColor("#f68f4b");
        if ("6".equals(routeId)) color = Color.parseColor("#77c696");
        if ("7".equals(routeId)) color = Color.parseColor("#f59fb3");
        if ("7bis".equals(routeId)) color = Color.parseColor("#77c696");
        if ("8".equals(routeId)) color = Color.parseColor("#c5a3cd");
        if ("9".equals(routeId)) color = Color.parseColor("#cec92b");
        if ("10".equals(routeId)) color = Color.parseColor("#e0b03b");
        if ("11".equals(routeId)) color = Color.parseColor("#906030");
        if ("12".equals(routeId)) color = Color.parseColor("#008b5a");
        if ("13".equals(routeId)) color = Color.parseColor("#87d3df");
        if ("14".equals(routeId)) color = Color.parseColor("#652c90");
        if ("15".equals(routeId)) color = Color.parseColor("#a90f32");
        if ("16".equals(routeId)) color = Color.parseColor("#ec7cae");
        if ("17".equals(routeId)) color = Color.parseColor("#ec7cae");
        if ("18".equals(routeId)) color = Color.parseColor("#95bf32");
        
        return color;
    }
}
