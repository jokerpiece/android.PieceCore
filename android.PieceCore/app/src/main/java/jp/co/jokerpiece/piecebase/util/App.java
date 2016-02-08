package jp.co.jokerpiece.piecebase.util;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import jp.co.jokerpiece.piecebase.config.Config;

public class App extends Application {

    private static Context mContext;
    private Tracker tracker;

    public synchronized  Tracker getTracker(){
        if(tracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(Config.PROPERTY_ID);
        }
        return tracker;
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
