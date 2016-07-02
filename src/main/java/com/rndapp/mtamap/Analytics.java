package com.rndapp.mtamap;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ell on 8/4/15.
 */
public class Analytics {
    private static final String PARSE_KEY_ONE = "HyWyfWUGFYQGcdOLnKABphJIZPwpzUrvE21fvUH8";
    private static final String PARSE_KEY_TWO = "MHPtabvRXUNUSO2kGQoVzPjHUZwjlR9AgxKofF8Q";
    private static final String FLURRY_KEY = "J8FRNPXSK99CH9VQNT38";

    private static final String STARTED_APP_KEY = "StartedApp";
    private static final String SESSION_END_KEY = "SessionEnding";
    private static final String MAP_KEY = "Map";
    private static final String SCHEDULES_KEY = "Schedules";
    private static final String APP_OPENS_KEY = "NumberOfAppOpens";
    private static final String NAG_RATING = "NagRating-v1";
    private static final String NAG_APP = "NagApp-v1";

    public static void init(Application application){
        Parse.initialize(application, PARSE_KEY_ONE, PARSE_KEY_TWO);
        FlurryAgent.init(application, FLURRY_KEY);
    }

    public static void activityCreated(AppCompatActivity activity){
        ParseAnalytics.trackAppOpenedInBackground(activity.getIntent());
    }

    public static void onStart(Context context){
        incrementNumberOfOpens(context);

        //Values
        Map<String, String> values = new HashMap<>();
        values.put(APP_OPENS_KEY, Integer.toString(getNumberOfOpens(context)));

        //Parse
        ParseAnalytics.trackEventInBackground(STARTED_APP_KEY, values);

        //Flurry
        FlurryAgent.onStartSession(context, FLURRY_KEY);
        FlurryAgent.logEvent(STARTED_APP_KEY, values);
    }

    public static void onStop(Context context){
        //Values
        Map<String, String> values = new HashMap<>();
        values.put(APP_OPENS_KEY, Integer.toString(getNumberOfOpens(context)));

        ParseAnalytics.trackEventInBackground(SESSION_END_KEY, values);

        FlurryAgent.logEvent(SESSION_END_KEY, values);
        FlurryAgent.onEndSession(context);
    }

    public static void mapShown(Context context){
        //GoogleAnalytics

        //Parse
        ParseAnalytics.trackEventInBackground(MAP_KEY);

        //Flurry
        FlurryAgent.logEvent(MAP_KEY);
    }

    public static void nagRating(Context context, String response){
        Map<String, String> map = new HashMap<String, String>();
        map.put("numberOfAppOpens", numberGroup(Analytics.getNumberOfOpens(context)));
        map.put("response", response);

        //Parse
        ParseAnalytics.trackEventInBackground(NAG_RATING, map);

        //Flurry
        FlurryAgent.logEvent(NAG_RATING, map);
    }

    public static void nagApp(Context context, String response){
        Map<String, String> map = new HashMap<String, String>();
        map.put("numberOfAppOpens", numberGroup(Analytics.getNumberOfOpens(context)));
        map.put("response", response);

        //Parse
        ParseAnalytics.trackEventInBackground(NAG_APP, map);

        //Flurry
        FlurryAgent.logEvent(NAG_APP, map);
    }

    public static void schedulesShown(Context context){
        //GoogleAnalytics

        //Parse
        ParseAnalytics.trackEventInBackground(SCHEDULES_KEY);

        //Flurry
        FlurryAgent.logEvent(SCHEDULES_KEY);
    }

    public static int getNumberOfOpens(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("Analytics", Context.MODE_PRIVATE);
        return preferences.getInt("number_of_app_opens", 0);
    }

    private static void incrementNumberOfOpens(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("Analytics", Context.MODE_PRIVATE);
        int numberOfOpens = preferences.getInt("number_of_app_opens", 0);
        numberOfOpens++;
        preferences.edit().putInt("number_of_app_opens", numberOfOpens).apply();
    }

    protected static String numberGroup(int number){
        if (number == 1){
            return "1";
        }else if (number < 6){
            return "2-5";
        }else if (number < 10){
            return "6-10";
        }else if (number < 20){
            return "11-20";
        }else if (number < 50){
            return "21-50";
        }else if (number < 100){
            return "51-100";
        }
        return "100+";
    }
}
