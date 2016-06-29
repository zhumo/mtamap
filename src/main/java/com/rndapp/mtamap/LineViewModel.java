package com.rndapp.mtamap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * Created by ladmin on 13/06/16.
 */
public class LineViewModel {

    ArrayList<String> routeIdsArrayList = new ArrayList<String>();
    int color;

    public String routeString() {
        String routesString = "";

        if (routeIdsArrayList.size() > 0) {
            Collections.sort(routeIdsArrayList);
            routesString = routeIdsArrayList.toString();
            routesString = routesString + ",";
        }
        return routesString;
    }

    public boolean isEqual(Objects object) {
        Objects obj = null;

        if (obj.equals(object)) {
            if (obj.getClass().isInstance(LineViewModel.class)) {
                LineViewModel line = new LineViewModel();
                if (line.color == this.color) {
                    return true;
                }
            }
        }
        return false;
    }
}