package com.subway.ladmin.subwaynyk;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.lang.String;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ladmin on 02/06/2016.
 */
public class Station implements Serializable {
    public String name;
    public ArrayList<Stop> parents = new ArrayList<Stop>();

    Station(String name) {
        super();
        this.name = name;
    }

    public Station() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equal(Station lhs, Station rhs) {
        String lhsName = lhs.name;
        if (lhsName != null && !lhsName.equals("")) {
            String rhsName = rhs.name;
            if (rhsName != null && !rhsName.equals("")) {
                if (lhsName.toLowerCase().equals(rhsName.toLowerCase())) {
                    return true;
                }
                String[] lhsArray = lhsName.toLowerCase().split(" ");
                String[] rhsArray = rhsName.toLowerCase().split(" ");
                List lhsList = Arrays.asList(lhsArray);
                List rhsList = Arrays.asList(rhsArray);

                if (lhsArray.length == rhsArray.length) {
                    for (String lhsComponent : lhsArray) {
                        if (!rhsList.contains(lhsComponent)) {
                            return false;
                        }
                    }
                    for (String rhsComponent : rhsArray) {
                        if (!lhsList.contains(rhsComponent)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}








