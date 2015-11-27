package jp.co.jokerpiece.piecebase;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import jp.co.jokerpiece.piecebase.api.GetFileDataAPI;
import jp.co.jokerpiece.piecebase.api.GetQuestionActivity;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.GetFileData;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class PlayBackActivity extends FragmentActivity {

    String order_id;
    int gps_type = -1;
    Context context;
    String PLAYBACK_MOVIE = "1";
    String PLAYBACK_HOLOGRAM = "2";
    String PLAYBACK_MESSAGE = "3";
    String PADLOCK_MESSAGE = "4";
    String TRESURE_HUNT = "5";
    String GPS_TRACKING = "6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(PlayBackMessageActivity.backFromMsg){
            PlayBackMessageActivity.backFromMsg = false;
            finish();
        }
        Intent intent = this.getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                order_id = uri.getQueryParameter("order_id");

                String strType = uri.getQueryParameter("type");
                if(strType != null) {
                    try {
                        gps_type = Integer.valueOf(strType);
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }

                Log.d("playback",order_id);
                getData();

            }
        }
    }

    public void modeChoice(String type,String data){

        if(type.equals(PLAYBACK_MESSAGE)) {
            Intent i = new Intent(this, PlayBackMessageActivity.class);
            i.putExtra("file_data",data);
            this.startActivity(i);
        }else if(type.equals(PLAYBACK_MOVIE)){
            Intent i = new Intent(this,PlayBackMovieActivity.class);
            i.putExtra("file_data",data);
            this.startActivity(i);
        }else if(type.equals(PLAYBACK_HOLOGRAM)){
            Intent i = new Intent(this,PlayBackHologramActivity.class);
            i.putExtra("file_data",data);
            this.startActivity(i);
        }else if(type.equals(PADLOCK_MESSAGE)){
            // TODO:南京錠画面への遷移を作成
            Intent i = new Intent(this,GetQuestionActivity.class);
            i.putExtra("order_id", decodeCD(order_id));
            this.startActivity(i);
        }else if(type.equals(TRESURE_HUNT)){
            Intent i = new Intent(this,RadarActivity.class);
            i.putExtra("file_data",data);
            this.startActivity(i);
        }else if(type.equals(GPS_TRACKING)){
            Intent i = new Intent(this,GPSLocationActivity.class);
            i.putExtra("file_data", data);
            i.putExtra("type", gps_type);
            i.putExtra("order_id", decodeCD(order_id));

            this.startActivity(i);
        }
    }
    public void getData(){
        this.getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<GetFileData>() {
            @Override
            public Loader<GetFileData> onCreateLoader(int id, Bundle args) {
                GetFileDataAPI getFileDataAPI = new GetFileDataAPI(context,order_id);
                getFileDataAPI.forceLoad();
                return getFileDataAPI;
            }

            @Override
            public void onLoadFinished(Loader<GetFileData> loader, GetFileData data) {
                if(data.status_code.equals("00")) {
                    modeChoice(data.type_code, data.file_data);
                }
                finish();

            }

            @Override
            public void onLoaderReset(Loader<GetFileData> loader) {

            }
        });
    }
    private String decodeCD(String cdID){
        String result;
        if(cdID != null && cdID.length() >= 7){
            int intNum = Integer.parseInt(cdID.substring(0,6));
            result = String.valueOf(intNum);
        }else {
            result = cdID;
        }
        return result;
    }
}
