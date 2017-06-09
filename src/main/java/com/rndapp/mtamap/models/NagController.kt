package com.rndapp.mtamap.models

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Handler
import android.text.TextUtils

import com.rndapp.mtamap.models.Analytics

import java.util.Date

/**
 * Created by ell on 8/4/15.
 */
class NagController(protected var activity: Activity?) {
    var nagtions: List<Nagtion> = ArrayList()
    var delayInSeconds: Int = 17
    var daysBetweenNags: Int = 2
    var opensOffset: Int = 4
    var opensBetweenNags: Int = 7

    fun startNag() {
        val appOpens = Analytics.getNumberOfOpens(activity)
        if (isNaggable(appOpens)){
            for (nagtion in nagtions){
                if (isNaggable(nagtion)){
                    scheduleNag(nagtion)
                }
            }
        }
    }

    fun isNaggable(appOpens: Int = 0): Boolean {
        val canNagCount = ((appOpens - opensOffset) % opensBetweenNags == 0)
        val canNagDate = lastNagged.before(daysBetweenNags.daysAgo())
        return canNagCount && canNagDate
    }

    fun isNaggable(nagtion: Nagtion): Boolean = activity!!.applicationContext.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).getBoolean(nagtion.defaultsKey, true)

    fun setNaggable(nagtion: Nagtion, isNaggable: Boolean) {
        val editor = activity!!.applicationContext.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit()
        editor.putBoolean(nagtion.defaultsKey, isNaggable).apply()
    }

    fun scheduleNag(nagtion: Nagtion) {
        Handler().postDelayed({
            showAlertFor(nagtion)
            lastNagged = Date()
        }, delayInSeconds.toLong() * 1000)
    }

    fun showAlertFor(nagtion: Nagtion){
        if (activity != null && !activity!!.isFinishing) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(nagtion.title)
            builder.setMessage(nagtion.message)
            builder.setCancelable(false)
            if (nagtion.yesAction != null && !TextUtils.isEmpty(nagtion.yesText)){
                builder.setNeutralButton(nagtion.yesText) { dialogInterface, i ->
                    setNaggable(nagtion, false)
                    nagtion.yesAction!!.invoke()
                    dialogInterface.cancel()
                }
            }
            if (nagtion.noAction != null && !TextUtils.isEmpty(nagtion.noText)){
                builder.setNegativeButton(nagtion.noText) { dialog, which ->
                    setNaggable(nagtion, false)
                    nagtion.noAction!!.invoke()
                    dialog.cancel()
                }
            }
            if (nagtion.maybeAction != null && !TextUtils.isEmpty(nagtion.maybeText)){
                builder.setPositiveButton(nagtion.maybeText) { dialog, which ->
                    nagtion.maybeAction!!.invoke()
                    dialog.cancel()
                }
            }
            builder.create().show()
        }
    }

    protected var lastNagged: Date
        get() {
            val preferences = activity!!.applicationContext.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
            if (preferences.getLong(kLastNaggedKey, 0) == 0.toLong()) {
                return Date(preferences.getLong(kLastNaggedKey, 0))
            } else {
                return Date(preferences.getLong(kLastNaggedKey, Date().time))
            }
        }
        set(value) {
            val editor = activity!!.applicationContext.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit()
            editor.putLong(kLastNaggedKey, value.time).apply()
        }

    fun Int.daysAgo() = Date(Date().time - this * 24 * 60 * 60 * 1000)

    companion object {
        protected val PREFS_NAME = "nagger"
        protected val kLastNaggedKey: String = "lastNagged"
    }
}
