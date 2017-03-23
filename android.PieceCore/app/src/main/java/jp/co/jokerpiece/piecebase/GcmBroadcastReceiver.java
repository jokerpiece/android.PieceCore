package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import jp.co.jokerpiece.piecebase.config.Constants;
import jp.co.jokerpiece.piecebase.util.ForegroundCheckTask;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Check the app is in foreground or not when push notification delivered.
        try {
            boolean foreground = new ForegroundCheckTask().execute(context).get();
            if(!foreground){
                if(BuildConfig.DEBUG){
                    Log.d("is foreground","false");
                }
                GcmIntentService.notifyMode = Constants.IS_START_FROM_NOTIFICATION;
            }
            else
            {
                if(BuildConfig.DEBUG){
                    Log.d("is foreground","true");
                }
                GcmIntentService.notifyMode = Constants.IS_NOT_START_FROM_NOTIFACTION;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        // Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
