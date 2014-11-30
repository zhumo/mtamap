package com.rndapp.mtamap;

import com.rndapp.subway_lib.Line;
import com.rndapp.subway_lib.MainActivity;

import com.flurry.android.FlurryAgent;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import org.json.JSONObject;

public class MtaActivity extends MainActivity implements OnClickListener{

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 11) findViewById(R.id.touchImg).setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected String getLineUrl(Line line) {
        return "";
    }

    @Override
    protected String getFlurryApiKey() {
        return "J8FRNPXSK99CH9VQNT38";
    }

    @Override
    protected void onStart(){
    	super.onStart();
    	FlurryAgent.onStartSession(this, "J8FRNPXSK99CH9VQNT38");
    }
     
    @Override
    protected void onStop(){
    	super.onStop();		
    	FlurryAgent.onEndSession(this);
    }
}