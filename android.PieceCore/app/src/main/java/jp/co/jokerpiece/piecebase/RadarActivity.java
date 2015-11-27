package jp.co.jokerpiece.piecebase;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

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

        timerTask = new RadarTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule( timerTask, 0, 20);

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
                    if(beaconData != null){
                        Log.d("BeaconData", beaconData.getMajor());
                        ImageView treasure = (ImageView)findViewById(R.id.ivTreasure);
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
                    }
                }
            });
        }
    }
}
