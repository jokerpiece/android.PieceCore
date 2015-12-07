package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import jp.co.jokerpiece.piecebase.util.AppUtil;
import jp.co.jokerpiece.piecebase.util.RadarUtil;

public class RadarActivity extends Activity {
    Handler mHandler = new Handler();
    RadarTimerTask timerTask = null;
    Timer mTimer   = null;

    // 検索するビーコンの情報
    private String uuid = null;
    private int major = -1;
    private int minor = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        RadarUtil.init(this);
        beaconScan();

        String data = getIntent().getStringExtra("file_data");
        JSONObject rootObject;
        try {
            rootObject = new JSONObject(data);
            uuid = rootObject.getString("uuid");
            major = rootObject.getInt("major_id");
            minor = rootObject.getInt("minor_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        timerTask = new RadarTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule(timerTask, 0, 20);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void  beaconScan(){
        RadarUtil.startScan();
    }

    class RadarTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post( new Runnable() {
                public void run() {
                    beaconScan();
                    RadarUtil.BeaconData beaconData = RadarUtil.getBeaconWithUUID(uuid,major,minor);
                    ImageView treasure = (ImageView)findViewById(R.id.ivTreasure);

                    if(beaconData != null){
                        AppUtil.debugLog("BeaconData", beaconData.getMajor());
                        int rssi = beaconData.getRssiAverage();
                        if(rssi > -65){
                            treasure.setImageResource(R.drawable.treasure1);
                        }else if(rssi > -70){
                            treasure.setImageResource(R.drawable.treasure2);
                        }else if(rssi > -80){
                            treasure.setImageResource(R.drawable.treasure3);
                        }else if(rssi > -90){
                            treasure.setImageResource(R.drawable.treasure4);
                        }else{
                            treasure.setImageResource(R.drawable.treasure5);
                        }
                    }else{
                        treasure.setImageResource(R.drawable.treasure5);
                    }
                }
            });
        }
    }
}
