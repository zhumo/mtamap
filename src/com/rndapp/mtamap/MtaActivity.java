package com.rndapp.mtamap;

import com.rndapp.subway_lib.MainActivity;

import com.flurry.android.FlurryAgent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MtaActivity extends MainActivity implements OnClickListener{

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = this;

        setXML();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.see_map:
                va.setInAnimation(slideRightIn);
                va.setOutAnimation(slideRightOut);
                va.showPrevious();
                break;
            case R.id.see_sched:
                va.setInAnimation(slideLeftIn);
                va.setOutAnimation(slideLeftOut);
                va.showNext();
                break;
            case R.id.back_to_sched:
                va.setInAnimation(slideRightIn);
                va.setOutAnimation(slideRightOut);
                va.showPrevious();
                break;
        }
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	FlurryAgent.onStartSession(this, "J8FRNPXSK99CH9VQNT38");
    }
     
    @Override
    protected void onStop()
    {
    	super.onStop();		
    	FlurryAgent.onEndSession(this);
    }
}