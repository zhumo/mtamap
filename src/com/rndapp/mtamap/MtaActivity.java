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
        if (v.getId() == R.id.see_map){
            va.setInAnimation(slideRightIn);
            va.setOutAnimation(slideRightOut);
            va.showPrevious();
        }else if (v.getId() == R.id.see_sched){
            va.setInAnimation(slideLeftIn);
            va.setOutAnimation(slideLeftOut);
            va.showNext();
        }else if (v.getId() == R.id.back_to_sched){
            va.setInAnimation(slideRightIn);
            va.setOutAnimation(slideRightOut);
            va.showPrevious();
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