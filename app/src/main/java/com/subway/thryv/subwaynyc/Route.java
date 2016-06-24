package com.subway.thryv.subwaynyc;

import java.io.Serializable;

/**
 * Created by ladmin on 06/06/2016.
 */
public class Route implements Serializable {

    public String color;
    public String objectId;

    public Route(String objectId) {
        super();
        this.objectId = objectId;
    }
}
