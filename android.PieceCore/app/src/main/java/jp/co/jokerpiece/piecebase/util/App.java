package jp.co.jokerpiece.piecebase.util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import jp.co.jokerpiece.piecebase.R;
import jp.co.jokerpiece.piecebase.config.Config;

public class App extends Application {

    private static Context mContext;
   // private Tracker tracker = null;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    public enum TrackerName{
        APP_TRACKER,
        GLOBAL_TRACKER,
        ECOMMERCE_TRACKER,
    }
    HashMap<TrackerName,Tracker> mTrackers = new HashMap<TrackerName,Tracker>();
    public synchronized  Tracker getTracker(TrackerName trackerId){
//        if(tracker == null){
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            AppUtil.debugLog("PROPERTY_ID",Config.PROPERTY_ID);
//
//            tracker = analytics.newTracker(Config.PROPERTY_ID);
//        }
        if(!mTrackers.containsKey(trackerId)){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId,t);
        }
        return mTrackers.get(trackerId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }

    public static Context getContext(){
        return mContext;
    }
}
