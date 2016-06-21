package com.subway.ladmin.subwaynyk;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by ladmin on 13/06/16.
 */
public class LineViewModel {

    ArrayList<String> routeIds = new ArrayList<String>();
    int color;

    public String routeString() {
        String routesString = "";

        if (routeIds.size() > 0) {
            Collections.sort(routeIds);
            routesString = routeIds.toString();
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