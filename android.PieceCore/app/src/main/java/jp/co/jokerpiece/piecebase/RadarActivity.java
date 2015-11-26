package jp.co.jokerpiece.piecebase;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.jokerpiece.piecebase.util.BeaconUtil;
import jp.co.jokerpiece.piecebase.util.RadarUtil;

public class RadarActivity extends Activity {
    Handler mHandler = new Handler();
    RadarTimerTask timerTask = null;
    Timer mTimer   = null;

    // 検索するビーコンの情報
    private String uuid;
    private String major;
    private String minor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        RadarUtil.init(this);
        beaconScan();

        timerTask = new RadarTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule( timerTask, 0, 20);

        uuid="00000000-5668-1001-B000-000000000009";


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
                    RadarUtil.BeaconData beaconData = RadarUtil.getBeaconWithUUID(uuid);
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
