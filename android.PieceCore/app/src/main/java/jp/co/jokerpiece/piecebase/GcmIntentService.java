package jp.co.jokerpiece.piecebase;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import jp.co.jokerpiece.piecebase.data.NewsListData;
import jp.co.jokerpiece.piecebase.util.AppUtil;

public class GcmIntentService extends IntentService {
    public static final String TAG = "GcmIntentService";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static boolean start_from_notification = false;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        start_from_notification = true;
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                AppUtil.debugLog("LOG", "messageType(error): " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                AppUtil.debugLog("LOG", "messageType(deleted): " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                AppUtil.debugLog("LOG", "messageType(message): " + messageType + ",body:" + extras.toString());

                Iterator<String> it = extras.keySet().iterator();
                String result = "";
                while(it.hasNext()) {
                    String key = it.next() + "";
                    String value = extras.getString(key) + "";
                    if (key.equals("message")) {
                        result = value;
                    }
                }
                HashMap<String, String> map = parseJSON(result);

                //通知バーに表示
                sendNotification(map);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);


    }

    public HashMap<String, String> parseJSON(String result) {
        //AppUtil.debugLog(TAG, "result= " + result);
        HashMap<String, String> map = new HashMap<String, String>();

        try {
            JSONObject rootObject = new JSONObject(result);

            JSONObject infoObject = rootObject.getJSONObject("info");
            map.put("title", infoObject.getString("title"));
            map.put("type", infoObject.getString("type"));
            map.put("id", infoObject.getString("id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AppUtil.debugLog(TAG, "map= " + map);
        return map;
    }

    private void sendNotification(HashMap<String, String> map) {

        String title = getBlankIfNull(map.get("title"));
        String msg = getBlankIfNull(map.get("alert"));
        AppUtil.debugLog("sendNotification", "title= " + title + "\nmsg= " + msg);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = getSelectedIntent(map);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.status_icon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public Intent getSelectedIntent(HashMap<String, String> map) {
        Intent intent = null;
        switch (getBlankIfNull(map.get("type"))) {
            case NewsListData.NEWS_DATA_TYPE_INFOMATION + "":
                intent = new Intent(this, MainBaseActivity.class);
                intent.putExtra("type", getBlankIfNull(map.get("type")));
                intent.putExtra("newsId", getBlankIfNull(map.get("id")));
                AppUtil.debugLog(TAG, "type: Infomation, newsId: " + getBlankIfNull(map.get("id")));
                break;
            case NewsListData.NEWS_DATA_TYPE_FLYER + "":
                intent = new Intent(this, MainBaseActivity.class);
                intent.putExtra("type", getBlankIfNull(map.get("type")));
                intent.putExtra("flyer_ID", getBlankIfNull(map.get("id")));
                AppUtil.debugLog(TAG, "type: Flyer, flyer_ID: " + getBlankIfNull(map.get("id")));
                break;
            case NewsListData.NEWS_DATA_TYPE_COUPON + "":
                intent = new Intent(this, MainBaseActivity.class);
                intent.putExtra("type", getBlankIfNull(map.get("type")));
                intent.putExtra("coupon_code", getBlankIfNull(map.get("id")));
                AppUtil.debugLog(TAG, "type: Coupon, coupon_ID: " + getBlankIfNull(map.get("id")));
                break;
            default:
                intent = new Intent(this, MainBaseActivity.class);
                break;
        }
        return intent;
    }

    public String getBlankIfNull(String s) {
        s = (s == null ? "": s);
        return s;
    }

}
