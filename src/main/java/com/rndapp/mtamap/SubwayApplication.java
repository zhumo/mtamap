package com.rndapp.mtamap;

import android.support.multidex.MultiDexApplication;

/*import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;*/
import com.crashlytics.android.Crashlytics;
import com.rndapp.mtamap.models.Analytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ell on 8/5/15.
 */
public class SubwayApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        Analytics.init(this);
    }

}
