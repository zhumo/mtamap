package com.subway.thryv.subwaynyc;

import java.io.Serializable;

/**
 * Created by ladmin on 02/06/2016.
 */
public class Stop implements Serializable {

    public String name;
    public String objectId;
    public String parentId;

    public Stop(String name, String objectId, String parentId) {
        super();
        this.name = name;
        this.objectId = objectId;
        this.parentId = parentId;
    }

    public Stop() {
        super();

    }
}
