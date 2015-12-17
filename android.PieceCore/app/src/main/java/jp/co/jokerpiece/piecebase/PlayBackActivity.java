package jp.co.jokerpiece.piecebase;

import android.*;
import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import jp.co.jokerpiece.piecebase.api.GetFileDataAPI;
import jp.co.jokerpiece.piecebase.config.Common;
import jp.co.jokerpiece.piecebase.config.Config;
import jp.co.jokerpiece.piecebase.data.GetFileData;
import jp.co.jokerpiece.piecebase.util.AppUtil;

/**
 * Created by wenHsin on 2015/11/19.
 */
public class PlayBackActivity extends FragmentActivity {

    String order_id;
    String file_data;
    int gps_type = -1;
    Context context;
    String PLAYBACK_MOVIE = "1";
    String PLAYBACK_HOLOGRAM = "2";
    String PLAYBACK_MESSAGE = "3";
    String PADLOCK_MESSAGE = "4";
    String TREASURE_HUNT = "5";
    String GPS_TRACKING = "6";
    private final int REQUEST_LOCATION_PERMISSION = 0;
    private final int REQUEST_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PlayBackMessageActivity.backFromMsg) {
            PlayBackMessageActivity.backFromMsg = false;
            finish();
        }
        Intent intent = this.getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                if(uri.getHost().equals("rapping")) {
                    order_id = uri.getQueryParameter("order_id");

                    String strType = uri.getQueryParameter("type");
                    if (strType != null) {
                        try {
                            gps_type = Integer.valueOf(strType);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    AppUtil.debugLog("playback", order_id);
                    getData();
                }
            }
        }
    }

    public void modeChoice(String type, String data) {
        Intent i = null;
        if (type.equals(PLAYBACK_MESSAGE)) {
            i = new Intent(this, PlayBackMessageActivity.class);
            i.putExtra("file_data", data);
        } else if (type.equals(PLAYBACK_MOVIE)) {
            i = new Intent(this, PlayBackMovieActivity.class);
            i.putExtra("file_data", data);
        } else if (type.equals(PLAYBACK_HOLOGRAM)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    runDownloadMovie();
                }
            } else {
                runDownloadMovie();
//                i = new Intent(this, MovieDownloadActivity.class);
//                i.putExtra("file_data", data);
            }
        } else if (type.equals(PADLOCK_MESSAGE)) {
            i = new Intent(this, GetQuestionActivity.class);
            i.putExtra("order_id", AppUtil.decodeCD(order_id));
        } else if (type.equals(TREASURE_HUNT)) {
            i = new Intent(this, RadarActivity.class);
            i.putExtra("file_data", data);
        } else if (type.equals(GPS_TRACKING)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermission();
                } else {
                    runGPSLocationActivity();
//                    i = new Intent(this, GPSLocationActivity.class);
//                    i.putExtra("file_data", data);
//                    i.putExtra("type", gps_type);
//                    i.putExtra("order_id", AppUtil.decodeCD(order_id));
                }
            } else {
                runGPSLocationActivity();
            }
        }
        if (i != null) {
            this.startActivity(i);
        }
    }

    public void runGPSLocationActivity() {
        Intent i = new Intent(this, GPSLocationActivity.class);
        i.putExtra("file_data", file_data);
        i.putExtra("type", gps_type);
        i.putExtra("order_id", AppUtil.decodeCD(order_id));
        this.startActivity(i);
    }

    private void runDownloadMovie() {
        Intent i = new Intent(this, MovieDownloadActivity.class);
        i.putExtra("file_data", file_data);
        this.startActivity(i);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            Toast toast = Toast.makeText(this, "許可されないと実行できません。", Toast.LENGTH_SHORT);
            toast.show();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            Toast toast = Toast.makeText(this, "許可されないと実行できません。", Toast.LENGTH_SHORT);
            toast.show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runGPSLocationActivity();
                return;
            } else {
                Toast toast = Toast.makeText(this, "許可されないと実行できません", Toast.LENGTH_SHORT);
                toast.show();

            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runDownloadMovie();
                return;
            } else {
                Toast toast = Toast.makeText(this, "許可されないと実行できません。", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void getData() {
        this.getLoaderManager().initLoader(Config.loaderCnt++, null, new LoaderManager.LoaderCallbacks<GetFileData>() {
            @Override
            public Loader<GetFileData> onCreateLoader(int id, Bundle args) {
                GetFileDataAPI getFileDataAPI = new GetFileDataAPI(context, order_id);
                getFileDataAPI.forceLoad();
                return getFileDataAPI;
            }

            @Override
            public void onLoadFinished(Loader<GetFileData> loader, GetFileData data) {
                if (data == null) {
                    Common.serverErrorMessage(context);
                } else if (data.status_code.equals("00")) {
                    file_data = data.file_data;
                    modeChoice(data.type_code, data.file_data);
                }
                finish();
            }

            @Override
            public void onLoaderReset(Loader<GetFileData> loader) {

            }
        });
    }
}