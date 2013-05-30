package com.rndapp.mtamap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.rndapp.mtamap.R;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class MainActivity extends Activity implements OnClickListener{
	ViewAnimator va;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private Context context;
	private JSONObject fetchedData;
	
	boolean zoomed = false;

    private ProgressDialog pd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	if (fetchedData != null){
	            pd.dismiss();
	            ListView lv = (ListView)findViewById(R.id.line_list);
	            ScheduleAdapter sa = new ScheduleAdapter(context, R.layout.item, fetchedData);
	            lv.setAdapter(sa);
				va.setInAnimation(slideLeftIn);
				va.setOutAnimation(slideLeftOut);
				va.showNext();
        	}else{
	            pd.dismiss();
	            Toast.makeText(context, "Please make sure you are connected to the internet.", 8000).show();
        	}
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = this;

        setXML();
    }
    
    private void setXML(){
		va = (ViewAnimator)findViewById(R.id.chooser_anim);
		setAnimations();

    	Button sched = (Button)findViewById(R.id.see_sched);

        TouchImageView img = (TouchImageView)findViewById(R.id.touchImg);
        Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap subway = BitmapFactory.decodeResource(getResources(), R.drawable.subway1, options);
        img.setImageBitmap(subway);
        
    	Button map = (Button)findViewById(R.id.see_map);
    	
    	Button red = (Button)findViewById(R.id.red_btn);
    	Button blue = (Button)findViewById(R.id.blue_btn);
    	Button orange = (Button)findViewById(R.id.orange_btn);
    	Button green = (Button)findViewById(R.id.green_btn);
    	
    	Button backsched = (Button)findViewById(R.id.back_to_sched);
    	
    	sched.setOnClickListener(this);
    	sched.setBackgroundColor(getResources().getColor(R.color.grey));
    	map.setOnClickListener(this);
    	map.setBackgroundColor(getResources().getColor(R.color.grey));
    	backsched.setOnClickListener(this);
    	backsched.setBackgroundColor(getResources().getColor(R.color.grey));

    	red.setOnClickListener(this);
    	red.setBackgroundColor(getResources().getColor(R.color.red));
    	blue.setOnClickListener(this);
    	blue.setBackgroundColor(getResources().getColor(R.color.blue));
    	orange.setOnClickListener(this);
    	orange.setBackgroundColor(getResources().getColor(R.color.orange));
    	green.setOnClickListener(this);
    	green.setBackgroundColor(getResources().getColor(R.color.green));
    }
    
    private void setAnimations(){
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        slideLeftIn.setAnimationListener(new ScrollLeft());
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        slideRightIn.setAnimationListener(new ScrollRight());
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out);
	}

	@Override
	public void onClick(View v) {
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
		case R.id.orange_btn:
			pd = ProgressDialog.show(this, "", "Loading...", true, true);
	    	Thread thread = new Thread(new Runnable(){
	        	public void run(){
	        		fetchedData = null;
	        		String sched = getSchedule("orange");
	        		try {
	        			fetchedData = new JSONObject(sched);
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
	        		handler.sendEmptyMessage(0);
	        	}
	    	});
	    	thread.start();
	    	break;
		case R.id.red_btn:
			pd = ProgressDialog.show(this, "", "Loading...", true, true);
	    	Thread thread2 = new Thread(new Runnable(){
	        	public void run(){
	        		fetchedData = null;
	        		String sched = getSchedule("red");
	        		try {
	        			fetchedData = new JSONObject(sched);
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
	        		handler.sendEmptyMessage(0);
	        	}
	    	});
	    	thread2.start();
	    	break;
		case R.id.blue_btn:
	    	pd = ProgressDialog.show(this, "", "Loading...", true, true);
	    	Thread thread3 = new Thread(new Runnable(){
	        	public void run(){
	        		fetchedData = null;
	        		String sched = getSchedule("blue");
	        		try {
	        			fetchedData = new JSONObject(sched);
	        		} catch (Exception e) {
	        			e.printStackTrace();
	        		}
	        		handler.sendEmptyMessage(0);
	        	}
	    	});
	    	thread3.start();
			break;
		case R.id.green_btn:
			Intent i = new Intent(this, Notification.class);
			startActivity(i);
			break;
		}
	}
    
    private String getSchedule(String line){
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://developer.mbta.com/lib/rthr/"+line+".json");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String l;
				while ((l = reader.readLine()) != null) {
					builder.append(l);
				}
			} else {
				//Log.e(ParseJSON.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	FlurryAgent.onStartSession(this, "G9S4S9H9JXBK884NW625");
    }
     
    @Override
    protected void onStop()
    {
    	super.onStop();		
    	FlurryAgent.onEndSession(this);
    }
    
    class ScrollRight implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			va.postDelayed(new Runnable(){
				@Override
				public void run(){
					//va.showPrevious();
				}
			}, 10);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}
	}

	class ScrollLeft implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			va.postDelayed(new Runnable(){
				@Override
				public void run(){
					//va.showNext();
				}
			}, 10);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

	}
}