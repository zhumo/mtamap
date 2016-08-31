package com.thryv.subway.paris;

import com.thryv.subway.abstractions.Route;

import java.util.ArrayList;

/**
 * Created by ell on 7/8/16.
 */
public class ParisRoute extends Route {
    public ArrayList<String> routeIds = new ArrayList<>();

    public ParisRoute(String objectId) {
        super(objectId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParisRoute)) return false;
        ParisRoute route = (ParisRoute) o;

        return route.objectId == this.objectId;
    }
}
