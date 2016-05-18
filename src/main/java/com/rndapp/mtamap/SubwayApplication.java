package com.rndapp.mtamap;

import android.support.multidex.MultiDexApplication;

/*import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;*/
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ell on 8/5/15.
 */
public class SubwayApplication  extends MultiDexApplication {
    private static SubwayApplication mInstance;
    //we have commented RequestQueue Code

    /*private static RequestQueue mRequestQueue;*/

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        /*mRequestQueue = Volley.newRequestQueue(this);*/

        Fabric.with(this, new Crashlytics());
        Analytics.init(this);
    }

    public static SubwayApplication getInstance(){return mInstance;};

  /*  public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }*/
}
