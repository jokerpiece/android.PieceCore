package jp.co.jokerpiece.piecebase;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import jp.co.jokerpiece.piecebase.api.GetFileDataAPI;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.GetFileData;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class PlayBackActivity extends FragmentActivity {

    String order_id;
    Context context;
    String PLAYBACK_MESSAGE = "3";
    String PLAYBACK_MOVIE = "1";
    String PLAYBACK_HOLOGRAM = "2";
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
                }else{
                    finish();
                }
            }

            @Override
            public void onLoaderReset(Loader<GetFileData> loader) {

            }
        });
    }

}
