package com.rndapp.mtamap.models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;

import com.rndapp.mtamap.models.Analytics;

import java.util.Date;

/**
 * Created by ell on 8/4/15.
 */
public class Nagger {
    protected static final String PREFS_NAME = "nagger";
    protected Activity activity;

    public Nagger(Activity activity) {
        this.activity = activity;
    }

    public void startNag(final String freeLink, final String paidLink){
        final int appOpens = Analytics.getNumberOfOpens(activity);
        if (appOpens != 0 && appOpens % 7 - 3 == 0){
            if (getLastNagged().before(new Date(new Date().getTime() - 3 * 24 * 60 * 60 * 1000))){
                if (canNagRating()){
                    if (freeLink != null){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (activity != null && !activity.isFinishing()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setTitle("Sorry to interrupt...");
                                    builder.setMessage("...but would you mind terribly taking a moment to rate this app?");
                                    builder.setCancelable(false);
                                    builder.setNeutralButton("Yes, I'd be\ndelighted!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Analytics.nagRating(activity, "Yes, I'd be delighted!");
                                            setCanNagRating(false);
                                            dialogInterface.cancel();
                                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(freeLink)));
                                        }
                                    });
                                    builder.setNegativeButton("Nope,\nnever", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Analytics.nagRating(activity, "Nope, never");
                                            setCanNagRating(false);
                                            dialog.cancel();
                                        }
                                    });
                                    builder.setPositiveButton("Mmmm,\nmaybe later", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Analytics.nagRating(activity, "Mmmm, maybe later");
                                            dialog.cancel();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        }, 17000);
                    }
                }else if (canNagApp()){
                    if (paidLink != null){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (activity != null && !activity.isFinishing()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setTitle("Hey!");
                                    builder.setMessage("You seem to be enjoying the app, would you like to help support an independent app developer and download the paid version?");
                                    builder.setCancelable(false);
                                    builder.setNeutralButton("Yes, I'd be\ndelighted!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Analytics.nagApp(activity, "Yes, I'd be delighted!");
                                            setCanNagApp(false);
                                            dialogInterface.cancel();
                                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(paidLink)));
                                        }
                                    });
                                    builder.setNegativeButton("I prefer\nclicking ads", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Analytics.nagApp(activity, "I prefer clicking ads");
                                            setCanNagApp(false);
                                            dialog.cancel();
                                        }
                                    });
                                    builder.setPositiveButton("Mmmm,\nmaybe later", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Analytics.nagApp(activity, "Mmmm, maybe later");
                                            dialog.cancel();
                                        }
                                    });

                                    builder.create().show();
                                }
                            }
                        }, 17000);
                    }
                }
            }
        }
    }

    protected boolean canNagRating(){
        return activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).getBoolean("canNagRating", true);
    }

    protected boolean canNagApp(){
        return activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).getBoolean("canNagApp", true);
    }

    protected void setCanNagRating(boolean canNag){
        SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit();
        editor.putBoolean("canNagRating", canNag).apply();
    }

    protected void setCanNagApp(boolean canNag){
        SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit();
        editor.putBoolean("canNagApp", canNag).apply();
    }

    protected Date getLastNagged(){
        SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        if (preferences.getLong("lastNagged", 0) == 0){
            return new Date(preferences.getLong("lastNagged", 0));
        }else {
            return new Date(preferences.getLong("lastNagged", new Date().getTime()));
        }
    }
}
